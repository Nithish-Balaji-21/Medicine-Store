package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class MedicineForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public MedicineForm(String role) {
        // Window setup
        setTitle("Manage Medicines — " + role);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Menu Bar ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(createMenuItem("Dashboard", () -> new Dashboard(role)));
        menu.add(createMenuItem("Billing", () -> new BillingForm()));
        menu.add(createMenuItem("Invoice History", () -> new InvoiceHistory()));
        menu.add(createMenuItem("Stock Report", () -> new StockReport()));
        menu.add(createMenuItem("Daily/Monthly Report", () -> new DailyMonthlyReport()));
        menu.add(createMenuItem("Logout", LoginForm::new));
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // --- Table ---
        model = new DefaultTableModel(new String[]{"ID", "Name", "Batch No", "Qty", "Price", "Expiry"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Buttons Panel ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.add(createButton("Add Medicine", this::addMedicine));
        btnPanel.add(createButton("Edit Selected", this::editMedicine));
        btnPanel.add(createButton("Delete Selected", this::deleteMedicine));
        btnPanel.add(createButton("Refresh", this::loadMedicines));
        add(btnPanel, BorderLayout.SOUTH);

        // Load data
        loadMedicines();
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

    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void loadMedicines() {
        model.setRowCount(0);
        String sql = "SELECT * FROM medicines";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("batch_no"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDate("expiry_date").toLocalDate()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading medicines: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMedicine() {
        JTextField nameF = new JTextField();
        JTextField batchF = new JTextField();
        JTextField qtyF = new JTextField();
        JTextField priceF = new JTextField();
        JTextField expF = new JTextField("yyyy-MM-dd");

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Name:")); panel.add(nameF);
        panel.add(new JLabel("Batch No:")); panel.add(batchF);
        panel.add(new JLabel("Quantity:")); panel.add(qtyF);
        panel.add(new JLabel("Price:")); panel.add(priceF);
        panel.add(new JLabel("Expiry Date:")); panel.add(expF);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Medicine",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO medicines (name, batch_no, quantity, price, expiry_date) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, nameF.getText().trim());
            ps.setString(2, batchF.getText().trim());
            ps.setInt(3, Integer.parseInt(qtyF.getText().trim()));
            ps.setDouble(4, Double.parseDouble(priceF.getText().trim()));
            ps.setDate(5, Date.valueOf(LocalDate.parse(expF.getText().trim())));
            ps.executeUpdate();
            loadMedicines();
            JOptionPane.showMessageDialog(this, "Medicine added.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding medicine: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editMedicine() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a medicine to edit.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

        JTextField nameF = new JTextField((String) model.getValueAt(row, 1));
        JTextField batchF = new JTextField((String) model.getValueAt(row, 2));
        JTextField qtyF = new JTextField(model.getValueAt(row, 3).toString());
        JTextField priceF = new JTextField(model.getValueAt(row, 4).toString());
        JTextField expF = new JTextField(model.getValueAt(row, 5).toString());

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Name:")); panel.add(nameF);
        panel.add(new JLabel("Batch No:")); panel.add(batchF);
        panel.add(new JLabel("Quantity:")); panel.add(qtyF);
        panel.add(new JLabel("Price:")); panel.add(priceF);
        panel.add(new JLabel("Expiry Date:")); panel.add(expF);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Medicine",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE medicines SET name=?, batch_no=?, quantity=?, price=?, expiry_date=? WHERE id=?")) {
            ps.setString(1, nameF.getText().trim());
            ps.setString(2, batchF.getText().trim());
            ps.setInt(3, Integer.parseInt(qtyF.getText().trim()));
            ps.setDouble(4, Double.parseDouble(priceF.getText().trim()));
            ps.setDate(5, Date.valueOf(LocalDate.parse(expF.getText().trim())));
            ps.setInt(6, id);
            ps.executeUpdate();
            loadMedicines();
            JOptionPane.showMessageDialog(this, "Medicine updated.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating medicine: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicine() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a medicine to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete selected medicine?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM medicines WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadMedicines();
            JOptionPane.showMessageDialog(this, "Medicine deleted.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting medicine: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MedicineForm("Admin"));
    }
}
