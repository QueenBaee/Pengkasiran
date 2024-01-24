
import com.mysql.jdbc.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Robby
 */
public class GUI_Kasir extends javax.swing.JFrame {
String nama;
int Harga1;
    public GUI_Kasir() {
        initComponents();
        tampilkode();
        jam();
    }
    public void jam(){
        Thread t = new Thread() {
            public void run() {
                try {
                    for (;;) {
                        DateFormat date = new SimpleDateFormat("dd.MM.yy - hh : mm : ss ");
                        String datestring = date.format(new Date()).toString();
                        labelWaktu.setText(datestring);
                        labelWaktu.setText(datestring);
                        sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }     
        };
        t.start();
    }
    public Connection conn;
    public void koneksi() throws SQLException {
        try {
            conn = null;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/projektubes?user=root&password=");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_Kasir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            Logger.getLogger(GUI_Kasir.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception es) {
            Logger.getLogger(GUI_Kasir.class.getName()).log(Level.SEVERE, null, es);
        }
    }
    public void tampilkode(){
        try {
            koneksi();
            String sql = "SELECT id_barang,nama_barang,harga FROM tb_barang order by id_barang asc";
            Statement stt = conn.createStatement();
            ResultSet res = stt.executeQuery(sql);
            while (res.next()) {
                Object[] ob = new Object[3]; 
                ob[0] = res.getString(1);
                ob[1] = res.getString(2);
                ob[2] = res.getString(3);
                cmbKode.addItem((String) ob[0]);
                nama = res.getString(2);
                Harga1 = Integer.parseInt(res.getString(3));
                //break;
            }
            res.close();
            stt.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void tampilData(){
       try {
            koneksi();
            String sql = "SELECT id_barang,nama_barang,harga FROM tb_barang "
                    + " WHERE id_barang = '"+cmbKode.getSelectedItem()+"'";
            System.out.println(sql);
            Statement stt = conn.createStatement();
            ResultSet res = stt.executeQuery(sql);
            while (res.next()) { 
                nama = res.getString(2);
                Harga1 = Integer.parseInt(res.getString(3));
                //break;
            }
            res.close();
            stt.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
    }
    public void insert() {
        try {
            koneksi();
            Statement statement = conn.createStatement();
            statement.executeUpdate("INSERT INTO tb_transaksi(id_transaksi)"
                    + "VALUES('" + txtIDtrans.getText() + "')");
            statement.close();
            JOptionPane.showMessageDialog(null, "Berhasil Memasukan Data Nilai!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi Kesalahan Input!");
        }
        //refresh();
    }
    public void tampil() {
        DefaultTableModel tabelhead = new DefaultTableModel();
        tabelhead.addColumn("ID Keranjang");
        tabelhead.addColumn("ID Transaksi");
        tabelhead.addColumn("ID Barang");
        tabelhead.addColumn("Nama Barang");
        tabelhead.addColumn("Jumlah Barang");
        tabelhead.addColumn("Total Harga");
        try {
            koneksi();
            String sql = "SELECT a.*,b.nama_barang FROM tb_keranjang a INNER JOIN tb_barang b ON a.id_barang = b.id_barang"
                    + " WHERE id_transaksi = '"+ txtIDtrans.getText() +"'";
            System.out.println(sql);
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                tabelhead.addRow(new Object[]{res.getString(1), res.getString(2), res.getString(3), res.getString(6), res.getString(4),res.getString(5)});
            }
            tabel_keranjang.setModel(tabelhead);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "BELUM TERKONEKSI");
        }
    }
    public void insertKeranjang() {
        String IdTransaksi = txtIDtrans.getText();
        String IdBarang = (String)cmbKode.getSelectedItem();
        String JumBarang = txtJumBarang.getText();
        String TotHarga = txtTotal.getText();
        try {
            koneksi();
            String sql ="INSERT INTO tb_keranjang (id_keranjang, id_transaksi, id_barang, jumlah_barang, total_harga)"
                    + " VALUES(NULL,'" + IdTransaksi + "','" + IdBarang + "','" + JumBarang + "','" + TotHarga + "')";
            System.out.println(sql);
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi Kesalahan Input!");
        }
        tampil();
        sum();
    }
    public void sum(){
        try {
            koneksi();
            String sql = "SELECT SUM(total_harga) AS Total FROM tb_keranjang WHERE id_transaksi = '"+txtIDtrans.getText()+"'";
            System.out.println(sql);
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
            labelTotal.setText(res.getString(1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "BELUM TERKONEKSI");
        }    
    }
    public void batal(){
        txtIDtrans.setEnabled(true);
        txtIDtrans.setText(null);
        cmbKode.setSelectedIndex(0);
        txtNama.setText(null);
        txtJumBarang.setText(null);
        txtHarga.setText(null);
        txtTotal.setText(null);
        txtBayar.setText(null);
        txtKembali.setText(null);
        chcDonasi.setSelected(false);
    }
    public void InsertTransaksi(){
        String idTrans = txtIDtrans.getText();
        String TotalBayar = labelTotal.getText();
        String dibayar = txtBayar.getText();
        String kasir = labelKaryawan.getText();
        LocalDateTime tanggal = LocalDateTime.now();
        DateTimeFormatter FormatTanggal = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
        String Waktu = tanggal.format(FormatTanggal);
        try {
            koneksi();
            String sql = "UPDATE tb_transaksi SET total_bayar = '"+ TotalBayar +"', dibayar = '"+dibayar+"', nama_kasir = '"+ kasir +"',tanggal = '"+ Waktu +"' "
                    + "WHERE id_transaksi = '"+ idTrans +"'";
                    //"INSERT INTO tb_transaksi (id_transaksi,total_bayar,dibayar,nama_kasir,tanggal)"
                    //+ " VALUES('" + idTrans + "','" + TotalBayar + "','" + dibayar + "','" + kasir + "','"+ Waktu +"')";
            System.out.println(sql);
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            JOptionPane.showMessageDialog(null, "Berhasil Memasukan Data Barang!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi Kesalahan Input!");
        }
        batal();
    }
    public void refresh(){
        new GUI_Kasir().setVisible(true);
        this.setVisible(false);
    }
    public void delete(){
        int ok = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin akan menghapus ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == 0) {
            try {
                String sql = "DELETE FROM tb_keranjang WHERE id_keranjang ='" + txtIDtrans.getText() + "'";
                PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sql);
                System.out.println(sql);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data Berhasil di hapus");
                batal();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Data gagal di hapus");
            }
        }
        refresh();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField6 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        labelKaryawan = new javax.swing.JLabel();
        cmbKaryawan = new javax.swing.JComboBox<>();
        txtNama = new javax.swing.JTextField();
        labelWaktu = new javax.swing.JLabel();
        txtJumBarang = new javax.swing.JTextField();
        btnGuiTrans = new javax.swing.JButton();
        txtTotal = new javax.swing.JTextField();
        btnGuiBarang = new javax.swing.JButton();
        cmbKode = new javax.swing.JComboBox<>();
        txtHarga = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_keranjang = new javax.swing.JTable();
        btnTambah = new javax.swing.JButton();
        btnHitung = new javax.swing.JButton();
        txtIDtrans = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnTransaksi = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        labelTotal = new javax.swing.JLabel();
        txtBayar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtKembali = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnBayar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        chcDonasi = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        jTextField6.setText("jTextField6");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 51, 51));
        setForeground(new java.awt.Color(204, 0, 0));

        jPanel1.setBackground(new java.awt.Color(117, 207, 71));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/waoo2.jpg"))); // NOI18N

        labelKaryawan.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cmbKaryawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Karyawan !", "0001", "0002" }));
        cmbKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbKaryawanActionPerformed(evt);
            }
        });

        labelWaktu.setFont(new java.awt.Font("Digital-7 Mono", 1, 24)); // NOI18N
        labelWaktu.setText("waktu");

        btnGuiTrans.setText("Transaksi");
        btnGuiTrans.setPreferredSize(new java.awt.Dimension(117, 23));
        btnGuiTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuiTransActionPerformed(evt);
            }
        });

        btnGuiBarang.setText("Inventory Barang");
        btnGuiBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuiBarangActionPerformed(evt);
            }
        });

        cmbKode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masukkan Kode Barang" }));
        cmbKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbKodeActionPerformed(evt);
            }
        });

        tabel_keranjang.setBackground(new java.awt.Color(175, 212, 156));
        tabel_keranjang.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tabel_keranjang);

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnHitung.setText("Hitung");
        btnHitung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHitungActionPerformed(evt);
            }
        });

        jLabel8.setText("ID Transaksi");

        btnTransaksi.setText("Tambah Transaksi");
        btnTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransaksiActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("Total Harga");

        labelTotal.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        labelTotal.setText("0000000");
        labelTotal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelTotalMouseClicked(evt);
            }
        });

        jLabel1.setText("Kode Barang");

        jLabel2.setText("Nama Barang");

        jButton1.setText("Batal");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Jumlah Barang");

        jButton2.setText("Simpan");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Harga Jual");

        btnBayar.setText("Bayar");
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });

        jLabel5.setText("Harga Barang");

        chcDonasi.setText("Rp 500 Untuk Donasi");
        chcDonasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chcDonasiActionPerformed(evt);
            }
        });

        jLabel6.setText("Bayar");

        jLabel7.setText("Kembalian");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(cmbKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(132, 132, 132)
                                        .addComponent(labelKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel5)
                                            .addGap(76, 76, 76)
                                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtIDtrans, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel3)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(txtJumBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel2)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel1)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(cmbKode, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel4)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(btnTransaksi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnHitung, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelWaktu)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chcDonasi, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(108, 108, 108)
                                        .addComponent(btnGuiBarang)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnGuiTrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel9)
                                        .addGap(82, 82, 82)
                                        .addComponent(labelTotal))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelKaryawan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelWaktu, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtIDtrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTransaksi)
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cmbKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtJumBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHitung)
                            .addComponent(btnTambah))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(chcDonasi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelTotal)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuiBarang)
                            .addComponent(btnGuiTrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnBayar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chcDonasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chcDonasiActionPerformed
        int total, HargaTotal, Donasi;
        total = Integer.parseInt(labelTotal.getText());
        Donasi = 500;
        HargaTotal = total + Donasi;
        if (chcDonasi.isSelected()) {
            labelTotal.setText(String.valueOf(HargaTotal));
        }
    }//GEN-LAST:event_chcDonasiActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        try {
            Kasir b = new Kasir();
            b.dibayar = Integer.parseInt(txtBayar.getText());
            b.Total = Integer.parseInt(labelTotal.getText());
            txtKembali.setText(String.valueOf(b.dataKembalian()));
            if (b.dibayar < b.Total) {
                txtKembali.setText(null);
                JOptionPane.showMessageDialog(null, "UANG ANDA KURANG");
            }
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Masukkan Angka !!!", "Peringatan", 2);
            txtBayar.setText("");
        }

        //(txtKembali.setText(b.kembalian()));
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBayarActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        InsertTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        batal();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void labelTotalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelTotalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelTotalMouseClicked

    private void btnTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransaksiActionPerformed
        switch (cmbKaryawan.getSelectedIndex()) {
            case 0:
            JOptionPane.showMessageDialog(null, "Silahkan Pilih Karyawan!!!", "Perhatian", 3);
            break;
            case 1:
            Kasir1 Kasir1 = new Kasir1();
            labelKaryawan.setText(Kasir1.namaKaryawan());
            insert();
            txtIDtrans.setEnabled(false);
            break;
            case 2:
            Kasir2 Kasir2 = new Kasir2();
            labelKaryawan.setText(Kasir2.namaKaryawan());
            insert();
            txtIDtrans.setEnabled(false);
            break;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTransaksiActionPerformed

    private void btnHitungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHitungActionPerformed
        try{
            Kasir a = new Kasir();
            a.Harga = Integer.parseInt(txtHarga.getText());//txtHarga.getText();
            a.jumlahBarang = Integer.parseInt(txtJumBarang.getText());
            txtTotal.setText(Integer.toString(a.Total()));
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Masukkan Angka !!!", "Peringatan", 2);
            txtJumBarang.setText("");
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_btnHitungActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        insertKeranjang();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void cmbKodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbKodeActionPerformed
        tampilData();
        txtNama.setText(nama);
        txtHarga.setText(Integer.toString(Harga1));
    }//GEN-LAST:event_cmbKodeActionPerformed

    private void btnGuiBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuiBarangActionPerformed
        GUI_Barang barang = new GUI_Barang();
        barang.show();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuiBarangActionPerformed

    private void btnGuiTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuiTransActionPerformed
        GUI_Transaksi trans = new GUI_Transaksi();
        trans.show();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuiTransActionPerformed

    private void cmbKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbKaryawanActionPerformed

        // TODO add your handling code here:
    }//GEN-LAST:event_cmbKaryawanActionPerformed

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
            java.util.logging.Logger.getLogger(GUI_Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI_Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI_Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI_Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI_Kasir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnGuiBarang;
    private javax.swing.JButton btnGuiTrans;
    private javax.swing.JButton btnHitung;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnTransaksi;
    private javax.swing.JCheckBox chcDonasi;
    private javax.swing.JComboBox<String> cmbKaryawan;
    private javax.swing.JComboBox<String> cmbKode;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JLabel labelKaryawan;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JLabel labelWaktu;
    private javax.swing.JTable tabel_keranjang;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIDtrans;
    private javax.swing.JTextField txtJumBarang;
    private javax.swing.JTextField txtKembali;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
