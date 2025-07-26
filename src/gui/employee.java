package gui;

import com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.MySql;

public class employee extends javax.swing.JDialog {

    private static HashMap<String, String> cityMap = new HashMap<>();
    private static HashMap<String, String> typeMap = new HashMap<>();

    home hm;

    public employee(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadType();
        loadCity();
        loadRegisterEmployee();
        setupFullKeyboardSupport();

        if (isPasswordVisible) {
            passwordField.setEchoChar((char) 0);
            jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/eye1.png")));
            jButton6.setToolTipText("Hide Password");
        } else {
            passwordField.setEchoChar('\u2022');
            jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/eye2.png")));
            jButton6.setToolTipText("Show Password");
        }

        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = String.valueOf(typeComboBox.getSelectedItem());
                boolean isAdmin = "Admin".equalsIgnoreCase(selectedType);

                passwordField.setEnabled(isAdmin);

                if (!isAdmin) {
                    passwordField.setText("");
                }
            }
        });

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/WC.png")));

        hm = (home) parent;

    }

    public void loadRegisterEmployee() {

        try {
            ResultSet rs = MySql.executeSearch(
                    "SELECT e.id, e.first_name, e.last_name, e.mobile, g.name AS gender, "
                    + "e.email, ea.line1 AS address_line_1, ea.line2 AS address_line_2, "
                    + "c.name AS city, et.name AS employee_type "
                    + "FROM employee e "
                    + "JOIN gender g ON e.gender_id = g.id "
                    + "JOIN employee_address ea ON e.employee_address_id = ea.id "
                    + "JOIN city c ON ea.city_id = c.id "
                    + "JOIN employee_type et ON e.employee_type_id = et.id "
                    + "ORDER BY e.first_name ASC"
            );

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("mobile"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("email"));
                row.add(rs.getString("address_line_1"));
                row.add(rs.getString("address_line_2"));
                row.add(rs.getString("city"));
                row.add(rs.getString("employee_type"));
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data loading failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadType() {

        try {

            ResultSet resultSet = MySql.executeSearch("SELECT * FROM `employee_type`");

            Vector<String> vector = new Vector<>();

            vector.add("Select Type");

            while (resultSet.next()) {

                vector.add(resultSet.getString("name"));

                typeMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);

            typeComboBox.setModel(model);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void loadCity() {

        try {

            ResultSet resultSet = MySql.executeSearch("SELECT * FROM `city` ORDER BY `name` ASC");

            Vector<String> vector = new Vector<>();

            vector.add("Select City");

            while (resultSet.next()) {

                vector.add(resultSet.getString("name"));

                cityMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);

            cityComboBox.setModel(model);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private void deleteEmployee(String empId) {

        try {

            ResultSet rs = MySql.executeSearch("SELECT employee_address_id FROM employee WHERE  id = '" + empId + "'");
            int addressId = -1;

            if (rs.next()) {
                addressId = rs.getInt("employee_address_id");
            }

            MySql.executeUpdate("DELETE FROM employee WHERE id = '" + empId + "'");

            if (addressId != -1) {
                MySql.executeUpdate("DELETE FROM employee_address WHERE id = '" + addressId + "'");
            }

            JOptionPane.showMessageDialog(this, "Employee deleted successfully !");
            loadRegisterEmployee();
            home.getInstance().getEmployeeCount();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupRadioButtons = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        firstNameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lastNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        mobileTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        maleRadioButton = new javax.swing.JRadioButton();
        femaleRadioButton = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        emailTextField = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        addressLine1TextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        addressLine2TextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cityComboBox = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Employee");
        setResizable(false);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(22, 160, 133));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Mobile");

        firstNameTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        firstNameTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("First Name");

        lastNameTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lastNameTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Last Name");

        mobileTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        mobileTextField.setMinimumSize(new java.awt.Dimension(80, 35));
        mobileTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Address Line 1 ");

        buttonGroupRadioButtons.add(maleRadioButton);
        maleRadioButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        maleRadioButton.setForeground(new java.awt.Color(255, 255, 255));
        maleRadioButton.setSelected(true);
        maleRadioButton.setText("Male");

        buttonGroupRadioButtons.add(femaleRadioButton);
        femaleRadioButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        femaleRadioButton.setForeground(new java.awt.Color(255, 255, 255));
        femaleRadioButton.setText("Female");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Gender");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Email");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Type");

        typeComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        typeComboBox.setPreferredSize(new java.awt.Dimension(90, 35));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Register");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setPreferredSize(new java.awt.Dimension(90, 35));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Update");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setPreferredSize(new java.awt.Dimension(90, 35));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Clear All");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setPreferredSize(new java.awt.Dimension(94, 35));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Password");

        passwordField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        passwordField.setMinimumSize(new java.awt.Dimension(80, 35));
        passwordField.setPreferredSize(new java.awt.Dimension(80, 35));

        emailTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        emailTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eye2.png"))); // NOI18N
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        addressLine1TextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Address Line 2 ");

        addressLine2TextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("City");

        cityComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        cityComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Add City");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Add Type");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52)
                                .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(maleRadioButton)
                                        .addGap(34, 34, 34)
                                        .addComponent(femaleRadioButton)
                                        .addGap(61, 61, 61))
                                    .addComponent(mobileTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lastNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(firstNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emailTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton8)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(addressLine1TextField))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(addressLine2TextField))))
                .addGap(25, 25, 25))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mobileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maleRadioButton)
                    .addComponent(femaleRadioButton)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLine1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLine2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(cityComboBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 610));

        jPanel3.setBackground(new java.awt.Color(22, 160, 133));

        searchTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        searchTextField.setPreferredSize(new java.awt.Dimension(90, 35));
        searchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTextFieldKeyReleased(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search.png"))); // NOI18N
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setMinimumSize(new java.awt.Dimension(94, 35));
        jButton4.setPreferredSize(new java.awt.Dimension(94, 35));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(360, Short.MAX_VALUE)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 0, 800, 80));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Mobile", "Gender", "Email", "Address Line 1", "Address Line 2", "City", "Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 780, 490));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        FlatCobalt2IJTheme.setup();
        addType addTypeDialog = new addType(this, true, this);
        addTypeDialog.setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        FlatCobalt2IJTheme.setup();
        addCity addCityDialog = new addCity(this, true, this);
        addCityDialog.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        try {
            String firstName = firstNameTextField.getText().trim();
            String lastName = lastNameTextField.getText().trim();
            String mobile = mobileTextField.getText().trim();
            Boolean isMale = maleRadioButton.isSelected();
            Boolean isFemale = femaleRadioButton.isSelected();
            String type = String.valueOf(typeComboBox.getSelectedItem());
            String email = emailTextField.getText().trim();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars).trim();
            String line1 = addressLine1TextField.getText().trim();
            String line2 = addressLine2TextField.getText().trim();
            String city = String.valueOf(cityComboBox.getSelectedItem());

            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter first name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!firstName.matches("[a-zA-Z]+")) {
                JOptionPane.showMessageDialog(this, "First name can only contain letters", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter last name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!lastName.matches("[a-zA-Z]+")) {
                JOptionPane.showMessageDialog(this, "Last name can only contain letters", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (mobile.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter mobile", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
                JOptionPane.showMessageDialog(this, "Invalid Mobile Number !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ResultSet checkMobile = MySql.executeSearch("SELECT id FROM employee WHERE mobile = '" + mobile + "'");
            if (checkMobile.next()) {
                JOptionPane.showMessageDialog(this, "This mobile number is already registered !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!isMale && !isFemale) {
                JOptionPane.showMessageDialog(this, "Please select gender", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (type.equals("Select Type")) {
                JOptionPane.showMessageDialog(this, "Please select employee type", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean isAdmin = "Admin".equalsIgnoreCase(type);

            if (isAdmin && email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.isEmpty()) {
                if (!email.matches("^[a-zA-Z0-9._%+-]+@(gmail|yahoo|hotmail|outlook|gov|live)\\.(com|lk|edu\\.lk|ac\\.lk|gov\\.lk|sch\\.lk|org\\.lk|net|in)$")) {
                    JOptionPane.showMessageDialog(this, "Invalid Email Address !", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (isAdmin) {
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter password", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#]).+$";
                if (!password.matches(passwordPattern)) {
                    JOptionPane.showMessageDialog(this, "Password must contain at least:\n- One uppercase letter\n- One lowercase letter\n- One digit\n- One special character (@$!%*?&)", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (!email.isEmpty() && password.isEmpty() && isAdmin) {
                JOptionPane.showMessageDialog(this, "Please enter password", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (line1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter address line 1", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (line2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter address line 2", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (city.equals("Select City")) {
                JOptionPane.showMessageDialog(this, "Please select city", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String gender = isMale ? "Male" : "Female";
            ResultSet rsGender = MySql.executeSearch("SELECT id FROM gender WHERE name = '" + gender + "'");
            String genderID = "";
            if (rsGender.next()) {
                genderID = rsGender.getString("id");
            } else {
                JOptionPane.showMessageDialog(this, "Gender not found in the database !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String cityID = cityMap.get(city);
            if (cityID == null) {
                JOptionPane.showMessageDialog(this, "Selected city not found in map !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String employeeTypeID = typeMap.get(type);
            if (employeeTypeID == null) {
                JOptionPane.showMessageDialog(this, "Selected employee type not found in map !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MySql.executeUpdate("INSERT INTO employee_address(line1, line2, city_id) VALUES('" + line1 + "', '" + line2 + "', '" + cityID + "')");

            ResultSet rsAddress = MySql.executeSearch("SELECT id FROM employee_address WHERE line1 = '" + line1 + "' AND line2 = '" + line2 + "' AND city_id = '" + cityID + "' ORDER BY id DESC LIMIT 1");
            String addressID = "";
            if (rsAddress.next()) {
                addressID = rsAddress.getString("id");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve address ID !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            long id = System.currentTimeMillis();
            String employeeID = "ERI" + String.valueOf(id);
            MySql.executeUpdate("INSERT INTO employee (id, first_name, last_name, mobile, email, password, register_date, employee_type_id, gender_id, employee_address_id) "
                    + "VALUES ('" + employeeID + "','" + firstName + "', '" + lastName + "', '" + mobile + "', '" + email + "', '" + password + "', NOW(), '" + employeeTypeID + "', '" + genderID + "', '" + addressID + "')");

            JOptionPane.showMessageDialog(this, "Employee registered successfully !", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRegisterEmployee();
            home.getInstance().getEmployeeCount();
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Adding Register: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private boolean isPasswordVisible = false;

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        if (!passwordField.isEnabled()) {

            jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/eye2.png")));
            jButton6.setToolTipText("Show Password");
            return;
        }

        if (isPasswordVisible) {
            passwordField.setEchoChar('\u2022');
            jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/eye2.png")));
            jButton6.setToolTipText("Show Password");
        } else {
            passwordField.setEchoChar((char) 0);
            jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/eye1.png")));
            jButton6.setToolTipText("Hide Password");
        }
        isPasswordVisible = !isPasswordVisible;

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        refresh();

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        try {

            String firstName = firstNameTextField.getText().trim();
            String lastName = lastNameTextField.getText().trim();
            String mobile = mobileTextField.getText().trim();
            Boolean isMale = maleRadioButton.isSelected();
            Boolean isFemale = femaleRadioButton.isSelected();
            String type = String.valueOf(typeComboBox.getSelectedItem());
            String email = emailTextField.getText().trim();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars).trim();
            String line1 = addressLine1TextField.getText().trim();
            String line2 = addressLine2TextField.getText().trim();
            String city = String.valueOf(cityComboBox.getSelectedItem());

            int row = jTable1.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an employee first to update", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter first name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!firstName.matches("[a-zA-Z]+")) {
                JOptionPane.showMessageDialog(this, "First name can only contain letters", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter last name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!lastName.matches("[a-zA-Z]+")) {
                JOptionPane.showMessageDialog(this, "Last name can only contain letters", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (mobile.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter mobile", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
                JOptionPane.showMessageDialog(this, "Invalid Mobile Number !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedEmployeeId = jTable1.getValueAt(row, 0).toString();
            ResultSet checkMobile = MySql.executeSearch("SELECT id FROM employee WHERE mobile = '" + mobile + "' AND id != '" + selectedEmployeeId + "'");
            if (checkMobile.next()) {
                JOptionPane.showMessageDialog(this, "This mobile number is already registered !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isMale && !isFemale) {
                JOptionPane.showMessageDialog(this, "Please select gender", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (type.equals("Select Type")) {
                JOptionPane.showMessageDialog(this, "Please select employee type", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean isAdmin = "Admin".equalsIgnoreCase(type);

            if (isAdmin && email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._%+-]+@(gmail|yahoo|hotmail|outlook|gov|live)\\.(com|lk|edu\\.lk|ac\\.lk|gov\\.lk|sch\\.lk|org\\.lk|net|in)$")) {
                JOptionPane.showMessageDialog(this, "Invalid Email Address !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isAdmin) {
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter password", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#]).+$";
                if (!password.matches(passwordPattern)) {
                    JOptionPane.showMessageDialog(this, "Password must contain at least:\n- One uppercase letter\n- One lowercase letter\n- One digit\n- One special character (@$!%*?&)", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (line1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter address line 1", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (line2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter address line 2", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (city.equals("Select City")) {
                JOptionPane.showMessageDialog(this, "Please select city", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String gender = isMale ? "Male" : "Female";
            ResultSet rsGender = MySql.executeSearch("SELECT id FROM gender WHERE name = '" + gender + "'");
            String genderID = rsGender.next() ? rsGender.getString("id") : "";
            String cityID = cityMap.get(city);
            String employeeTypeID = typeMap.get(type);

            if (genderID.isEmpty() || cityID == null || employeeTypeID == null) {
                JOptionPane.showMessageDialog(this, "Reference values not found in database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MySql.executeUpdate("UPDATE employee_address ea JOIN employee e ON ea.id = e.employee_address_id SET ea.line1 = '" + line1 + "', ea.line2 = '" + line2 + "', ea.city_id = '" + cityID + "' WHERE e.id = '" + selectedEmployeeId + "'");
            MySql.executeUpdate("UPDATE employee SET first_name = '" + firstName + "', last_name = '" + lastName + "', mobile = '" + mobile + "', email = '" + email + "', employee_type_id = '" + employeeTypeID + "', gender_id = '" + genderID + "'" + (password.isEmpty() ? "" : ", password = '" + password + "'") + " WHERE id = '" + selectedEmployeeId + "'");

            JOptionPane.showMessageDialog(this, "Employee updated successfully !", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRegisterEmployee();
            refresh();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Updating Employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        int row = jTable1.getSelectedRow();

        if (evt.getClickCount() == 2 && row != -1) {
            int option = JOptionPane.showConfirmDialog(this, "Do you want to delete this employee ?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                String empId = jTable1.getValueAt(row, 0).toString();
                deleteEmployee(empId);
                refresh();
            }
            return;
        }

        if (row != -1) {
            firstNameTextField.setText(jTable1.getValueAt(row, 1).toString());
            lastNameTextField.setText(jTable1.getValueAt(row, 2).toString());
            mobileTextField.setText(jTable1.getValueAt(row, 3).toString());

            String gender = jTable1.getValueAt(row, 4).toString();
            if (gender.equalsIgnoreCase("Male")) {
                maleRadioButton.setSelected(true);
            } else {
                femaleRadioButton.setSelected(true);
            }

            emailTextField.setText(jTable1.getValueAt(row, 5).toString());
            addressLine1TextField.setText(jTable1.getValueAt(row, 6).toString());
            addressLine2TextField.setText(jTable1.getValueAt(row, 7).toString());

            String city = jTable1.getValueAt(row, 8).toString();
            cityComboBox.setSelectedItem(city);

            String type = jTable1.getValueAt(row, 9).toString();
            typeComboBox.setSelectedItem(type);

            passwordField.setText("");
            passwordField.setEchoChar('•');
            jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/eye2.png")));
            jButton6.setToolTipText("Show Password");
            isPasswordVisible = false;

            if (type.equalsIgnoreCase("Admin")) {
                String empId = jTable1.getValueAt(row, 0).toString();
                try {
                    ResultSet rs = MySql.executeSearch("SELECT password FROM employee WHERE id = '" + empId + "'");
                    if (rs.next()) {
                        String password = rs.getString("password");
                        passwordField.setText(password);
                    } else {
                        passwordField.setText("");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error loading password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void searchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTextFieldKeyReleased

        String searchText = searchTextField.getText().trim();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT e.id AS id, e.first_name, e.last_name, e.mobile, g.name AS gender, e.email, "
                    + "ea.line1 AS address_line1, ea.line2 AS address_line2, c.name AS city, et.name AS type "
                    + "FROM employee e "
                    + "JOIN gender g ON e.gender_id = g.id "
                    + "JOIN employee_address ea ON e.employee_address_id = ea.id "
                    + "JOIN city c ON ea.city_id = c.id "
                    + "JOIN employee_type et ON e.employee_type_id = et.id "
                    + "WHERE CONCAT(e.first_name, ' ', e.last_name) LIKE '%" + searchText + "%' "
                    + "OR e.first_name LIKE '%" + searchText + "%' "
                    + "OR e.last_name LIKE '%" + searchText + "%'"
                    + "OR e.id LIKE '%" + searchText + "%'"
            );

            while (rs.next()) {
                Object[] row = {
                    rs.getString("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("mobile"),
                    rs.getString("gender"),
                    rs.getString("email"),
                    rs.getString("address_line1"),
                    rs.getString("address_line2"),
                    rs.getString("city"),
                    rs.getString("type")
                };
                model.addRow(row);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_searchTextFieldKeyReleased

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        String search = searchTextField.getText();

        if (search.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please enter your employee registerID, first name, last name or full name in the search bar.", "Warning", JOptionPane.WARNING_MESSAGE);

        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void setupFullKeyboardSupport() {

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");

        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(getRootPane());
                if (window != null && !(window instanceof home)) {
                    window.dispose();
                }
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getID() != KeyEvent.KEY_PRESSED) {
                    return false;
                }

                Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Window window = SwingUtilities.getWindowAncestor(comp);
                    if (window instanceof JDialog dialog) {
                        dialog.dispose();
                        return true;
                    } else if (window instanceof JFrame frame && !(frame instanceof home)) {
                        frame.dispose();
                        return true;
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    if (comp instanceof JButton button) {
                        button.doClick();
                        return true;
                    } else if (comp instanceof JRadioButton) {
                        comp.transferFocus();
                        return true;
                    } else if (comp instanceof JComboBox<?> comboBox) {
                        if (!comboBox.isPopupVisible()) {
                            comboBox.showPopup();
                        } else {
                            comboBox.setSelectedItem(comboBox.getSelectedItem());
                            comboBox.hidePopup();
                        }
                        return true;
                    } else if (comp instanceof JTable table) {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow != -1) {
                            Rectangle cellRect = table.getCellRect(selectedRow, 0, true);
                            Point point = new Point(cellRect.x, cellRect.y);

                            MouseEvent clickEvent = new MouseEvent(
                                    table,
                                    MouseEvent.MOUSE_CLICKED,
                                    System.currentTimeMillis(),
                                    0,
                                    point.x,
                                    point.y,
                                    1,
                                    false
                            );

                            table.dispatchEvent(clickEvent);
                            return true;
                        }
                    }
                }

                if (comp instanceof JRadioButton currentRadio
                        && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)) {

                    Enumeration<AbstractButton> buttons = buttonGroupRadioButtons.getElements();
                    List<AbstractButton> list = Collections.list(buttons);
                    int index = list.indexOf(currentRadio);
                    if (index != -1) {
                        int nextIndex = (e.getKeyCode() == KeyEvent.VK_DOWN)
                                ? (index + 1) % list.size()
                                : (index - 1 + list.size()) % list.size();

                        AbstractButton nextButton = list.get(nextIndex);
                        nextButton.setSelected(true);
                        nextButton.requestFocus();
                        return true;
                    }
                }

                if (comp instanceof JTable table) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        table.transferFocusBackward();
                        return true;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        table.transferFocus();
                        return true;
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (!(comp instanceof JComboBox<?>) || !((JComboBox<?>) comp).isPopupVisible()) {
                        comp.transferFocus();
                        return true;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (!(comp instanceof JComboBox<?>) || !((JComboBox<?>) comp).isPopupVisible()) {
                        comp.transferFocusBackward();
                        return true;
                    }
                }

                if (comp instanceof JComboBox<?> comboBox && comboBox.isPopupVisible()) {
                    return false;
                }

                return false;
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressLine1TextField;
    private javax.swing.JTextField addressLine2TextField;
    private javax.swing.ButtonGroup buttonGroupRadioButtons;
    private javax.swing.JComboBox<String> cityComboBox;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JRadioButton femaleRadioButton;
    private javax.swing.JTextField firstNameTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField lastNameTextField;
    private javax.swing.JRadioButton maleRadioButton;
    private javax.swing.JTextField mobileTextField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JComboBox<String> typeComboBox;
    // End of variables declaration//GEN-END:variables

    private void refresh() {

        firstNameTextField.setText("");
        lastNameTextField.setText("");
        mobileTextField.setText("");
        maleRadioButton.setSelected(true);
        typeComboBox.setSelectedItem("Select Type");
        emailTextField.setText("");
        passwordField.setText("");
        addressLine1TextField.setText("");
        addressLine2TextField.setText("");
        cityComboBox.setSelectedItem("Select City");
        jTable1.clearSelection();
        searchTextField.setText("");
        firstNameTextField.grabFocus();
        loadRegisterEmployee();

        isPasswordVisible = false;
        passwordField.setEchoChar('•');
        jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/eye2.png")));
        jButton6.setToolTipText("Show Password");

    }
}
