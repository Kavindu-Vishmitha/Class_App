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

public class teacher extends javax.swing.JDialog {

    private static HashMap<String, String> cityMap = new HashMap<>();
    private static HashMap<String, String> subjectMap = new HashMap<>();
    home hm;

    public teacher(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadCity();
        loadSubject();
        loadRegisterTeacher();
        setupFullKeyboardSupport();

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/WC.png")));

        hm = (home) parent;
    }

    public void loadRegisterTeacher() {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT t.id, t.first_name, t.last_name, t.mobile, t.email, t.register_date, "
                    + "g.name AS gender, ta.line1 AS address_line_1, ta.line2 AS address_line_2, "
                    + "c.name AS city, s.name AS subject_name "
                    + "FROM teacher t "
                    + "JOIN gender g ON t.gender_id = g.id "
                    + "JOIN teacher_address ta ON t.teacher_address_id = ta.id "
                    + "JOIN city c ON ta.city_id = c.id "
                    + "LEFT JOIN subject s ON t.subject_id = s.id "
                    + "ORDER BY t.first_name ASC"
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
                row.add(rs.getString("subject_name"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data loading failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public void loadSubject() {

        try {

            ResultSet resultSet = MySql.executeSearch("SELECT * FROM `subject` ORDER BY `name` ASC");

            Vector<String> vector = new Vector<>();

            vector.add("Select Subject");

            while (resultSet.next()) {

                vector.add(resultSet.getString("name"));

                subjectMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);

            subjectComboBox.setModel(model);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private void deleteTeacher(String tId) {

        try {

            ResultSet rs = MySql.executeSearch("SELECT teacher_address_id FROM teacher WHERE  id = '" + tId + "'");

            int addressId = -1;

            if (rs.next()) {
                addressId = rs.getInt("teacher_address_id");
            }

            MySql.executeUpdate("DELETE FROM teacher WHERE id = '" + tId + "'");

            if (addressId != -1) {
                MySql.executeUpdate("DELETE FROM teacher_address WHERE id = '" + addressId + "'");
            }

            JOptionPane.showMessageDialog(this, "Teacher deleted successfully !");
            loadRegisterTeacher();
            home.getInstance().getTeacherCount();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting teacher: " + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
        emailTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        subjectComboBox = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        addressLine1TextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        addressLine2TextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cityComboBox = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Teacher");
        setResizable(false);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(22, 160, 133));
        jPanel2.setPreferredSize(new java.awt.Dimension(601, 603));

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
        jLabel4.setText("Subject");

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
        jLabel6.setText("Email");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Address Line 1");

        subjectComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        subjectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        subjectComboBox.setPreferredSize(new java.awt.Dimension(90, 35));

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

        addressLine1TextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Address Line 2");

        addressLine2TextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("City");

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

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Add Subject");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(firstNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lastNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mobileTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(addressLine2TextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                                .addComponent(addressLine1TextField, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(subjectComboBox, 0, 295, Short.MAX_VALUE)
                                        .addComponent(cityComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))))))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(maleRadioButton)
                        .addGap(34, 34, 34)
                        .addComponent(femaleRadioButton))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mobileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maleRadioButton)
                    .addComponent(femaleRadioButton)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLine1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLine2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 601, 560));

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
                .addContainerGap(377, Short.MAX_VALUE)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 0, 820, 80));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Mobile", "Gender", "Email", "Address Line 1", "Address Line 2", "City", "Subject"
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

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 800, 440));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        FlatCobalt2IJTheme.setup();
        addCity addCityDialog = new addCity(this, true, this);
        addCityDialog.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        FlatCobalt2IJTheme.setup();
        addSubject addSubjectDialog = new addSubject(this, true, this);
        addSubjectDialog.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        try {

            String firstName = firstNameTextField.getText().trim();
            String lastName = lastNameTextField.getText().trim();
            String mobile = mobileTextField.getText().trim();
            Boolean isMale = maleRadioButton.isSelected();
            Boolean isFemale = femaleRadioButton.isSelected();
            String email = emailTextField.getText().trim();
            String line1 = addressLine1TextField.getText().trim();
            String line2 = addressLine2TextField.getText().trim();
            String city = String.valueOf(cityComboBox.getSelectedItem());
            String subject = String.valueOf(subjectComboBox.getSelectedItem());

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

            ResultSet checkMobile = MySql.executeSearch("SELECT id FROM teacher WHERE mobile = '" + mobile + "'");

            if (checkMobile.next()) {
                JOptionPane.showMessageDialog(this, "This mobile number is already registered !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!isMale && !isFemale) {
                JOptionPane.showMessageDialog(this, "Please select gender", "Warning", JOptionPane.WARNING_MESSAGE);
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
            } else if (subject.equals("Select Subject")) {
                JOptionPane.showMessageDialog(this, "Please select subject", "Warning", JOptionPane.WARNING_MESSAGE);
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

            String subjectID = subjectMap.get(subject);
            if (subjectID == null) {
                JOptionPane.showMessageDialog(this, "Selected subject not found in map !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MySql.executeUpdate("INSERT INTO teacher_address(line1, line2, city_id) VALUES('" + line1 + "', '" + line2 + "', '" + cityID + "')");

            ResultSet rsAddress = MySql.executeSearch("SELECT id FROM teacher_address WHERE line1 = '" + line1 + "' AND line2 = '" + line2 + "' AND city_id = '" + cityID + "' ORDER BY id DESC LIMIT 1");
            String addressID = "";
            if (rsAddress.next()) {
                addressID = rsAddress.getString("id");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve address ID !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            long id = System.currentTimeMillis();
            String teacherID = "TRI" + String.valueOf(id);
            MySql.executeUpdate("INSERT INTO teacher (id, first_name, last_name, mobile, email, register_date, teacher_address_id, gender_id, subject_id) "
                    + "VALUES ('" + teacherID + "','" + firstName + "', '" + lastName + "', '" + mobile + "', '" + email + "', NOW(), '" + addressID + "', '" + genderID + "', '" + subjectID + "')");

            JOptionPane.showMessageDialog(this, "Teacher registered successfully !", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRegisterTeacher();
            home.getInstance().getTeacherCount();
            refresh();

        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred while registering the teacher.\nPlease try again.", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            String email = emailTextField.getText().trim();
            String line1 = addressLine1TextField.getText().trim();
            String line2 = addressLine2TextField.getText().trim();
            String city = String.valueOf(cityComboBox.getSelectedItem());
            String subject = String.valueOf(subjectComboBox.getSelectedItem());

            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an teacher first to update", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String selectedTeacherID = jTable1.getValueAt(selectedRow, 0).toString();

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

            ResultSet checkMobile = MySql.executeSearch("SELECT id FROM teacher WHERE mobile = '" + mobile + "' AND id != '" + selectedTeacherID + "'");

            if (checkMobile.next()) {
                JOptionPane.showMessageDialog(this, "This mobile number is already registered !", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isMale && !isFemale) {
                JOptionPane.showMessageDialog(this, "Please select gender", "Warning", JOptionPane.WARNING_MESSAGE);
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
            } else if (subject.equals("Select Subject")) {
                JOptionPane.showMessageDialog(this, "Please select subject", "Warning", JOptionPane.WARNING_MESSAGE);
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

            String subjectID = subjectMap.get(subject);
            if (subjectID == null) {
                JOptionPane.showMessageDialog(this, "Selected subject not found in map !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ResultSet rsAddress = MySql.executeSearch("SELECT teacher_address_id FROM teacher WHERE id = '" + selectedTeacherID + "'");
            String addressID = "";
            if (rsAddress.next()) {
                addressID = rsAddress.getString("teacher_address_id");
            } else {
                JOptionPane.showMessageDialog(this, "Address ID not found !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MySql.executeUpdate("UPDATE teacher_address SET line1 = '" + line1 + "', line2 = '" + line2 + "', city_id = '" + cityID + "' WHERE id = '" + addressID + "'");

            MySql.executeUpdate("UPDATE teacher SET first_name = '" + firstName + "', last_name = '" + lastName + "', mobile = '" + mobile + "', email = '" + email + "', gender_id = '" + genderID + "', subject_id = '" + subjectID + "' WHERE id = '" + selectedTeacherID + "'");

            JOptionPane.showMessageDialog(this, "Teacher updated successfully !", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRegisterTeacher();
            refresh();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the teacher", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        int row = jTable1.getSelectedRow();

        if (evt.getClickCount() == 2 && row != -1) {
            int option = JOptionPane.showConfirmDialog(this, "Do you want to delete this teacher ?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                String tId = jTable1.getValueAt(row, 0).toString();
                deleteTeacher(tId);
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

            String subject = jTable1.getValueAt(row, 9).toString();
            subjectComboBox.setSelectedItem(subject);

        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        String search = searchTextField.getText();

        if (search.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please enter your teacher registerID, first name, last name or full name in the search bar.", "Warning", JOptionPane.WARNING_MESSAGE);

        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void searchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTextFieldKeyReleased

        String searchText = searchTextField.getText().trim();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT t.id, t.first_name, t.last_name, t.mobile, t.email, t.register_date, "
                    + "g.name AS gender, ta.line1 AS address_line_1, ta.line2 AS address_line_2, "
                    + "c.name AS city, s.name AS subject_name "
                    + "FROM teacher t "
                    + "JOIN gender g ON t.gender_id = g.id "
                    + "JOIN teacher_address ta ON t.teacher_address_id = ta.id "
                    + "JOIN city c ON ta.city_id = c.id "
                    + "LEFT JOIN subject s ON t.subject_id = s.id "
                    + "WHERE CONCAT(t.first_name, ' ', t.last_name) LIKE '%" + searchText + "%' "
                    + "OR t.first_name LIKE '%" + searchText + "%' "
                    + "OR t.last_name LIKE '%" + searchText + "%' "
                    + "OR t.id LIKE '%" + searchText + "%' "
                    + "ORDER BY t.first_name ASC"
            );

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
                row.add(rs.getString("subject_name"));
                model.addRow(row);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Search failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_searchTextFieldKeyReleased

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
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JTextField searchTextField;
    private javax.swing.JComboBox<String> subjectComboBox;
    // End of variables declaration//GEN-END:variables

    private void refresh() {

        firstNameTextField.setText("");
        lastNameTextField.setText("");
        mobileTextField.setText("");
        maleRadioButton.setSelected(true);
        emailTextField.setText("");
        addressLine1TextField.setText("");
        addressLine2TextField.setText("");
        cityComboBox.setSelectedItem("Select City");
        subjectComboBox.setSelectedItem("Select Subject");
        jTable1.clearSelection();
        searchTextField.setText("");
        firstNameTextField.grabFocus();
        loadRegisterTeacher();

    }
}
