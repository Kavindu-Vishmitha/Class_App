package gui;

import java.sql.ResultSet;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import model.MySql;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

public class registerPayment extends javax.swing.JDialog {

    student st;

    public registerPayment(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadRegisterPayment();
        paymentFormattedTextField.grabFocus();

        if (parent instanceof student) {
            st = (student) parent;

            String registerID = st.getRegisterID();
            String firstName = st.getFirstName();
            String lastName = st.getLastName();
            boolean isMale = st.isMaleSelected();
            boolean isFemale = st.isFemaleSelected();
            String mobile = st.getMobile();
            String email = st.getEmail();
            String line1 = st.getLine1();
            String line2 = st.getLine2();
            String city = st.getCity();
            String grade = st.getGrade();

            String payment = amountPayableFormattedTextField.getText();

            studentIDTextField.setText(registerID);
            studentNameTextField.setText(firstName + " " + lastName);

        }
    }

    private void loadRegisterPayment() {

        try {

            ResultSet resultSet = MySql.executeSearch("SELECT payment FROM register_payment WHERE id = 1");

            if (resultSet.next()) {
                double paymentValue = resultSet.getDouble("payment");
                java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
                String formattedPayment = df.format(paymentValue);
                amountPayableFormattedTextField.setText(formattedPayment);
            } else {
                amountPayableFormattedTextField.setText("0.00");
            }

            amountPayableFormattedTextField.grabFocus();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving payment");
        }
    }

    private void calculateBalance() {

        try {

            double total = Double.parseDouble(amountPayableFormattedTextField.getText().trim());
            double paid = Double.parseDouble(paymentFormattedTextField.getText().trim());
            double balance = paid - total;

            balanceFormattedTextField.setText(String.format("%.2f", balance));

        } catch (NumberFormatException e) {

            balanceFormattedTextField.setText("0.00");

        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        studentIDTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        studentNameTextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        amountPayableFormattedTextField = new javax.swing.JFormattedTextField();
        balanceFormattedTextField = new javax.swing.JFormattedTextField();
        paymentFormattedTextField = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Register Payment");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(22, 160, 133));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ID :");

        studentIDTextField.setEditable(false);
        studentIDTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        studentIDTextField.setForeground(new java.awt.Color(255, 255, 255));
        studentIDTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        studentIDTextField.setEnabled(false);
        studentIDTextField.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Name :");

        studentNameTextField.setEditable(false);
        studentNameTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        studentNameTextField.setForeground(new java.awt.Color(255, 255, 255));
        studentNameTextField.setEnabled(false);
        studentNameTextField.setMinimumSize(new java.awt.Dimension(80, 35));
        studentNameTextField.setPreferredSize(new java.awt.Dimension(90, 35));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(studentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(studentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(22, 160, 133));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Amount Payable :");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(59, 49, 136, 31));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Payment :");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(59, 99, 136, 31));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Balance :");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(59, 149, 136, 31));

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
        jPanel4.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(211, 217, 250, -1));

        amountPayableFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        amountPayableFormattedTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        amountPayableFormattedTextField.setEnabled(false);
        amountPayableFormattedTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        amountPayableFormattedTextField.setPreferredSize(new java.awt.Dimension(159, 30));
        jPanel4.add(amountPayableFormattedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 250, 30));

        balanceFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        balanceFormattedTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        balanceFormattedTextField.setEnabled(false);
        balanceFormattedTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        balanceFormattedTextField.setPreferredSize(new java.awt.Dimension(159, 30));
        jPanel4.add(balanceFormattedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 250, 30));

        paymentFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        paymentFormattedTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        paymentFormattedTextField.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        paymentFormattedTextField.setPreferredSize(new java.awt.Dimension(159, 30));
        paymentFormattedTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                paymentFormattedTextFieldKeyReleased(evt);
            }
        });
        jPanel4.add(paymentFormattedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 250, 30));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        try {

            String registerID = st.getRegisterID();
            String firstName = st.getFirstName();
            String lastName = st.getLastName();
            boolean isMale = st.isMaleSelected();
            boolean isFemale = st.isFemaleSelected();
            String mobile = st.getMobile();
            String email = st.getEmail();
            String line1 = st.getLine1();
            String line2 = st.getLine2();
            String city = st.getCity();
            String grade = st.getGrade();

            String regPaymentStr = amountPayableFormattedTextField.getText().trim();
            String typedPaymentStr = paymentFormattedTextField.getText().trim();

            ResultSet RS = MySql.executeSearch("SELECT reg_id FROM student WHERE reg_id = '" + registerID + "'");

            if (RS.next()) {
                JOptionPane.showMessageDialog(this, "A student already exists with this Register ID !", "Warning", JOptionPane.WARNING_MESSAGE);
                this.dispose();
                return;
            }

            int genderId = isMale ? 1 : 2;
            java.time.LocalDate date = java.time.LocalDate.now();

            ResultSet gradeRS = MySql.executeSearch("SELECT id FROM grade WHERE name = '" + grade + "'");

            int gradeId = 0;
            if (gradeRS.next()) {
                gradeId = gradeRS.getInt("id");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Grade selected");
                return;
            }

            if (typedPaymentStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter payment amount", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double requiredPaymentAmount;
            double typedPaymentAmount;

            try {

                requiredPaymentAmount = Double.parseDouble(regPaymentStr);
                typedPaymentAmount = Double.parseDouble(typedPaymentStr);

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(this, "Invalid payment amount entered", "Error", JOptionPane.ERROR_MESSAGE);
                return;

            }

            if (typedPaymentAmount < requiredPaymentAmount) {
                String formattedRequired = String.format("%.2f", requiredPaymentAmount);
                JOptionPane.showMessageDialog(this, "Entered payment is less than required: " + formattedRequired, "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            MySql.executeUpdate("INSERT INTO st_reg_payment (payment) VALUES ('" + regPaymentStr + "')");
            ResultSet newPaymentRS = MySql.executeSearch("SELECT LAST_INSERT_ID() AS id");

            int paymentId = 0;
            if (newPaymentRS.next()) {
                paymentId = newPaymentRS.getInt("id");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve inserted payment ID");
                return;
            }

            ResultSet cityRS = MySql.executeSearch("SELECT id FROM city WHERE name = '" + city + "'");
            int cityId = 0;
            if (cityRS.next()) {
                cityId = cityRS.getInt("id");
            } else {
                MySql.executeUpdate("INSERT INTO city (name) VALUES ('" + city + "')");
                ResultSet newCityRS = MySql.executeSearch("SELECT LAST_INSERT_ID() AS id");
                if (newCityRS.next()) {
                    cityId = newCityRS.getInt("id");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to retrieve inserted city ID");
                    return;
                }
            }

            MySql.executeUpdate("INSERT INTO student_address (line1, line2, city_id) VALUES ('" + line1 + "', '" + line2 + "', " + cityId + ")");
            ResultSet addressRS = MySql.executeSearch("SELECT LAST_INSERT_ID() AS id");

            int addressId = 0;
            if (addressRS.next()) {
                addressId = addressRS.getInt("id");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve inserted address ID");
                return;
            }

            MySql.executeUpdate("INSERT INTO student (reg_id, first_name, last_name, mobile, email, register_date, st_reg_payment_id, gender_id, student_address_id, grade_id) "
                    + "VALUES ('" + registerID + "', '" + firstName + "', '" + lastName + "', '" + mobile + "', '" + email + "', '" + date + "', " + paymentId + ", " + genderId + ", " + addressId + ", " + gradeId + ")");

            JOptionPane.showMessageDialog(this, "Student Registered Successfully !");

            home.getInstance().getStudentCount();
            home.getInstance().TodayRevenue();
            home.getInstance().MonthlyRevenue();
            home.getInstance().YearlyRevenue();
            st.loadRegisterStudent();
            st.clearForm();
            this.dispose();

            String path = "src//reports//register.jasper";

            HashMap<String, Object> params = new HashMap<>();
            params.put("Parameter1", registerID);
            params.put("Parameter2", firstName.trim() + " " + lastName.trim());

            params.put("Parameter3", amountPayableFormattedTextField.getText());
            params.put("Parameter4", paymentFormattedTextField.getText());
            params.put("Parameter5", balanceFormattedTextField.getText());

            JDialog reportDialog = new JDialog(this, "Registration Invoice", true);
            reportDialog.setSize(800, 600);
            reportDialog.setLocationRelativeTo(this);

            JasperPrint jasperPrint = JasperFillManager.fillReport(path, params, new JREmptyDataSource());
            JRViewer viewer = new JRViewer(jasperPrint);
            reportDialog.getContentPane().add(viewer);

            reportDialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration Failed: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void paymentFormattedTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentFormattedTextFieldKeyReleased
        calculateBalance();
    }//GEN-LAST:event_paymentFormattedTextFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField amountPayableFormattedTextField;
    private javax.swing.JFormattedTextField balanceFormattedTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JFormattedTextField paymentFormattedTextField;
    private javax.swing.JTextField studentIDTextField;
    private javax.swing.JTextField studentNameTextField;
    // End of variables declaration//GEN-END:variables

}
