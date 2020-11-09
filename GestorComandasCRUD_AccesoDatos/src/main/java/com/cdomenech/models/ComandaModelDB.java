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
package com.cdomenech.models;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cristina Domenech Moreno, Javier Torres Sevilla
 */
public class ComandaModelDB {

    DBConnection connection = new DBConnection();
    private final Connection conn;
    private final Statement stmt;
    private PreparedStatement preparedStmt;
    private ResultSet generatedKeys;
    private int idLastComanda;

    private final String selectAllComandas = "SELECT co.id_comanda, pr.nombre, pr.precio, pe.cantidad, co.nombre_cliente FROM comanda co, pedido pe, producto pr WHERE co.id_comanda = pe.id_comanda AND pe.id_producto = pr.id_producto AND co.entregada = FALSE ORDER BY co.id_comanda";
    private final String insertNewComanda = "INSERT INTO comanda (nombre_cliente) VALUES (?)";
    private final String insertPedidoToComanda = "INSERT INTO pedido (id_comanda, id_producto, cantidad) VALUES (?, ?, ?)";
    private final String getProductosPedidos = "SELECT pe.id_producto, pr.nombre, pr.precio, pe.cantidad FROM producto pr, pedido pe WHERE pr.id_producto = pe.id_producto AND pe.id_comanda = ?";
    private final String updateComanda = "UPDATE pedido SET id_producto = ?, cantidad = ? WHERE id_comanda = ? AND id_producto = ?";
    private final String deleteComanda = "DELETE FROM comanda WHERE id_comanda = ?";
    private final String setComandaEntregada = "UPDATE comanda co SET co.precio =( SELECT SUM(pr.precio * pe.cantidad) FROM producto pr, pedido pe WHERE pe.id_producto = pr.id_producto AND pe.id_comanda = ? ), co.entregada = TRUE WHERE co.id_comanda = ? ";
    private final String selectPrecioTotal = "SELECT precio FROM comanda WHERE id_comanda = ?";

    /**
     *
     * @param connection
     * @throws IOException
     * @throws SQLException
     */
    public ComandaModelDB(Connection connection) throws IOException, SQLException {
        this.conn = connection;
        this.stmt = conn.createStatement();
    }

    /**
     * Method that get all the orders made by the user
     *
     */
    public void getAllComandas() {
        ArrayList<Object[]> result = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery(selectAllComandas);
            while (rs.next()) {
                Object[] r = {
                    rs.getInt("id_comanda"),
                    rs.getString("nombre"),
                    rs.getDouble("precio") + " €",
                    rs.getInt("cantidad") + " Ud",
                    rs.getString("nombre_cliente")
                };
                result.add(r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComandaModelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        printComandas(result);
    }

    /**
     * Printer method
     *
     * @param result
     */
    public void printComandas(ArrayList<Object[]> result) {
        System.out.println("========================================");
        System.out.println("Estas son las comandas no entregadas: ");
        System.out.println("============================");
        for (Object[] r : result) {
            System.out.println(Arrays.deepToString(r));
        }
        System.out.println("========================================");
    }

    /**
     * Void method that inserts a new order
     *
     * @param nombre_cliente
     */
    public void insertNewComanda(String nombre_cliente) {
        try {
            preparedStmt = conn.prepareStatement(insertNewComanda, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, nombre_cliente);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda creada correctamente");
                // Generated Keys -> get id of the last row created in the database
                generatedKeys = preparedStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idLastComanda = generatedKeys.getInt(1);
                }
            } else {
                System.out.println("Algo fue mal.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComandaModelDB.class.getName()).log(Level.SEVERE, null, ex);
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
            preparedStmt = conn.prepareStatement(insertPedidoToComanda);
            // idLastComanda -> saved the id of the last comanda
            preparedStmt.setInt(1, idLastComanda);
            preparedStmt.setInt(2, id_producto);
            preparedStmt.setInt(3, cantidad);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Pedido realizado correctamente");
                preparedStmt.close();
            } else {
                System.out.println("Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComandaModelDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Method that get all the products in the selected order
     *
     * @param id_comanda
     */
    public void getProductosComanda(int id_comanda) {
        ArrayList<Object[]> result = new ArrayList<>();
        try {
            preparedStmt = conn.prepareStatement(getProductosPedidos);
            preparedStmt.setInt(1, id_comanda);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                Object[] r = {
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad")
                };
                result.add(r);
            }
            for (Object[] r : result) {
                System.out.println(Arrays.deepToString(r));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComandaModelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Void method that update an specific order that has been edited by the user
     * 
     * @param id_comanda id of an specific order
     * @param id_productoSustituir product to be deleted
     * @param id_productoNuevo new product
     * @param cantidad quantity
     */
    public void updateComanda(int id_comanda, int id_productoSustituir, int id_productoNuevo, int cantidad) {
        try {
            preparedStmt = conn.prepareStatement(updateComanda);
            preparedStmt.setInt(1, id_productoNuevo);
            preparedStmt.setInt(2, cantidad);
            preparedStmt.setInt(3, id_comanda);
            preparedStmt.setInt(4, id_productoSustituir);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda actualizada correctamente");
                preparedStmt.close();
            } else {
                System.out.println("> Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComandaModelDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        getAllComandas();
    }

    /**
     * Void method that insert new products to an order
     * 
     * @param id_comanda
     * @param id_producto
     * @param cantidad
     */
    public void insertPedidoToComanda(int id_comanda, int id_producto, int cantidad) {
        try {
            preparedStmt = conn.prepareStatement(insertPedidoToComanda);
            preparedStmt.setInt(1, id_comanda);
            preparedStmt.setInt(2, id_producto);
            preparedStmt.setInt(3, cantidad);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda actualizada correctamente");
                preparedStmt.close();
            } else {
                System.out.println("> Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComandaModelDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Void method that delete an order
     *
     * @param id_comanda
     */
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
            Logger.getLogger(ComandaModelDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        getAllComandas();
    }

    /**
     * Update order and set it delivered
     * 
     * @param id_comanda
     * @return total amount
     */
    public double setComandaEntregada(int id_comanda) {
        double precioTotal = 0;
        try {
            preparedStmt = conn.prepareStatement(setComandaEntregada);
            preparedStmt.setInt(1, id_comanda);
            preparedStmt.setInt(2, id_comanda);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Comanda entregada");
                preparedStmt.close();
                preparedStmt = conn.prepareStatement(selectPrecioTotal);
                preparedStmt.setInt(1, id_comanda);
                ResultSet rs = preparedStmt.executeQuery();
                while (rs.next()) {
                    precioTotal = rs.getDouble("precio");
                }
            } else {
                System.out.println("> Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComandaModelDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return precioTotal;
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
