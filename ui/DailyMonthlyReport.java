package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class DailyMonthlyReport extends JFrame {

    private JTable table;
    private JLabel totalSalesLabel;
    private JComboBox<String> filterCombo;

    public DailyMonthlyReport() {
        setTitle("Daily/Monthly Sales Report");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> {
            new Dashboard("Admin");
            dispose();
        });
        menu.add(dashboardItem);

        JMenuItem billingItem = new JMenuItem("Billing");
        billingItem.addActionListener(e -> {
            new BillingForm();
            dispose();
        });
        menu.add(billingItem);

        JMenuItem historyItem = new JMenuItem("Invoice History");
        historyItem.addActionListener(e -> {
            new InvoiceHistory();
            dispose();
        });
        menu.add(historyItem);

        JMenuItem stockItem = new JMenuItem("Stock Report");
        stockItem.addActionListener(e -> {
            new StockReport();
            dispose();
        });
        menu.add(stockItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            new LoginForm();
            dispose();
        });
        menu.add(logoutItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        filterCombo = new JComboBox<>(new String[]{"Today", "This Month"});
        JButton loadBtn = new JButton("Load Report");
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        topPanel.add(loadBtn);
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        totalSalesLabel = new JLabel("Total Sales: ₹0.00", SwingConstants.RIGHT);
        totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(totalSalesLabel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadReport());

        setVisible(true);
    }

    private void loadReport() {
        String filter = (String) filterCombo.getSelectedItem();
        String sql;
        if ("Today".equals(filter)) {
            sql = "SELECT * FROM invoices WHERE DATE(date) = CURDATE()";
        } else {
            sql = "SELECT * FROM invoices WHERE MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE())";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Invoice ID", "Date", "Total", "Discount", "Final Total"}, 0
            );
            double totalSales = 0;

            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp date = rs.getTimestamp("date");
                double total = rs.getDouble("total");
                double discount = rs.getDouble("discount");
                double finalTotal = rs.getDouble("final_total");

                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

                model.addRow(new Object[]{id, formattedDate, total, discount, finalTotal});
                totalSales += finalTotal;
            }

            table.setModel(model);
            totalSalesLabel.setText(String.format("Total Sales: ₹%.2f", totalSales));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading report: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DailyMonthlyReport::new);
    }
}
