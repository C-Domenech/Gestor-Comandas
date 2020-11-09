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
package com.cdomenech.gestorcomandas_ui;

import com.cdomenech.database.GestorComandas;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * @author Cristina Domenech <linkedin.com/in/c-domenech/>
 */
public class EditOrderController implements Initializable {

    private TextField tfName;
    private ComboBox<Producto> comboBoxProduct;
    private ComboBox<Integer> comboBoxNumber;
    private TableView<ProductoPedido> orderTable;
    @FXML
    private TableColumn<ProductoPedido, String> productoCol;
    @FXML
    private TableColumn<ProductoPedido, Integer> cantidadCol;
    @FXML
    private TableColumn<ProductoPedido, String> precioCol;
    @FXML
    private Label lbTotalAmount;
    private Button btnUpdate;

    private ObservableList<Producto> listProductos;
    private ObservableList<Integer> listNumbers;

    private ObservableList<ProductoPedido> listProductosComanda;

    private ObservableList<ProductoPedido> listProductosSeleccionados = FXCollections.observableArrayList();
    private double totalAmount;
    private LinkedHashMap<Integer, Integer> productsPedidoCantidadLHM = new LinkedHashMap<Integer, Integer>();
    private ArrayList<Integer> productoToDelete = new ArrayList<>();
    private int idComandaToBeEdited;
    GestorComandas DB;

    public EditOrderController() throws IOException {
        this.DB = new GestorComandas();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            listProductos = DB.getProductos();
            comboBoxProduct.setItems(listProductos);

            listNumbers = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            comboBoxNumber.setItems(listNumbers);

        } catch (IOException ex) {
            Logger.getLogger(EditOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initData(int id_comanda) throws IOException {
        this.idComandaToBeEdited = id_comanda;
        Comanda c = new Comanda(DB.showNameCliente(id_comanda));
        tfName.setText(c.getNombreCliente());
        
        listProductosComanda = DB.showComandaData(id_comanda);
        productoCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("nombre"));
        cantidadCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, Integer>("cantidad"));
        precioCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("precioString"));

        orderTable.setItems(listProductosComanda);
        listProductosSeleccionados = listProductosComanda;
        for (ProductoPedido p : listProductosComanda) {
            productsPedidoCantidadLHM.put(p.getIdProducto(), p.getCantidad());
            totalAmount += p.getPrecio();
            System.out.println("LHM: " + productsPedidoCantidadLHM);
        }
        lbTotalAmount.setText(String.format("%,.2f", totalAmount) + " €");
    }

    private void addProduct(ActionEvent event) {
        if (checkFields()) {
            Producto selectedProduct = comboBoxProduct.getValue();
            int selectedNumber = comboBoxNumber.getValue();

            int idselectedProduct = selectedProduct.getId();
            ProductoPedido p = new ProductoPedido(selectedProduct.getId(), selectedProduct.getNombre(), selectedNumber, selectedProduct.getPrecio() * selectedNumber);
            if (productsPedidoCantidadLHM.containsKey(idselectedProduct) || listProductosSeleccionados.contains(p)) {
                for (ProductoPedido pp : listProductosSeleccionados) {
                    if (pp.getIdProducto() == p.getIdProducto()) {
                        pp.setCantidad(pp.getCantidad() + selectedNumber);
                        //System.out.println("pp cantidad: " + pp.getCantidad());
                        //System.out.println("va a entrar " + precioCantidad);
                        pp.setPrecio(selectedProduct.getPrecio() * pp.getCantidad());
                        //System.out.println("pp precio: " + pp.getPrecio());
                        //System.out.println("pp precioString: " + pp.getPrecioString());
                        orderTable.refresh();
                    }
                }
                productsPedidoCantidadLHM.put(idselectedProduct, productsPedidoCantidadLHM.get(idselectedProduct) + selectedNumber);
                System.out.println("productsPedidoCantidadLHM: " + productsPedidoCantidadLHM);
            } else {
                listProductosSeleccionados.add(p);

                productsPedidoCantidadLHM.put(idselectedProduct, selectedNumber);
                System.out.println("productsPedidoCantidadLHM: " + productsPedidoCantidadLHM);
            }
            productoCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("nombre"));
            cantidadCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, Integer>("cantidad"));
            precioCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("precioString"));
            orderTable.setItems(listProductosSeleccionados);

            totalAmount += selectedProduct.getPrecio() * selectedNumber;
            lbTotalAmount.setText(String.format("%,.2f", totalAmount) + " €");

//            comboBoxProduct.getSelectionModel().clearAndSelect(-1);
//            comboBoxProduct.setPromptText("Selecciona un producto");
//            comboBoxNumber.getSelectionModel().clearAndSelect(-1);
//            comboBoxNumber.setPromptText("Cantidad");
        }
    }

    private boolean checkFields() {
        return !(tfName.getText().isBlank() || comboBoxProduct.getSelectionModel().isEmpty() || comboBoxNumber.getSelectionModel().isEmpty());
    }

    private void deleteProductoPedido(ActionEvent event) {
        ProductoPedido selectedRow = orderTable.getSelectionModel().getSelectedItem();

        totalAmount -= selectedRow.getPrecio();
        productoToDelete.add(selectedRow.getIdProducto());
        productsPedidoCantidadLHM.remove(selectedRow.getIdProducto());
        System.out.println("productsPedidoCantidadLHM: " + productsPedidoCantidadLHM);

        orderTable.getItems().remove(selectedRow);
        lbTotalAmount.setText(String.format("%,.2f", totalAmount) + " €");

    }

    private void editComandaAndPedido(ActionEvent event) {
        if (checkTable()) {
            for (Map.Entry<Integer, Integer> mapElement : productsPedidoCantidadLHM.entrySet()) {
                DB.updateComandaEdited(idComandaToBeEdited, mapElement.getKey(), mapElement.getValue());
            }
            for (Integer id_producto : productoToDelete) {
                try {
                    DB.deletePedido(idComandaToBeEdited, id_producto);
                } catch (SQLException ex) {
                    Logger.getLogger(EditOrderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
            tfName.setText("");
            // comboBoxProduct.getSelectionModel().clearAndSelect(-1);
            // comboBoxProduct.setPromptText("Selecciona un producto");
            // comboBoxNumber.getSelectionModel().clearAndSelect(-1);
            // comboBoxNumber.setPromptText("Cantidad");
            orderTable.getItems().clear();
            productsPedidoCantidadLHM.clear();
            lbTotalAmount.setText("0,00 €");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Información");
            alert.setContentText("Comanda modificada correctamente");
            alert.showAndWait();
            Stage stage = (Stage) btnUpdate.getScene().getWindow();
            stage.close();

        }
    }

    private boolean checkTable() {
        return !productsPedidoCantidadLHM.isEmpty();
    }

}
