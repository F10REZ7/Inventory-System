package co.edu.tdea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class InventorySystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField productField, priceField, quantityField;
    private JTextArea inventoryArea;
    private HashMap<String, Product> inventory;

    public InventorySystem() {
        setTitle("Sistema de Inventario");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        inventory = new HashMap<>();

        createLoginPanel();
        createInventoryPanel();

        add(mainPanel);
    }

    private void createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));

        loginPanel.add(new JLabel("Usuario:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(new LoginAction());
        loginPanel.add(loginButton);

        mainPanel.add(loginPanel, "Login");
    }

    private void createInventoryPanel() {
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BorderLayout());

        inventoryArea = new JTextArea(10, 30);
        inventoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(inventoryArea);
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

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
        addButton.addActionListener(new AddProductAction());
        inputPanel.add(addButton);

        JButton removeButton = new JButton("Quitar Producto");
        removeButton.addActionListener(new RemoveProductAction());
        inputPanel.add(removeButton);

        inventoryPanel.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(inventoryPanel, "Inventory");
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (password.equals("123")) {
                cardLayout.show(mainPanel, "Inventory");
            } else {
                JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
            }
        }
    }

    private class AddProductAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String productName = productField.getText();
            double price;
            int quantity;

            try {
                price = Double.parseDouble(priceField.getText());
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Precio o cantidad inválidos");
                return;
            }

            Product product = inventory.get(productName);
            if (product == null) {
                inventory.put(productName, new Product(productName, price, quantity));
            } else {
                product.addQuantity(quantity);
            }

            updateInventoryArea();
            clearFields();
        }
    }

    private class RemoveProductAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String productName = productField.getText();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Cantidad inválida");
                return;
            }

            Product product = inventory.get(productName);
            if (product != null) {
                product.removeQuantity(quantity);
                if (product.getQuantity() <= 0) {
                    inventory.remove(productName);
                }
            }

            updateInventoryArea();
            clearFields();
        }
    }

    private void updateInventoryArea() {
        inventoryArea.setText("");
        for (Product product : inventory.values()) {
            inventoryArea.append(product.toString() + "\n");
        }
    }

    private void clearFields() {
        productField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }

    private class Product {
        private String name;
        private double price;
        private int quantity;

        public Product(String name, double price, int quantity) {
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

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return name + " - Precio: " + price + ", Cantidad: " + quantity;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InventorySystem frame = new InventorySystem();
            frame.setVisible(true);
        });
    }
}