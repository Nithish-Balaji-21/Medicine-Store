package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InvoiceHistory extends JFrame {
    private JTable invoiceTable, itemTable;
    private DefaultTableModel invoiceModel, itemModel;

    public InvoiceHistory() {
        setTitle("Invoice History");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> {
            new Dashboard("Admin");
            dispose();
        });
        menu.add(dashboardItem);

        JMenuItem medItem = new JMenuItem("Manage Medicines");
        medItem.addActionListener(e -> {
            new MedicineForm("Admin");
            dispose();
        });
        menu.add(medItem);

        JMenuItem billingItem = new JMenuItem("Billing");
        billingItem.addActionListener(e -> {
            new BillingForm();
            dispose();
        });
        menu.add(billingItem);

        JMenuItem stockItem = new JMenuItem("Stock Report");
        stockItem.addActionListener(e -> {
            new StockReport();
            dispose();
        });
        menu.add(stockItem);

        JMenuItem reportItem = new JMenuItem("Daily/Monthly Report");
        reportItem.addActionListener(e -> {
            new DailyMonthlyReport();
            dispose();
        });
        menu.add(reportItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            new LoginForm();
            dispose();
        });
        menu.add(logoutItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        invoiceModel = new DefaultTableModel(new String[]{
            "Invoice ID", "Date", "Total", "Discount", "Final Total"
        }, 0);
        invoiceTable = new JTable(invoiceModel);

        itemModel = new DefaultTableModel(new String[]{
            "Medicine Name", "Qty", "Price"
        }, 0);
        itemTable = new JTable(itemModel);

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(invoiceTable),
            new JScrollPane(itemTable)
        );
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        loadInvoices();
        ListSelectionListener listener = e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                int invoiceId = (int) invoiceModel.getValueAt(invoiceTable.getSelectedRow(), 0);
                loadItems(invoiceId);
            }
        };
        invoiceTable.getSelectionModel().addListSelectionListener(listener);

        setVisible(true);
    }

    private void loadInvoices() {
        invoiceModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM invoices")) {

            while (rs.next()) {
                invoiceModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getTimestamp("date"),
                    rs.getDouble("total"),
                    rs.getDouble("discount"),
                    rs.getDouble("final_total")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading invoices: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadItems(int invoiceId) {
        itemModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT m.name, ii.quantity, ii.price " +
                 "FROM invoice_items ii " +
                 "JOIN medicines m ON ii.medicine_id = m.id " +
                 "WHERE ii.invoice_id = ?"
             )) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    itemModel.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading items: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InvoiceHistory::new);
    }
}
