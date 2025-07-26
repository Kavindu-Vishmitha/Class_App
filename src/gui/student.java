package gui;

import com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
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

public class student extends javax.swing.JDialog {

    private static HashMap<String, String> gradeMap = new HashMap<>();
    private static HashMap<String, String> cityMap = new HashMap<>();
    home hm;

    public student(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadgrade();
        loadCity();
        loadRegisterStudent();
        setupFullKeyboardSupport();

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/WC.png")));

        hm = (home) parent;
    }

    public String getRegisterID() {
        return registerIDTextField.getText().trim();
    }

    public String getFirstName() {
        return firstNameTextField.getText().trim();
    }

    public String getLastName() {
        return lastNameTextField.getText().trim();
    }

    public boolean isMaleSelected() {
        return maleRadioButton.isSelected();
    }

    public boolean isFemaleSelected() {
        return femaleRadioButton.isSelected();
    }

    public String getMobile() {
        return mobileTextField.getText().trim();
    }

    public String getEmail() {
        return emailTextField.getText().trim();
    }

    public String getLine1() {
        return addressLine1TextField.getText().trim();
    }

    public String getLine2() {
        return addressLine2TextField.getText().trim();
    }

    public String getCity() {
        return String.valueOf(cityComboBox.getSelectedItem());
    }

    public String getGrade() {
        return String.valueOf(gradeComboBox.getSelectedItem());
    }

    public void loadRegisterStudent() {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT s.reg_id AS student_id, "
                    + "s.first_name, s.last_name, g.name AS gender, "
                    + "s.mobile, s.email, sa.line1 AS address_line_1, "
                    + "sa.line2 AS address_line_2, c.name AS city, gr.name AS grade "
                    + "FROM student s "
                    + "JOIN gender g ON s.gender_id = g.id "
                    + "JOIN student_address sa ON s.student_address_id = sa.id "
                    + "JOIN city c ON sa.city_id = c.id "
                    + "JOIN grade gr ON s.grade_id = gr.id "
                    + "ORDER BY s.first_name ASC"
            );

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("mobile"));
                row.add(rs.getString("email"));
                row.add(rs.getString("address_line_1"));
                row.add(rs.getString("address_line_2"));
                row.add(rs.getString("city"));
                row.add(rs.getString("grade"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data loading failed: " + e.getMessage());
        }

    }

    public void loadCity() {

        try {

            ResultSet resultSet = MySql.executeSearch("SELECT * FROM `city`ORDER BY `name` ASC");

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

    public void loadgrade() {

        try {

            ResultSet resultSet = MySql.executeSearch(
                    "SELECT * FROM `grade` ORDER BY CAST(SUBSTRING(`name`, 7) AS UNSIGNED) ASC"
            );

            Vector<String> vector = new Vector<>();
            vector.add("Select Grade");

            gradeMap.clear();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String id = resultSet.getString("id");

                vector.add(name);
                gradeMap.put(name, id);
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            gradeComboBox.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteStudent(String regId) {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT student_address_id, st_reg_payment_id, register_date FROM student WHERE reg_id = '" + regId + "'"
            );

            int addressId = -1;
            int regPaymentId = -1;
            double regPaymentAmount = 0;
            String regPaymentDate = null;

            if (rs.next()) {
                addressId = rs.getInt("student_address_id");
                regPaymentId = rs.getInt("st_reg_payment_id");
                regPaymentDate = rs.getString("register_date");
            }

            if (regPaymentId != -1 && regPaymentDate != null) {
                ResultSet regRs = MySql.executeSearch(
                        "SELECT payment FROM st_reg_payment WHERE id = '" + regPaymentId + "'"
                );
                if (regRs.next()) {
                    regPaymentAmount = regRs.getDouble("payment");
                }

                MySql.executeUpdate(
                        "INSERT INTO inactive_st_reg_payment (inactive_st_reg_id, inactive_payment, reg_payment_date) "
                        + "VALUES ('" + regId + "', '" + regPaymentAmount + "', '" + regPaymentDate + "')"
                );
            }

            ResultSet feeRs = MySql.executeSearch(
                    "SELECT fee, payment_date, subject_id FROM st_subject_fee WHERE student_reg_id = '" + regId + "'"
            );

            while (feeRs.next()) {
                double fee = feeRs.getDouble("fee");
                Date paymentDate = feeRs.getDate("payment_date");
                int subjectId = feeRs.getInt("subject_id");

                MySql.executeUpdate(
                        "INSERT INTO inactive_st_subject_fee (inactive_st_reg_id, fee, payment_date, subject_id) "
                        + "VALUES ('" + regId + "', '" + fee + "', '" + paymentDate + "', '" + subjectId + "')"
                );
            }

            MySql.executeUpdate("DELETE FROM attendance WHERE student_reg_id = '" + regId + "'");
            MySql.executeUpdate("DELETE FROM st_subject_fee WHERE student_reg_id = '" + regId + "'");
            MySql.executeUpdate("DELETE FROM student WHERE reg_id = '" + regId + "'");

            if (regPaymentId != -1) {
                MySql.executeUpdate("DELETE FROM st_reg_payment WHERE id = '" + regPaymentId + "'");
            }

            if (addressId != -1) {
                MySql.executeUpdate("DELETE FROM student_address WHERE id = '" + addressId + "'");
            }

            JOptionPane.showMessageDialog(this, "Student deleted successfully !");
            loadRegisterStudent();
            home.getInstance().getStudentCount();
            home.getInstance().TodayRevenue();
            home.getInstance().MonthlyRevenue();
            home.getInstance().YearlyRevenue();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupRadioButtons = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        registerIDTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        firstNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lastNameTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        maleRadioButton = new javax.swing.JRadioButton();
        femaleRadioButton = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        gradeComboBox = new javax.swing.JComboBox<>();
        mobileTextField = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        addressLine1TextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        addressLine2TextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cityComboBox = new javax.swing.JComboBox<>();
        jButton9 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Student");
        setResizable(false);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(22, 160, 133));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Last Name");

        registerIDTextField.setEditable(false);
        registerIDTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        registerIDTextField.setEnabled(false);
        registerIDTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Register ID");

        firstNameTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        firstNameTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("First Name");

        lastNameTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lastNameTextField.setMinimumSize(new java.awt.Dimension(80, 35));
        lastNameTextField.setPreferredSize(new java.awt.Dimension(80, 35));

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

        emailTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        emailTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Mobile");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Email");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Grade");

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

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Add Grade");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setPreferredSize(new java.awt.Dimension(94, 35));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        gradeComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        gradeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gradeComboBox.setPreferredSize(new java.awt.Dimension(90, 35));

        mobileTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        mobileTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/generate.png"))); // NOI18N
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        addressLine1TextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Address Line 2 ");

        addressLine2TextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("City");

        cityComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        cityComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("Add City");
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.setPreferredSize(new java.awt.Dimension(94, 35));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
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
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(maleRadioButton)
                                .addGap(34, 34, 34)
                                .addComponent(femaleRadioButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(firstNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lastNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(emailTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(registerIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(mobileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cityComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(gradeComboBox, 0, 296, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(addressLine2TextField))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(addressLine1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registerIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maleRadioButton)
                    .addComponent(femaleRadioButton)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mobileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLine1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addressLine2TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gradeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

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
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(357, Short.MAX_VALUE)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 0, 820, 80));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Gender", "Mobile", "Email", "Address Line 1", "Address Line 2", "City", "Grade"
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1398, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        try {

            String registerID = registerIDTextField.getText();
            String firstName = firstNameTextField.getText().trim();
            String lastName = lastNameTextField.getText().trim();
            Boolean Male = maleRadioButton.isSelected();
            Boolean Female = femaleRadioButton.isSelected();
            String mobile = mobileTextField.getText().trim();
            String email = emailTextField.getText().trim();
            String line1 = addressLine1TextField.getText().trim();
            String line2 = addressLine2TextField.getText().trim();
            String city = String.valueOf(cityComboBox.getSelectedItem());
            String grade = String.valueOf(gradeComboBox.getSelectedItem());
            String id = gradeMap.get(grade);

            if (registerID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please generate student id", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (firstName.isEmpty()) {
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
            } else if (buttonGroupRadioButtons.getSelection() == null) {
                JOptionPane.showMessageDialog(this, "Please select gender", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (mobile.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter mobile", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
                JOptionPane.showMessageDialog(this, "Invalid Mobile Number !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!email.isEmpty()) {
                if (!email.matches("^[a-zA-Z0-9._%+-]+@(gmail|yahoo|hotmail|outlook|gov|live)\\.(com|lk|edu\\.lk|ac\\.lk|gov\\.lk|sch\\.lk|org\\.lk|net|in)$")) {
                    JOptionPane.showMessageDialog(this, "Invalid Email Address !", "Warning", JOptionPane.WARNING_MESSAGE);
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
            } else if (grade.equals("Select Grade")) {
                JOptionPane.showMessageDialog(this, "Please select grade", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            FlatCobalt2IJTheme.setup();
            registerPayment st = new registerPayment(this, true);
            st.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Adding Register: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        FlatCobalt2IJTheme.setup();
        addGrade addGradeDialog = new addGrade(this, true, this);
        addGradeDialog.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        registerIDTextField.setCursor(new Cursor(Cursor.HAND_CURSOR));

        long id = System.currentTimeMillis();
        registerIDTextField.setText("SRI" + String.valueOf(id));

        registerIDTextField.setEnabled(true);

        registerIDTextField.setFocusable(true);

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        refresh();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        FlatCobalt2IJTheme.setup();
        addCity addCityDialog = new addCity(this, true, this);
        addCityDialog.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        String registerID = registerIDTextField.getText().trim();
        String firstName = firstNameTextField.getText().trim();
        String lastName = lastNameTextField.getText().trim();
        boolean isMale = maleRadioButton.isSelected();
        boolean isFemale = femaleRadioButton.isSelected();
        String mobile = mobileTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String line1 = addressLine1TextField.getText().trim();
        String line2 = addressLine2TextField.getText().trim();
        String city = cityComboBox.getSelectedItem().toString();
        String grade = gradeComboBox.getSelectedItem().toString();

        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (firstName.isEmpty()) {
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
        } else if (!isMale && !isFemale) {
            JOptionPane.showMessageDialog(this, "Please select gender", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (mobile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter mobile", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
            JOptionPane.showMessageDialog(this, "Invalid Mobile Number !", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.isEmpty()) {
            if (!email.matches("^[a-zA-Z0-9._%+-]+@(gmail|yahoo|hotmail|outlook|gov|live)\\.(com|lk|edu\\.lk|ac\\.lk|gov\\.lk|sch\\.lk|org\\.lk|net|in)$")) {
                JOptionPane.showMessageDialog(this, "Invalid Email Address !", "Warning", JOptionPane.WARNING_MESSAGE);
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
        } else if (grade.equals("Select Grade")) {
            JOptionPane.showMessageDialog(this, "Please select grade", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            ResultSet rs = MySql.executeSearch("SELECT * FROM student WHERE reg_id = '" + registerID + "'");
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No student found with this ID !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResultSet rsCity = MySql.executeSearch("SELECT id FROM city WHERE name = '" + city + "'");
            String cityID = "";
            if (rsCity.next()) {
                cityID = rsCity.getString("id");
            } else {
                MySql.executeUpdate("INSERT INTO city(name) VALUES('" + city + "')");
                ResultSet rsNewCity = MySql.executeSearch("SELECT id FROM city WHERE name = '" + city + "'");
                if (rsNewCity.next()) {
                    cityID = rsNewCity.getString("id");
                }
            }

            ResultSet rsGrade = MySql.executeSearch("SELECT id FROM grade WHERE name = '" + grade + "'");
            String gradeID = "";
            if (rsGrade.next()) {
                gradeID = rsGrade.getString("id");
            } else {
                JOptionPane.showMessageDialog(this, "Selected grade not found in the database !", "Error", JOptionPane.ERROR_MESSAGE);
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

            MySql.executeUpdate(
                    "UPDATE student_address SET line1 = '" + line1 + "', line2 = '" + line2 + "', city_id = '" + cityID + "' "
                    + "WHERE id = (SELECT student_address_id FROM student WHERE reg_id = '" + registerID + "')"
            );

            MySql.executeUpdate(
                    "UPDATE student SET first_name = '" + firstName + "', last_name = '" + lastName + "', gender_id = '" + genderID + "', "
                    + "mobile = '" + mobile + "', email = '" + email + "', grade_id = '" + gradeID + "' "
                    + "WHERE reg_id = '" + registerID + "'"
            );

            JOptionPane.showMessageDialog(this, "Student updated successfully !", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            searchTextField.setText("");
            loadRegisterStudent();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void searchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTextFieldKeyReleased

        String searchText = searchTextField.getText().trim();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT s.reg_id AS student_id, s.first_name, s.last_name, g.name AS gender, s.mobile, s.email, "
                    + "sa.line1 AS address_line1, sa.line2 AS address_line2, c.name AS city, gr.name AS grade "
                    + "FROM student s "
                    + "JOIN gender g ON s.gender_id = g.id "
                    + "JOIN student_address sa ON s.student_address_id = sa.id "
                    + "JOIN city c ON sa.city_id = c.id "
                    + "JOIN grade gr ON s.grade_id = gr.id "
                    + "WHERE CONCAT(s.first_name, ' ', s.last_name) LIKE '%" + searchText + "%' "
                    + "OR s.first_name LIKE '%" + searchText + "%' "
                    + "OR s.last_name LIKE '%" + searchText + "%' "
                    + "OR s.reg_id LIKE '%" + searchText + "%'"
            );

            while (rs.next()) {
                Object[] row = {
                    rs.getString("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("gender"),
                    rs.getString("mobile"),
                    rs.getString("email"),
                    rs.getString("address_line1"),
                    rs.getString("address_line2"),
                    rs.getString("city"),
                    rs.getString("grade")
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

            JOptionPane.showMessageDialog(this, "Please enter your student registerID, first name, last name or full name in the search bar.", "Warning", JOptionPane.WARNING_MESSAGE);

        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        int row = jTable1.getSelectedRow();

        if (evt.getClickCount() == 2 && row != -1) {
            int option = JOptionPane.showConfirmDialog(this, "Do you want to delete this student ?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                String regId = jTable1.getValueAt(row, 0).toString();
                deleteStudent(regId);
                clearForm();
            }
            return;
        }
        if (row != -1) {

            jButton7.setEnabled(false);
            registerIDTextField.setText(jTable1.getValueAt(row, 0).toString());
            firstNameTextField.setText(jTable1.getValueAt(row, 1).toString());
            lastNameTextField.setText(jTable1.getValueAt(row, 2).toString());

            String gender = jTable1.getValueAt(row, 3).toString();
            if (gender.equalsIgnoreCase("Male")) {
                maleRadioButton.setSelected(true);
            } else {
                femaleRadioButton.setSelected(true);
            }

            mobileTextField.setText(jTable1.getValueAt(row, 4).toString());
            emailTextField.setText(jTable1.getValueAt(row, 5).toString());

            addressLine1TextField.setText(jTable1.getValueAt(row, 6).toString());
            addressLine2TextField.setText(jTable1.getValueAt(row, 7).toString());

            String city = jTable1.getValueAt(row, 8).toString();
            cityComboBox.setSelectedItem(city);

            String grade = jTable1.getValueAt(row, 9).toString();
            gradeComboBox.setSelectedItem(grade);
        }

    }//GEN-LAST:event_jTable1MouseClicked

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
                if (comp == null) {
                    return false;
                }

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
    private javax.swing.JComboBox<String> gradeComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JTextField registerIDTextField;
    private javax.swing.JTextField searchTextField;
    // End of variables declaration//GEN-END:variables

    private void refresh() {

        jButton7.setEnabled(true);
        registerIDTextField.setText("");
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        maleRadioButton.setSelected(true);
        mobileTextField.setText("");
        emailTextField.setText("");
        addressLine1TextField.setText("");
        addressLine2TextField.setText("");
        cityComboBox.setSelectedItem("Select City");
        gradeComboBox.setSelectedItem("Select Grade");
        jTable1.clearSelection();
        searchTextField.setText("");
        jButton7.grabFocus();
        loadRegisterStudent();

    }

    public void clearForm() {

        jButton7.setEnabled(true);
        registerIDTextField.setText("");
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        maleRadioButton.setSelected(true);
        femaleRadioButton.setSelected(false);
        mobileTextField.setText("");
        emailTextField.setText("");
        addressLine1TextField.setText("");
        addressLine2TextField.setText("");
        cityComboBox.setSelectedIndex(0);
        gradeComboBox.setSelectedIndex(0);

    }

}
