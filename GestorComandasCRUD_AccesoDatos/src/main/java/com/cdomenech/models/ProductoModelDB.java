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
public class ProductoModelDB {

    DBConnection connection = new DBConnection();
    private final Connection conn;
    private final Statement stmt;
    private PreparedStatement preparedStmt;

    private final String selectAllProductos = "SELECT id_producto, nombre, precio FROM producto";
    private final String insertNewProducto = "INSERT INTO producto (nombre, precio) VALUES (?,?)";
    private final String updateProducto = "UPDATE producto SET nombre = ?, precio = ? WHERE id_producto = ?";
    private final String deleteProducto = "DELETE FROM producto WHERE producto.id_producto = ?";

    /**
     *
     * @param connection
     * @throws IOException
     * @throws SQLException
     */
    public ProductoModelDB(Connection connection) throws IOException, SQLException {
        this.conn = connection;
        this.stmt = conn.createStatement();
    }

    /**
     * Get all the products the are in the database
     */
    public void getAllProductos() {
        ArrayList<Object[]> result = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery(selectAllProductos);
            while (rs.next()) {
                Object[] r = {
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getDouble("precio") + " €", 
                };
                result.add(r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoModelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        printProductos(result);
    }

    /**
     * Printer method 
     * 
     * @param result
     */
    public void printProductos(ArrayList<Object[]> result) {
        System.out.println("========================================");
        System.out.println("Estos son los productos disponibles: ");
        System.out.println("============================");
        for (Object[] r : result) {
            System.out.println(Arrays.deepToString(r));
        }
    }

    /**
     * Insert new products
     * 
     * @param nombre
     * @param precio
     */
    public void insertNewProducto(String nombre, double precio) {
        try {
            preparedStmt = conn.prepareStatement(insertNewProducto);
            preparedStmt.setString(1, nombre);
            preparedStmt.setDouble(2, precio);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Producto añadido correctamente");
                preparedStmt.close();
            } else {
                System.out.println("Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoModelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        getAllProductos();
    }

    /**
     * Change a product
     * 
     * @param id_producto
     * @param nombre
     * @param precio
     */
    public void updateProducto(int id_producto, String nombre, double precio) {
        try {
            preparedStmt = conn.prepareStatement(updateProducto);
            preparedStmt.setString(1, nombre);
            preparedStmt.setDouble(2, precio);
            preparedStmt.setInt(3, id_producto);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Producto modificado correctamente");
                preparedStmt.close();
            } else {
                System.out.println("Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoModelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        getAllProductos();
    }

    /**
     * Delete a product
     * 
     * @param id_producto
     */
    public void deleteProducto(int id_producto) {
        try {
            preparedStmt = conn.prepareStatement(deleteProducto);
            preparedStmt.setInt(1, id_producto);
            int affectedRows = preparedStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("> Producto eliminado correctamente");
                preparedStmt.close();
            } else {
                System.out.println("Algo fue mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoModelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        getAllProductos();
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
