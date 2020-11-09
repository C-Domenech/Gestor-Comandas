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

import java.security.Timestamp;

/**
 *
 * @author Cristina Domenech Moreno, Javier Torres Sevilla
 */
public class Comanda {
    private int idComanda;
    private Timestamp fecha;
    private double precio;
    private String nombreCliente;
    private boolean entregada;

    /**
     *
     * @param nombreCliente
     */
    public Comanda(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return idComanda;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.idComanda = id;
    }

    /**
     *
     * @return
     */
    public Timestamp getFecha() {
        return fecha;
    }

    /**
     *
     * @param fecha
     */
    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    /**
     *
     * @return
     */
    public double getPrecio() {
        return precio;
    }

    /**
     *
     * @param precio
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     *
     * @return
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     *
     * @param nombreCliente
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     *
     * @return
     */
    public boolean isEntregada() {
        return entregada;
    }

    /**
     *
     * @param entregada
     */
    public void setEntregada(boolean entregada) {
        this.entregada = entregada;
    }
    
    
    
    
}
