package gui;

import java.sql.ResultSet;
import javax.swing.JOptionPane;
import model.MySql;

public class addCity extends javax.swing.JDialog {

    private student studentGui;
    private employee employeeGui;
    private teacher teacherGui;

    public addCity(java.awt.Dialog parent, boolean modal, student studentGui) {
        super(parent, modal);
        this.studentGui = studentGui;
        initComponents();
    }

    public addCity(java.awt.Dialog parent, boolean modal, employee employeeGui) {
        super(parent, modal);
        this.employeeGui = employeeGui;
        initComponents();
    }
    
     public addCity(java.awt.Dialog parent, boolean modal, teacher teacherGui) {
        super(parent, modal);
        this.teacherGui = teacherGui;
        initComponents();
    }

    public addCity(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cityTextField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("City");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(22, 160, 133));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("City :");

        cityTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        cityTextField.setPreferredSize(new java.awt.Dimension(90, 35));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(cityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

            String addCity = cityTextField.getText();

            if (addCity.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter city name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!addCity.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(this, "Please enter only letters for city name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResultSet rs1 = MySql.executeSearch("SELECT * FROM city WHERE name = '" + addCity + "'");
            if (rs1.next()) {
                JOptionPane.showMessageDialog(this, "City already exists", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            MySql.executeUpdate("INSERT INTO city(name) VALUES ('" + addCity + "')");

            JOptionPane.showMessageDialog(this, "City added successfully !", "Success", JOptionPane.INFORMATION_MESSAGE);

            if (studentGui != null) {
                studentGui.loadCity();
            } else if (employeeGui != null) {
                employeeGui.loadCity();
            } else if(teacherGui != null){
                teacherGui.loadCity();
            }

            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Adding City: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }//GEN-LAST:event_addButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField cityTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
