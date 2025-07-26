package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

/**
 *
 * @author 2003k
 */
public class gradeAndSubjectReport extends javax.swing.JDialog {

    private static HashMap<String, String> subjectMap = new HashMap<>();
    private static HashMap<String, String> gradeMap = new HashMap<>();
    private String selectedGradeId = "0";
    private String selectedSubjectId = "0";

    public gradeAndSubjectReport(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadSubject();
        loadGrade();
        loadStudents();
        setupFullKeyboardSupport();

        subjectComboBox.addActionListener(e -> {
            String selectedSubject = subjectComboBox.getSelectedItem().toString();
            selectedSubjectId = subjectMap.getOrDefault(selectedSubject, "0");

            loadStudentsByFilters(selectedGradeId, selectedSubjectId);
        });

        gradeComboBox.addActionListener(e -> {
            String selectedGrade = gradeComboBox.getSelectedItem().toString();
            selectedGradeId = gradeMap.getOrDefault(selectedGrade, "0");

            loadStudentsByFilters(selectedGradeId, selectedSubjectId);
        });

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/WC.png")));
    }

    private void loadStudentsByFilters(String gradeId, String subjectId) {
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {
            StringBuilder query = new StringBuilder(
                    "SELECT DISTINCT s.reg_id AS student_reg_id, s.first_name, s.last_name, "
                    + "g.name AS gender, s.mobile "
                    + "FROM student s "
                    + "JOIN gender g ON s.gender_id = g.id "
            );

            boolean hasGradeFilter = gradeId != null && !gradeId.equals("0");
            boolean hasSubjectFilter = subjectId != null && !subjectId.equals("0");

            if (hasSubjectFilter) {
                query.append("JOIN st_subject_fee ssf ON s.reg_id = ssf.student_reg_id ");
            }

            boolean whereAdded = false;
            if (hasGradeFilter) {
                query.append("WHERE s.grade_id = '").append(gradeId).append("' ");
                whereAdded = true;
            }

            if (hasSubjectFilter) {
                query.append(whereAdded ? "AND " : "WHERE ");
                query.append("ssf.subject_id = '").append(subjectId).append("' ");
            }

            query.append("ORDER BY s.first_name ASC");

            ResultSet rs = MySql.executeSearch(query.toString());

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_reg_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("mobile"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error filtering students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterStudentsByGrade(String gradeId) {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {
            ResultSet rs = MySql.executeSearch(
                    "SELECT s.reg_id AS student_reg_id, s.first_name, s.last_name, "
                    + "g.name AS gender, s.mobile "
                    + "FROM student s "
                    + "JOIN gender g ON s.gender_id = g.id "
                    + "WHERE s.grade_id = '" + gradeId + "' "
                    + "ORDER BY s.first_name ASC"
            );

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_reg_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("mobile"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error filtering by grade: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterStudentsBySubject(String subjectId) {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {
            ResultSet rs = MySql.executeSearch(
                    "SELECT s.reg_id AS student_reg_id, s.first_name, s.last_name, "
                    + "g.name AS gender, s.mobile "
                    + "FROM student s "
                    + "JOIN gender g ON s.gender_id = g.id "
                    + "JOIN st_subject_fee ssf ON s.reg_id = ssf.student_reg_id "
                    + "WHERE ssf.subject_id = '" + subjectId + "' "
                    + "GROUP BY s.reg_id ORDER BY s.first_name ASC"
            );

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_reg_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("mobile"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error filtering students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudents() {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {
            ResultSet rs = MySql.executeSearch(
                    "SELECT s.reg_id AS student_reg_id, s.first_name, s.last_name, "
                    + "g.name AS gender, s.mobile "
                    + "FROM student s "
                    + "JOIN gender g ON s.gender_id = g.id ORDER BY s.first_name ASC"
            );

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("student_reg_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("mobile"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel5 = new javax.swing.JLabel();
        gradeComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        subjectComboBox = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Grade & Subject Report");

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(22, 160, 133));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Grade :");

        gradeComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        gradeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Subject :");

        subjectComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        subjectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                .addGap(46, 46, 46)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(gradeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gradeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, -1));

        jPanel3.setBackground(new java.awt.Color(22, 160, 133));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "First Name", "Last Name", "Gender", "Mobile"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Print");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 884, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 920, 420));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
                row.put("COLUMN_3", model.getValueAt(i, 2));
                row.put("COLUMN_4", model.getValueAt(i, 3));
                row.put("COLUMN_5", model.getValueAt(i, 4));
                dataList.add(row);
            }

            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data found in the table to display in the report", "No Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedSubject = subjectComboBox.getSelectedItem() != null ? subjectComboBox.getSelectedItem().toString() : "not";
            String selectedGrade = gradeComboBox.getSelectedItem() != null ? gradeComboBox.getSelectedItem().toString() : "not";

            if (selectedSubject.equals("Select Subject")) {
                selectedSubject = "not";
            }

            if (selectedGrade.equals("Select Grade")) {
                selectedGrade = "not";
            }

            InputStream reportStream = getClass().getResourceAsStream("/reports/grade&subjectReport.jasper");
            if (reportStream == null) {
                JOptionPane.showMessageDialog(this, "Report file not found !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Parameter1", selectedGrade);
            parameters.put("Parameter2", selectedSubject);

            JRDataSource dataSource = new JRBeanCollectionDataSource(dataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);

            JRViewer viewer = new JRViewer(jasperPrint);
            JDialog reportDialog = new JDialog(this, "Grade & Subject Report", true);
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> subjectComboBox;
    // End of variables declaration//GEN-END:variables

    private void refresh() {

        subjectComboBox.setSelectedIndex(0);
        gradeComboBox.setSelectedIndex(0);
        jTable1.clearSelection();
        gradeComboBox.grabFocus();

    }
}
