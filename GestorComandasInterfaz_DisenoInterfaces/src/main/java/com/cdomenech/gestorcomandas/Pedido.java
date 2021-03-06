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
public class Pedido {

    private int idPedido;
    private Comanda comanda;
    private Producto producto;
    private int cantidad;

    /**
     *
     * @param comanda
     * @param producto
     * @param cantidad
     */
    public Pedido(Comanda comanda, Producto producto, int cantidad) {
        this.comanda = comanda;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /**
     *
     * @return
     */
    public int getIdPedido() {
        return idPedido;
    }

    /**
     *
     * @param idPedido
     */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    /**
     *
     * @return
     */
    public Comanda getComanda() {
        return comanda;
    }

    /**
     *
     * @param comanda
     */
    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    /**
     *
     * @return
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     *
     * @param producto
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
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

}
