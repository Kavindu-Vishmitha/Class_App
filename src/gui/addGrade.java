package gui;

import java.sql.ResultSet;
import javax.swing.JOptionPane;
import model.MySql;

public class addGrade extends javax.swing.JDialog {

    private student studentGui;

    public addGrade(java.awt.Dialog parent, boolean modal, student studentGui) {
        super(parent, modal);
        this.studentGui = studentGui;
        initComponents();
    }

    public addGrade(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        gradeTextField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Grade");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(22, 160, 133));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Grade :");

        gradeTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        gradeTextField.setPreferredSize(new java.awt.Dimension(90, 35));

        addButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setText("ADD");
        addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new java.awt.Dimension(94, 35));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gradeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gradeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(70, Short.MAX_VALUE))
        );

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

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

        try {

            String addGrade = gradeTextField.getText();

            if (addGrade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter grade number", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!addGrade.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Grade must be a number", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                ResultSet rs1 = MySql.executeSearch("SELECT * FROM grade WHERE name = '" + addGrade + "'");
                if (rs1.next()) {
                    JOptionPane.showMessageDialog(this, "Grade number already exists", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int gradeValue = Integer.parseInt(addGrade);
                if (gradeValue < 1 || gradeValue > 13) {
                    JOptionPane.showMessageDialog(this, "Grade must be between 1 and 13", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                MySql.executeUpdate("INSERT INTO grade(name) VALUES ('" + addGrade + "')");

                JOptionPane.showMessageDialog(this, "Grade added successfully !", "Success", JOptionPane.INFORMATION_MESSAGE);

                if (studentGui != null) {
                    studentGui.loadgrade();
                }
                this.dispose();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error Adding Grade:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }//GEN-LAST:event_addButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField gradeTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
