package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import db.DBConnection;
import java.awt.*;
import java.sql.*;

public class StockReport extends JFrame {
    JTable table;
    DefaultTableModel model;
    JComboBox<String> filterBox;

    public StockReport() {
        setTitle("Stock Report");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Batch", "Qty", "Price", "Expiry"}, 0);
        table = new JTable(model);
        filterBox = new JComboBox<>(new String[]{"All", "Low Stock (<10)", "Expired"});
        filterBox.addActionListener(e -> loadStock());

        add(filterBox, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> {
            new Dashboard("Admin");  
            dispose();
        });

        JMenuItem billingItem = new JMenuItem("Billing");
        billingItem.addActionListener(e -> {
            new BillingForm();
            dispose();
        });

        JMenuItem historyItem = new JMenuItem("Invoice History");
        historyItem.addActionListener(e -> {
            new InvoiceHistory();
            dispose();
        });

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            new LoginForm();
            dispose();
        });

        menu.add(dashboardItem);
        menu.add(billingItem);
        menu.add(historyItem);
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        loadStock();
        setVisible(true);
    }

    void loadStock() {
        model.setRowCount(0);
        String selected = (String) filterBox.getSelectedItem();

        String query = switch (selected) {
            case "Low Stock (<10)" -> "SELECT * FROM medicines WHERE quantity < 10";
            case "Expired" -> "SELECT * FROM medicines WHERE expiry_date < CURDATE()";
            default -> "SELECT * FROM medicines";
        };

        try (Connection con = DBConnection.getConnection();
             ResultSet rs = con.createStatement().executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("batch_no"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDate("expiry_date")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load stock: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new StockReport();
    }
}
