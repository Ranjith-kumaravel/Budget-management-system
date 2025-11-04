import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.Date;

/**
 * BudgetManagementSystem - AWT single-page CRUD app using MySQL.
 *
 * Notes:
 * - Change DB_URL, DB_USER, DB_PASS to match your MySQL setup.
 * - Requires MySQL Connector/J (e.g., mysql-connector-j-8.x.x.jar) on classpath.
 */
public class BudgetManagementSystem extends Frame implements ActionListener {
    // DB connection details - change as needed
    private static final String DB_URL = "jdbc:mysql://localhost:3306/budgetdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Ranjith@123"; // IMPORTANT: Change this to your MySQL root password

    // UI components
    private Label lblId, lblDate, lblCategory, lblDesc, lblAmount, lblType, lblSearch;
    private TextField tfId, tfDate, tfCategory, tfAmount, tfSearch;
    private TextArea taDescription, taOutput;
    private Choice chType;
    private Button btnCreate, btnRead, btnUpdate, btnDelete, btnListAll, btnClear, btnSearch;

    // DB connection (opened/closed per operation)
    public BudgetManagementSystem() {
        super("Budget Management System - AWT");

        // -- UI STYLING --
        // Colors
        Color bgColor = new Color(240, 240, 240); // Light gray
        Color btnColor = new Color(70, 130, 180); // Steel blue
        Color textColor = new Color(50, 50, 50);   // Dark gray
        Color accentColor = new Color(255, 255, 255); // White

        // Font
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // -- LAYOUT --
        setLayout(new BorderLayout(10, 10));
        setBackground(bgColor);

        // Main container panel with padding
        Panel mainPanel = new Panel(new BorderLayout(10, 10));

        // Form Panel (using GridBagLayout for flexibility)
        Panel formPanel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST;

        // Helper method to create styled labels
        Label[] labels = {
            lblId = new Label("ID (for update/delete):"),
            lblDate = new Label("Date (yyyy-MM-dd):"),
            lblCategory = new Label("Category:"),
            lblDesc = new Label("Description:"),
            lblAmount = new Label("Amount:"),
            lblType = new Label("Type:")
        };
        for (Label lbl : labels) {
            lbl.setFont(boldFont);
            lbl.setForeground(textColor);
        }

        // Form components
        tfId = new TextField(10);
        tfDate = new TextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 20);
        tfCategory = new TextField(20);
        taDescription = new TextArea(3, 20);
        tfAmount = new TextField(20);
        chType = new Choice();
        chType.add("Expense");
        chType.add("Income");

        Component[] fields = {tfId, tfDate, tfCategory, taDescription, tfAmount, chType};
        for(Component c : fields) {
            c.setFont(defaultFont);
        }

        // Add components to form panel using GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblId, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(tfId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(lblDate, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(tfDate, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(lblCategory, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(tfCategory, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(lblDesc, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(taDescription, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(lblAmount, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(tfAmount, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(lblType, gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(chType, gbc);

        // Action Buttons Panel
        Panel actionsPanel = new Panel(new GridLayout(3, 2, 8, 8));
        btnCreate = new Button("Create");
        btnRead   = new Button("Read by ID");
        btnUpdate = new Button("Update");
        btnDelete = new Button("Delete");
        btnListAll= new Button("List All");
        btnClear  = new Button("Clear Form");

        Button[] buttons = {btnCreate, btnRead, btnUpdate, btnDelete, btnListAll, btnClear};
        for (Button btn : buttons) {
            btn.setFont(boldFont);
            btn.setBackground(btnColor);
            btn.setForeground(accentColor);
            btn.addActionListener(this);
            actionsPanel.add(btn);
        }

        // Top panel containing form and actions
        Panel topPanel = new Panel(new BorderLayout(20, 0));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(actionsPanel, BorderLayout.EAST);

        // Bottom area for search and output
        Panel bottomPanel = new Panel(new BorderLayout(10, 10));
        Panel searchPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        lblSearch = new Label("Search:");
        lblSearch.setFont(boldFont);
        tfSearch = new TextField(25);
        tfSearch.setFont(defaultFont);
        btnSearch = new Button("Search");
        btnSearch.setFont(boldFont);
        btnSearch.setBackground(btnColor);
        btnSearch.setForeground(accentColor);
        btnSearch.addActionListener(this);
        searchPanel.add(lblSearch);
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);

        taOutput = new TextArea();
        taOutput.setEditable(false);
        taOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));

        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(taOutput, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        // Add a border/padding to the main frame
        add(new Panel(), BorderLayout.NORTH);
        add(new Panel(), BorderLayout.SOUTH);
        add(new Panel(), BorderLayout.EAST);
        add(new Panel(), BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Window settings
        setSize(950, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // Window close handler
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                dispose();
                System.exit(0);
            }
        });
    }

    // Validate numeric amount
    private boolean isValidAmount(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    // Clear form fields
    private void clearForm() {
        tfId.setText("");
        tfDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tfCategory.setText("");
        taDescription.setText("");
        tfAmount.setText("");
        chType.select("Expense");
        taOutput.setText("");
    }

    // Action handling
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnCreate) {
            createEntry();
        } else if (src == btnRead) {
            readEntry();
        } else if (src == btnUpdate) {
            updateEntry();
        } else if (src == btnDelete) {
            deleteEntry();
        } else if (src == btnListAll) {
            listAll();
        } else if (src == btnClear) {
            clearForm();
        } else if (src == btnSearch) {
            searchEntries();
        }
    }

    // DB helper: get connection
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load driver
        } catch (ClassNotFoundException ex) {
            // If driver not found, user will see exception when attempting DB ops
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // Create (INSERT)
    private void createEntry() {
        String date = tfDate.getText().trim();
        String category = tfCategory.getText().trim();
        String desc = taDescription.getText().trim();
        String amountStr = tfAmount.getText().trim();
        String type = chType.getSelectedItem();

        if (category.isEmpty() || amountStr.isEmpty()) {
            taOutput.setText("Category and Amount are required.");
            return;
        }
        if (!isValidAmount(amountStr)) {
            taOutput.setText("Invalid amount. Enter numeric value.");
            return;
        }

        String sql = "INSERT INTO budget_entries (date, category, description, amount, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        pst.setDate(1, java.sql.Date.valueOf(date));
            pst.setString(2, category);
            pst.setString(3, desc);
            pst.setBigDecimal(4, new java.math.BigDecimal(amountStr));
            pst.setString(5, type);
            int affected = pst.executeUpdate();
            if (affected > 0) {
                ResultSet keys = pst.getGeneratedKeys();
                String id = "";
                if (keys.next()) id = String.valueOf(keys.getInt(1));
                taOutput.setText("Inserted successfully. New ID: " + id);
                clearForm();
            } else {
                taOutput.setText("Insert failed.");
            }
        } catch (SQLException ex) {
            taOutput.setText("SQL Error (Insert): " + ex.getMessage());
        } catch (IllegalArgumentException ie) {
            taOutput.setText("Date format error. Use yyyy-MM-dd.");
        }
    }

    // Read (by ID)
    private void readEntry() {
        String idStr = tfId.getText().trim();
        if (idStr.isEmpty()) {
            taOutput.setText("Enter ID to read record.");
            return;
        }
        String sql = "SELECT id, date, category, description, amount, type, created_at FROM budget_entries WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(idStr));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                tfDate.setText(rs.getDate("date").toString());
                tfCategory.setText(rs.getString("category"));
                taDescription.setText(rs.getString("description"));
                tfAmount.setText(rs.getBigDecimal("amount").toPlainString());
                chType.select(rs.getString("type"));
                taOutput.setText("Record loaded. ID: " + rs.getInt("id"));
            } else {
                taOutput.setText("No record found with ID " + idStr);
            }
        } catch (SQLException ex) {
            taOutput.setText("SQL Error (Read): " + ex.getMessage());
        } catch (NumberFormatException nfe) {
            taOutput.setText("Invalid ID format.");
        }
    }

    // Update
    private void updateEntry() {
        String idStr = tfId.getText().trim();
        if (idStr.isEmpty()) {
            taOutput.setText("Enter ID to update.");
            return;
        }
        String date = tfDate.getText().trim();
        String category = tfCategory.getText().trim();
        String desc = taDescription.getText().trim();
        String amountStr = tfAmount.getText().trim();
        String type = chType.getSelectedItem();

        if (category.isEmpty() || amountStr.isEmpty()) {
            taOutput.setText("Category and Amount required.");
            return;
        }
        if (!isValidAmount(amountStr)) {
            taOutput.setText("Invalid amount.");
            return;
        }

        String sql = "UPDATE budget_entries SET date = ?, category = ?, description = ?, amount = ?, type = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setDate(1, java.sql.Date.valueOf(date));
            pst.setString(2, category);
            pst.setString(3, desc);
            pst.setBigDecimal(4, new java.math.BigDecimal(amountStr));
            pst.setString(5, type);
            pst.setInt(6, Integer.parseInt(idStr));
            int affected = pst.executeUpdate();
            if (affected > 0) {
                taOutput.setText("Updated successfully for ID: " + idStr);
                clearForm();
            } else {
                taOutput.setText("No record found with ID " + idStr);
            }
        } catch (SQLException ex) {
            taOutput.setText("SQL Error (Update): " + ex.getMessage());
        } catch (IllegalArgumentException ie) {
            taOutput.setText("Date format error. Use yyyy-MM-dd.");
        }
    }

    // Delete
    private void deleteEntry() {
        String idStr = tfId.getText().trim();
        if (idStr.isEmpty()) {
            taOutput.setText("Enter ID to delete.");
            return;
        }
        String sql = "DELETE FROM budget_entries WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(idStr));
            int affected = pst.executeUpdate();
            if (affected > 0) {
                taOutput.setText("Deleted record ID: " + idStr);
                clearForm();
            } else {
                taOutput.setText("No record found with ID " + idStr);
            }
        } catch (SQLException ex) {
            taOutput.setText("SQL Error (Delete): " + ex.getMessage());
        }
    }

    // List all
    private void listAll() {
        String sql = "SELECT id, date, category, description, amount, type FROM budget_entries ORDER BY date DESC, id DESC";
        StringBuilder sb = new StringBuilder();
        sb.append("ID | Date       | Type    | Amount    | Category | Description\n");
        sb.append("-----------------------------------------------------------------\n");
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Date d = rs.getDate("date");
                String type = rs.getString("type");
                String amt = rs.getBigDecimal("amount").toPlainString();
                String cat = rs.getString("category");
                String desc = rs.getString("description");
                sb.append(String.format("%-3d | %-10s | %-7s | %9s | %-8s | %s\n",
                        id, d.toString(), type, amt, cat, (desc != null ? desc : "")));
            }
            taOutput.setText(sb.toString());
        } catch (SQLException ex) {
            taOutput.setText("SQL Error (List): " + ex.getMessage());
        }
    }

    // Search (by partial category or description)
    private void searchEntries() {
        String q = tfSearch.getText().trim();
        if (q.isEmpty()) {
            taOutput.setText("Enter search text.");
            return;
        }
        String sql = "SELECT id, date, category, description, amount, type FROM budget_entries " +
                     "WHERE category LIKE ? OR description LIKE ? ORDER BY date DESC";
        StringBuilder sb = new StringBuilder();
        sb.append("ID | Date       | Type    | Amount    | Category | Description\n");
        sb.append("-----------------------------------------------------------------\n");
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            String like = "%" + q + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            try (ResultSet rs = pst.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    sb.append(String.format("%-3d | %-10s | %-7s | %9s | %-8s | %s\n",
                            rs.getInt("id"),
                            rs.getDate("date").toString(),
                            rs.getString("type"),
                            rs.getBigDecimal("amount").toPlainString(),
                            rs.getString("category"),
                            rs.getString("description")));
                }
                if (!any) sb.append("No records match the search.\n");
            }
            taOutput.setText(sb.toString());
        } catch (SQLException ex) {
            taOutput.setText("SQL Error (Search): " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Optional: allow DB credentials via command-line
        if (args.length >= 3) {
            // override defaults if provided: DB_URL DB_USER DB_PASS
            // Example: java BudgetManagementSystem "jdbc:mysql://..." root secret
            try {
                // Not ideal to change constants; but just for quick override:
                java.lang.reflect.Field fUrl = BudgetManagementSystem.class.getDeclaredField("DB_URL");
                java.lang.reflect.Field fUser = BudgetManagementSystem.class.getDeclaredField("DB_USER");
                java.lang.reflect.Field fPass = BudgetManagementSystem.class.getDeclaredField("DB_PASS");
                fUrl.setAccessible(true); fUser.setAccessible(true); fPass.setAccessible(true);
                fUrl.set(null, args[0]);
                fUser.set(null, args[1]);
                fPass.set(null, args[2]);
            } catch (Exception ex) {
                System.out.println("Could not override DB credentials via args: " + ex.getMessage());
            }
        }
        new BudgetManagementSystem();
    }
}