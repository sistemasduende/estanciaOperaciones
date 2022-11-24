/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.Date;

/**
 *
 * @author Propietario
 */
public class TropaStock {
    private int id;
    private String numeroTropa;
    private String tipo;
    private Date fec_ing;
    private String productor;
    private String deposito;
    private double disponible;
    private double entregado;
    private double totalKilos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroTropa() {
        return numeroTropa;
    }

    public void setNumeroTropa(String numeroTropa) {
        this.numeroTropa = numeroTropa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFec_ing() {
        return fec_ing;
    }

    public void setFec_ing(Date fec_ing) {
        this.fec_ing = fec_ing;
    }

    public String getProductor() {
        return productor;
    }

    public void setProductor(String productor) {
        this.productor = productor;
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }

    public double getDisponible() {
        return disponible;
    }

    public void setDisponible(double disponible) {
        this.disponible = disponible;
    }

    public double getEntregado() {
        return entregado;
    }

    public void setEntregado(double entregado) {
        this.entregado = entregado;
    }

    public double getTotalKilos() {
        return totalKilos;
    }

    public void setTotalKilos(double totalKilos) {
        this.totalKilos = totalKilos;
    }
    
    public TropaStock() {
    }

    public TropaStock(int id, String numeroTropa, String tipo, Date fec_ing, String productor, String deposito, 
                      double disponible,double entregado,double totalKilos) {
        this.id = id;
        this.numeroTropa = numeroTropa;
        this.tipo = tipo;
        this.fec_ing = fec_ing;
        this.productor = productor;
        this.deposito = deposito;
        this.disponible = disponible;
        this.entregado=entregado;
        this.totalKilos=totalKilos;
    }
    
    
    
}
