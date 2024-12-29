

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Afifah
 */
public class aplikasiCafe extends javax.swing.JFrame {
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private String username;
    JasperReport jr;
    JasperPrint jp;
    Map param = new HashMap();
    JasperDesign jd;

    public aplikasiCafe() {
        con = koneksi2.getConnection();
        setTitle("Aplikasi Cafe Lune et Sucre");
        initComponents();
        load_menu();
        loadMenuCombo();
        load_transaksi();
        initListeners();
        load_karyawan();
    }
    
    public void setUsername(String username) {
        this.username = username;
        setWelcomeMessage(); // Tampilkan pesan setelah username diatur
    }
    private void setWelcomeMessage() {
        lblSelamat.setText("Selamat Datang, " + username);
    }
    
    // panel menu
    private void load_menu() {
        Object header[] = {"ID MENU", "NAMA MENU", "HARGA MENU", "STATUS MENU"};
        DefaultTableModel data = new DefaultTableModel(null, header);
        tabelMenu.setModel(data);
        tabelMenu2.setModel(data);
        String sql = "SELECT id_menu, nama_menu, harga, status FROM menu";
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                String k1 = rs.getString(1);
                String k2 = rs.getString(2);
                String k3 = rs.getString(3);
                String k4 = rs.getString(4);
                String k[] = {k1, k2, k3, k4};
                data.addRow(k);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void clear_menu() {
        txtIDmenu.setText(""); 
        txtNamamenu.setText("");
        txtHarga.setText("");
        txtStatus.setSelectedIndex(0);
    }
    public void input_menu(){
        if (txtNamamenu.getText().trim().isEmpty() ||
            txtHarga.getText().trim().isEmpty() ||
            txtStatus.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(
                    null,
                    "Semua Data Harus Diisi!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
                );
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin menyimpan menu?",
            "Konfirmasi Simpan Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "INSERT INTO menu (id_menu, nama_menu, harga, status) VALUES ('" +
                        txtIDmenu.getText() + "','" +
                        txtNamamenu.getText() + "','" +
                        txtHarga.getText()+ "','" +
                        txtStatus.getSelectedItem() + "')";

                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Menu Berhasil Disimpan");
                clear_menu();
                load_menu();
            } catch (java.sql.SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi disimpan.");
        }
    }
    
    //panel transaksi
    private void load_transaksi() {
        Object header[] = {"ID TRANSAKSI", "ID MENU", "NAMA PELANGGAN", "JUMLAH", "TOTAL BAYAR", "TANGGAL TRANSAKSI"};
        DefaultTableModel data = new DefaultTableModel(null, header);
        tabelTransaksi.setModel(data);
        tabelTransaksi2.setModel(data);
        String sql = "SELECT id_transaksi, id_menu, nama_pelanggan, jumlah, total_bayar, tanggal_transaksi FROM transaksi";
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                String k1 = rs.getString(1);
                String k2 = rs.getString(2);
                String k3 = rs.getString(3);
                String k4 = rs.getString(4);
                String k5 = rs.getString(5);
                String k6 = rs.getString(6);
                String k[] = {k1, k2, k3, k4, k5, k6};
                data.addRow(k);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void clear_transaksi() {
        txtTanggaltransaksi.setDate(null);
        txtIDtransaksi.setText("");
        txtNamapelanggan.setText("");
        cmbIDmenu.setSelectedIndex(-1);
        txtJumlah.setText(""); 
    }
    private void loadMenuCombo() {
        try {
            String sql = "SELECT id_menu, nama_menu, harga FROM menu WHERE status = 'Tersedia'";
            st = con.createStatement();
            rs = st.executeQuery(sql);
            cmbIDmenu.removeAllItems();
            while (rs.next()) {
                String idMenu = rs.getString("id_menu");
                String namaMenu = rs.getString("nama_menu");
                int harga = rs.getInt("harga");
                String item = idMenu + " - " + namaMenu + " - Rp " + harga;
                cmbIDmenu.addItem(item);
            }
            cmbIDmenu.setSelectedIndex(-1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading menu: " + e.getMessage());
        }
    }
    private void initListeners() {
        // Listener untuk ComboBox txtIDMenu
        cmbIDmenu.addActionListener(e -> menghitungTotal());

        // Listener untuk txtJumlah
        txtJumlah.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                menghitungTotal();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                menghitungTotal();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                menghitungTotal();
            }
        });
    }
    private void menghitungTotal() {
        try {
            if (cmbIDmenu.getSelectedItem() != null && !txtJumlah.getText().trim().isEmpty()) {
                String selectedItem = cmbIDmenu.getSelectedItem().toString();
                String[] menuData = selectedItem.split(" - ");
                String hargaStr = menuData[2].replace("Rp ", "").replace(",", "").trim();

                int harga = Integer.parseInt(hargaStr);
                int jumlah = Integer.parseInt(txtJumlah.getText().trim());
                int totalBayar = harga * jumlah;
                txtTotal.setText("Rp " + String.valueOf(totalBayar));
                txtHarga.setText(String.valueOf(totalBayar));
            } else {
                txtTotal.setText("0");
                txtHarga.setText("");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Jumlah harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            txtJumlah.setText("");
            txtTotal.setText("");
        }
    }
    public void input_transaksi() {
        if (cmbIDmenu.getSelectedItem() == null || 
            txtNamapelanggan.getText().trim().isEmpty() || 
            txtJumlah.getText().trim().isEmpty() || 
            txtTanggaltransaksi.getDate() == null) {
            JOptionPane.showMessageDialog(
                null,
                "Semua Data Harus Diisi!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin menyimpan transaksi?",
            "Konfirmasi Simpan Transaksi",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String selectedItem = cmbIDmenu.getSelectedItem().toString();
                String[] menuData = selectedItem.split(" - ");
                String idMenu = menuData[0];

                String sql = "INSERT INTO transaksi (id_transaksi, id_menu, nama_pelanggan, jumlah, tanggal_transaksi) VALUES ('" +
                        txtIDtransaksi.getText() + "','" +
                        idMenu + "','" +
                        txtNamapelanggan.getText() + "','" +
                        txtJumlah.getText() + "','" +
                        new java.sql.Date(txtTanggaltransaksi.getDate().getTime()) + "')";

                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Transaksi Berhasil Disimpan");
                clear_transaksi();
                load_transaksi();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi disimpan.");
        }
    }
    public void input_transaksi2() {
    if (cmbIDmenu.getSelectedItem() == null || 
        txtNamapelanggan.getText().trim().isEmpty() || 
        txtJumlah.getText().trim().isEmpty() || 
        txtTanggaltransaksi.getDate() == null) {
        JOptionPane.showMessageDialog(
            null,
            "Semua Data Harus Diisi!",
            "Peringatan",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }
        try {
            String selectedItem = cmbIDmenu.getSelectedItem().toString();
            String[] menuData = selectedItem.split(" - ");
            String idMenu = menuData[0];

            String sql = "INSERT INTO transaksi (id_transaksi, id_menu, nama_pelanggan, jumlah, tanggal_transaksi) VALUES ('" +
                        txtIDtransaksi.getText() + "','" +
                        idMenu + "','" +
                        txtNamapelanggan.getText() + "','" +
                        txtJumlah.getText() + "','" +
                        new java.sql.Date(txtTanggaltransaksi.getDate().getTime()) + "')";

            st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Transaksi Berhasil Disimpan");
                load_transaksi();
                int a = Integer.parseInt(txtIDtransaksi.getText().trim());
                int c = a + 1;
                txtIDtransaksi.setText(String.valueOf(c));
                cmbIDmenu.setSelectedIndex(-1);
                txtJumlah.setText(""); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //panel karyawan
    private void load_karyawan() {
    Object header[] = {"ID KARYAWAN", "NAMA LENGKAP", "JK", "EMAIL", "JABATAN", "TANGGAL MASUK"};
    DefaultTableModel data = new DefaultTableModel(null, header);
    tabelKaryawan.setModel(data);
    tabelKaryawan2.setModel(data);
    String sql = "SELECT id_karyawan, nama_lengkap, jenis_kelamin, email, jabatan, tanggal_masuk FROM karyawan";
    try {
        st = con.createStatement();
        rs = st.executeQuery(sql);
        while (rs.next()) {
            String k1 = rs.getString(1);
            String k2 = rs.getString(2);
            String k3 = rs.getString(3);
            String k4 = rs.getString(4);
            String k5 = rs.getString(5);
            String k6 = rs.getString(6);
            String k[] = {k1, k2, k3, k4, k5, k6};
            data.addRow(k);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        e.printStackTrace();
    }
}
    private void clear_karyawan() {
        txtIDkaryawan.setText("");
        txtNamakaryawan.setText("");
        bgJK.clearSelection();
        txtEmail.setText("");
        cmbJabatan.setSelectedIndex(0);
        txtTanggalmasuk.setDate(null);
    }
    public void input_karyawan() {
        if (txtIDkaryawan.getText().trim().isEmpty() || 
            txtNamakaryawan.getText().trim().isEmpty() || 
            (!rbLaki.isSelected() && !rbPr.isSelected()) || 
            txtEmail.getText().trim().isEmpty() || 
            cmbJabatan.getSelectedItem() == null || 
            txtTanggalmasuk.getDate() == null) {
            JOptionPane.showMessageDialog(
                null,
                "Semua Data Harus Diisi!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin menyimpan data karyawan?",
            "Konfirmasi Simpan Data",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String jk = rbLaki.isSelected() ? "L" : "P";

                String sql = "INSERT INTO karyawan (id_karyawan, nama_lengkap, jenis_kelamin, email, jabatan, tanggal_masuk) VALUES ('" +
                        txtIDkaryawan.getText() + "','" +
                        txtNamakaryawan.getText() + "','" +
                        jk + "','" +
                        txtEmail.getText() + "','" +
                        cmbJabatan.getSelectedItem().toString() + "','" +
                        new java.sql.Date(txtTanggalmasuk.getDate().getTime()) + "')";

                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Karyawan Berhasil Disimpan");
                clear_karyawan();
                load_karyawan();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi disimpan.");
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

        bgJK = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panelNav = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnHome = new javax.swing.JPanel();
        lblHome = new javax.swing.JLabel();
        btnMenu = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnTransaksi = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btnLaporan = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btnKaryawan = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        panelUtama = new javax.swing.JPanel();
        panelHome = new javax.swing.JPanel();
        lblSelamat = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();
        panelMenu = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtIDmenu = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtNamamenu = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JComboBox<>();
        btnSimpanmenu = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelMenu = new javax.swing.JTable();
        btnEditmenu = new javax.swing.JButton();
        btnEdithapus = new javax.swing.JButton();
        btnCarimenu = new javax.swing.JButton();
        txtCarimenu = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        panelTransaksi = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtIDtransaksi = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        btnSimpanTransaksi = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelTransaksi = new javax.swing.JTable();
        btnEditTransaksi = new javax.swing.JButton();
        btnHapustransaksi = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        btnCetaktransaksi = new javax.swing.JButton();
        cmbIDmenu = new javax.swing.JComboBox<>();
        btnLihatMenu = new javax.swing.JButton();
        txtTotal = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtNamapelanggan = new javax.swing.JTextField();
        txtTanggaltransaksi = new com.toedter.calendar.JDateChooser();
        btnTambah = new javax.swing.JButton();
        txtCaritransaksi = new javax.swing.JTextField();
        btnCaritransaksi = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        panelLaporan = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelMenu2 = new javax.swing.JTable();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabelTransaksi2 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabelKaryawan2 = new javax.swing.JTable();
        btnCetakmenu = new javax.swing.JButton();
        btnCetaktransaksi2 = new javax.swing.JButton();
        btnCetakkaryawan = new javax.swing.JButton();
        panelKaryawan = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtIDkaryawan = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtNamakaryawan = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnSimpankaryawan = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelKaryawan = new javax.swing.JTable();
        btnEditkaryawan = new javax.swing.JButton();
        btnHapuskaryawan = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        rbLaki = new javax.swing.JRadioButton();
        rbPr = new javax.swing.JRadioButton();
        jLabel27 = new javax.swing.JLabel();
        cmbJabatan = new javax.swing.JComboBox<>();
        txtTanggalmasuk = new com.toedter.calendar.JDateChooser();
        jLabel30 = new javax.swing.JLabel();
        btnCarikaryawan = new javax.swing.JButton();
        txtCarikaryawan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(108, 88, 76));
        jPanel1.setPreferredSize(new java.awt.Dimension(1024, 768));

        panelNav.setBackground(new java.awt.Color(166, 132, 103));
        panelNav.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, java.awt.Color.white, java.awt.Color.white, java.awt.Color.white));
        panelNav.setPreferredSize(new java.awt.Dimension(265, 620));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logoCafe_kecil.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Agbalumo", 0, 34)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 234));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Lune et Sucre");

        btnHome.setBackground(new java.awt.Color(251, 242, 196));
        btnHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHomeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHomeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHomeMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHomeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHomeMouseReleased(evt);
            }
        });

        lblHome.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        lblHome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHome.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\home (1) (1).png")); // NOI18N
        lblHome.setText("Beranda");

        javax.swing.GroupLayout btnHomeLayout = new javax.swing.GroupLayout(btnHome);
        btnHome.setLayout(btnHomeLayout);
        btnHomeLayout.setHorizontalGroup(
            btnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnHomeLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblHome, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btnHomeLayout.setVerticalGroup(
            btnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnHomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHome)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnMenu.setBackground(new java.awt.Color(251, 242, 196));
        btnMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnMenuMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMenuMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnMenuMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnMenuMouseReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\menu (2).png")); // NOI18N
        jLabel4.setText("Menu");

        javax.swing.GroupLayout btnMenuLayout = new javax.swing.GroupLayout(btnMenu);
        btnMenu.setLayout(btnMenuLayout);
        btnMenuLayout.setHorizontalGroup(
            btnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        btnMenuLayout.setVerticalGroup(
            btnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnTransaksi.setBackground(new java.awt.Color(251, 242, 196));
        btnTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTransaksiMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTransaksiMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTransaksiMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTransaksiMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTransaksiMouseReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\money (1).png")); // NOI18N
        jLabel6.setText("Transaksi");

        javax.swing.GroupLayout btnTransaksiLayout = new javax.swing.GroupLayout(btnTransaksi);
        btnTransaksi.setLayout(btnTransaksiLayout);
        btnTransaksiLayout.setHorizontalGroup(
            btnTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnTransaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        btnTransaksiLayout.setVerticalGroup(
            btnTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnTransaksiLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        btnLaporan.setBackground(new java.awt.Color(251, 242, 196));
        btnLaporan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLaporanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLaporanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLaporanMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnLaporanMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnLaporanMouseReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\report (1).png")); // NOI18N
        jLabel8.setText("Laporan");

        javax.swing.GroupLayout btnLaporanLayout = new javax.swing.GroupLayout(btnLaporan);
        btnLaporan.setLayout(btnLaporanLayout);
        btnLaporanLayout.setHorizontalGroup(
            btnLaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnLaporanLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        btnLaporanLayout.setVerticalGroup(
            btnLaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnLaporanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnKaryawan.setBackground(new java.awt.Color(251, 242, 196));
        btnKaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKaryawanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnKaryawanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnKaryawanMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnKaryawanMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnKaryawanMouseReleased(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\teamwork (1).png")); // NOI18N
        jLabel7.setText("Karyawan");

        javax.swing.GroupLayout btnKaryawanLayout = new javax.swing.GroupLayout(btnKaryawan);
        btnKaryawan.setLayout(btnKaryawanLayout);
        btnKaryawanLayout.setHorizontalGroup(
            btnKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnKaryawanLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        btnKaryawanLayout.setVerticalGroup(
            btnKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnKaryawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnLogout.setBackground(new java.awt.Color(251, 242, 196));
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLogoutMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogoutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogoutMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnLogoutMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnLogoutMouseReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\logout (1).png")); // NOI18N
        jLabel5.setText("Logout");

        javax.swing.GroupLayout btnLogoutLayout = new javax.swing.GroupLayout(btnLogout);
        btnLogout.setLayout(btnLogoutLayout);
        btnLogoutLayout.setHorizontalGroup(
            btnLogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnLogoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        btnLogoutLayout.setVerticalGroup(
            btnLogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnLogoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelNavLayout = new javax.swing.GroupLayout(panelNav);
        panelNav.setLayout(panelNavLayout);
        panelNavLayout.setHorizontalGroup(
            panelNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNavLayout.createSequentialGroup()
                .addGroup(panelNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelNavLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelNavLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(panelNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLaporan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 9, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNavLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49))
        );
        panelNavLayout.setVerticalGroup(
            panelNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNavLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        panelUtama.setBackground(new java.awt.Color(166, 132, 103));
        panelUtama.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, java.awt.Color.white, java.awt.Color.white, java.awt.Color.white));
        panelUtama.setPreferredSize(new java.awt.Dimension(580, 640));
        panelUtama.setLayout(new java.awt.CardLayout());

        panelHome.setBackground(new java.awt.Color(166, 132, 103));
        panelHome.setPreferredSize(new java.awt.Dimension(580, 640));

        lblSelamat.setFont(new java.awt.Font("Agbalumo", 1, 32)); // NOI18N
        lblSelamat.setForeground(new java.awt.Color(255, 255, 255));
        lblSelamat.setText("Selamat Datang, ");

        jTextPane1.setEditable(false);
        jTextPane1.setBackground(new java.awt.Color(166, 132, 103));
        jTextPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTextPane1.setFont(new java.awt.Font("Agbalumo", 0, 18)); // NOI18N
        jTextPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTextPane1.setText("Lune et Sucre, yang berarti \"Bulan dan Gula\" dalam bahasa Prancis, adalah kafe yang berdiri sejak tahun 2015. \nNama ini mencerminkan filosofi kehangatan dan kelembutan: \"Lune\" (bulan) melambangkan ketenangan dan keindahan malam, sementara \"Sucre\" (gula) mewakili manisnya kebahagiaan yang hadir melalui setiap sajian. Dengan fokus pada cita rasa autentik  dan bahan-bahan segar, Lune et Sucre telah menjadi tempat favorit untuk menikmati kopi, teh, dan beragam pencuci mulut istimewa.");
        jTextPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane4.setViewportView(jTextPane1);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logoCafe_besar.png"))); // NOI18N

        javax.swing.GroupLayout panelHomeLayout = new javax.swing.GroupLayout(panelHome);
        panelHome.setLayout(panelHomeLayout);
        panelHomeLayout.setHorizontalGroup(
            panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHomeLayout.createSequentialGroup()
                .addGap(189, 189, 189)
                .addComponent(jLabel3)
                .addContainerGap(175, Short.MAX_VALUE))
            .addGroup(panelHomeLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                    .addComponent(lblSelamat, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        panelHomeLayout.setVerticalGroup(
            panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHomeLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSelamat)
                .addGap(52, 52, 52)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addGap(32, 32, 32))
        );

        panelUtama.add(panelHome, "card2");

        panelMenu.setBackground(new java.awt.Color(166, 132, 103));
        panelMenu.setRequestFocusEnabled(false);
        panelMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMenuMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Comic Sans MS", 1, 30)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("DATA MENU CAFE");

        jLabel10.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("ID Menu");

        txtIDmenu.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Nama Menu");

        txtNamamenu.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Harga");

        txtHarga.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Status Menu");

        txtStatus.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        txtStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- Pilih -", "Tersedia", "Habis" }));
        txtStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStatusActionPerformed(evt);
            }
        });

        btnSimpanmenu.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnSimpanmenu.setText("SIMPAN");
        btnSimpanmenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpanmenuMouseClicked(evt);
            }
        });
        btnSimpanmenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanmenuActionPerformed(evt);
            }
        });

        tabelMenu.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        tabelMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelMenu);

        btnEditmenu.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnEditmenu.setText("EDIT");
        btnEditmenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditmenuMouseClicked(evt);
            }
        });
        btnEditmenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditmenuActionPerformed(evt);
            }
        });

        btnEdithapus.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnEdithapus.setText("HAPUS");
        btnEdithapus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEdithapusMouseClicked(evt);
            }
        });
        btnEdithapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEdithapusActionPerformed(evt);
            }
        });

        btnCarimenu.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\loupe (3).png")); // NOI18N
        btnCarimenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCarimenuMouseClicked(evt);
            }
        });
        btnCarimenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarimenuActionPerformed(evt);
            }
        });

        txtCarimenu.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        txtCarimenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtCarimenuMousePressed(evt);
            }
        });
        txtCarimenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCarimenuActionPerformed(evt);
            }
        });
        txtCarimenu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCarimenuKeyPressed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("MENU CAFE");

        javax.swing.GroupLayout panelMenuLayout = new javax.swing.GroupLayout(panelMenu);
        panelMenu.setLayout(panelMenuLayout);
        panelMenuLayout.setHorizontalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMenuLayout.createSequentialGroup()
                        .addComponent(btnSimpanmenu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditmenu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEdithapus)
                        .addContainerGap(219, Short.MAX_VALUE))
                    .addGroup(panelMenuLayout.createSequentialGroup()
                        .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHarga, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtNamamenu)
                            .addComponent(txtStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIDmenu))
                        .addGap(90, 90, 90))))
            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMenuLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 665, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMenuLayout.createSequentialGroup()
                        .addComponent(btnCarimenu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCarimenu, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23))
        );
        panelMenuLayout.setVerticalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDmenu, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNamamenu, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpanmenu)
                    .addComponent(btnEditmenu)
                    .addComponent(btnEdithapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCarimenu)
                    .addComponent(btnCarimenu))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        panelUtama.add(panelMenu, "card3");

        panelTransaksi.setBackground(new java.awt.Color(166, 132, 103));

        jLabel14.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("TRANSAKSI");

        jLabel15.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("ID Transaksi");

        txtIDtransaksi.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Nama Pelanggan");

        jLabel17.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Tanggal");

        txtJumlah.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        txtJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJumlahActionPerformed(evt);
            }
        });
        txtJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtJumlahKeyReleased(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Jumlah Beli");

        btnSimpanTransaksi.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnSimpanTransaksi.setText("SIMPAN");
        btnSimpanTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpanTransaksiMouseClicked(evt);
            }
        });
        btnSimpanTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanTransaksiActionPerformed(evt);
            }
        });

        tabelTransaksi.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        tabelTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        jScrollPane2.setViewportView(tabelTransaksi);

        btnEditTransaksi.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnEditTransaksi.setText("EDIT");
        btnEditTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditTransaksiMouseClicked(evt);
            }
        });
        btnEditTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditTransaksiActionPerformed(evt);
            }
        });

        btnHapustransaksi.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnHapustransaksi.setText("HAPUS");
        btnHapustransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapustransaksiMouseClicked(evt);
            }
        });
        btnHapustransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapustransaksiActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Total Bayar");

        btnCetaktransaksi.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnCetaktransaksi.setText("CETAK");
        btnCetaktransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetaktransaksiActionPerformed(evt);
            }
        });

        cmbIDmenu.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        cmbIDmenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIDmenuActionPerformed(evt);
            }
        });

        btnLihatMenu.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnLihatMenu.setText("LIHAT MENU");
        btnLihatMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLihatMenuMouseClicked(evt);
            }
        });
        btnLihatMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihatMenuActionPerformed(evt);
            }
        });

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        txtTotal.setEnabled(false);
        txtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalActionPerformed(evt);
            }
        });
        txtTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotalKeyReleased(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Pilih Menu");

        txtNamapelanggan.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        txtTanggaltransaksi.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        btnTambah.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnTambah.setText("TAMBAH");
        btnTambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambahMouseClicked(evt);
            }
        });
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        txtCaritransaksi.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        txtCaritransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtCaritransaksiMousePressed(evt);
            }
        });
        txtCaritransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCaritransaksiActionPerformed(evt);
            }
        });
        txtCaritransaksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCaritransaksiKeyPressed(evt);
            }
        });

        btnCaritransaksi.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\loupe (3).png")); // NOI18N
        btnCaritransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCaritransaksiMouseClicked(evt);
            }
        });
        btnCaritransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCaritransaksiActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Comic Sans MS", 1, 30)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("DATA TRANSAKSI");

        javax.swing.GroupLayout panelTransaksiLayout = new javax.swing.GroupLayout(panelTransaksi);
        panelTransaksi.setLayout(panelTransaksiLayout);
        panelTransaksiLayout.setHorizontalGroup(
            panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransaksiLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTransaksiLayout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTanggaltransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panelTransaksiLayout.createSequentialGroup()
                            .addComponent(btnTambah)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnSimpanTransaksi)
                            .addGap(12, 12, 12)
                            .addComponent(btnEditTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnHapustransaksi)
                            .addGap(12, 12, 12)
                            .addComponent(btnCetaktransaksi))
                        .addGroup(panelTransaksiLayout.createSequentialGroup()
                            .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(panelTransaksiLayout.createSequentialGroup()
                                    .addComponent(cmbIDmenu, 0, 343, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnLihatMenu))
                                .addComponent(txtTotal)
                                .addComponent(txtJumlah)
                                .addComponent(txtNamapelanggan)
                                .addGroup(panelTransaksiLayout.createSequentialGroup()
                                    .addComponent(txtIDtransaksi)
                                    .addGap(139, 139, 139))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelTransaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTransaksiLayout.createSequentialGroup()
                        .addGap(0, 344, Short.MAX_VALUE)
                        .addComponent(btnCaritransaksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCaritransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelTransaksiLayout.setVerticalGroup(
            panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransaksiLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTanggaltransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDtransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNamapelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(cmbIDmenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLihatMenu))
                .addGap(8, 8, 8)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel19)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpanTransaksi)
                    .addComponent(btnEditTransaksi)
                    .addComponent(btnHapustransaksi)
                    .addComponent(btnCetaktransaksi)
                    .addComponent(btnTambah))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCaritransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCaritransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        panelUtama.add(panelTransaksi, "card4");

        panelLaporan.setBackground(new java.awt.Color(166, 132, 103));

        jLabel31.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("LAPORAN");

        tabelMenu2.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        tabelMenu2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(tabelMenu2);

        jLabel32.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("DATA TRANSAKSI");

        jLabel33.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("DATA MENU");

        jLabel34.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("DATA KARYAWAN");

        tabelTransaksi2.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        tabelTransaksi2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        jScrollPane6.setViewportView(tabelTransaksi2);

        tabelKaryawan2.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        tabelKaryawan2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        jScrollPane7.setViewportView(tabelKaryawan2);

        btnCetakmenu.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnCetakmenu.setText("CETAK");
        btnCetakmenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCetakmenuMouseClicked(evt);
            }
        });

        btnCetaktransaksi2.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnCetaktransaksi2.setText("CETAK");
        btnCetaktransaksi2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCetaktransaksi2MouseClicked(evt);
            }
        });
        btnCetaktransaksi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetaktransaksi2ActionPerformed(evt);
            }
        });

        btnCetakkaryawan.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnCetakkaryawan.setText("CETAK");
        btnCetakkaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCetakkaryawanMouseClicked(evt);
            }
        });
        btnCetakkaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakkaryawanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLaporanLayout = new javax.swing.GroupLayout(panelLaporan);
        panelLaporan.setLayout(panelLaporanLayout);
        panelLaporanLayout.setHorizontalGroup(
            panelLaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLaporanLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(panelLaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                    .addComponent(jScrollPane6)
                    .addComponent(jScrollPane5)
                    .addComponent(btnCetakkaryawan, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCetaktransaksi2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCetakmenu, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(21, 21, 21))
            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelLaporanLayout.setVerticalGroup(
            panelLaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLaporanLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCetakmenu)
                .addGap(25, 25, 25)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCetaktransaksi2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCetakkaryawan)
                .addGap(63, 63, 63))
        );

        panelUtama.add(panelLaporan, "card6");

        panelKaryawan.setBackground(new java.awt.Color(166, 132, 103));

        jLabel20.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("KARYAWAN");

        jLabel21.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("ID Karyawan");

        txtIDkaryawan.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Nama Lengkap");

        txtNamakaryawan.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Jenis Kelamin");

        txtEmail.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Jabatan");

        btnSimpankaryawan.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnSimpankaryawan.setText("SIMPAN");
        btnSimpankaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpankaryawanMouseClicked(evt);
            }
        });
        btnSimpankaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpankaryawanActionPerformed(evt);
            }
        });

        tabelKaryawan.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        tabelKaryawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        jScrollPane3.setViewportView(tabelKaryawan);

        btnEditkaryawan.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnEditkaryawan.setText("EDIT");
        btnEditkaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditkaryawanMouseClicked(evt);
            }
        });
        btnEditkaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditkaryawanActionPerformed(evt);
            }
        });

        btnHapuskaryawan.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        btnHapuskaryawan.setText("HAPUS");
        btnHapuskaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapuskaryawanMouseClicked(evt);
            }
        });
        btnHapuskaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapuskaryawanActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Email");

        bgJK.add(rbLaki);
        rbLaki.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        rbLaki.setForeground(new java.awt.Color(255, 255, 255));
        rbLaki.setText("Laki-Laki");
        rbLaki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbLakiActionPerformed(evt);
            }
        });

        bgJK.add(rbPr);
        rbPr.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        rbPr.setForeground(new java.awt.Color(255, 255, 255));
        rbPr.setText("Perempuan");

        jLabel27.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Tanggal Masuk");

        cmbJabatan.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        cmbJabatan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- Pilih Jabatan -", "Kasir" }));
        cmbJabatan.setPreferredSize(new java.awt.Dimension(158, 32));

        jLabel30.setFont(new java.awt.Font("Comic Sans MS", 1, 30)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("DATA KARYAWAN");

        btnCarikaryawan.setIcon(new javax.swing.ImageIcon("C:\\Users\\Afifah\\Downloads\\From Browser\\loupe (3).png")); // NOI18N
        btnCarikaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCarikaryawanMouseClicked(evt);
            }
        });
        btnCarikaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarikaryawanActionPerformed(evt);
            }
        });

        txtCarikaryawan.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        txtCarikaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtCarikaryawanMousePressed(evt);
            }
        });
        txtCarikaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCarikaryawanActionPerformed(evt);
            }
        });
        txtCarikaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCarikaryawanKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout panelKaryawanLayout = new javax.swing.GroupLayout(panelKaryawan);
        panelKaryawan.setLayout(panelKaryawanLayout);
        panelKaryawanLayout.setHorizontalGroup(
            panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelKaryawanLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelKaryawanLayout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(rbLaki, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbPr))
                    .addGroup(panelKaryawanLayout.createSequentialGroup()
                        .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKaryawanLayout.createSequentialGroup()
                                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelKaryawanLayout.createSequentialGroup()
                                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(12, 12, 12)))
                        .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmail)
                            .addComponent(txtNamakaryawan)
                            .addComponent(txtTanggalmasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIDkaryawan, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelKaryawanLayout.createSequentialGroup()
                                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelKaryawanLayout.createSequentialGroup()
                                        .addComponent(btnSimpankaryawan)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnEditkaryawan)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnHapuskaryawan))
                                    .addComponent(cmbJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(80, 80, 80))
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelKaryawanLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelKaryawanLayout.createSequentialGroup()
                        .addComponent(btnCarikaryawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCarikaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelKaryawanLayout.setVerticalGroup(
            panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKaryawanLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDkaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNamakaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23)
                    .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rbPr)
                        .addComponent(rbLaki)))
                .addGap(4, 4, 4)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbJabatan, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(txtTanggalmasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpankaryawan)
                    .addComponent(btnEditkaryawan)
                    .addComponent(btnHapuskaryawan))
                .addGap(58, 58, 58)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelKaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCarikaryawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCarikaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104))
        );

        panelUtama.add(panelKaryawan, "card5");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(panelNav, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelUtama, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelNav, javax.swing.GroupLayout.DEFAULT_SIZE, 743, Short.MAX_VALUE)
                    .addComponent(panelUtama, javax.swing.GroupLayout.PREFERRED_SIZE, 743, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(1044, 812));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void btnSimpanmenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanmenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanmenuActionPerformed

    private void btnEditmenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditmenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditmenuActionPerformed

    private void btnEdithapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEdithapusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEdithapusActionPerformed

    private void txtJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJumlahActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtJumlahActionPerformed

    private void btnSimpanTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanTransaksiActionPerformed

    private void btnEditTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditTransaksiActionPerformed

    private void btnHapustransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapustransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapustransaksiActionPerformed

    private void btnLihatMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLihatMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLihatMenuActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

    private void btnHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHomeMouseClicked
        // TODO add your handling code here:
        panelUtama.removeAll();
        panelUtama.repaint();
        panelUtama.revalidate();
        
        panelUtama.add(panelHome);
        panelUtama.repaint();
        panelUtama.revalidate();
    }//GEN-LAST:event_btnHomeMouseClicked

    private void btnMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMouseClicked
        // TODO add your handling code here:
        panelUtama.removeAll();
        panelUtama.repaint();
        panelUtama.revalidate();
        
        panelUtama.add(panelMenu);
        panelUtama.repaint();
        panelUtama.revalidate();
    }//GEN-LAST:event_btnMenuMouseClicked

    private void btnTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTransaksiMouseClicked
        // TODO add your handling code here:
        panelUtama.removeAll();
        panelUtama.repaint();
        panelUtama.revalidate();
        
        panelUtama.add(panelTransaksi);
        panelUtama.repaint();
        panelUtama.revalidate();
        
        loadMenuCombo();
        load_transaksi();
    }//GEN-LAST:event_btnTransaksiMouseClicked

    private void btnSimpankaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpankaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpankaryawanActionPerformed

    private void btnEditkaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditkaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditkaryawanActionPerformed

    private void btnHapuskaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapuskaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapuskaryawanActionPerformed

    private void txtStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStatusActionPerformed

    private void btnKaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKaryawanMouseClicked
        // TODO add your handling code here:
        panelUtama.removeAll();
        panelUtama.repaint();
        panelUtama.revalidate();
        
        panelUtama.add(panelKaryawan);
        panelUtama.repaint();
        panelUtama.revalidate();
    }//GEN-LAST:event_btnKaryawanMouseClicked

    private void btnLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLogoutMouseClicked
        // TODO add your handling code here:
        int option = JOptionPane.showConfirmDialog(null, 
                "Apakah Anda yakin ingin keluar?", 
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    this.dispose();
                    Login loginform = new Login();
                    loginform.setVisible(true);
                }
    }//GEN-LAST:event_btnLogoutMouseClicked

    private void btnHomeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHomeMouseEntered
        // TODO add your handling code here:
        btnHome.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnHomeMouseEntered

    private void btnHomeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHomeMouseExited
        // TODO add your handling code here:
        btnHome.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnHomeMouseExited

    private void btnHomeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHomeMousePressed
        // TODO add your handling code here:
        btnHome.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnHomeMousePressed

    private void btnHomeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHomeMouseReleased
        // TODO add your handling code here:
        btnHome.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnHomeMouseReleased

    private void btnLogoutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLogoutMouseEntered
        // TODO add your handling code here:
        btnLogout.setBackground(new Color(238, 82, 83));
    }//GEN-LAST:event_btnLogoutMouseEntered

    private void btnLogoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLogoutMouseExited
        // TODO add your handling code here:
        btnLogout.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnLogoutMouseExited

    private void btnLogoutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLogoutMousePressed
        // TODO add your handling code here:
        btnLogout.setBackground(new Color(238, 82, 83));
    }//GEN-LAST:event_btnLogoutMousePressed

    private void btnLogoutMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLogoutMouseReleased
        // TODO add your handling code here:
        btnLogout.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnLogoutMouseReleased

    private void btnMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMouseEntered
        // TODO add your handling code here:
        btnMenu.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnMenuMouseEntered

    private void btnMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMouseExited
        // TODO add your handling code here:
        btnMenu.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnMenuMouseExited

    private void btnMenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMousePressed
        // TODO add your handling code here:
        btnMenu.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnMenuMousePressed

    private void btnMenuMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMouseReleased
        // TODO add your handling code here:
        btnMenu.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnMenuMouseReleased

    private void btnTransaksiMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTransaksiMouseEntered
        // TODO add your handling code here:
        btnTransaksi.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnTransaksiMouseEntered

    private void btnTransaksiMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTransaksiMouseExited
        // TODO add your handling code here:
        btnTransaksi.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnTransaksiMouseExited

    private void btnTransaksiMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTransaksiMousePressed
        // TODO add your handling code here:
        btnTransaksi.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnTransaksiMousePressed

    private void btnTransaksiMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTransaksiMouseReleased
        // TODO add your handling code here:
        btnTransaksi.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnTransaksiMouseReleased

    private void btnKaryawanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKaryawanMouseEntered
        // TODO add your handling code here:
        btnKaryawan.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnKaryawanMouseEntered

    private void btnKaryawanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKaryawanMouseExited
        // TODO add your handling code here:
        btnKaryawan.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnKaryawanMouseExited

    private void btnKaryawanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKaryawanMousePressed
        // TODO add your handling code here:
        btnKaryawan.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnKaryawanMousePressed

    private void btnKaryawanMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKaryawanMouseReleased
        // TODO add your handling code here:
        btnKaryawan.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnKaryawanMouseReleased

    private void btnSimpanmenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanmenuMouseClicked
        // TODO add your handling code here:
        input_menu();
    }//GEN-LAST:event_btnSimpanmenuMouseClicked

    private void btnEditmenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditmenuMouseClicked
        // TODO add your handling code here:
        if (txtIDmenu.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(
            null,
            "ID Menu Harus Diisi!",
            "Peringatan",
            JOptionPane.WARNING_MESSAGE);
        return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin mengedit menu?",
            "Konfirmasi Edit Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "UPDATE menu SET nama_menu = '" + 
                             txtNamamenu.getText() + "', harga = '" +
                             txtHarga.getText() + "', status = '" +
                             txtStatus.getSelectedItem() + 
                             "' WHERE id_menu = '" + txtIDmenu.getText() + "'";

                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Menu Berhasil Diperbarui");
                clear_menu();
                load_menu();
            } catch (java.sql.SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi diperbarui");
        }
    }//GEN-LAST:event_btnEditmenuMouseClicked

    private void btnEdithapusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEdithapusMouseClicked
        // TODO add your handling code here:
        if (txtIDmenu.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(
            null,
            "ID Menu harus diisi!",
            "Peringatan",
            JOptionPane.WARNING_MESSAGE);
        return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin menghapus menu dengan ID: " + txtIDmenu.getText() + "?",
            "Konfirmasi Hapus Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM menu WHERE id_menu = '" + txtIDmenu.getText() + "'";
                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Menu Berhasil Dihapus");
                clear_menu();
                load_menu();
            } catch (java.sql.SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi dihapus");
        }
    }//GEN-LAST:event_btnEdithapusMouseClicked

    private void btnSimpanTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanTransaksiMouseClicked
        // TODO add your handling code here:
        input_transaksi();
    }//GEN-LAST:event_btnSimpanTransaksiMouseClicked

    private void cmbIDmenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIDmenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIDmenuActionPerformed

    private void txtTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotalKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalKeyReleased

    private void txtJumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJumlahKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJumlahKeyReleased

    private void btnEditTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditTransaksiMouseClicked
        // TODO add your handling code here:
        if (txtIDtransaksi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "ID Transaksi harus diisi!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (cmbIDmenu.getSelectedItem() == null || 
            txtNamapelanggan.getText().trim().isEmpty() || 
            txtJumlah.getText().trim().isEmpty() || 
            txtTanggaltransaksi.getDate() == null) {
            JOptionPane.showMessageDialog(
                null,
                "Semua Data Harus Diisi!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Konfirmasi edit transaksi
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin mengedit transaksi?",
            "Konfirmasi Edit Transaksi",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String selectedItem = cmbIDmenu.getSelectedItem().toString();
                String[] menuData = selectedItem.split(" - ");
                String idMenu = menuData[0];

                String sql = "UPDATE transaksi SET " +
                        "id_menu = '" + idMenu + "', " +
                        "nama_pelanggan = '" + txtNamapelanggan.getText() + "', " +
                        "jumlah = '" + txtJumlah.getText() + "', " +
                        "total_bayar = '" + txtHarga.getText() + "', " +
                        "tanggal_transaksi = '" + new java.sql.Date(txtTanggaltransaksi.getDate().getTime()) + "' " +
                        "WHERE id_transaksi = '" + txtIDtransaksi.getText() + "'";

                st.executeUpdate(sql);

                JOptionPane.showMessageDialog(null, "Data Transaksi Berhasil Diubah");
                clear_transaksi();
                load_transaksi();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi diubah.");
        }
    }//GEN-LAST:event_btnEditTransaksiMouseClicked

    private void btnHapustransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapustransaksiMouseClicked
        // TODO add your handling code here:
        if (txtIDtransaksi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "ID Transaksi Harus Diisi!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin menghapus transaksi dengan ID: " + txtIDtransaksi.getText() + "?",
            "Konfirmasi Hapus Transaksi",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM transaksi WHERE id_transaksi = '" + txtIDtransaksi.getText() + "'";
                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Transaksi Berhasil Dihapus");
                clear_transaksi();
                load_transaksi();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi dihapus.");
        }
    }//GEN-LAST:event_btnHapustransaksiMouseClicked

    private void btnTambahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseClicked
        // TODO add your handling code here:
        input_transaksi2();
    }//GEN-LAST:event_btnTambahMouseClicked

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void rbLakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbLakiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbLakiActionPerformed

    private void btnSimpankaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpankaryawanMouseClicked
        // TODO add your handling code here:
        input_karyawan();
    }//GEN-LAST:event_btnSimpankaryawanMouseClicked

    private void btnEditkaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditkaryawanMouseClicked
        // TODO add your handling code here:
        if (txtIDkaryawan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "ID Karyawan harus diisi untuk mengedit data!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin mengedit data karyawan?",
            "Konfirmasi Edit Data",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String jk = rbLaki.isSelected() ? "L" : "P";

                String sql = "UPDATE karyawan SET nama_lengkap='" + txtNamakaryawan.getText() +
                             "', jenis_kelamin='" + jk +
                             "', email='" + txtEmail.getText() +
                             "', jabatan='" + cmbJabatan.getSelectedItem().toString() +
                             "', tanggal_masuk='" + new java.sql.Date(txtTanggalmasuk.getDate().getTime()) +
                             "' WHERE id_karyawan='" + txtIDkaryawan.getText() + "'";

                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Karyawan Berhasil Diedit");
                load_karyawan();
                clear_karyawan();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi diedit");
        }
    }//GEN-LAST:event_btnEditkaryawanMouseClicked

    private void btnHapuskaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapuskaryawanMouseClicked
        // TODO add your handling code here:
        if (txtIDkaryawan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "ID Karyawan harus diisi untuk menghapus data!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin menghapus data karyawan dengan ID: " + txtIDkaryawan.getText() + "?",
            "Konfirmasi Hapus Data",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM karyawan WHERE id_karyawan='" + txtIDkaryawan.getText() + "'";

                st.execute(sql);
                JOptionPane.showMessageDialog(null, "Data Karyawan Berhasil Dihapus");
                clear_karyawan();
                load_karyawan();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak jadi dihapus.");
        }
    }//GEN-LAST:event_btnHapuskaryawanMouseClicked

    private void btnLaporanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLaporanMouseClicked
        // TODO add your handling code here:
        panelUtama.removeAll();
        panelUtama.repaint();
        panelUtama.revalidate();
        
        panelUtama.add(panelLaporan);
        panelUtama.repaint();
        panelUtama.revalidate();
    }//GEN-LAST:event_btnLaporanMouseClicked

    private void btnLaporanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLaporanMouseEntered
        // TODO add your handling code here:
        btnLaporan.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnLaporanMouseEntered

    private void btnLaporanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLaporanMouseExited
        // TODO add your handling code here:
        btnLaporan.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnLaporanMouseExited

    private void btnLaporanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLaporanMousePressed
        // TODO add your handling code here:
        btnLaporan.setBackground(new Color(245,222,179));
    }//GEN-LAST:event_btnLaporanMousePressed

    private void btnLaporanMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLaporanMouseReleased
        // TODO add your handling code here:
        btnLaporan.setBackground(new Color(251,242,196));
    }//GEN-LAST:event_btnLaporanMouseReleased

    private void txtCarimenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCarimenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarimenuActionPerformed

    private void txtCarimenuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCarimenuKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarimenuKeyPressed

    private void txtCarimenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCarimenuMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarimenuMousePressed

    private void btnCarimenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCarimenuMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCarimenuMouseClicked

    private void btnCarimenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarimenuActionPerformed
        // TODO add your handling code here:
        if (txtCarimenu.getText().trim().isEmpty()) {
            try {
                String sql = "SELECT * FROM menu";
                ResultSet rs = st.executeQuery(sql);

                DefaultTableModel model = (DefaultTableModel) tabelMenu.getModel();
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("id_menu"),
                        rs.getString("nama_menu"),
                        rs.getString("harga"),
                        rs.getString("status")
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Error saat mengambil semua data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
            txtCarimenu.setText("");
            return;
        }

        try {
            String sql = "SELECT * FROM menu WHERE id_menu = '" + txtCarimenu.getText().trim() +
                         "' OR nama_menu LIKE '%" + txtCarimenu.getText().trim() + "%'";
            ResultSet rs = st.executeQuery(sql);
            DefaultTableModel model = (DefaultTableModel) tabelMenu.getModel();
            model.setRowCount(0);

            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                model.addRow(new Object[]{
                    rs.getString("id_menu"),
                    rs.getString("nama_menu"),
                    rs.getString("harga"),
                    rs.getString("status")
                });
            }
            if (!dataFound) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error saat mencari data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
        txtCarimenu.setText("");
    }//GEN-LAST:event_btnCarimenuActionPerformed

    private void panelMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMenuMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panelMenuMouseClicked

    private void txtCaritransaksiMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCaritransaksiMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCaritransaksiMousePressed

    private void txtCaritransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCaritransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCaritransaksiActionPerformed

    private void txtCaritransaksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCaritransaksiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCaritransaksiKeyPressed

    private void btnCaritransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCaritransaksiMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCaritransaksiMouseClicked

    private void btnCaritransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCaritransaksiActionPerformed
        // TODO add your handling code here:
        if (txtCaritransaksi.getText().trim().isEmpty()) {
            try {
                Object header[] = {"ID TRANSAKSI", "ID MENU", "NAMA PELANGGAN", "JUMLAH", "TOTAL BAYAR", "TANGGAL TRANSAKSI"};
                DefaultTableModel data = new DefaultTableModel(null, header);
                tabelTransaksi.setModel(data);

                String sql = "SELECT id_transaksi, id_menu, nama_pelanggan, jumlah, total_bayar, tanggal_transaksi FROM transaksi";
                st = con.createStatement();
                rs = st.executeQuery(sql);

                while (rs.next()) {
                    String k1 = rs.getString("id_transaksi");
                    String k2 = rs.getString("id_menu");
                    String k3 = rs.getString("nama_pelanggan");
                    String k4 = rs.getString("jumlah");
                    String k5 = rs.getString("total_bayar");
                    String k6 = rs.getString("tanggal_transaksi");
                    String k[] = {k1, k2, k3, k4, k5, k6};
                    data.addRow(k);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Error saat mengambil semua data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
            txtCaritransaksi.setText("");
            return;
        }

        try {
            Object header[] = {"ID TRANSAKSI", "ID MENU", "NAMA PELANGGAN", "JUMLAH", "TOTAL BAYAR", "TANGGAL TRANSAKSI"};
            DefaultTableModel data = new DefaultTableModel(null, header);
            tabelTransaksi.setModel(data);

            String sql = "SELECT id_transaksi, id_menu, nama_pelanggan, jumlah, total_bayar, tanggal_transaksi " +
                         "FROM transaksi WHERE id_transaksi = '" + txtCaritransaksi.getText().trim() +
                         "' OR nama_pelanggan LIKE '%" + txtCaritransaksi.getText().trim() + "%'";
            st = con.createStatement();
            rs = st.executeQuery(sql);

            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                String k1 = rs.getString("id_transaksi");
                String k2 = rs.getString("id_menu");
                String k3 = rs.getString("nama_pelanggan");
                String k4 = rs.getString("jumlah");
                String k5 = rs.getString("total_bayar");
                String k6 = rs.getString("tanggal_transaksi");
                String k[] = {k1, k2, k3, k4, k5, k6};
                data.addRow(k);
            }

            if (!dataFound) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error saat mencari data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
        txtCaritransaksi.setText("");
    }//GEN-LAST:event_btnCaritransaksiActionPerformed

    private void btnCarikaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCarikaryawanMouseClicked
        // TODO add your handling code here:
        if (txtCarikaryawan.getText().trim().isEmpty()) {
    try {
        Object header[] = {"ID KARYAWAN", "NAMA LENGKAP", "JENIS KELAMIN", "EMAIL", "JABATAN", "TANGGAL MASUK"};
        DefaultTableModel data = new DefaultTableModel(null, header);
        tabelKaryawan.setModel(data);

        String sql = "SELECT id_karyawan, nama_lengkap, jenis_kelamin, email, jabatan, tanggal_masuk FROM karyawan";
        st = con.createStatement();
        rs = st.executeQuery(sql);

        while (rs.next()) {
            String k1 = rs.getString("id_karyawan");
            String k2 = rs.getString("nama_lengkap");
            String k3 = rs.getString("jenis_kelamin");
            String k4 = rs.getString("email");
            String k5 = rs.getString("jabatan");
            String k6 = rs.getString("tanggal_masuk");
            String k[] = {k1, k2, k3, k4, k5, k6};
            data.addRow(k);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            null,
            "Error saat mengambil semua data: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        e.printStackTrace();
    }
    txtCarikaryawan.setText("");
    return;
}

try {
    Object header[] = {"ID KARYAWAN", "NAMA LENGKAP", "JENIS KELAMIN", "EMAIL", "JABATAN", "TANGGAL MASUK"};
    DefaultTableModel data = new DefaultTableModel(null, header);
    tabelKaryawan.setModel(data);

    String sql = "SELECT id_karyawan, nama_lengkap, jenis_kelamin, email, jabatan, tanggal_masuk " +
                 "FROM karyawan WHERE id_karyawan = '" + txtCarikaryawan.getText().trim() +
                 "' OR nama_lengkap LIKE '%" + txtCarikaryawan.getText().trim() + "%'";
    st = con.createStatement();
    rs = st.executeQuery(sql);

    boolean dataFound = false;
    while (rs.next()) {
        dataFound = true;
        String k1 = rs.getString("id_karyawan");
        String k2 = rs.getString("nama_lengkap");
        String k3 = rs.getString("jenis_kelamin");
        String k4 = rs.getString("email");
        String k5 = rs.getString("jabatan");
        String k6 = rs.getString("tanggal_masuk");
        String k[] = {k1, k2, k3, k4, k5, k6};
        data.addRow(k);
    }

    if (!dataFound) {
        JOptionPane.showMessageDialog(null, "Data tidak ditemukan.");
    }
} catch (Exception e) {
    JOptionPane.showMessageDialog(
        null,
        "Error saat mencari data: " + e.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
    );
    e.printStackTrace();
}
txtCarikaryawan.setText("");

    }//GEN-LAST:event_btnCarikaryawanMouseClicked

    private void btnCarikaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarikaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCarikaryawanActionPerformed

    private void txtCarikaryawanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCarikaryawanMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarikaryawanMousePressed

    private void txtCarikaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCarikaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarikaryawanActionPerformed

    private void txtCarikaryawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCarikaryawanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarikaryawanKeyPressed

    private void btnCetaktransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetaktransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetaktransaksiActionPerformed

    private void btnCetaktransaksi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetaktransaksi2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetaktransaksi2ActionPerformed

    private void btnCetakkaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakkaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakkaryawanActionPerformed

    private void btnCetakmenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakmenuMouseClicked
        // TODO add your handling code here:
        try{
            File file = new File("C:\\Users\\Afifah\\OneDrive\\Dokumen\\NetBeansProjects\\ProjekBP1_AppCafe\\src\\LaporanMenu.jrxml");
            jd = JRXmlLoader.load(file);
            param.clear();
            jr = JasperCompileManager.compileReport(jd);
            jp = JasperFillManager.fillReport(jr, param, koneksi2.con);
            JasperViewer jv = new JasperViewer(jp, false);
            jv.setVisible(true);
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }//GEN-LAST:event_btnCetakmenuMouseClicked

    private void btnCetaktransaksi2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetaktransaksi2MouseClicked
        // TODO add your handling code here:
        try{
            File file = new File("C:\\Users\\Afifah\\OneDrive\\Dokumen\\NetBeansProjects\\ProjekBP1_AppCafe\\src\\LaporanTransaksi.jrxml");
            jd = JRXmlLoader.load(file);
            param.clear();
            jr = JasperCompileManager.compileReport(jd);
            jp = JasperFillManager.fillReport(jr, param, koneksi2.con);
            JasperViewer jv = new JasperViewer(jp, false);
            jv.setVisible(true);
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }//GEN-LAST:event_btnCetaktransaksi2MouseClicked

    private void btnCetakkaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakkaryawanMouseClicked
        // TODO add your handling code here:
        try{
            File file = new File("C:\\Users\\Afifah\\OneDrive\\Dokumen\\NetBeansProjects\\ProjekBP1_AppCafe\\src\\LaporanKaryawan.jrxml");
            jd = JRXmlLoader.load(file);
            param.clear();
            jr = JasperCompileManager.compileReport(jd);
            jp = JasperFillManager.fillReport(jr, param, koneksi2.con);
            JasperViewer jv = new JasperViewer(jp, false);
            jv.setVisible(true);
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }//GEN-LAST:event_btnCetakkaryawanMouseClicked

    private void btnLihatMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLihatMenuMouseClicked
        // TODO add your handling code here:
        LihatMenu menuForm = new LihatMenu();
        menuForm.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        menuForm.setVisible(true);
                
    }//GEN-LAST:event_btnLihatMenuMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(aplikasiCafe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(aplikasiCafe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(aplikasiCafe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(aplikasiCafe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new aplikasiCafe().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgJK;
    private javax.swing.JButton btnCarikaryawan;
    private javax.swing.JButton btnCarimenu;
    private javax.swing.JButton btnCaritransaksi;
    private javax.swing.JButton btnCetakkaryawan;
    private javax.swing.JButton btnCetakmenu;
    private javax.swing.JButton btnCetaktransaksi;
    private javax.swing.JButton btnCetaktransaksi2;
    private javax.swing.JButton btnEditTransaksi;
    private javax.swing.JButton btnEdithapus;
    private javax.swing.JButton btnEditkaryawan;
    private javax.swing.JButton btnEditmenu;
    private javax.swing.JButton btnHapuskaryawan;
    private javax.swing.JButton btnHapustransaksi;
    private javax.swing.JPanel btnHome;
    private javax.swing.JPanel btnKaryawan;
    private javax.swing.JPanel btnLaporan;
    private javax.swing.JButton btnLihatMenu;
    private javax.swing.JPanel btnLogout;
    private javax.swing.JPanel btnMenu;
    private javax.swing.JButton btnSimpanTransaksi;
    private javax.swing.JButton btnSimpankaryawan;
    private javax.swing.JButton btnSimpanmenu;
    private javax.swing.JButton btnTambah;
    private javax.swing.JPanel btnTransaksi;
    private javax.swing.JComboBox<String> cmbIDmenu;
    private javax.swing.JComboBox<String> cmbJabatan;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblSelamat;
    private javax.swing.JPanel panelHome;
    private javax.swing.JPanel panelKaryawan;
    private javax.swing.JPanel panelLaporan;
    private javax.swing.JPanel panelMenu;
    private javax.swing.JPanel panelNav;
    private javax.swing.JPanel panelTransaksi;
    private javax.swing.JPanel panelUtama;
    private javax.swing.JRadioButton rbLaki;
    private javax.swing.JRadioButton rbPr;
    private javax.swing.JTable tabelKaryawan;
    private javax.swing.JTable tabelKaryawan2;
    private javax.swing.JTable tabelMenu;
    private javax.swing.JTable tabelMenu2;
    private javax.swing.JTable tabelTransaksi;
    private javax.swing.JTable tabelTransaksi2;
    private javax.swing.JTextField txtCarikaryawan;
    private javax.swing.JTextField txtCarimenu;
    private javax.swing.JTextField txtCaritransaksi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIDkaryawan;
    private javax.swing.JTextField txtIDmenu;
    private javax.swing.JTextField txtIDtransaksi;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtNamakaryawan;
    private javax.swing.JTextField txtNamamenu;
    private javax.swing.JTextField txtNamapelanggan;
    private javax.swing.JComboBox<String> txtStatus;
    private com.toedter.calendar.JDateChooser txtTanggalmasuk;
    private com.toedter.calendar.JDateChooser txtTanggaltransaksi;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
