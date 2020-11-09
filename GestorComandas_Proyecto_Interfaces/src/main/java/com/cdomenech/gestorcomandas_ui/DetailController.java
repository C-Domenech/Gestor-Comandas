package com.cdomenech.gestorcomandas_ui;

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
import com.cdomenech.database.GestorComandas;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Cristina Domenech <linkedin.com/in/c-domenech/>
 */
public class DetailController implements Initializable {

    @FXML
    private Label lbName;
    @FXML
    private TableView<ProductoPedido> detailTable;
    @FXML
    private TableColumn<ProductoPedido, String> productoCol;
    @FXML
    private TableColumn<ProductoPedido, Integer> cantidadCol;
    @FXML
    private TableColumn<ProductoPedido, String> precioCol;
    @FXML
    private Label lbTotalAmount;

    private ObservableList<ProductoPedido> listProductosComanda;

    private int idComanda;
    private double totalAmount;
    GestorComandas DB;

    public DetailController() throws IOException {
        this.DB = new GestorComandas();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void initDataDetails(int id_comanda) throws IOException {
        listProductosComanda = DB.showComandaData(id_comanda);
        Comanda c = new Comanda(DB.showNameCliente(id_comanda));
        lbName.setText("Nombre: " + c.getNombreCliente());

        listProductosComanda = DB.showComandaData(id_comanda);
        productoCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("nombre"));
        cantidadCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, Integer>("cantidad"));
        precioCol.setCellValueFactory(new PropertyValueFactory<ProductoPedido, String>("precioString"));

        detailTable.setItems(listProductosComanda);
        for (ProductoPedido p : listProductosComanda) {
            totalAmount += p.getPrecio();
        }
        lbTotalAmount.setText(String.format("%,.2f", totalAmount) + " €");
    }

}
