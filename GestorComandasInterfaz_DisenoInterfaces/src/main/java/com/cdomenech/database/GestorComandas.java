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
package com.cdomenech.database;

import com.cdomenech.gestorcomandas.PedidosComanda;
import com.cdomenech.gestorcomandas.Producto;
import com.cdomenech.gestorcomandas.ProductoPedido;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

/**
 *
 * @author  Cristina Domenech Moreno, Javier Torres Sevilla
 */
public final class GestorComandas {

    private Connection conn;
    private Statement stmt;
    private PreparedStatement preparedStmt;
    private ResultSet generatedKeys;
    private int idLastComanda;

    private final String selectAllComandas = "SELECT DISTINCT co.id_comanda AS \"id\", ( SELECT GROUP_CONCAT(\" \", pe.cantidad, \" \", pr.nombre) FROM pedido pe, producto pr WHERE pe.id_producto = pr.id_producto AND pe.id_comanda = co.id_comanda ) AS \"cantidadYProducto\", ( SELECT SUM(pr.precio * pe.cantidad) FROM producto pr, pedido pe WHERE pe.id_producto = pr.id_producto AND pe.id_comanda = co.id_comanda ) AS \"precioTotal\", co.nombre_cliente AS \"nombre\" FROM comanda co, pedido pe, producto pr WHERE co.id_comanda = pe.id_comanda AND pe.id_producto = pr.id_producto AND DATE(co.fecha) = CURRENT_DATE AND co.entregada = FALSE ORDER BY co.id_comanda ";
    private final String selectAllProductos = "SELECT id_producto, nombre, precio FROM producto";
    private final String insertNewComanda = "INSERT INTO comanda (nombre_cliente) VALUES (?)";
    private final String insertPedidoToComanda = "INSERT INTO pedido (id_comanda, id_producto, cantidad) VALUES (?, ?, ?)";
    private final String deleteComanda = "DELETE FROM comanda WHERE id_comanda = ?";
    private final String updateComandaEntregada = "UPDATE comanda co SET co.precio =( SELECT SUM(pr.precio * pe.cantidad) FROM producto pr, pedido pe WHERE pe.id_producto = pr.id_producto AND pe.id_comanda = ? ), co.entregada = TRUE WHERE co.id_comanda = ? ";
    private final String selectComandaName = "SELECT id_comanda, nombre_cliente  FROM comanda WHERE id_comanda = ?";
    private final String selectComandaData = "SELECT pe.id_producto, pr.nombre, pe.cantidad, (pe.cantidad * pr.precio) AS \"cantidad_precio\" FROM comanda co, pedido pe, producto pr WHERE pe.id_producto = pr.id_producto AND pe.id_comanda = co.id_comanda AND pe.id_comanda = ?";
    private final String updateEditComanda = "UPDATE pedido SET id_producto = ?, cantidad = ? WHERE id_comanda = ? AND id_producto = ?";
    private final String deletePedido = "DELETE FROM pedido WHERE id_comanda = ? AND id_producto = ?";

    private final String selectDayIncomes = "SELECT Date_FORMAT(fecha, '%d-%m-%Y') AS \"fecha\", SUM(precio) AS \"total\" FROM comanda GROUP BY Date_FORMAT(fecha, '%d-%m-%Y')";

    /**
     * Constructor
     * 
     * @throws IOException
     */
    public GestorComandas() throws IOException {
        this.conn = init();
    }

    /**
     * Method that connect to database
     * 
     * @throws IOException
     * @return conn Connection to the database
     */
    public Connection init() throws IOException {
        try {
            Properties params = new Properties();
            params.load(new FileReader("bbdd_phpmyadmin.cfg"));

            String protocol = params.getProperty("protocol");
            String server = params.getProperty("server");
            String port = params.getProperty("port");
            String database = params.getProperty("database");
            String extra = params.getProperty("extra");
            String user = params.getProperty("user");
            String password = params.getProperty("password");

            String url = protocol + server + ":" + port + "/" + database + "?" + extra;
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión con la base de datos establecida.");
        } catch (FileNotFoundException | SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return conn;
    }

    /**
     * Method that get all the orders made by the user
     * 
     * @throws IOException
     * @return list ObservableList of PedidosComandas objects
     */
    public ObservableList<PedidosComanda> getPedidosComanda() throws IOException {
        ObservableList<PedidosComanda> list = FXCollections.observableArrayList();
        
        try {
            this.stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectAllComandas);
            while (rs.next()) {
                list.add(new PedidosComanda(rs.getInt("id"), rs.getString("cantidadYProducto"), String.format("%,.2f", rs.getDouble("precioTotal")) + " €", rs.getString("nombre")));
            }
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Method that get all the products from the database
     * 
     * @throws IOException
     * @return list ObservableList of Producto objects
     */
    public ObservableList<Producto> getProductos() throws IOException {
        ObservableList<Producto> list = FXCollections.observableArrayList();

        try {
            this.stmt = this.conn.createStatement();
            ResultSet rs = this.stmt.executeQuery(selectAllProductos);
            while (rs.next()) {
                list.add(new Producto(rs.getInt("id_producto"), rs.getString("nombre"), rs.getDouble("precio")));
            }
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Void method that inserts a new order
     * 
     * @param nombre_cliente Name added by the user
     */
    public void insertNewComanda(String nombre_cliente) {
        try {
            this.preparedStmt = this.conn.prepareStatement(insertNewComanda, Statement.RETURN_GENERATED_KEYS);
            this.preparedStmt.setString(1, nombre_cliente);
            int affectedRows = this.preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda creada correctamente");
                // Generated Keys -> get id of the last row created in the database
                this.generatedKeys = this.preparedStmt.getGeneratedKeys();
                if (this.generatedKeys.next()) {
                    this.idLastComanda = this.generatedKeys.getInt(1);
                }
            } else {
                System.out.println("Algo fue mal.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Void method that inserts all the products ordered in a comanda
     * 
     * @param id_producto Product selected by the user
     * @param cantidad Number of products
     */
    public void insertPedidoToLastNewComanda(int id_producto, int cantidad) {
        try {
            this.preparedStmt = this.conn.prepareStatement(insertPedidoToComanda);
            // idLastComanda -> saved the id of the last comanda
            this.preparedStmt.setInt(1, this.idLastComanda);
            this.preparedStmt.setInt(2, id_producto);
            this.preparedStmt.setInt(3, cantidad);
            int affectedRows = this.preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Pedido realizado correctamente");
            } else {
                System.out.println("Algo fue mal");
            }
            this.preparedStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Void method that update an order that has been delivered
     *
     * @param id_comanda id of the order that is going to be modified
     */
    public void updateComandaEntregada(int id_comanda) {
        try {
            this.preparedStmt = this.conn.prepareStatement(updateComandaEntregada);
            this.preparedStmt.setInt(1, id_comanda);
            this.preparedStmt.setInt(2, id_comanda);
            int affectedRows = this.preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda entregada");
                this.preparedStmt.close();
            } else {
                System.out.println("> Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Void method that delete an order
     *
     * @param id_comanda id of the order that is going to be deleted
     */
    public void deleteComanda(int id_comanda) {
        try {
            this.preparedStmt = this.conn.prepareStatement(deleteComanda);
            this.preparedStmt.setInt(1, id_comanda);
            int affectedRows = this.preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda eliminada correctamente");
                this.preparedStmt.close();
            } else {
                System.out.println("> Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method that get the name of an specific client
     * 
     * @param id_comanda id of the selected order
     * @return nombreCliente Name of the client
     * @throws IOException
     */
    public String showNameCliente(int id_comanda) throws IOException {
        String nombreCliente = "";
        try {
            this.preparedStmt = this.conn.prepareStatement(selectComandaName);
            this.preparedStmt.setInt(1, id_comanda);
            ResultSet rs = this.preparedStmt.executeQuery();
            while (rs.next()) {
                nombreCliente = rs.getString("nombre_cliente");
            }
            this.preparedStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nombreCliente;
    }

    /**
     * Method that get all the products in the selected order
     *
     * @param id_comanda id of the selected order
     * @return list ObservableList of all the products, number and total amount
     * @throws IOException
     */
    public ObservableList<ProductoPedido> showComandaData(int id_comanda) throws IOException {
        ObservableList<ProductoPedido> list = FXCollections.observableArrayList();
        try {
            this.preparedStmt = this.conn.prepareStatement(selectComandaData);
            this.preparedStmt.setInt(1, id_comanda);
            ResultSet rs = this.preparedStmt.executeQuery();
            while (rs.next()) {
                list.add(new ProductoPedido(rs.getInt("id_producto"), rs.getString("nombre"), rs.getInt("cantidad"), rs.getDouble("cantidad_precio")));
            }
            this.preparedStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Void method that update an specific order that has been edited by the user
     *
     * @param id_comanda id of an specific order
     * @param id_producto id of the product added by the user
     * @param cantidad number of products
     */
    public void updateComandaEdited(int id_comanda, int id_producto, int cantidad) {
        try {
            // First -> try to change an existing product
            this.preparedStmt = this.conn.prepareStatement(updateEditComanda);
            this.preparedStmt.setInt(1, id_producto);
            this.preparedStmt.setInt(2, cantidad);
            this.preparedStmt.setInt(3, id_comanda);
            this.preparedStmt.setInt(4, id_producto);
            int affectedRows = this.preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Cambio realizado correctamente");
            } else {
                // If there is no change
                try {
                    // Second -> insert a new product
                    this.preparedStmt = this.conn.prepareStatement(insertPedidoToComanda);
                    this.preparedStmt.setInt(1, id_comanda);
                    this.preparedStmt.setInt(2, id_producto);
                    this.preparedStmt.setInt(3, cantidad);
                    affectedRows = this.preparedStmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("> Cambio realizado correctamente");
                    } else {
                        System.out.println("> Algo fue mal.");
                    }
                    this.preparedStmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.preparedStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Void method that delete a product in an specific order
     *
     * @param id_comanda
     * @param id_producto product to be deleted
     * @throws SQLException
     */
    public void deletePedido(int id_comanda, int id_producto) throws SQLException {
        this.preparedStmt = this.conn.prepareStatement(deletePedido);
        this.preparedStmt.setInt(1, id_comanda);
        this.preparedStmt.setInt(2, id_producto);
        int affectedRows = this.preparedStmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("> Cambio realizado correctamente");
        } else {
            System.out.println("> Algo fue mal.");
        }
    }

    /**
     * Method that get the data for de Bar Chart
     *
     * @return XYChart.Series compound of date and total amount 
     * @throws IOException
     */
    public XYChart.Series<String, Double> getBarChartData() throws IOException {
        XYChart.Series<String, Double> series = new XYChart.Series<>();

        try {
            this.stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectDayIncomes);
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("fecha"), rs.getDouble("total")));
            }
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return series;
    }

    /**
     *
     * @param e
     */
    public static void muestraErrorSQL(SQLException e) {
        System.out.println("Error SQL: " + e.getMessage());
        System.out.println("Estado: " + e.getSQLState());
        System.out.println("Código: " + e.getErrorCode());
    }

}
