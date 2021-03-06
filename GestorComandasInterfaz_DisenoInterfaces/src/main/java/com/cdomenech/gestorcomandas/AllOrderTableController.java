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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Cristina Domenech Moreno, Javier Torres Sevilla
 */
public class AllOrderTableController implements Initializable {

    @FXML
    private TableView<PedidosComanda> allOrdersTable;
    @FXML
    private TableColumn<PedidosComanda, Integer> idCol;
    @FXML
    private TableColumn<PedidosComanda, String> comandaCol;
    @FXML
    private TableColumn<PedidosComanda, String> precioTotalCol;
    @FXML
    private TableColumn<PedidosComanda, String> nombreCol;
    @FXML
    private Button btnDelivered;
    @FXML
    private Button btnDeleteOrder;
    @FXML
    private Button btnNewOrder;
    @FXML
    private HBox hBoxButtons;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnAcercaDe;
    @FXML
    private Button btnComandasScene;
    @FXML
    private Button btnMetricasScene;

    private ObservableList<PedidosComanda> listPedidosComanda;
    GestorComandas DB;

    /**
     *
     * @throws IOException
     */
    public AllOrderTableController() throws IOException {
        this.DB = new GestorComandas();
    }

    /**
     * Method that refresh the data in the table
     */
    public void updateTable() {
        // Set the data that is going to be shown in the TableView
        idCol.setCellValueFactory(new PropertyValueFactory<PedidosComanda, Integer>("id"));
        comandaCol.setCellValueFactory(new PropertyValueFactory<PedidosComanda, String>("cantidadYProducto"));
        precioTotalCol.setCellValueFactory(new PropertyValueFactory<PedidosComanda, String>("precioTotal"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<PedidosComanda, String>("nombre"));

        try {
            // Get select of all order from the database
            listPedidosComanda = DB.getPedidosComanda();
            // Final set
            allOrdersTable.setItems(listPedidosComanda);
        } catch (IOException ex) {
            Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set an specific width depending of the width of the TableView
        idCol.prefWidthProperty().bind(allOrdersTable.widthProperty().divide(9)); // w * 1/9
        comandaCol.prefWidthProperty().bind(allOrdersTable.widthProperty().divide(2)); // w * 1/2
        precioTotalCol.prefWidthProperty().bind(allOrdersTable.widthProperty().divide(5)); // w * 1/5
        nombreCol.prefWidthProperty().bind(allOrdersTable.widthProperty().divide(5)); // w * 1/5
        updateTable();
    }

    /**
     * Method that get the id of a selected row and send it to set it as
     * delivered
     *
     * @param event
     */
    @FXML
    private void updateDelivered(ActionEvent event) {
        PedidosComanda selectedComanda = allOrdersTable.getSelectionModel().getSelectedItem();
        DB.updateComandaEntregada(selectedComanda.getId());
        // Update TableView
        updateTable();
    }

    /**
     * Method that get the id of a selected row and send it to be deleted
     *
     * @param event
     */
    @FXML
    private void deleteComanda(ActionEvent event) {
        // Alert dialog to warn the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Eliminar comanda");
        alert.setContentText("¿Deseas eliminar esta comanda?");
        ButtonType okButton = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);
        alert.showAndWait();

        if (alert.getResult().getButtonData().equals(okButton.getButtonData())) {
            PedidosComanda selectedComanda = allOrdersTable.getSelectionModel().getSelectedItem();
            DB.deleteComanda(selectedComanda.getId());
            updateTable();
        }
    }

    /**
     * Open a window to create a new order
     *
     * @param event
     */
    @FXML
    private void newOrderWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newOrderView.fxml"));

            Parent root = fxmlLoader.load();

            NewOrderController controller = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false); // perfect size -> 495,750
            stage.setTitle("Añadir nueva comanda");
            Image image = new Image("images/restaurant.png");
            stage.getIcons().add(image);
            // User can not do anything until the window is closed
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            updateTable();
        } catch (IOException ex) {
            Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Open a window to edit a selected order
     *
     * @param event
     */
    @FXML
    private void editComanda(ActionEvent event) throws IOException {
        PedidosComanda selectedComandaToEdit = allOrdersTable.getSelectionModel().getSelectedItem();
        int id_comanda = selectedComandaToEdit.getId();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editOrderView.fxml"));

            Parent root = fxmlLoader.load();

            EditOrderController controller = fxmlLoader.getController();
            // Send through the controller the id of the selected order
            controller.initData(id_comanda);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false); // perfect size -> 495,750
            stage.setTitle("Editar comanda");
            Image image = new Image("images/restaurant.png");
            stage.getIcons().add(image);
            // User can not do anything until the window is closed
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            updateTable();
        } catch (IOException ex) {
            Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Window that shows our crew names
     * 
     * @param event 
     */
    @FXML
    private void acercaDeWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("acercaDeView.fxml"));

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false); // perfect size -> 495,750
            stage.setTitle("Acerca de...");
            Image image = new Image("images/restaurant.png");
            stage.getIcons().add(image);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Open a window with all the details of an specific order
     * 
     * @param event 
     */
    @FXML
    private void openDetails(MouseEvent event) {
        // EventHandler that is waiting a double click to open the window
        allOrdersTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getClickCount() == 2) {
                    PedidosComanda selectedComanda = allOrdersTable.getSelectionModel().getSelectedItem();
                    int id_comanda = selectedComanda.getId();
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("detailView.fxml"));

                        Parent root = fxmlLoader.load();

                        DetailController controller = fxmlLoader.getController();
                        // Send through the controller the id of the selected order
                        controller.initDataDetails(id_comanda);

                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.setResizable(false); // perfect size -> 495,750
                        stage.setTitle("Detalles de la comanda");
                        Image image = new Image("images/restaurant.png");
                        stage.getIcons().add(image);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();
                    } catch (IOException ex) {
                        Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    @FXML
    private void switchComandaScene(ActionEvent event) {
        // Do nothing
    }
    /**
     * Switch to the statistics root
     * 
     * @param event 
     */
    @FXML
    private void switchMetricasScene(ActionEvent event) {
        try {
            App.setRoot("barChart");
        } catch (IOException ex) {
            Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
