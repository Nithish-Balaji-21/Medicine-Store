package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InvoiceSummaryDialog extends JDialog {

    public InvoiceSummaryDialog(JFrame parent, Object[][] data, double grandTotal) {
        super(parent, "Invoice Summary", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        String[] columns = {"Medicine", "MRP", "Qty", "Discount %", "Line Total"};

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JLabel grandTotalLabel = new JLabel("Grand Total: ₹" + String.format("%.2f", grandTotal), SwingConstants.RIGHT);
        grandTotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(grandTotalLabel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
