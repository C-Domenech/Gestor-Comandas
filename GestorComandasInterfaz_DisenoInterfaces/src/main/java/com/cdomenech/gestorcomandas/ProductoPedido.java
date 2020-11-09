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

/**
 *
 * @author Cristina Domenech Moreno, Javier Torres Sevilla
 */
public class ProductoPedido {

    private int idProducto;
    private String nombre;
    private int cantidad;
    private double precio;

    /**
     *
     * @param idProducto
     * @param nombre
     * @param cantidad
     * @param precio
     */
    public ProductoPedido(int idProducto, String nombre, int cantidad, double precio) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    /**
     *
     * @return
     */
    public int getIdProducto() {
        return idProducto;
    }

    /**
     *
     * @param idProducto
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /**
     *
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     *
     * @param cantidad
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     *
     * @return
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Method that return the amount formatted
     * 
     * @return amount
     */
    public String getPrecioString() {
        return String.format("%,.2f", precio) + " €";
    }

    /**
     *
     * @param precio
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

}
