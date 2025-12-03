package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    public Dashboard(String role) {
        setTitle("Medical Store - Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setResizable(false);

        JButton medBtn     = new JButton("Manage Medicines");
        JButton billingBtn = new JButton("Billing");
        JButton invoiceBtn = new JButton("Invoice History");
        JButton stockBtn   = new JButton("Stock Report");
        JButton reportBtn  = new JButton("Daily/Monthly Report");

        if (!"admin".equalsIgnoreCase(role)) {
            medBtn.setEnabled(false);
            reportBtn.setEnabled(false);
        }

        medBtn.addActionListener(e -> {
            new MedicineForm(role);
            dispose();
        });

        billingBtn.addActionListener(e -> {
            new BillingForm();
            dispose();
        });

        invoiceBtn.addActionListener(e -> {
            new InvoiceHistory();
            dispose();
        });

        stockBtn.addActionListener(e -> {
            new StockReport();
            dispose();
        });

        reportBtn.addActionListener(e -> {
            new DailyMonthlyReport();
            dispose();
        });

        add(Box.createVerticalStrut(15));
        add(centeredPanel(medBtn));
        add(centeredPanel(billingBtn));
        add(centeredPanel(invoiceBtn));
        add(centeredPanel(stockBtn));
        add(centeredPanel(reportBtn));

        checkForExpired();

        setVisible(true);
    }

    private JPanel centeredPanel(JComponent comp) {
        JPanel p = new JPanel();
        p.add(comp);
        return p;
    }

    private void checkForExpired() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT name FROM medicines WHERE expiry_date < CURDATE()");
             ResultSet rs = ps.executeQuery()) {

            StringBuilder msg = new StringBuilder();
            while (rs.next()) {
                msg.append("• ").append(rs.getString("name")).append("\n");
            }
            if (msg.length() > 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "⚠️ Expired Medicines Found:\n" + msg,
                    "Expired Alert",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("Admin"));
    }
}
