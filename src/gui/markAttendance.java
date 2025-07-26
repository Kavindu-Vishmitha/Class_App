package gui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import model.MySql;

public class markAttendance extends javax.swing.JDialog implements Runnable, ThreadFactory {

    private static HashMap<String, String> subjectMap = new HashMap<>();
    attendance at;

    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private Executor executor = Executors.newSingleThreadExecutor(this);
    private volatile boolean running = true;
    private String lastScannedQR = "";

    public markAttendance(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadSubject();
        markAbsentStudents();
        lastScannedQR = "";
        setupFullKeyboardSupport();

        if (parent instanceof attendance) {
            at = (attendance) parent;
        }

        subjectComboBox.addActionListener(e -> handleSubjectSelection());

        handleSubjectSelection();

    }

    public markAttendance() {
        super((java.awt.Dialog) null, true);
        initComponents();
        loadSubject();
        markAbsentStudents();
        lastScannedQR = "";

    }

    private void handleSubjectSelection() {

        String subject = String.valueOf(subjectComboBox.getSelectedItem());

        if (subject == null || subject.equals("Select Subject")) {
            stopCamera();

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Please select a subject before starting the scan");
            });
        } else {
            if (webcam == null || !webcam.isOpen()) {
                startCamera();
            }
        }
    }

    public int getSubjectIdByName(String subjectName) {

        int id = -1;

        try {

            ResultSet rs = MySql.executeSearch("SELECT id FROM subject WHERE name = '" + subjectName + "'");
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;

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

    private boolean isAttendanceAlreadyMarked(String regId, String date, int subjectId) {

        try {
            ResultSet rs = MySql.executeSearch("SELECT * FROM attendance WHERE student_reg_id = '" + regId + "' AND date = '" + date + "' AND subject_id = " + subjectId);
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void startCamera() {

        new Thread(() -> {
            try {
                List<Webcam> cams = Webcam.getWebcams();

                if (cams == null || cams.isEmpty()) {
                    System.out.println("No webcams found.");
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "No webcam detected !", "Error", JOptionPane.ERROR_MESSAGE);
                    });
                    return;
                } else {
                    System.out.println("Available webcams:");
                    for (Webcam cam : cams) {
                        System.out.println("- " + cam.getName());
                    }
                }

                webcam = Webcam.getDefault();
                if (webcam == null) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Webcam not accessible !", "Error", JOptionPane.ERROR_MESSAGE);
                    });
                    return;
                }

                webcam.setViewSize(new Dimension(640, 480));
                webcam.open();

                webcamPanel = new WebcamPanel(webcam);
                webcamPanel.setFPSDisplayed(true);
                webcamPanel.setPreferredSize(new Dimension(640, 480));

                SwingUtilities.invokeLater(() -> {
                    jPanel3.setLayout(new BorderLayout());
                    jPanel3.add(webcamPanel, BorderLayout.CENTER);
                    jPanel3.revalidate();
                    jPanel3.repaint();
                });

                executor.execute(this);

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Webcam initialization failed.\n" + e.getMessage(), "Camera Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

    }

    @Override
    public void run() {

        lastScannedQR = "";
        while (running) {
            try {
                Thread.sleep(100);
                BufferedImage image = null;
                if (webcam != null && webcam.isOpen()) {
                    image = webcam.getImage();
                }
                if (image == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                Result result = null;
                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    continue;
                }

                if (result != null) {
                    String qrText = result.getText();

                    if (!qrText.equals(lastScannedQR)) {
                        lastScannedQR = qrText;

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "QR Scanned:\n" + lastScannedQR);

                            String[] parts = qrText.split("\\|");
                            if (parts.length < 3) {
                                JOptionPane.showMessageDialog(this, "Invalid QR format !", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            String regId = parts[0].split(":")[1].trim();
                            String name = parts[1].split(":")[1].trim();

                            String selectedSubject = (String) subjectComboBox.getSelectedItem();
                            int subjectId = getSubjectIdByName(selectedSubject);
                            String date = java.time.LocalDate.now().toString();
                            String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                            int statusId = 1;

                            if (isAttendanceAlreadyMarked(regId, date, subjectId)) {
                                JOptionPane.showMessageDialog(this,
                                        "Attendance already marked for this subject today !",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            } else {

                                String grade = "";

                                try {

                                    ResultSet rs = MySql.executeSearch(
                                            "SELECT g.name FROM student s "
                                            + "JOIN grade g ON s.grade_id = g.id "
                                            + "WHERE s.reg_id = '" + regId + "'"
                                    );
                                    if (rs.next()) {
                                        grade = rs.getString("name");
                                    } else {
                                        JOptionPane.showMessageDialog(this, "Grade not found !", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(this, "Error fetching grade !", "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                jTextField3.setText(regId);
                                jTextField4.setText(name);
                                jTextField5.setText(grade);
                                jTextField1.setText(date);
                                jTextField2.setText(time);

                                insertAttendance(regId, date, time, subjectId, statusId);
                                JOptionPane.showMessageDialog(this, "Attendance marked successfully !");

                                clearAttendanceMark();
                            }

                            lastScannedQR = "";
                        });

                        Thread.sleep(1500);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "QR Scanner error:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
                break;
            }
        }
    }

    public void insertAttendance(String regId, String date, String time, int subjectId, int statusId) {

        try {

            MySql.executeUpdate("INSERT INTO attendance (date, time, student_reg_id, subject_id, attendance_status_id) "
                    + "VALUES ('" + date + "', '" + time + "', '" + regId + "', " + subjectId + ", " + statusId + ")");

            at.loadStudentAttendance();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to insert attendance.\n" + e.getMessage());
        }
    }

    public void markAbsentStudents() {

        try {
            LocalDate today = LocalDate.now();
            int statusId = 2;

            for (String subjectName : subjectMap.keySet()) {
                int subjectId = Integer.parseInt(subjectMap.get(subjectName));

                String classDatesQuery = "SELECT DISTINCT date FROM attendance WHERE subject_id = " + subjectId + " ORDER BY date";
                ResultSet classDatesRs = MySql.executeSearch(classDatesQuery);

                Set<LocalDate> classDates = new LinkedHashSet<>();
                while (classDatesRs.next()) {
                    LocalDate classDate = classDatesRs.getDate("date").toLocalDate();
                    if (classDate.isBefore(today)) {
                        classDates.add(classDate);
                    }
                }

                ResultSet paidRs = MySql.executeSearch("SELECT student_reg_id, MIN(payment_date) AS first_payment_date FROM st_subject_fee "
                        + "WHERE subject_id = " + subjectId + " GROUP BY student_reg_id");

                Map<String, LocalDate> paidStudents = new HashMap<>();

                while (paidRs.next()) {
                    String regId = paidRs.getString("student_reg_id");
                    LocalDate firstPaymentDate = paidRs.getDate("first_payment_date").toLocalDate();
                    paidStudents.put(regId, firstPaymentDate);
                }

                for (LocalDate classDate : classDates) {

                    for (Map.Entry<String, LocalDate> entry : paidStudents.entrySet()) {
                        String regId = entry.getKey();
                        LocalDate paidDate = entry.getValue();

                        if (!classDate.isBefore(paidDate)) {

                            ResultSet checkRs = MySql.executeSearch("SELECT 1 FROM attendance WHERE date = '" + classDate + "' "
                                    + "AND student_reg_id = '" + regId + "' AND subject_id = " + subjectId);

                            if (!checkRs.next()) {

                                MySql.executeUpdate("INSERT INTO attendance (date, time, student_reg_id, subject_id, attendance_status_id) "
                                        + "VALUES ('" + classDate + "', NULL, '" + regId + "', " + subjectId + ", " + statusId + ")");
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to insert missed absents.\n" + e.getMessage());
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "QR-Scanner-Thread");
        t.setDaemon(true);
        return t;
    }

    private void stopCamera() {

        try {
            if (webcam != null && webcam.isOpen()) {
                webcam.close();
            }
            if (webcamPanel != null) {
                webcamPanel.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dispose() {

        running = false;
        stopCamera();
        super.dispose();

    }

    private void setupFullKeyboardSupport() {

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");

        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(getRootPane());
                if (window != null && !(window instanceof home)) {
                    window.dispose();
                }
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

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
                    return e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN;
                }

                if (comp instanceof JComboBox<?> comboBox && comboBox.isPopupVisible()) {
                    return false;
                }

                return false;
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        subjectComboBox = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        jScrollPane1.setViewportView(jTextPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mark Attendance");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(22, 160, 133));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Date :");

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jTextField1.setEnabled(false);
        jTextField1.setPreferredSize(new java.awt.Dimension(90, 35));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Subject :");

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jTextField2.setEnabled(false);
        jTextField2.setPreferredSize(new java.awt.Dimension(90, 35));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Time :");

        subjectComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        subjectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(12, 12, 12)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(subjectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel3.setPreferredSize(new java.awt.Dimension(898, 462));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 898, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 462, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(22, 160, 133));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ID :");

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jTextField3.setEnabled(false);
        jTextField3.setPreferredSize(new java.awt.Dimension(90, 35));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Name :");

        jTextField4.setEditable(false);
        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jTextField4.setEnabled(false);
        jTextField4.setPreferredSize(new java.awt.Dimension(90, 35));

        jTextField5.setEditable(false);
        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jTextField5.setEnabled(false);
        jTextField5.setPreferredSize(new java.awt.Dimension(90, 35));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Grade :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JComboBox<String> subjectComboBox;
    // End of variables declaration//GEN-END:variables

    public void clearAttendanceMark() {

        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");

    }

}
