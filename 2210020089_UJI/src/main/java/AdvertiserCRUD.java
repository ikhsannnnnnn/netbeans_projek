/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rikogimin
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdvertiserCRUD extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtContact, txtPhone, txtEmail, txtAddress;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public AdvertiserCRUD() {
        setTitle("CRUD Pengiklan");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel atas untuk form input
        JPanel panelInput = new JPanel(new GridLayout(6, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelInput.add(new JLabel("Nama:"));
        txtName = new JTextField();
        panelInput.add(txtName);

        panelInput.add(new JLabel("Contact Person:"));
        txtContact = new JTextField();
        panelInput.add(txtContact);

        panelInput.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        panelInput.add(txtPhone);

        panelInput.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelInput.add(txtEmail);

        panelInput.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panelInput.add(txtAddress);

        // Panel untuk tombol
        JPanel panelButtons = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnClear);

        panelInput.add(panelButtons);

        add(panelInput, BorderLayout.NORTH);

        // Table untuk menampilkan data
        table = new JTable();
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Contact Person", "Phone", "Email", "Address"}, 0);
        table.setModel(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load data awal
        loadData();

        // Event Listener
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAdvertiser();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAdvertiser();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAdvertiser();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        // Click pada table untuk mengisi form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtName.setText(model.getValueAt(selectedRow, 1).toString());
                    txtContact.setText(model.getValueAt(selectedRow, 2).toString());
                    txtPhone.setText(model.getValueAt(selectedRow, 3).toString());
                    txtEmail.setText(model.getValueAt(selectedRow, 4).toString());
                    txtAddress.setText(model.getValueAt(selectedRow, 5).toString());
                }
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Advertisers")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("AdvertiserID"),
                        rs.getString("Name"),
                        rs.getString("ContactPerson"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        rs.getString("Address")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    private void addAdvertiser() {
        String name = txtName.getText();
        String contact = txtContact.getText();
        String phone = txtPhone.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!");
            return;
        }

        String sql = "INSERT INTO Advertisers (Name, ContactPerson, Phone, Email, Address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, address);
            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding data: " + ex.getMessage());
        }
    }

    private void updateAdvertiser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diupdate!");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String name = txtName.getText();
        String contact = txtContact.getText();
        String phone = txtPhone.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!");
            return;
        }

        String sql = "UPDATE Advertisers SET Name=?, ContactPerson=?, Phone=?, Email=?, Address=? WHERE AdvertiserID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, address);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage());
        }
    }

    private void deleteAdvertiser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Advertisers WHERE AdvertiserID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting data: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtContact.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdvertiserCRUD().setVisible(true);
            }
        });
    }
}

