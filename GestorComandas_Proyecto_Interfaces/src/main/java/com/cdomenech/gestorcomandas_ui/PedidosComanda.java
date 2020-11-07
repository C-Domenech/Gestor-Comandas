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
public class PedidosComanda {

    private int idComanda;
    private String cantidadYProducto;
    private String precioTotal;
    private String nombre;

    public PedidosComanda(int id, String cantidadYProducto, String precioTotal, String nombre) {
        this.idComanda = id;
        this.cantidadYProducto = cantidadYProducto;
        this.precioTotal = precioTotal;
        this.nombre = nombre;
    }

    public int getId() {
        return idComanda;
    }

    public void setId(int id) {
        this.idComanda = id;
    }

    public String getCantidadYProducto() {
        return cantidadYProducto;
    }

    public void setCantidadYProducto(String cantidadYProducto) {
        this.cantidadYProducto = cantidadYProducto;
    }

    public String getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(String precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
