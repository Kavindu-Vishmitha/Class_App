package gui;

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
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.MySql;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

public class payment extends javax.swing.JDialog {

    private static HashMap<String, String> subjectMap = new HashMap<>();
    private List<Map<String, Object>> selectedSubjects = new ArrayList<>();
    private Set<String> tempSelectedKeys = new HashSet<>();

    private String lastSelectedStudentRegId = "";

    home hm;

    public payment(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadRegisterStudent();
        loadSubject();
        loadOtherPayment();
        filterStudentPaymentsTable();
        setupFullKeyboardSupport();

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/WC.png")));
        hm = (home) parent;
    }

    class PaymentData {

        int subjectId;
        double fee;
        String paymentDate;
        String studentRegId;

        public PaymentData(int subjectId, double fee, String paymentDate, String studentRegId) {
            this.subjectId = subjectId;
            this.fee = fee;
            this.paymentDate = paymentDate;
            this.studentRegId = studentRegId;
        }
    }

    public void filterStudentPaymentsTable() {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        int selectedRow = jTable3.getSelectedRow();
        String selectedStudentId = null;
        if (selectedRow != -1) {
            selectedStudentId = jTable3.getValueAt(selectedRow, 0).toString();
        }

        String selectedSubject = (String) subjectComboBox.getSelectedItem();
        boolean subjectSelected = selectedSubject != null && !selectedSubject.equals("Select Subject");

        StringBuilder query = new StringBuilder(
                "SELECT st.reg_id AS id, st.first_name, st.last_name, g.name AS grade, "
                + "sub.name AS subject, f.payment_date AS date "
                + "FROM st_subject_fee f "
                + "JOIN student st ON f.student_reg_id = st.reg_id "
                + "JOIN grade g ON st.grade_id = g.id "
                + "JOIN subject sub ON f.subject_id = sub.id "
        );

        boolean whereAdded = false;

        if (selectedStudentId != null) {
            query.append("WHERE st.reg_id = '").append(selectedStudentId).append("' ");
            whereAdded = true;
        }

        if (subjectSelected) {
            if (whereAdded) {
                query.append("AND sub.name = '").append(selectedSubject).append("' ");
            } else {
                query.append("WHERE sub.name = '").append(selectedSubject).append("' ");
                whereAdded = true;
            }
        }

        query.append("ORDER BY st.first_name ASC, f.payment_date DESC");

        try {

            ResultSet rs = MySql.executeSearch(query.toString());
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("grade"));
                row.add(rs.getString("subject"));
                row.add(rs.getString("date"));
                model.addRow(row);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data loading failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadOtherPayment() {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT st.reg_id AS id, st.first_name, st.last_name, g.name AS grade, "
                    + "sub.name AS subject, f.payment_date AS date "
                    + "FROM st_subject_fee f "
                    + "JOIN student st ON f.student_reg_id = st.reg_id "
                    + "JOIN grade g ON st.grade_id = g.id "
                    + "JOIN subject sub ON f.subject_id = sub.id "
                    + "ORDER BY st.first_name ASC,MONTH(f.payment_date) ASC, YEAR(f.payment_date) ASC"
            );

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("grade"));
                row.add(rs.getString("subject"));
                row.add(rs.getString("date"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data loading failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadRegisterStudent() {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT s.reg_id AS id, s.first_name, s.last_name, g.name AS grade "
                    + "FROM student s "
                    + "JOIN grade g ON s.grade_id = g.id "
                    + "ORDER BY s.first_name ASC"
            );

            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("grade"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data loading failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        studentIDTextField = new javax.swing.JTextField();
        studentNameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        subjectComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        payAmountTextField = new javax.swing.JFormattedTextField();
        payTextField = new javax.swing.JFormattedTextField();
        balanceTextField = new javax.swing.JFormattedTextField();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Payment");
        setResizable(false);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(22, 160, 133));

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(352, 352, 352)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(352, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1110, 60));

        jPanel6.setBackground(new java.awt.Color(22, 160, 133));

        studentIDTextField.setEditable(false);
        studentIDTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        studentIDTextField.setEnabled(false);
        studentIDTextField.setPreferredSize(new java.awt.Dimension(90, 35));

        studentNameTextField.setEditable(false);
        studentNameTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        studentNameTextField.setEnabled(false);
        studentNameTextField.setPreferredSize(new java.awt.Dimension(90, 35));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Subject :");

        subjectComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        subjectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        subjectComboBox.setPreferredSize(new java.awt.Dimension(90, 35));
        subjectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjectComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Balance :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Pay amount :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Pay :");

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Pay");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setPreferredSize(new java.awt.Dimension(94, 35));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("ADD");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setPreferredSize(new java.awt.Dimension(94, 35));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        payAmountTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        payAmountTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        payAmountTextField.setEnabled(false);
        payAmountTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        payTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        payTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        payTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        payTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                payTextFieldKeyReleased(evt);
            }
        });

        balanceTextField.setEditable(false);
        balanceTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        balanceTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        balanceTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("CLEAR");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setPreferredSize(new java.awt.Dimension(94, 35));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Date :");

        jDateChooser1.setForeground(new java.awt.Color(255, 255, 255));
        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jDateChooser1.setPreferredSize(new java.awt.Dimension(123, 35));

        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("  OTHER");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                            .addComponent(balanceTextField)
                            .addComponent(payAmountTextField)
                            .addComponent(payTextField)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(studentNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(52, 52, 52))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(72, 72, 72)))
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(subjectComboBox, 0, 259, Short.MAX_VALUE)
                                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(payAmountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(payTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(balanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Grade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.getTableHeader().setReorderingAllowed(false);
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 520, 610));

        jPanel1.setBackground(new java.awt.Color(22, 160, 133));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 60, 10, 610));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Grade", "Subject", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 70, 560, 600));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        String search = searchTextField.getText();

        if (search.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please enter your student registerID, first name, last name or full name in the search bar.", "Warning", JOptionPane.WARNING_MESSAGE);

        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void searchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTextFieldKeyReleased

        String searchText = searchTextField.getText().trim();

        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0);

        try {

            ResultSet rs = MySql.executeSearch("SELECT s.reg_id AS id, s.first_name, s.last_name, g.name AS grade "
                    + "FROM student s "
                    + "JOIN grade g ON s.grade_id = g.id "
                    + "WHERE s.first_name LIKE '%" + searchText + "%' "
                    + "OR s.last_name LIKE '%" + searchText + "%' "
                    + "OR CONCAT(s.first_name, ' ', s.last_name) LIKE '%" + searchText + "%' "
                    + "OR s.reg_id LIKE '%" + searchText + "%' "
                    + "ORDER BY s.first_name ASC");

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("grade"));
                model.addRow(row);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data loading failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_searchTextFieldKeyReleased

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked

        int row = jTable3.getSelectedRow();
        if (row != -1) {
            studentIDTextField.setText(jTable3.getValueAt(row, 0).toString());

            String firstName = jTable3.getValueAt(row, 1).toString();
            String lastName = jTable3.getValueAt(row, 2).toString();
            studentNameTextField.setText(firstName + " " + lastName);
            filterStudentPaymentsTable();
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        refresh();
    }//GEN-LAST:event_jButton3ActionPerformed


    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        String selectedSubject = (String) subjectComboBox.getSelectedItem();
        int selectedRow = jTable3.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (jDateChooser1.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select a date", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.util.Date selectedDateObj = jDateChooser1.getDate();
        java.util.Date today = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");

        if (!jCheckBox1.isSelected()) {
            if (!sdf.format(selectedDateObj).equals(sdf.format(today))) {
                JOptionPane.showMessageDialog(this,
                        "Please select only today's date.\nIf you want to select another date, please check the 'OTHER' checkbox.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            if (sdf.format(selectedDateObj).equals(sdf.format(today))) {
                JOptionPane.showMessageDialog(this,
                        "You have selected today's date and also checked 'OTHER'.\nUncheck the 'OTHER' checkbox if the date is today.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);

                jCheckBox1.setSelected(false);
            }
        }

        if (selectedSubject.equals("Select Subject")) {
            JOptionPane.showMessageDialog(this, "Please select subject", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            String studentRegId = jTable3.getValueAt(selectedRow, 0).toString();

            if (!studentRegId.equals(lastSelectedStudentRegId)) {
                selectedSubjects.clear();
                tempSelectedKeys.clear();
                payAmountTextField.setText("0.00");
                lastSelectedStudentRegId = studentRegId;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDate = dateFormat.format(jDateChooser1.getDate());

            SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
            String selectedYearMonth = yearMonthFormat.format(jDateChooser1.getDate());

            ResultSet subjectRs = MySql.executeSearch("SELECT id FROM subject WHERE name = '" + selectedSubject + "'");

            if (!subjectRs.next()) {
                JOptionPane.showMessageDialog(this, "Subject not found in database", "Error", JOptionPane.ERROR_MESSAGE);
                subjectRs.close();
                return;
            }
            int subjectId = subjectRs.getInt("id");
            subjectRs.close();

            List<String> alreadyPaidSubjects = new ArrayList<>();

            ResultSet paymentRs = MySql.executeSearch("SELECT id FROM st_subject_fee "
                    + "WHERE student_reg_id = '" + studentRegId + "' "
                    + "AND subject_id = " + subjectId + " "
                    + "AND DATE_FORMAT(payment_date, '%Y-%m') = '" + selectedYearMonth + "'");

            if (paymentRs.next()) {
                alreadyPaidSubjects.add(selectedSubject + " - " + selectedYearMonth);
            }
            paymentRs.close();

            if (!alreadyPaidSubjects.isEmpty()) {
                String msg = "A payment for \"" + selectedSubject + "\" has already been completed for " + selectedYearMonth + "";
                JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String uniqueKey = studentRegId + "_" + selectedSubject + "_" + selectedDate;
            if (tempSelectedKeys.contains(uniqueKey)) {
                JOptionPane.showMessageDialog(this, "This subject has already been added for this student", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResultSet rs = MySql.executeSearch("SELECT fee FROM subject WHERE id = " + subjectId);

            if (rs.next()) {
                double fee = rs.getDouble("fee");

                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("subjectId", subjectId);
                subjectData.put("fee", fee);
                subjectData.put("studentRegId", studentRegId);
                subjectData.put("paymentDate", selectedDate);

                selectedSubjects.add(subjectData);
                tempSelectedKeys.add(uniqueKey);

                double total = selectedSubjects.stream()
                        .mapToDouble(subj -> (Double) subj.get("fee"))
                        .sum();

                payAmountTextField.setText(String.format("%.2f", total));
            } else {
                JOptionPane.showMessageDialog(this, "Subject not found in database", "Error", JOptionPane.ERROR_MESSAGE);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String pay = payTextField.getText();
        String balance = balanceTextField.getText();

        try {

            int selectedRow = jTable3.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (jDateChooser1.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Please select a date", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            if (selectedSubject.equals("Select Subject")) {
                JOptionPane.showMessageDialog(this, "Please select subject", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String payAmount = payAmountTextField.getText().trim();
            if (payAmount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the payment amount.\nSelect subject and click 'Add' button.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String enteredAmount = payTextField.getText().trim();
            if (enteredAmount.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter the payment amount.\n"
                        + "Amount to pay: Rs. " + payAmount,
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double totalFee = selectedSubjects.stream()
                    .mapToDouble(subj -> (Double) subj.get("fee"))
                    .sum();

            double entered = 0.0;
            try {
                entered = Double.parseDouble(enteredAmount);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this,
                        "Invalid payment amount entered. Please enter a valid number.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (entered < totalFee) {
                JOptionPane.showMessageDialog(this,
                        "Entered amount cannot be less than the total fee !\n"
                        + "Please check: Rs. " + String.format("%.2f", totalFee),
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<String> alreadyPaidSubjects = new ArrayList<>();
            List<String> newlyPaidSubjects = new ArrayList<>();

            for (Map<String, Object> subject : selectedSubjects) {
                String studentRegId = (String) subject.get("studentRegId");
                int subjectId = (int) subject.get("subjectId");
                double fee = (Double) subject.get("fee");
                String paymentDate = (String) subject.get("paymentDate");

                java.time.LocalDate payDate = java.time.LocalDate.parse(paymentDate);
                int payYear = payDate.getYear();
                int payMonth = payDate.getMonthValue();

                boolean alreadyPaid = false;
                String subjectName = null;

                ResultSet checkRs = MySql.executeSearch("SELECT s.name FROM st_subject_fee f "
                        + "JOIN subject s ON f.subject_id = s.id "
                        + "WHERE f.student_reg_id = '" + studentRegId + "' "
                        + "AND f.subject_id = '" + subjectId + "' "
                        + "AND YEAR(f.payment_date) = " + payYear + " "
                        + "AND MONTH(f.payment_date) = " + payMonth);

                if (checkRs.next()) {
                    subjectName = checkRs.getString("name");
                    alreadyPaidSubjects.add(subjectName + " (" + payYear + "-" + String.format("%02d", payMonth) + ")");
                    alreadyPaid = true;
                }
                checkRs.close();

                if (alreadyPaid) {
                    continue;
                }

                int result = MySql.executeUpdate("INSERT INTO st_subject_fee (fee, payment_date, subject_id, student_reg_id) "
                        + "VALUES ('" + fee + "', '" + paymentDate + "', '" + subjectId + "', '" + studentRegId + "')");

                if (result > 0) {
                    if (subjectName == null) {
                        String subjectNameQuery = "SELECT name FROM subject WHERE id = '" + subjectId + "'";
                        ResultSet subjectNameRs = MySql.executeSearch(subjectNameQuery);
                        if (subjectNameRs.next()) {
                            subjectName = subjectNameRs.getString("name");
                        }
                        subjectNameRs.close();
                    }
                    newlyPaidSubjects.add(subjectName + " (" + payYear + "-" + String.format("%02d", payMonth) + ")");
                }
            }

            if (!alreadyPaidSubjects.isEmpty()) {
                String msg = "Payment already exists for subject(s) in these months: "
                        + String.join(", ", alreadyPaidSubjects);
                JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
            }

            if (!newlyPaidSubjects.isEmpty()) {
                String msg = "Payments recorded successfully for subjects: "
                        + String.join(", ", newlyPaidSubjects);
                JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                home.getInstance().TodayRevenue();
                home.getInstance().MonthlyRevenue();
                home.getInstance().YearlyRevenue();

                String registerID = jTable3.getValueAt(selectedRow, 0).toString().trim();
                String firstName = jTable3.getValueAt(selectedRow, 1).toString().trim();
                String lastName = jTable3.getValueAt(selectedRow, 2).toString().trim();
                String fullName = firstName + " " + lastName;

                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT s.name AS Field_1, ssf.payment_date AS Field_2, ")
                            .append("FORMAT(ssf.fee, 2) AS Field_3 ")
                            .append("FROM st_subject_fee ssf ")
                            .append("JOIN subject s ON ssf.subject_id = s.id ")
                            .append("WHERE ssf.student_reg_id = '").append(registerID).append("' ")
                            .append("AND (");

                    for (int i = 0; i < newlyPaidSubjects.size(); i++) {
                        String subj = newlyPaidSubjects.get(i);
                        String[] parts = subj.split(" \\(");
                        String subjectName = parts[0];
                        String[] y_m = parts[1].replace(")", "").split("-");
                        String year = y_m[0];
                        String month = y_m[1];

                        queryBuilder.append("(s.name = '").append(subjectName)
                                .append("' AND YEAR(ssf.payment_date) = ").append(year)
                                .append(" AND MONTH(ssf.payment_date) = ").append(month).append(")");

                        if (i < newlyPaidSubjects.size() - 1) {
                            queryBuilder.append(" OR ");
                        }
                    }
                    queryBuilder.append(")");

                    String query = queryBuilder.toString();
                    ResultSet rs = MySql.executeSearch(query);

                    if (!rs.isBeforeFirst()) {
                        JOptionPane.showMessageDialog(this, "No payment records found for student ID: " + registerID);
                        return;
                    }

                    HashMap<String, Object> params = new HashMap<>();
                    params.put("Parameter1", registerID);
                    params.put("Parameter2", fullName);
                    params.put("Parameter3", payAmount);
                    params.put("Parameter4", pay);
                    params.put("Parameter5", balance);

                    InputStream reportStream = getClass().getResourceAsStream("/reports/payment.jasper");

                    if (reportStream == null) {
                        JOptionPane.showMessageDialog(this, "Report file not found !", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(rs);
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, resultSetDataSource);

                    JRViewer viewer = new JRViewer(jasperPrint);

                    JDialog reportDialog = new JDialog(this, "Payment Invoice", true);
                    reportDialog.setSize(800, 600);
                    reportDialog.setLocationRelativeTo(this);
                    reportDialog.getContentPane().add(viewer);
                    reportDialog.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error generating report:\n" + e.getMessage(), "Report Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            selectedSubjects.clear();
            tempSelectedKeys.clear();
            payTextField.setText("");
            payAmountTextField.setText("");
            refresh();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void subjectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subjectComboBoxActionPerformed
        filterStudentPaymentsTable();
    }//GEN-LAST:event_subjectComboBoxActionPerformed

    private void payTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_payTextFieldKeyReleased

        try {

            String payText = payTextField.getText().trim();
            String totalText = payAmountTextField.getText().trim();

            double paid = payText.isEmpty() ? 0.0 : Double.parseDouble(payText);
            double total = totalText.isEmpty() ? 0.0 : Double.parseDouble(totalText);

            double balance = paid - total;

            DecimalFormat df = new DecimalFormat("0.00");
            balanceTextField.setText(df.format(balance));
        } catch (NumberFormatException e) {
            balanceTextField.setText("0.00");
        }

    }//GEN-LAST:event_payTextFieldKeyReleased

    private void refreshStudentTable() {

        try {

            ResultSet rs = MySql.executeSearch("SELECT s.reg_id, s.first_name, s.last_name, g.name AS grade "
                    + "FROM student s "
                    + "JOIN grade g ON s.grade_id = g.id "
                    + "ORDER BY s.first_name ASC");

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.addColumn("ID");
            model.addColumn("First Name");
            model.addColumn("Last Name");
            model.addColumn("Grade");

            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getString("reg_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("grade")
                };
                model.addRow(row);
            }

            rs.close();
            jTable3.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to refresh student table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
                    if (window != null && !(window instanceof home)) {
                        window.dispose();
                        return true;
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    if (comp instanceof JButton button) {
                        button.doClick();
                        return true;
                    } else if (comp instanceof JCheckBox checkBox) {
                        checkBox.setSelected(!checkBox.isSelected());
                        return true;
                    } else if (comp instanceof JComboBox<?> comboBox) {
                        if (!comboBox.isPopupVisible()) {
                            comboBox.showPopup();
                        } else {
                            Object selectedItem = comboBox.getSelectedItem();
                            comboBox.setSelectedItem(selectedItem);
                            comboBox.hidePopup();
                        }
                        return true;
                    } else if (comp instanceof JTable table && table == jTable3) {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow != -1) {
                            MouseEvent evt = new MouseEvent(
                                    table,
                                    MouseEvent.MOUSE_CLICKED,
                                    System.currentTimeMillis(),
                                    0,
                                    0,
                                    0,
                                    1,
                                    false
                            );
                            jTable3MouseClicked(evt);
                            return true;
                        }
                    }
                }

                if (comp instanceof JTable table) {
                    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                        return false;
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        table.transferFocusBackward();
                        return true;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        table.transferFocus();
                        return true;
                    }
                }

                if (comp instanceof JComboBox<?> comboBox && comboBox.isPopupVisible()) {
                    return false;
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

                return false;
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField balanceTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JFormattedTextField payAmountTextField;
    private javax.swing.JFormattedTextField payTextField;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTextField studentIDTextField;
    private javax.swing.JTextField studentNameTextField;
    private javax.swing.JComboBox<String> subjectComboBox;
    // End of variables declaration//GEN-END:variables

    private void refresh() {

        jTable3.clearSelection();
        refreshStudentTable();
        jTable1.clearSelection();
        studentIDTextField.setText("");
        studentNameTextField.setText("");
        subjectComboBox.setSelectedItem("Select Subject");
        payAmountTextField.setText("");
        payTextField.setText("");
        balanceTextField.setText("");
        searchTextField.setText("");
        jDateChooser1.setDate(null);
        jCheckBox1.setSelected(false);
        searchTextField.grabFocus();

    }
}
