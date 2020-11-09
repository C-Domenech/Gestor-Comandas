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

import com.cdomenech.models.ComandaModelDB;
import com.cdomenech.models.DBConnection;
import com.cdomenech.models.ProductoModelDB;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cristina Domenech <linkedin.com/in/c-domenech/>
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            DBConnection connection = new DBConnection();
            Connection conn = connection.init();
            Scanner sc = new Scanner(System.in);
            ProductoModelDB productoDB = new ProductoModelDB(conn);
            ComandaModelDB comandaDB = new ComandaModelDB(conn);
            String respuesta = "";
            String continuarPidiendo = "S";
            String eliminarSeguro = "";
            String id_producto = "";
            String id_comanda = "";
            String nombre = "";
            String precio = "";
            String cantidad = "";
            String nombre_cliente = "";
            System.out.println("¿Eres trabajador o administrador? \n    1. Cliente/Trabajador.\n    2. Administrador.\n    3. Salir.");
            respuesta = sc.nextLine();
            while (!respuesta.equals("3")) {
                if (respuesta.equals("1")) {
                    while (!respuesta.equals("6")) {
                        System.out.println("¿Qué quieres hacer? \n    1. Ver todas las comandas\n    2. Crear una comanda.\n    3. Editar una comanda existente.\n    4. Eliminar una comanda.\n    5. Entregar una comanda.\n    6. Salir.");
                        respuesta = sc.nextLine();
                        switch (respuesta) {
                            case "1":
                                comandaDB.getAllComandas();
                                break;
                            case "2":
                                System.out.println("Vas a crear una nueva comanda.");
                                System.out.println("Introduce el nombre del cliente: ");
                                nombre_cliente = sc.nextLine();
                                comandaDB.insertNewComanda(nombre_cliente);
                                continuarPidiendo = "S";
                                while (continuarPidiendo.toLowerCase().equals("s")) {
                                    productoDB.getAllProductos();
                                    System.out.println("Introduce el número/ID del producto a añadir: ");
                                    id_producto = sc.nextLine();
                                    System.out.println("Introduce la cantidad: ");
                                    cantidad = sc.nextLine();
                                    comandaDB.insertPedidoToLastNewComanda(Integer.parseInt(id_producto), Integer.parseInt(cantidad));
                                    System.out.println("¿Quieres añadir más productos a la comanda? S/N");
                                    continuarPidiendo = sc.nextLine();
                                }
                                continuarPidiendo = "S";
                                break;
                            case "3":
                                System.out.println("Vas a editar una comanda.");
                                System.out.println("¿Qué quieres hacer? \n    1. Sustituir producto pedido.\n    2. Pedir más productos.");
                                respuesta = sc.nextLine();
                                if (respuesta.equals("1")) {
                                    comandaDB.getAllComandas();
                                    System.out.println("Introduce el número/ID de la comanda a modificar: ");
                                    id_comanda = sc.nextLine();
                                    comandaDB.getProductosComanda(Integer.parseInt(id_comanda));
                                    System.out.println("Introduce el número/ID del producto que quieres sustituir: ");
                                    String id_productoSustituir = sc.nextLine();
                                    productoDB.getAllProductos();
                                    System.out.println("Introduce el número/ID el nuevo producto: ");
                                    String id_productoNuevo = sc.nextLine();
                                    System.out.println("Introduce la cantidad: ");
                                    cantidad = sc.nextLine();
                                    comandaDB.updateComanda(Integer.parseInt(id_comanda), Integer.parseInt(id_productoSustituir), Integer.parseInt(id_productoNuevo), Integer.parseInt(cantidad));
                                } else if (respuesta.equals("2")) {
                                    continuarPidiendo = "S";
                                    while (continuarPidiendo.toLowerCase().equals("s")) {
                                        comandaDB.getAllComandas();
                                        System.out.println("Introduce el número/ID de la comanda a modificar: ");
                                        id_comanda = sc.nextLine();
                                        productoDB.getAllProductos();
                                        System.out.println("Introduce el número/ID del producto a añadir: ");
                                        id_producto = sc.nextLine();
                                        System.out.println("Introduce la cantidad: ");
                                        cantidad = sc.nextLine();
                                        comandaDB.insertPedidoToComanda(Integer.parseInt(id_comanda), Integer.parseInt(id_producto), Integer.parseInt(cantidad));
                                        System.out.println("¿Quieres añadir más productos a la comanda? S/N");
                                        continuarPidiendo = sc.nextLine();
                                    }
                                }
                                break;
                            case "4":
                                System.out.println("Vas a eliminar una comanda.");
                                comandaDB.getAllComandas();
                                System.out.println("Introduce el número/ID de la comanda que quieres eliminar: ");
                                id_comanda = sc.nextLine();
                                System.out.println("¿Estás seguro de que quieres elminar esta comanda? S/N");
                                eliminarSeguro = sc.nextLine();
                                if (eliminarSeguro.toLowerCase().equals("s")) {
                                    comandaDB.deleteComanda(Integer.parseInt(id_comanda));
                                } else {
                                    System.out.println("> Operación cancelada.");
                                }
                                break;
                            case "5":
                                System.out.println("Vas a entregar una comanda y obtener el precio total a pagar.");
                                comandaDB.getAllComandas();
                                System.out.println("Introduce el número/ID de la comanda que vas a entregar: ");
                                id_comanda = sc.nextLine();
                                double precioTotal = comandaDB.setComandaEntregada(Integer.parseInt(id_comanda));
                                System.out.println("El precio total de la comanda es: " + precioTotal + " €");
                                break;
                            default:
                                break;
                        }
                    }
                    sc.close();
                    conn.close();
                    System.out.println("Gracias por usar nuestro gestor de comandas");
                    Runtime.getRuntime().exit(0);
                } else if (respuesta.equals("2")) {
                    String password = "4578";
                    System.out.println("Introduce la contraseña: ");
                    respuesta = sc.nextLine();
                    if (respuesta.equals(password)) {
                        System.out.println("Bienvenido, al menú del administrador");
                        while (!respuesta.equals("4")) {
                            System.out.println("¿Qué quieres hacer? \n    1. Añadir un nuevo producto.\n    2. Editar uno existente.\n    3. Eliminar un producto.\n    4. Salir.");
                            respuesta = sc.nextLine();
                            switch (respuesta) {
                                case "1":
                                    System.out.println("Vas a añadir un nuevo producto al menú.");
                                    System.out.println("Introduce el nombre del producto: ");
                                    nombre = sc.nextLine();
                                    System.out.println("Introduce el precio del producto: ");
                                    precio = sc.nextLine();
                                    System.out.println("Nombre: " + nombre);
                                    System.out.println("Precio: " + precio);
                                    productoDB.insertNewProducto(nombre, Double.parseDouble(precio));
                                    System.out.println("Has añadido al menú " + nombre + " a un precio de " + precio + "€");
                                    break;
                                case "2":
                                    System.out.println("Vas a editar un producto del menú.");
                                    productoDB.getAllProductos();
                                    System.out.println("Introduce el número/ID del producto: ");
                                    id_producto = sc.nextLine();
                                    System.out.println("Introduce el nombre del producto: ");
                                    nombre = sc.nextLine();
                                    System.out.println("Introduce el precio del producto: ");
                                    precio = sc.nextLine();
                                    productoDB.updateProducto(Integer.parseInt(id_producto), nombre, Double.parseDouble(precio));
                                    break;
                                case "3":
                                    System.out.println("Vas a eliminar un producto del menú.");
                                    productoDB.getAllProductos();
                                    System.out.println("Introduce el número/ID del producto que quieres eliminar: ");
                                    id_producto = sc.nextLine();
                                    System.out.println("¿Estás seguro de que quieres elminar este producto? S/N");
                                    eliminarSeguro = sc.nextLine();
                                    if (eliminarSeguro.toLowerCase().equals("s")) {
                                        productoDB.deleteProducto(Integer.parseInt(id_producto));
                                    } else {
                                        System.out.println("> Operación cancelada.");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        sc.close();
                        conn.close();
                        System.out.println("Gracias por usar nuestro gestor de comandas");
                        Runtime.getRuntime().exit(0);
                    } else {
                        System.out.println("Acceso denegado.");
                        Runtime.getRuntime().exit(0);
                    }
                }
            }
            sc.close();
            conn.close();
            System.out.println("Gracias por usar nuestro gestor de comandas");
            Runtime.getRuntime().exit(0);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
