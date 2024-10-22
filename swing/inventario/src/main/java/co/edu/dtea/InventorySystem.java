package co.edu.dtea;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;

public class InventorySystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField codeField, productField, priceField, quantityField;
    private JTable inventoryTable;
    private DefaultTableModel inventoryModel;
    private HashMap<String, Product> inventory; // usar el codigo como clave
    private static final String FILE_NAME = "inventory.csv";

    // ventana principal y los paneles
    public InventorySystem() {
        setTitle("Sistema de Inventario");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        inventory = new HashMap<>();

        createLoginPanel();
        createInventoryPanel();

        loadInventory(); // cargar inventario al iniciar

        add(mainPanel);
        setLocationRelativeTo(null); // centrar la ventana
        setResizable(false);
        mainPanel.setBackground(new Color(230, 230, 250)); 
    }

    // panel pa inicio de sesion
    private void createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 240, 255));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Inicio de Sesión"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // espaciadpo

        // logo
        // agarramos el logo original
        ImageIcon originalIcon = new ImageIcon("swing\\inventario\\src\\main\\java\\co\\edu\\dtea\\inicio_sesion.png");

        // cambiamos en tamaño de la imagen
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH); 

        // aqui se cambia el imageIcon por el q ya se cambio el tamaño
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        // jlabel con el logo cambiado de tamaño
        JLabel logoLabel = new JLabel(resizedIcon);

               
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        loginPanel.add(logoLabel, gbc);

        // usuario
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Usuario:"), gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        // contarseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Contraseña:"), gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // boton inicio sesion
        JButton loginButton = new JButton("Iniciar Sesion");
        loginButton.setBackground(new Color(100, 149, 237)); // Colorear el botón
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(new LoginAction());
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        mainPanel.add(loginPanel, "Login");
    }

    // panel de inventario
    private void createInventoryPanel() {
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BorderLayout());
        inventoryPanel.setBackground(new Color(240, 240, 255));
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("Inventario"));

        // tabla
        String[] columnNames = {"Codigo", "Producto", "Precio", "Cantidad"};
        inventoryModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(inventoryModel);
        inventoryTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 2)); 
        inputPanel.setBackground(new Color(240, 240, 255));

        inputPanel.add(new JLabel("Codigo:"));
        codeField = new JTextField();
        inputPanel.add(codeField);

        inputPanel.add(new JLabel("Producto:"));
        productField = new JTextField();
        inputPanel.add(productField);

        inputPanel.add(new JLabel("Precio por unidad:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Cantidad:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        JButton addButton = new JButton("Agregar Producto");
        addButton.setBackground(new Color(34, 139, 34)); 
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new AddProductAction());
        inputPanel.add(addButton);

        JButton removeButton = new JButton("Quitar Producto");
        removeButton.setBackground(new Color(220, 20, 60)); 
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(new RemoveProductAction());
        inputPanel.add(removeButton);

        JButton deleteButton = new JButton("Eliminar Producto");
        deleteButton.setBackground(new Color(255, 69, 0)); 
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new DeleteProductAction());
        inputPanel.add(deleteButton);

        
        JButton updateButton = new JButton("Actualizar Inventario");
        updateButton.setBackground(new Color(100, 149, 237)); 
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(new UpdateInventoryAction());
        inputPanel.add(updateButton);

        inventoryPanel.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(inventoryPanel, "Inventory");
    }

    // verificar inicio de sesion
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (password.equals("123")) {
                cardLayout.show(mainPanel, "Inventory");
            } else {
                JOptionPane.showMessageDialog(null, "Contraseña incorrecta, intenta de nuevo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // agregar producto
    private class AddProductAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String productCode = codeField.getText();
            String productName = productField.getText();
            double price;
            int quantity;

            try {
                price = Double.parseDouble(priceField.getText());
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Precio o cantidad invalidos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product product = inventory.get(productCode);
            if (product != null) {
                product.addQuantity(quantity);
            } else {
                product = new Product(productCode, productName, price, quantity);
                inventory.put(productCode, product);
                inventoryModel.addRow(new Object[]{productCode, productName, price, quantity});
            }

            clearFields();
            saveInventory(); // guarda el inventario luego de agregar alguna cosa
        }
    }

    // quita producto pero no lo elimina, solo para quitar cantidad
    private class RemoveProductAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String productCode = codeField.getText();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Cantidad invalida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product product = inventory.get(productCode);
            if (product != null) {
                product.removeQuantity(quantity);
                if (product.getQuantity() < 0) {
                    product.setQuantity(0); // No permitir cantidades negativas
                }
                updateInventoryTable();
            }

            clearFields();
            saveInventory(); // se guarda el inventario después de quitar cantidades
        }
    }

    // elimina producto, directamente lo quita de la tabla
    private class DeleteProductAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String productCode = codeField.getText();
            if (inventory.remove(productCode) != null) {
                removeProductFromTable(productCode);
                saveInventory(); // guarda el inventario luego de eliminar un producto
                clearFields();
            } else {
                JOptionPane.showMessageDialog(null, "Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // boton pa actualizar el inventario
    private class UpdateInventoryAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateInventoryTable(); 
            JOptionPane.showMessageDialog(null, "Inventario actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateInventoryTable() {
        inventoryModel.setRowCount(0); // limpia la tabla
        for (Product product : inventory.values()) {
            inventoryModel.addRow(new Object[]{product.getCode(), product.getName(), product.getPrice(), product.getQuantity()});
        }
    }

    private void removeProductFromTable(String productCode) {
        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            if (inventoryModel.getValueAt(i, 0).equals(productCode)) {
                inventoryModel.removeRow(i);
                break;
            }
        }
    }

    private void clearFields() {
        codeField.setText("");
        productField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }

    // para guardar el inventario
    private void saveInventory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product product : inventory.values()) {
                writer.write(product.getCode() + "," + product.getName() + "," + product.getPrice() + "," + product.getQuantity());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el inventario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // carga el inventario
    private void loadInventory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String code = data[0];
                String name = data[1];
                double price = Double.parseDouble(data[2]);
                int quantity = Integer.parseInt(data[3]);
                Product product = new Product(code, name, price, quantity);
                inventory.put(code, product);
                inventoryModel.addRow(new Object[]{code, name, price, quantity});
            }
        } catch (IOException e) {
            // si el archivo no existe o hay un error, se ignora
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en el formato de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class Product {
        private String code; 
        private String name;
        private double price;
        private int quantity;

        public Product(String code, String name, double price, int quantity) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public void addQuantity(int quantity) {
            this.quantity += quantity;
        }

        public void removeQuantity(int quantity) {
            this.quantity -= quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity; 
        }

        public int getQuantity() {
            return quantity;
        }

        public String getCode() {
            return code; 
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return code + ": " + name + " - Precio: " + price + ", Cantidad: " + quantity;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InventorySystem frame = new InventorySystem();
            frame.setVisible(true);
        });
    }
}
