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
package com.cdomenech.model;

import com.cdomenech.gestorcomandas_ui.PedidosComanda;
import com.cdomenech.gestorcomandas_ui.Producto;
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

/**
 *
 * @author Cristina Domenech <linkedin.com/in/c-domenech/>
 */
public final class GestorComandas {

    Connection conn;
    private Statement stmt;
    private PreparedStatement preparedStmt;
    private ResultSet generatedKeys;
    private int idLastComanda;

    private final String selectAllComandas = "SELECT DISTINCT\n"
            + "    co.id_comanda AS \"id\",\n"
            + "    (\n"
            + "    SELECT\n"
            + "        GROUP_CONCAT(\" \", pe.cantidad, \" \", pr.nombre)\n"
            + "    FROM\n"
            + "        pedido pe,\n"
            + "        producto pr\n"
            + "    WHERE\n"
            + "        pe.id_producto = pr.id_producto AND pe.id_comanda = co.id_comanda\n"
            + ") AS \"cantidadYProducto\",\n"
            + "(\n"
            + "    SELECT\n"
            + "        SUM(pr.precio * pe.cantidad)\n"
            + "    FROM\n"
            + "        producto pr,\n"
            + "        pedido pe\n"
            + "    WHERE\n"
            + "        pe.id_producto = pr.id_producto AND pe.id_comanda = co.id_comanda\n"
            + ") AS \"precioTotal\",\n"
            + "co.nombre_cliente AS \"nombre\"\n"
            + "FROM\n"
            + "    comanda co,\n"
            + "    pedido pe,\n"
            + "    producto pr\n"
            + "WHERE\n"
            + "    co.id_comanda = pe.id_comanda AND pe.id_producto = pr.id_producto AND co.entregada = FALSE\n"
            + "ORDER BY\n"
            + "    co.id_comanda";
    private final String selectAllProductos = "SELECT id_producto, nombre, precio FROM producto";
    private final String insertNewComanda = "INSERT INTO comanda (nombre_cliente) VALUES (?)";
    private final String insertPedidoToComanda = "INSERT INTO pedido (id_comanda, id_producto, cantidad) VALUES (?, ?, ?)";
    private final String deleteComanda = "DELETE FROM comanda WHERE id_comanda = ?";
    private final String updateComandaEntregada = "UPDATE comanda co SET co.precio =( SELECT SUM(pr.precio * pe.cantidad) FROM producto pr, pedido pe WHERE pe.id_producto = pr.id_producto AND pe.id_comanda = ? ), co.entregada = TRUE WHERE co.id_comanda = ? ";

    public GestorComandas() throws IOException {
        this.conn = init();
    }

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

    public void insertNewComanda(String nombre_cliente) {
        try {
            this.preparedStmt = this.conn.prepareStatement(insertNewComanda, Statement.RETURN_GENERATED_KEYS);
            this.preparedStmt.setString(1, nombre_cliente);
            int affectedRows = this.preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda creada correctamente");
                this.generatedKeys = this.preparedStmt.getGeneratedKeys();
                //preparedStmt.close();
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

    public void insertPedidoToLastNewComanda(int id_producto, int cantidad) {
        try {
            this.preparedStmt = this.conn.prepareStatement(insertPedidoToComanda);
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

    public void updateComandaEntregada(int id_comanda) {
        try {
            preparedStmt = conn.prepareStatement(updateComandaEntregada);
            preparedStmt.setInt(1, id_comanda);
            preparedStmt.setInt(2, id_comanda);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda entregada");
                preparedStmt.close();
            } else {
                System.out.println("> Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteComanda(int id_comanda) {
        try {
            preparedStmt = conn.prepareStatement(deleteComanda);
            preparedStmt.setInt(1, id_comanda);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda eliminada correctamente");
                preparedStmt.close();
            } else {
                System.out.println("> Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestorComandas.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void muestraErrorSQL(SQLException e) {
        System.out.println("Error SQL: " + e.getMessage());
        System.out.println("Estado: " + e.getSQLState());
        System.out.println("Código: " + e.getErrorCode());
    }

}
