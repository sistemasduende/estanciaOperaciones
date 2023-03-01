/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import java.util.Date;

/**
 *
 * @author Usuario
 */
public class ReporteEntregaDetalladoItem {
    private int idEntrega;
    private int idRemito;
    private int idLocal;
    private String nombreComercial;
    private Date fec_ent;
    private char tipoMov;
    private String numeroTropa;
    private double kilos;

    public ReporteEntregaDetalladoItem(int idEntrega, int idRemito, int idLocal, String nombreComercial, Date fec_ent, char tipoMov, String numeroTropa, double kilos) {
        this.idEntrega = idEntrega;
        this.idRemito = idRemito;
        this.idLocal = idLocal;
        this.nombreComercial = nombreComercial;
        this.fec_ent = fec_ent;
        this.tipoMov = tipoMov;
        this.numeroTropa = numeroTropa;
        this.kilos = kilos;
    }

    public int getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(int idEntrega) {
        this.idEntrega = idEntrega;
    }

    public int getIdRemito() {
        return idRemito;
    }

    public void setIdRemito(int idRemito) {
        this.idRemito = idRemito;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public Date getFec_ent() {
        return fec_ent;
    }

    public void setFec_ent(Date fec_ent) {
        this.fec_ent = fec_ent;
    }

    public char getTipoMov() {
        return tipoMov;
    }

    public void setTipoMov(char tipoMov) {
        this.tipoMov = tipoMov;
    }

    public String getNumeroTropa() {
        return numeroTropa;
    }

    public void setNumeroTropa(String numeroTropa) {
        this.numeroTropa = numeroTropa;
    }

    public double getKilos() {
        return kilos;
    }

    public void setKilos(double kilos) {
        this.kilos = kilos;
    }

    @Override
    public String toString() {
        return "ReporteEntregaDetalladoItem{" + "idEntrega=" + idEntrega + ", idRemito=" + idRemito + ", idLocal=" + idLocal + ", nombreComercial=" + nombreComercial + ", fec_ent=" + fec_ent + ", tipoMov=" + tipoMov + ", numeroTropa=" + numeroTropa + ", kilos=" + kilos + '}';
    }
    
    
}
