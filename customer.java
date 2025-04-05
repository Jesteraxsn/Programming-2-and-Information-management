import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class customer extends JFrame {
    private JTextField IDTextField;
    private JTextField NameTextField;
    private JTextField AddressTextField;
    private JTextField NumTextField;
    private JButton saveButton;
    private JButton resetButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTable customerTable;
    private JButton availCarButton;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private List<Customer> customers = new ArrayList<>();
    private int selectedRow = -1;
    private static customer instance;
    private static Customer selectedCustomer;
    public customer() {
        setTitle("Put your Information");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setupLayout();
        attachEventListeners();
        addSampleData();
        instance = this;
        setVisible(true);
    }
    private void initComponents() {
        IDTextField = new JTextField(20);
        NameTextField = new JTextField(20);
        AddressTextField = new JTextField(20);
        NumTextField = new JTextField(20);
        saveButton = new JButton("Save");
        resetButton = new JButton("Reset");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        availCarButton = new JButton("Available Car ");
        String[] columnNames = {"ID", "Name", "Address", "Number"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        scrollPane = new JScrollPane(customerTable);
        customerTable.setFillsViewportHeight(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        availCarButton.setEnabled(false);
    }
    private void setupLayout() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        formPanel = new JPanel(new GridBagLayout());
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Your  ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(IDTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(" Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(NameTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel(" Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(AddressTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel(" Phone Num:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(NumTextField, gbc);
        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(availCarButton);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);
    }
    private void attachEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    saveCustomer();
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    updateCustomer();
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        availCarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRow >= 0) {
                    selectedCustomer = customers.get(selectedRow);
                    setVisible(false);
                    manage carManageWindow = new manage();
                    carManageWindow.setVisible(true);
                    carManageWindow.enableRentButton();
                } else {
                    JOptionPane.showMessageDialog(customer.this,
                            "Please select a customer first!",
                            "No Customer Selected",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = customerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateForm(selectedRow);
                    editButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    saveButton.setEnabled(false);
                    availCarButton.setEnabled(true);
                    IDTextField.setEditable(false);
                }
            }
        });
    }
    private boolean validateInputs() {
        if (IDTextField.getText().trim().isEmpty() ||
                NameTextField.getText().trim().isEmpty() ||
                AddressTextField.getText().trim().isEmpty() ||
                NumTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(IDTextField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Customer ID must be a number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String phone = NumTextField.getText().trim();
        if (!phone.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "Phone number should be 11 digits!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private void saveCustomer() {
        String id = IDTextField.getText().trim();
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                JOptionPane.showMessageDialog(this, "Customer ID already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        Customer customer = new Customer(
                id,
                NameTextField.getText().trim(),
                AddressTextField.getText().trim(),
                NumTextField.getText().trim()
        );
        customers.add(customer);
        addCustomerToTable(customer);
        JOptionPane.showMessageDialog(this, "Customer saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }
    private void updateCustomer() {
        if (selectedRow >= 0) {
            String id = IDTextField.getText().trim();
            Customer customer = customers.get(selectedRow);
            customer.setName(NameTextField.getText().trim());
            customer.setAddress(AddressTextField.getText().trim());
            customer.setNumber(NumTextField.getText().trim());
            tableModel.setValueAt(customer.getName(), selectedRow, 1);
            tableModel.setValueAt(customer.getAddress(), selectedRow, 2);
            tableModel.setValueAt(customer.getNumber(), selectedRow, 3);
            JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        }
    }
    private void deleteCustomer() {
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this customer?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                customers.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            }
        }
    }
    private void clearForm() {
        IDTextField.setText("");
        NameTextField.setText("");
        AddressTextField.setText("");
        NumTextField.setText("");
        selectedRow = -1;
        customerTable.clearSelection();
        IDTextField.setEditable(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(true);
        availCarButton.setEnabled(false);
        IDTextField.requestFocus();
    }
    private void populateForm(int row) {
        Customer customer = customers.get(row);

        IDTextField.setText(customer.getId());
        NameTextField.setText(customer.getName());
        AddressTextField.setText(customer.getAddress());
        NumTextField.setText(customer.getNumber());
    }
    private void addCustomerToTable(Customer customer) {
        tableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getNumber()
        });
    }
    private void addSampleData() {
        Customer c1 = new Customer("2043", "John Lester B. Tupas", "Bangkal, Lemery, Iloilo", "0961821011");
        Customer c2 = new Customer("2406", "Froi C. Razonable", "Balingasa, Balintawak, Quezon City", "16342066669");
        Customer c3 = new Customer("1003", "Mon Christian Meriones", "Lopez Jaena Street, Pototan, Iloilo", "21314566788");
        customers.add(c1);
        customers.add(c2);
        customers.add(c3);
        addCustomerToTable(c1);
        addCustomerToTable(c2);
        addCustomerToTable(c3);
    }
    public static void returnToCustomerScreen() {
        if (instance != null) {
            instance.setVisible(true);
            instance.clearForm();
        }
    }
    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    public static class Customer {
        private String id;
        private String name;
        private String address;
        private String number;
        public Customer(String id, String name, String address, String number) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.number = number;
        }
        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public String getNumber() {
            return number;
        }
        public void setNumber(String phone) {
            this.number = number;
        }
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new customer();
            }
        });
    }
}