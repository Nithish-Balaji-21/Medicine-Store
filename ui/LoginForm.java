package ui;

import javax.swing.*;
import db.DBConnection;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginForm() {
        setTitle("Medicine Billing System - Login");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("MEDICAL STORE LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> showRegistrationDialog());

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Demo mode credentials
        if ((username.equalsIgnoreCase("admin") && password.equals("admin123")) ||
            (username.equalsIgnoreCase("cashier") && password.equals("cashier123"))) {
            String role = username.equalsIgnoreCase("admin") ? "Admin" : "Cashier";
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome " + role + " (Demo Mode).");
            new Dashboard(role);
            dispose();
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT role FROM users WHERE username = ? AND password = ?")) {
            
            if (con == null) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid credentials. Demo mode: admin/cashier with password admin123/cashier123", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    JOptionPane.showMessageDialog(this, 
                        "Login successful! Welcome " + role + ".");
                    new Dashboard(role);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid credentials. Demo mode: admin/cashier with password admin123/cashier123", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationDialog() {
        JDialog registerDialog = new JDialog(this, "Register New User", true);
        registerDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("New User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        registerDialog.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        registerDialog.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        JTextField regUsername = new JTextField(20);
        registerDialog.add(regUsername, gbc);

        
        gbc.gridy = 2;
        gbc.gridx = 0;
        registerDialog.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField regPassword = new JPasswordField(20);
        registerDialog.add(regPassword, gbc);

    
        gbc.gridy = 3;
        gbc.gridx = 0;
        registerDialog.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField confirmPassword = new JPasswordField(20);
        registerDialog.add(confirmPassword, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        registerDialog.add(new JLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Cashier"});
        registerDialog.add(roleCombo, gbc);

       
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(120, 30));
        registerDialog.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            String username = regUsername.getText().trim();
            String password = new String(regPassword.getPassword()).trim();
            String confirmPass = new String(confirmPassword.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, 
                    "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPass)) {
                JOptionPane.showMessageDialog(registerDialog, 
                    "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (registerUser(username, password, role)) {
                JOptionPane.showMessageDialog(registerDialog, 
                    "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
            }
        });

        registerDialog.pack();
        registerDialog.setSize(350, 300);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setVisible(true);
    }

    private boolean registerUser(String username, String password, String role) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkStmt = con.prepareStatement(
                 "SELECT username FROM users WHERE username = ?");
             PreparedStatement insertStmt = con.prepareStatement(
                 "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
            
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, 
                        "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, role);
            insertStmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        createUsersTable();
        SwingUtilities.invokeLater(() -> new LoginForm());
    }

    private static void createUsersTable() {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {
            
            if (con == null) {
                System.out.println("Database not available - running in demo mode");
                return;
            }
            
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                         "username VARCHAR(50) UNIQUE NOT NULL," +
                         "password VARCHAR(100) NOT NULL," +
                         "role VARCHAR(20) NOT NULL)";
            stmt.execute(sql);

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM users")) {
                if (rs.next() && rs.getInt("count") == 0) {
                    stmt.executeUpdate("INSERT INTO users (username, password, role) " +
                                      "VALUES ('admin', 'admin123', 'Admin')");
                    stmt.executeUpdate("INSERT INTO users (username, password, role) " +
                                      "VALUES ('cashier', 'cashier123', 'Cashier')");
                }
            }
        } catch (SQLException | NullPointerException ex) {
            System.out.println("Database unavailable - running in demo mode");
            System.out.println("Demo credentials: admin/admin123 or cashier/cashier123");
        }
    }
}