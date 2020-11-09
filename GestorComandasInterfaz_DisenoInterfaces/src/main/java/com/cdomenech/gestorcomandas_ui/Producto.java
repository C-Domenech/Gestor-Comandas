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

/**
 *
 * @author Cristina Domenech <linkedin.com/in/c-domenech/>
 */
public class Producto {

    private int idProducto;
    private String nombreProducto;
    private double precioProducto;

    public Producto(int id, String nombre, double precio) {
        this.idProducto = id;
        this.nombreProducto = nombre;
        this.precioProducto = precio;
    }

    public Producto(int idProducto) {
        this.idProducto = idProducto;
    }

//    public Producto(String nombre, String precio) {
//        this.nombreProducto = nombre;
//        this.precioProducto = precio;
//    }

    public int getId() {
        return idProducto;
    }

    public void setId(int id) {
        this.idProducto = id;
    }

    public String getNombre() {
        return nombreProducto;
    }

    public void setNombre(String nombre) {
        this.nombreProducto = nombre;
    }

    public double getPrecio() {
        return precioProducto;
    }

    public void setPrecio(double precio) {
        this.precioProducto = precio;
    }

    @Override
    public String toString() {
        return this.nombreProducto + " - " + String.format("%,.2f", this.precioProducto) + " €";
    }

}
