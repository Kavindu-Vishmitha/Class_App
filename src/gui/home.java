package gui;

import com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;
import model.MySql;
import java.sql.ResultSet;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class home extends javax.swing.JFrame {

    private static home instance;

    public home(String firstName, String lastName) {

        instance = this;
        initComponents();
        employeeName.setText(firstName + " " + lastName);
        getEmployeeCount();
        getTeacherCount();
        getStudentCount();
        TodayRevenue();
        MonthlyRevenue();
        YearlyRevenue();
        setupFullKeyboardSupport();

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/company name.png")));
        dt();
        times();
    }

    public static home getInstance() {
        return instance;
    }

    public void dt() {

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dd = sdf.format(d);
        jLabel21.setText(dd);

    }

    Timer t;
    SimpleDateFormat st;

    public void times() {

        t = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Date dt = new Date();
                st = new SimpleDateFormat("hh:mm:ss");

                String tt = st.format(dt);
                jLabel20.setText(tt);
            }
        });
        t.start();
    }

    public int getEmployeeCount() {

        int count = 0;
        try {
            ResultSet rs = MySql.executeSearch("SELECT COUNT(*) AS emp_count FROM employee");
            if (rs.next()) {
                count = rs.getInt("emp_count");
                employee.setText(String.valueOf(count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getTeacherCount() {

        int count = 0;
        try {
            ResultSet rs = MySql.executeSearch("SELECT COUNT(*) AS t_count FROM teacher");
            if (rs.next()) {
                count = rs.getInt("t_count");
                teacher.setText(String.valueOf(count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public void getStudentCount() {
        try {
            ResultSet rs = MySql.executeSearch("SELECT COUNT(*) AS student_count FROM student");
            if (rs.next()) {
                int count = rs.getInt("student_count");
                studentL.setText(String.valueOf(count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TodayRevenue() {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT "
                    + "  IFNULL((SELECT SUM(sr.payment) "
                    + "          FROM student s "
                    + "          JOIN st_reg_payment sr ON s.st_reg_payment_id = sr.id "
                    + "          WHERE s.register_date = CURDATE()), 0) AS active_reg_total, "
                    + "  IFNULL((SELECT SUM(irp.inactive_payment) "
                    + "          FROM inactive_st_reg_payment irp "
                    + "          WHERE irp.reg_payment_date = CURDATE()), 0) AS inactive_reg_total, "
                    + "  IFNULL((SELECT SUM(fee) "
                    + "          FROM st_subject_fee "
                    + "          WHERE payment_date = CURDATE()), 0) AS active_sub_total, "
                    + "  IFNULL((SELECT SUM(fee) "
                    + "          FROM inactive_st_subject_fee "
                    + "          WHERE payment_date = CURDATE()), 0) AS inactive_sub_total"
            );

            if (rs.next()) {
                double activeReg = rs.getDouble("active_reg_total");
                double inactiveReg = rs.getDouble("inactive_reg_total");
                double activeSub = rs.getDouble("active_sub_total");
                double inactiveSub = rs.getDouble("inactive_sub_total");

                double totalRevenue = activeReg + inactiveReg + activeSub + inactiveSub;
                daily.setText(String.format("%.2f", totalRevenue));
            } else {
                daily.setText("0.00");
            }

        } catch (Exception e) {
            e.printStackTrace();
            daily.setText("Error");
        }
    }

    public void MonthlyRevenue() {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT "
                    + "  IFNULL((SELECT SUM(sr.payment) "
                    + "          FROM student s "
                    + "          JOIN st_reg_payment sr ON s.st_reg_payment_id = sr.id "
                    + "          WHERE MONTH(s.register_date) = MONTH(CURDATE()) "
                    + "            AND YEAR(s.register_date) = YEAR(CURDATE())), 0) AS active_reg_total, "
                    + "  IFNULL((SELECT SUM(irp.inactive_payment) "
                    + "          FROM inactive_st_reg_payment irp "
                    + "          WHERE MONTH(irp.reg_payment_date) = MONTH(CURDATE()) "
                    + "            AND YEAR(irp.reg_payment_date) = YEAR(CURDATE())), 0) AS inactive_reg_total, "
                    + "  IFNULL((SELECT SUM(fee) "
                    + "          FROM st_subject_fee "
                    + "          WHERE MONTH(payment_date) = MONTH(CURDATE()) "
                    + "            AND YEAR(payment_date) = YEAR(CURDATE())), 0) AS active_sub_total, "
                    + "  IFNULL((SELECT SUM(fee) "
                    + "          FROM inactive_st_subject_fee "
                    + "          WHERE MONTH(payment_date) = MONTH(CURDATE()) "
                    + "            AND YEAR(payment_date) = YEAR(CURDATE())), 0) AS inactive_sub_total"
            );

            if (rs.next()) {
                double activeReg = rs.getDouble("active_reg_total");
                double inactiveReg = rs.getDouble("inactive_reg_total");
                double activeSub = rs.getDouble("active_sub_total");
                double inactiveSub = rs.getDouble("inactive_sub_total");

                double totalRevenue = activeReg + inactiveReg + activeSub + inactiveSub;
                monthly.setText(String.format("%.2f", totalRevenue));
            } else {
                monthly.setText("0.00");
            }

        } catch (Exception e) {
            e.printStackTrace();
            monthly.setText("Error");
        }
    }

    public void YearlyRevenue() {

        try {

            ResultSet rs = MySql.executeSearch(
                    "SELECT "
                    + "  IFNULL((SELECT SUM(sr.payment) "
                    + "          FROM student s "
                    + "          JOIN st_reg_payment sr ON s.st_reg_payment_id = sr.id "
                    + "          WHERE YEAR(s.register_date) = YEAR(CURDATE())), 0) AS active_reg_total, "
                    + "  IFNULL((SELECT SUM(irp.inactive_payment) "
                    + "          FROM inactive_st_reg_payment irp "
                    + "          WHERE YEAR(irp.reg_payment_date) = YEAR(CURDATE())), 0) AS inactive_reg_total, "
                    + "  IFNULL((SELECT SUM(fee) "
                    + "          FROM st_subject_fee "
                    + "          WHERE YEAR(payment_date) = YEAR(CURDATE())), 0) AS active_sub_total, "
                    + "  IFNULL((SELECT SUM(fee) "
                    + "          FROM inactive_st_subject_fee "
                    + "          WHERE YEAR(payment_date) = YEAR(CURDATE())), 0) AS inactive_sub_total"
            );

            if (rs.next()) {
                double activeReg = rs.getDouble("active_reg_total");
                double inactiveReg = rs.getDouble("inactive_reg_total");
                double activeSub = rs.getDouble("active_sub_total");
                double inactiveSub = rs.getDouble("inactive_sub_total");

                double totalRevenue = activeReg + inactiveReg + activeSub + inactiveSub;
                yearly.setText(String.format("%.2f", totalRevenue));
            } else {
                yearly.setText("0.00");
            }

        } catch (Exception e) {
            e.printStackTrace();
            yearly.setText("Error");
        }
    }

    private void setupFullKeyboardSupport() {

        JRootPane rootPane = getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");

        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(rootPane);
                if (window != null) {
                    window.dispose();
                }

                try {
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                login loginWindow = new login();
                SwingUtilities.updateComponentTreeUI(loginWindow);
                loginWindow.setLocationRelativeTo(null);
                loginWindow.setVisible(true);
            }
        });

        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "PRESS_ENTER");

        rootPane.getActionMap().put("PRESS_ENTER", new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (comp instanceof JButton button) {
                    button.doClick();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "FOCUS_UP");

        rootPane.getActionMap().put("FOCUS_UP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().transferFocusBackward();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "FOCUS_DOWN");

        rootPane.getActionMap().put("FOCUS_DOWN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().transferFocus();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        employeeName = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tab1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tab2 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tab3 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tab4 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tab5 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tab6 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tab7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tab8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        employee = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        teacher = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        studentL = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        daily = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        monthly = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        yearly = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Home");
        setLocationByPlatform(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(22, 160, 133));
        jPanel5.setPreferredSize(new java.awt.Dimension(1024, 50));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/admin.png"))); // NOI18N
        jPanel5.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 50));

        employeeName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        employeeName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        employeeName.setText("Kavindu Vishmitha");
        jPanel5.add(employeeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 170, 30));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("Time :");
        jPanel5.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 20, -1, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("hh:mm:ss");
        jPanel5.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 20, 100, -1));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("yyyy-mm-dd");
        jPanel5.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 20, 120, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setText("Date :");
        jPanel5.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 20, -1, -1));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1010, 60));

        jPanel1.setBackground(new java.awt.Color(22, 160, 133));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 550));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tab1.setBackground(new java.awt.Color(0, 0, 0));
        tab1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab1.setPreferredSize(new java.awt.Dimension(200, 100));
        tab1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/home.png"))); // NOI18N
        tab1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Home");
        tab1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 90, 30));

        jPanel1.add(tab1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 190, 50));

        tab2.setBackground(new java.awt.Color(22, 160, 133));
        tab2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab2.setPreferredSize(new java.awt.Dimension(200, 100));
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tab2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tab2MouseExited(evt);
            }
        });
        tab2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(255, 51, 0));
        jPanel13.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        tab2.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 190, 50));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/employee.png"))); // NOI18N
        tab2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Employee");
        tab2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 100, 30));

        jPanel1.add(tab2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 190, 50));

        tab3.setBackground(new java.awt.Color(22, 160, 133));
        tab3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab3.setPreferredSize(new java.awt.Dimension(200, 100));
        tab3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tab3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tab3MouseExited(evt);
            }
        });
        tab3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBackground(new java.awt.Color(204, 255, 255));
        jPanel15.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel14.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 190, 50));

        jPanel16.setBackground(new java.awt.Color(255, 204, 204));
        jPanel16.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel14.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, 190, 50));

        tab3.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 830, 540));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/teacher.png"))); // NOI18N
        tab3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Teacher");
        tab3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 100, 30));

        jPanel1.add(tab3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 190, 50));

        tab4.setBackground(new java.awt.Color(22, 160, 133));
        tab4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab4.setPreferredSize(new java.awt.Dimension(200, 100));
        tab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tab4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tab4MouseExited(evt);
            }
        });
        tab4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel18.setBackground(new java.awt.Color(255, 204, 204));
        jPanel18.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel17.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 190, 50));

        jPanel19.setBackground(new java.awt.Color(51, 51, 51));
        jPanel19.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel17.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 190, 50));

        tab4.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 830, 540));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/student.png"))); // NOI18N
        tab4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Student");
        tab4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 100, 30));

        jPanel1.add(tab4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 190, 50));

        tab5.setBackground(new java.awt.Color(22, 160, 133));
        tab5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab5.setPreferredSize(new java.awt.Dimension(200, 100));
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tab5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tab5MouseExited(evt);
            }
        });
        tab5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel20.setBackground(new java.awt.Color(255, 204, 204));
        jPanel20.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        tab5.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 270, 190, 50));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/qr.png"))); // NOI18N
        tab5.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("QR");
        tab5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 100, 30));

        jPanel1.add(tab5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 190, 50));

        tab6.setBackground(new java.awt.Color(22, 160, 133));
        tab6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab6.setPreferredSize(new java.awt.Dimension(200, 100));
        tab6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tab6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tab6MouseExited(evt);
            }
        });
        tab6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel22.setBackground(new java.awt.Color(0, 153, 153));
        jPanel22.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel21.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 190, 50));

        jPanel23.setBackground(new java.awt.Color(102, 0, 255));
        jPanel23.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel21.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 190, 50));

        jPanel24.setBackground(new java.awt.Color(153, 255, 204));
        jPanel24.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel21.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 310, 190, 40));

        tab6.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 830, 540));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/attendance.png"))); // NOI18N
        tab6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Attendance");
        tab6.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 110, 30));

        jPanel1.add(tab6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 320, 190, 50));

        tab7.setBackground(new java.awt.Color(22, 160, 133));
        tab7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab7.setPreferredSize(new java.awt.Dimension(200, 100));
        tab7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab7MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tab7MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tab7MouseExited(evt);
            }
        });
        tab7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salary.png"))); // NOI18N
        tab7.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Payment");
        tab7.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 110, 30));

        jPanel1.add(tab7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 190, 50));

        tab8.setBackground(new java.awt.Color(22, 160, 133));
        tab8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tab8.setPreferredSize(new java.awt.Dimension(200, 100));
        tab8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tab8MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tab8MouseExited(evt);
            }
        });
        tab8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/about.png"))); // NOI18N
        tab8.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 40, -1));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("About");
        tab8.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 110, 30));

        jPanel1.add(tab8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 190, 50));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 190, 510));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(22, 160, 133));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/E.png"))); // NOI18N
        jPanel3.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 50, 50));

        employee.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        employee.setForeground(new java.awt.Color(255, 255, 255));
        employee.setText("10");
        jPanel3.add(employee, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 90, 30));

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jLabel23.setText("sfsf");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 240, 110));

        jPanel4.setBackground(new java.awt.Color(0, 204, 204));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/T.png"))); // NOI18N
        jPanel4.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 50, 50));

        teacher.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        teacher.setForeground(new java.awt.Color(255, 255, 255));
        teacher.setText("20");
        jPanel4.add(teacher, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 90, 30));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel4.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 20, 240, 110));

        jPanel6.setBackground(new java.awt.Color(255, 0, 204));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Attendance");
        jPanel6.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 160, 30));

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Report");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jPanel6.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 80, -1));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel6.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 240, 110));

        jPanel7.setBackground(new java.awt.Color(102, 255, 102));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/S.png"))); // NOI18N
        jPanel7.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 50, 50));

        studentL.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        studentL.setForeground(new java.awt.Color(255, 255, 255));
        studentL.setText("10000");
        jPanel7.add(studentL, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 110, 30));

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel7.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, 240, 110));

        jPanel8.setBackground(new java.awt.Color(0, 255, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Grade , Subject");
        jPanel8.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 180, 30));

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Report");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 80, -1));

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel8.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 190, 240, 110));

        jPanel9.setBackground(new java.awt.Color(255, 0, 0));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("Daily ");
        jPanel9.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 70, 30));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel45.setText("Rs :");
        jPanel9.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 40, 30));

        daily.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        daily.setText("10000");
        jPanel9.add(daily, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 130, 30));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel9.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 240, 110));

        jPanel10.setBackground(new java.awt.Color(0, 153, 153));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("Payment");
        jPanel10.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 160, 30));

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Report");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 80, -1));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel10.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 190, 240, 110));

        jPanel11.setBackground(new java.awt.Color(255, 255, 51));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("Monthly ");
        jPanel11.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 170, 30));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel47.setText("Rs :");
        jPanel11.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 40, 30));

        monthly.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        monthly.setText("2,00,000");
        jPanel11.add(monthly, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 170, 30));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel11.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 360, 240, 110));

        jPanel12.setBackground(new java.awt.Color(255, 153, 153));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 255, 255));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("Yearly ");
        jPanel12.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, 30));

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel49.setText("Rs :");
        jPanel12.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 40, 30));

        yearly.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        yearly.setText("5,000,000");
        jPanel12.add(yearly, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 50, 160, 30));

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/background.jpeg"))); // NOI18N
        jPanel12.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 110));

        jPanel2.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 360, 240, 110));

        jSeparator2.setBackground(new java.awt.Color(22, 160, 133));
        jSeparator2.setForeground(new java.awt.Color(22, 160, 133));
        jSeparator2.setPreferredSize(new java.awt.Dimension(50, 10));
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 780, 10));

        jSeparator3.setBackground(new java.awt.Color(22, 160, 133));
        jSeparator3.setForeground(new java.awt.Color(22, 160, 133));
        jSeparator3.setPreferredSize(new java.awt.Dimension(50, 10));
        jPanel2.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 780, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 820, 510));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tab2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab2MouseClicked
        FlatCobalt2IJTheme.setup();
        employee hm = new employee(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_tab2MouseClicked

    private void tab3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab3MouseClicked
        FlatCobalt2IJTheme.setup();
        teacher hm = new teacher(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_tab3MouseClicked

    private void tab4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab4MouseClicked
        FlatCobalt2IJTheme.setup();
        student hm = new student(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_tab4MouseClicked

    private void tab5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab5MouseClicked
        FlatCobalt2IJTheme.setup();
        qr hm = new qr(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_tab5MouseClicked

    private void tab6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab6MouseClicked
        FlatCobalt2IJTheme.setup();
        attendance hm = new attendance(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_tab6MouseClicked

    private void tab7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab7MouseClicked
        FlatCobalt2IJTheme.setup();
        payment hm = new payment(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_tab7MouseClicked

    private void tab8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab8MouseClicked
        FlatCobalt2IJTheme.setup();
        about hm = new about(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_tab8MouseClicked

    private void tab2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab2MouseEntered
        tab2.setBackground(new Color(0, 0, 0));
    }//GEN-LAST:event_tab2MouseEntered

    private void tab2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab2MouseExited
        tab2.setBackground(new Color(22, 160, 133));
    }//GEN-LAST:event_tab2MouseExited

    private void tab3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab3MouseEntered
        tab3.setBackground(new Color(0, 0, 0));
    }//GEN-LAST:event_tab3MouseEntered

    private void tab3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab3MouseExited
        tab3.setBackground(new Color(22, 160, 133));
    }//GEN-LAST:event_tab3MouseExited

    private void tab4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab4MouseEntered
        tab4.setBackground(new Color(0, 0, 0));
    }//GEN-LAST:event_tab4MouseEntered

    private void tab4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab4MouseExited
        tab4.setBackground(new Color(22, 160, 133));
    }//GEN-LAST:event_tab4MouseExited

    private void tab5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab5MouseEntered
        tab5.setBackground(new Color(0, 0, 0));
    }//GEN-LAST:event_tab5MouseEntered

    private void tab5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab5MouseExited
        tab5.setBackground(new Color(22, 160, 133));
    }//GEN-LAST:event_tab5MouseExited

    private void tab6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab6MouseEntered
        tab6.setBackground(new Color(0, 0, 0));
    }//GEN-LAST:event_tab6MouseEntered

    private void tab6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab6MouseExited
        tab6.setBackground(new Color(22, 160, 133));
    }//GEN-LAST:event_tab6MouseExited

    private void tab7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab7MouseEntered
        tab7.setBackground(new Color(0, 0, 0));
    }//GEN-LAST:event_tab7MouseEntered

    private void tab7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab7MouseExited
        tab7.setBackground(new Color(22, 160, 133));
    }//GEN-LAST:event_tab7MouseExited

    private void tab8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab8MouseEntered
        tab8.setBackground(new Color(0, 0, 0));
    }//GEN-LAST:event_tab8MouseEntered

    private void tab8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab8MouseExited
        tab8.setBackground(new Color(22, 160, 133));
    }//GEN-LAST:event_tab8MouseExited

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        FlatCobalt2IJTheme.setup();
        attendanceReport hm = new attendanceReport(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        FlatCobalt2IJTheme.setup();
        gradeAndSubjectReport hm = new gradeAndSubjectReport(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        FlatCobalt2IJTheme.setup();
        paymentReport hm = new paymentReport(this, true);
        hm.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel daily;
    private javax.swing.JLabel employee;
    private javax.swing.JLabel employeeName;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel monthly;
    private javax.swing.JLabel studentL;
    private javax.swing.JPanel tab1;
    private javax.swing.JPanel tab2;
    private javax.swing.JPanel tab3;
    private javax.swing.JPanel tab4;
    private javax.swing.JPanel tab5;
    private javax.swing.JPanel tab6;
    private javax.swing.JPanel tab7;
    private javax.swing.JPanel tab8;
    private javax.swing.JLabel teacher;
    private javax.swing.JLabel yearly;
    // End of variables declaration//GEN-END:variables
}
