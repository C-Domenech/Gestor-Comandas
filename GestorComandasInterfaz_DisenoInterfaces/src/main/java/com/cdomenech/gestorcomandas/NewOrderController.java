/*
 * Copyright (C) 2020 Cristina Domenech <linkedin.com/in/c-domenech/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cdomenech.gestorcomandas;

import com.cdomenech.database.GestorComandas;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Cristina Domenech Moreno, Javier Torres Sevilla
 */
public class NewOrderController implements Initializable {

    @FXML
    private TextField tfName;
    @FXML
    private ComboBox<Producto> comboBoxProduct;
    @FXML
    private ComboBox<Integer> comboBoxNumber;
    @FXML
    private Button btnAdd;
    @FXML
    private TableView<ProductoPedido> orderTable;
    @FXML
    private TableColumn<ProductoPedido, String> productoCol;
    @FXML
    private TableColumn<ProductoPedido, Integer> cantidadCol;
    @FXML
    private TableColumn<ProductoPedido, String> precioCol;
    @FXML
    private Label lbTotalAmount;
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnDelete;

    private ObservableList<Producto> listProductos;
    private ObservableList<Integer> listNumbers;
    private ObservableList<ProductoPedido> listProductosSeleccionados = FXCollections.observableArrayList();
    private double totalAmount;
    // LinkedHashMap save the id of a product and the quantity of that product in the added order
    private LinkedHashMap<Integer, Integer> productsPedidoCantidadLHM = new LinkedHashMap<Integer, Integer>();
    GestorComandas DB;

    /**
     *
     * @throws IOException
     */
    public NewOrderController() throws IOException {
        this.DB = new GestorComandas();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Set products ComboBox
            listProductos = DB.getProductos();
            comboBoxProduct.setItems(listProductos);
            // Set numbers ComboBox
            listNumbers = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            comboBoxNumber.setItems(listNumbers);
        } catch (IOException ex) {
            Logger.getLogger(NewOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Add a product to the table and the LinkedHashMap
     *
     * @param event
     */
    @FXML
    private void addProduct(ActionEvent event) {
        // Check there is no empty fields
        if (checkFields()) {
            Producto selectedProduct = comboBoxProduct.getValue();
            int selectedNumber = comboBoxNumber.getValue();

            int idselectedProduct = selectedProduct.getId();
            ProductoPedido p = new ProductoPedido(selectedProduct.getId(), selectedProduct.getNombre(), selectedNumber, selectedProduct.getPrecio() * selectedNumber);
            // If the key/id of the product is in the LinkedHashMap, we update the value/quantity
            if (productsPedidoCantidadLHM.containsKey(idselectedProduct) || listProductosSeleccionados.contains(p)) {
                for (ProductoPedido pp : listProductosSeleccionados) {
                    if (pp.getIdProducto() == p.getIdProducto()) {
                        // Update quantity in the POJO class
                        pp.setCantidad(pp.getCantidad() + selectedNumber);
                        // Update amount in the POJO class
                        pp.setPrecio(selectedProduct.getPrecio() * pp.getCantidad());
                        // Refresh data
                        orderTable.refresh();
                    }
                }
                productsPedidoCantidadLHM.put(idselectedProduct, productsPedidoCantidadLHM.get(idselectedProduct) + selectedNumber);
            } else {
                // Add the new object
                listProductosSeleccionados.add(p);
                // If the id is not in the LinkedHashMap
                productsPedidoCantidadLHM.put(idselectedProduct, selectedNumber);
            }
            // Set data in the table
            productoCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("nombre"));
            cantidadCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, Integer>("cantidad"));
            precioCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("precioString"));
            orderTable.setItems(listProductosSeleccionados);
            // Set total amount
            totalAmount += selectedProduct.getPrecio() * selectedNumber;
            lbTotalAmount.setText(String.format("%,.2f", totalAmount) + " €");

            //comboBoxProduct.getSelectionModel().clearAndSelect(-1);
            //comboBoxProduct.setPromptText("Selecciona un producto");
            //comboBoxNumber.getSelectionModel().clearAndSelect(-1);
            //comboBoxNumber.setPromptText("Cantidad");
        }
    }

    /**
     * Check that there is not empty fields
     *
     * @return boolean
     */
    private boolean checkFields() {
        return !(tfName.getText().isBlank() || comboBoxProduct.getSelectionModel().isEmpty() || comboBoxNumber.getSelectionModel().isEmpty());
    }
    /**
     * Method that remove an element from the list and table
     * 
     * @param event 
     */
    @FXML
    private void deleteProductoPedido(ActionEvent event) {
        ProductoPedido selectedRow = orderTable.getSelectionModel().getSelectedItem();

        totalAmount -= selectedRow.getPrecio();
        productsPedidoCantidadLHM.remove(selectedRow.getIdProducto());
        System.out.println("productsPedidoCantidadLHM: " + productsPedidoCantidadLHM);

        orderTable.getItems().remove(selectedRow);
        lbTotalAmount.setText(String.format("%,.2f", totalAmount) + " €");

    }
    /**
     * Insert all the products that the user ordered into the database
     * 
     * @param event 
     */
    @FXML
    private void newComandaAndPedido(ActionEvent event) {
        if (checkTable()) {
            String nameClient = tfName.getText();
            // First -> create the main order with the name of the client 
            DB.insertNewComanda(nameClient);
            // Second -> create as orders as products
            for (Map.Entry<Integer, Integer> mapElement : productsPedidoCantidadLHM.entrySet()) {
                // Iter the LinkedHashMap sending key/id_product and value/cantidad
                DB.insertPedidoToLastNewComanda(mapElement.getKey(), mapElement.getValue());
            }
            // Reset everything and close the window
            tfName.setText("");
            orderTable.getItems().clear();
            productsPedidoCantidadLHM.clear();
            lbTotalAmount.setText("0,00 €");
            // Alert dialog that inform about the success of the operation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Información");
            alert.setContentText("Comanda añadida correctamente");
            alert.showAndWait();
            Stage stage = (Stage) btnOrder.getScene().getWindow();
            stage.close();

        }
    }
    /**
     * Check that the table is not empty
     * 
     * @return boolean
     */
    private boolean checkTable() {
        return !productsPedidoCantidadLHM.isEmpty();
    }

}
