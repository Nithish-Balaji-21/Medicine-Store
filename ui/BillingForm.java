package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;

public class BillingForm extends JFrame {
    private JTextField searchField, qtyField;
    private JButton addToCartBtn, generateInvoiceBtn;
    private JTable cartTable;
    private JLabel totalLabel, finalTotalLabel;

    private DefaultTableModel cartModel;
    private double total = 0;

    public BillingForm() {
        setTitle("Billing System");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        menu.add(createMenuItem("Dashboard", () -> new Dashboard("Admin")));
        menu.add(createMenuItem("Manage Medicines", () -> new MedicineForm("Admin")));
        menu.add(createMenuItem("Invoice History", InvoiceHistory::new));
        menu.add(createMenuItem("Stock Report", StockReport::new));
        menu.add(createMenuItem("Daily/Monthly Report", DailyMonthlyReport::new));
        menu.add(createMenuItem("Logout", LoginForm::new));

        menuBar.add(menu);
        setJMenuBar(menuBar);

        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        searchField = new JTextField();
        qtyField = new JTextField();
        addToCartBtn = new JButton("Add to Cart");
        generateInvoiceBtn = new JButton("Generate Invoice");

        topPanel.add(new JLabel("Search Medicine:"));
        topPanel.add(searchField);
        topPanel.add(addToCartBtn);
        topPanel.add(new JLabel("Quantity:"));
        topPanel.add(qtyField);
        topPanel.add(generateInvoiceBtn);

        add(topPanel, BorderLayout.NORTH);

        // Updated table model
        cartModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Qty", "Line Total", "Discount %"}, 0);
        cartTable = new JTable(cartModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        totalLabel = new JLabel("Total: ₹0", SwingConstants.LEFT);
        finalTotalLabel = new JLabel("Final Total: ₹0", SwingConstants.RIGHT);
        bottomPanel.add(totalLabel);
        bottomPanel.add(finalTotalLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        addToCartBtn.addActionListener(e -> addToCart());
        generateInvoiceBtn.addActionListener(e -> generateInvoice());

        setVisible(true);
    }

    private JMenuItem createMenuItem(String title, Runnable action) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(e -> {
            action.run();
            dispose();
        });
        return item;
    }

    private void addToCart() {
        String keyword = searchField.getText().trim();
        int qty;

        try {
            qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT m.*, cd.discount_percent FROM medicines m " +
                 "LEFT JOIN category_discounts cd ON m.category = cd.category " +
                 "WHERE m.name LIKE ?")) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                double price = rs.getDouble("price");
                int stockQty = rs.getInt("quantity");
                double discountPercent = rs.getDouble("discount_percent");

                if (rs.wasNull()) {
                    discountPercent = 0;
                }

                if (qty > stockQty) {
                    JOptionPane.showMessageDialog(this, "Insufficient stock!");
                    return;
                }

                double lineTotal = price * qty * (1 - discountPercent / 100.0);
                total += lineTotal;

                cartModel.addRow(new Object[]{id, name, category, price, qty, lineTotal, discountPercent});
                totalLabel.setText("Total: ₹" + String.format("%.2f", total));
                finalTotalLabel.setText("Final Total: ₹" + String.format("%.2f", total));
            } else {
                JOptionPane.showMessageDialog(this, "Medicine not found!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while adding to cart: " + ex.getMessage());
        }
    }

    private void generateInvoice() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            double finalTotal = total;

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO invoices (date, total, discount, final_total) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setDouble(2, total);
            ps.setDouble(3, 0); // Category-based discounts only
            ps.setDouble(4, finalTotal);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int invoiceId = rs.getInt(1);

            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int medId = (int) cartModel.getValueAt(i, 0);
                int qty = (int) cartModel.getValueAt(i, 4);
                double price = (double) cartModel.getValueAt(i, 3);

                try (PreparedStatement itemPs = con.prepareStatement(
                        "INSERT INTO invoice_items (invoice_id, medicine_id, quantity, price) VALUES (?, ?, ?, ?)");
                     PreparedStatement updateStock = con.prepareStatement(
                        "UPDATE medicines SET quantity = quantity - ? WHERE id = ?")) {

                    itemPs.setInt(1, invoiceId);
                    itemPs.setInt(2, medId);
                    itemPs.setInt(3, qty);
                    itemPs.setDouble(4, price);
                    itemPs.executeUpdate();

                    updateStock.setInt(1, qty);
                    updateStock.setInt(2, medId);
                    updateStock.executeUpdate();
                }
            }

            // Build data for invoice summary window
            Object[][] invoiceData = new Object[cartModel.getRowCount()][5];
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                invoiceData[i][0] = cartModel.getValueAt(i, 1); // Name
                invoiceData[i][1] = cartModel.getValueAt(i, 3); // Price
                invoiceData[i][2] = cartModel.getValueAt(i, 4); // Qty
                invoiceData[i][3] = cartModel.getValueAt(i, 6); // Discount %
                invoiceData[i][4] = cartModel.getValueAt(i, 5); // Line Total
            }

            new InvoiceSummaryDialog(this, invoiceData, finalTotal);

            JOptionPane.showMessageDialog(this, "Invoice generated successfully!");
            cartModel.setRowCount(0);
            total = 0;
            totalLabel.setText("Total: ₹0");
            finalTotalLabel.setText("Final Total: ₹0");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating invoice: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BillingForm::new);
    }
}
