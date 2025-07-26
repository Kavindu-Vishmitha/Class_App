package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.MySql;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

public class attendanceReport extends javax.swing.JDialog {

    private static HashMap<String, String> subjectMap = new HashMap<>();
    private static HashMap<String, String> gradeMap = new HashMap<>();
    private String currentSearchText = "";
    private String currentSelectedSubject = null;
    private Date currentFromDate = null;
    private Date currentToDate = null;
    private String selectedStudentRegId = null;

    home hm;

    public attendanceReport(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadSubject();
        loadGrade();
        loadStudentAttendance();
        setupFullKeyboardSupport();

        jDateChooserFrom.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                searchAttendanceByDate();
            }
        });

        jDateChooserTo.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                searchAttendanceByDate();
            }
        });

        try {
            markAttendance ma = new markAttendance();
            loadStudentAttendance();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to mark absents.\n" + e.getMessage());
        }

        subjectComboBox.addActionListener(e -> {

            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            currentSelectedSubject = selectedSubject;

            if (jDateChooserFrom.getDate() == null && jDateChooserTo.getDate() == null) {
                currentFromDate = null;
                currentToDate = null;
                selectedStudentRegId = null;

            }

            filterAttendanceData();
        });

        gradeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                filterAttendanceData();

            }
        });

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/WC.png")));
        hm = (home) parent;
    }

    private void loadStudentAttendanceByGrade(String selectedGrade) {

        try {

            ResultSet rs = MySql.executeSearch("SELECT s.reg_id AS student_reg_id, s.first_name, s.last_name, "
                    + "g.name AS gender, gr.name AS grade, sub.name AS subject, "
                    + "a.date, a.time, ast.name AS attendance_status "
                    + "FROM attendance a "
                    + "JOIN student s ON a.student_reg_id = s.reg_id "
                    + "JOIN gender g ON s.gender_id = g.id "
                    + "JOIN grade gr ON s.grade_id = gr.id "
                    + "JOIN subject sub ON a.subject_id = sub.id "
                    + "JOIN attendance_status ast ON a.attendance_status_id = ast.id "
                    + "WHERE gr.name = '" + selectedGrade + "' "
                    + "ORDER BY a.date DESC,a.time DESC");

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_reg_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("grade"));
                row.add(rs.getString("subject"));
                row.add(rs.getString("date"));
                row.add(rs.getString("time") != null ? rs.getString("time") : "");
                row.add(rs.getString("attendance_status"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading attendance for grade: " + selectedGrade);
        }
    }

    private boolean isResettingDates = false;

    private void searchAttendanceByDate() {

        if (isResettingDates) {
            return;
        }

        Date fromDate = jDateChooserFrom.getDate();
        Date toDate = jDateChooserTo.getDate();
        Date today = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        boolean isSameDate = false;
        if (fromDate != null && toDate != null) {
            String from = sdf.format(fromDate);
            String to = sdf.format(toDate);
            isSameDate = from.equals(to);
        }

        if ((fromDate != null && fromDate.after(today))
                || (toDate != null && toDate.after(today))
                || (fromDate != null && toDate != null && fromDate.after(toDate))
                || (fromDate != null && toDate != null && isSameDate)) {
            JOptionPane.showMessageDialog(null, "Please select valid dates", "Warning", JOptionPane.WARNING_MESSAGE);

            isResettingDates = true;

            jDateChooserFrom.setDate(null);
            jDateChooserTo.setDate(null);

            currentFromDate = null;
            currentToDate = null;
            isResettingDates = false;
            return;
        }

        if (fromDate == null && toDate == null) {
            return;
        }

        currentFromDate = fromDate;
        currentToDate = toDate;

        filterAttendanceData();
    }

    private void filterAttendanceData() {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT s.reg_id AS student_reg_id, s.first_name, s.last_name, ");
            query.append("g.name AS gender, gr.name AS grade, sub.name AS subject, ");
            query.append("a.date, a.time, ast.name AS attendance_status ");
            query.append("FROM attendance a ");
            query.append("JOIN student s ON a.student_reg_id = s.reg_id ");
            query.append("JOIN gender g ON s.gender_id = g.id ");
            query.append("JOIN grade gr ON s.grade_id = gr.id ");
            query.append("JOIN subject sub ON a.subject_id = sub.id ");
            query.append("JOIN attendance_status ast ON a.attendance_status_id = ast.id ");
            query.append("WHERE 1=1 ");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (currentFromDate != null && currentToDate != null) {
                String from = sdf.format(currentFromDate);
                String to = sdf.format(currentToDate);
                query.append("AND a.date BETWEEN '").append(from).append("' AND '").append(to).append("' ");
            } else if (currentFromDate != null) {
                String from = sdf.format(currentFromDate);
                query.append("AND a.date = '").append(from).append("' ");
            } else if (currentToDate != null) {
                String to = sdf.format(currentToDate);
                query.append("AND a.date = '").append(to).append("' ");
            }

            if (currentSearchText != null && !currentSearchText.isEmpty()) {
                query.append("AND (CONCAT(s.first_name, ' ', s.last_name) LIKE '%").append(currentSearchText).append("%' ")
                        .append("OR s.first_name LIKE '%").append(currentSearchText).append("%' ")
                        .append("OR s.last_name LIKE '%").append(currentSearchText).append("%' ")
                        .append("OR s.reg_id LIKE '%").append(currentSearchText).append("%') ");
            }

            if (currentSelectedSubject != null && !currentSelectedSubject.equals("Select Subject")) {
                query.append("AND sub.name = '").append(currentSelectedSubject).append("' ");
            }

            String currentSelectedGrade = gradeComboBox.getSelectedItem().toString();
            if (currentSelectedGrade != null && !currentSelectedGrade.equals("Select Grade")) {
                query.append("AND gr.name = '").append(currentSelectedGrade).append("' ");
            }

            query.append("ORDER BY a.date DESC,a.time DESC");

            ResultSet rs = MySql.executeSearch(query.toString());

            Set<String> studentRegIdSet = new HashSet<>();

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_reg_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("grade"));
                row.add(rs.getString("subject"));
                row.add(rs.getString("date"));
                row.add(rs.getString("time") != null ? rs.getString("time") : "");
                row.add(rs.getString("attendance_status"));
                model.addRow(row);

                studentRegIdSet.add(rs.getString("student_reg_id"));
            }

            rs.close();

            int rowCount = jTable1.getRowCount();
            boolean isDateSelected = currentFromDate != null || currentToDate != null;

            if (rowCount > 0) {
                boolean isSingleStudent = studentRegIdSet.size() == 1;
                boolean isSubjectSelected = currentSelectedSubject != null && !currentSelectedSubject.equals("Select Subject");

                if (isSingleStudent && isSubjectSelected && isDateSelected) {
                    selectedStudentRegId = studentRegIdSet.iterator().next();

                } else {
                    selectedStudentRegId = null;

                }

            } else {
                selectedStudentRegId = null;

            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Attendance filtering failed: " + e.getMessage());
        }
    }

    public void loadSubjectAttendance(String subjectName) {

        currentSelectedSubject = subjectComboBox.getSelectedItem().toString();

        if (jDateChooserFrom.getDate() == null && jDateChooserTo.getDate() == null) {

            currentFromDate = null;
            currentToDate = null;
            selectedStudentRegId = null;

            return;
        }

        currentFromDate = jDateChooserFrom.getDate();
        currentToDate = jDateChooserTo.getDate();

        filterAttendanceData();
    }

    public void loadStudentAttendance() {

        try {
            ResultSet rs = MySql.executeSearch(
                    "SELECT s.reg_id AS student_reg_id, s.first_name, s.last_name, "
                    + "g.name AS gender, gr.name AS grade, sub.name AS subject, "
                    + "a.date, a.time, ast.name AS attendance_status "
                    + "FROM attendance a "
                    + "JOIN student s ON a.student_reg_id = s.reg_id "
                    + "JOIN gender g ON s.gender_id = g.id "
                    + "JOIN grade gr ON s.grade_id = gr.id "
                    + "JOIN subject sub ON a.subject_id = sub.id "
                    + "JOIN attendance_status ast ON a.attendance_status_id = ast.id "
                    + "ORDER BY a.date DESC,a.time DESC"
            );

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_reg_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("grade"));
                row.add(rs.getString("subject"));
                row.add(rs.getString("date"));
                row.add(rs.getString("time") != null ? rs.getString("time") : "");
                row.add(rs.getString("attendance_status"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Attendance loading failed: " + e.getMessage());
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

    public void loadGrade() {

        try {

            ResultSet resultSet = MySql.executeSearch("SELECT * FROM `grade`");

            Vector<String> vector = new Vector<>();

            vector.add("Select Grade");

            while (resultSet.next()) {

                vector.add(resultSet.getString("name"));

                gradeMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);

            gradeComboBox.setModel(model);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jDateChooserFrom = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jDateChooserTo = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        subjectComboBox = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        gradeComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Attendance Report");
        setResizable(false);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(22, 160, 133));

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

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Refresh");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(418, 418, 418)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(284, 284, 284)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1340, -1));

        jPanel3.setBackground(new java.awt.Color(22, 160, 133));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Gender", "Grade", "Subject", "Date", "Time", "Attendance"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("From :");

        jDateChooserFrom.setForeground(new java.awt.Color(255, 255, 255));
        jDateChooserFrom.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jDateChooserFrom.setPreferredSize(new java.awt.Dimension(123, 35));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("To :");

        jDateChooserTo.setForeground(new java.awt.Color(255, 255, 255));
        jDateChooserTo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jDateChooserTo.setPreferredSize(new java.awt.Dimension(123, 35));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Subject :");

        subjectComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        subjectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Print");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Grade :");

        gradeComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        gradeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(28, 28, 28)
                        .addComponent(jDateChooserFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel2)
                        .addGap(28, 28, 28)
                        .addComponent(jDateChooserTo, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(gradeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addGap(21, 21, 21))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jDateChooserFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jDateChooserTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(gradeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 70, 1340, 490));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        currentSearchText = searchTextField.getText().trim();
        filterAttendanceData();

    }//GEN-LAST:event_searchTextFieldKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        refresh();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        try {

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

            List<Map<String, Object>> dataList = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {
                Map<String, Object> row = new HashMap<>();
                row.put("COLUMN_1", model.getValueAt(i, 0));
                row.put("COLUMN_2", model.getValueAt(i, 1));
                row.put("COLUMN_5", model.getValueAt(i, 4));
                row.put("COLUMN_6", model.getValueAt(i, 5));
                row.put("COLUMN_7", model.getValueAt(i, 6));
                row.put("COLUMN_8", model.getValueAt(i, 7));
                row.put("COLUMN_9", model.getValueAt(i, 8));
                dataList.add(row);
            }

            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data found in the table to display in the report", "No Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedSubject = subjectComboBox.getSelectedItem() != null ? subjectComboBox.getSelectedItem().toString() : "not";
            if (selectedSubject.equals("Select Subject")) {
                selectedSubject = "not";
            }

            String selectedGrade = gradeComboBox.getSelectedItem() != null ? gradeComboBox.getSelectedItem().toString() : "not";
            if (selectedGrade.equals("Select Grade")) {
                selectedGrade = "not";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fromDate = jDateChooserFrom.getDate() != null ? sdf.format(jDateChooserFrom.getDate()) : "not";
            String toDate = jDateChooserTo.getDate() != null ? sdf.format(jDateChooserTo.getDate()) : "not";

            InputStream reportStream = getClass().getResourceAsStream("/reports/attendanceReport.jasper");

            if (reportStream == null) {
                JOptionPane.showMessageDialog(this, "Report file not found !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Parameter1", fromDate);
            parameters.put("Parameter2", toDate);
            parameters.put("Parameter3", selectedSubject);
            parameters.put("Parameter4", selectedGrade);

            JRDataSource dataSource = new JRBeanCollectionDataSource(dataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);

            JRViewer viewer = new JRViewer(jasperPrint);

            JDialog reportDialog = new JDialog(this, "Attendance Report", true);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int padding = 50;
            int width = screenSize.width - (2 * padding);
            int height = screenSize.height - (2 * padding);
            reportDialog.setSize(width, height);
            reportDialog.setResizable(true);
            reportDialog.setLocationRelativeTo(null);
            reportDialog.setLayout(new BorderLayout());
            reportDialog.getContentPane().add(viewer, BorderLayout.CENTER);
            reportDialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

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
                    } else if (comp instanceof JComboBox<?> comboBox) {
                        if (!comboBox.isPopupVisible()) {
                            comboBox.showPopup();
                        } else {
                            Object selectedItem = comboBox.getSelectedItem();
                            comboBox.setSelectedItem(selectedItem);
                            comboBox.hidePopup();
                        }
                        return true;
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
    private javax.swing.JComboBox<String> gradeComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private com.toedter.calendar.JDateChooser jDateChooserFrom;
    private com.toedter.calendar.JDateChooser jDateChooserTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JComboBox<String> subjectComboBox;
    // End of variables declaration//GEN-END:variables

    private void refresh() {

        searchTextField.setText("");
        currentSearchText = "";
        jDateChooserFrom.setDate(null);
        jDateChooserTo.setDate(null);
        currentFromDate = null;
        currentToDate = null;
        subjectComboBox.setSelectedIndex(0);
        gradeComboBox.setSelectedIndex(0);
        currentSelectedSubject = null;
        selectedStudentRegId = null;
        jTable1.clearSelection();
        loadStudentAttendance();
        searchTextField.grabFocus();

    }
}
