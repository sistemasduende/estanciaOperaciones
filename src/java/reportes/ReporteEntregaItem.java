/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Propietario
 */
public class ReporteEntregaItem {
    String numeroTropa;
    int idLocal;
    String nombreComercial;
    Date fec_ent;
    char tipoMov;
    String categoria;
    String tipo;
    double kilos;

    public ReporteEntregaItem() {
    }

    public ReporteEntregaItem(String numeroTropa, int idLocal, String nombreComercial, Date fec_ent, char tipoMov, String categoria, String tipo, double kilos) {
        this.numeroTropa = numeroTropa;
        this.idLocal = idLocal;
        this.nombreComercial = nombreComercial;
        this.fec_ent = fec_ent;
        this.tipoMov = tipoMov;
        this.categoria = categoria;
        this.tipo = tipo;
        this.kilos = kilos;
    }

    public String getNumeroTropa() {
        return numeroTropa;
    }

    public void setNumeroTropa(String numeroTropa) {
        this.numeroTropa = numeroTropa;
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
    
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public double getKilos() {
        return kilos;
    }

    public void setKilos(double kilos) {
        this.kilos = kilos;
    }

    @Override
    public String toString() {
        return "ReporteEntregaItem{" + "numeroTropa=" + numeroTropa + ", idLocal=" + idLocal + ", nombreComercial=" + nombreComercial + ", fec_ent=" + fec_ent + ", tipoMov=" + tipoMov + ", categoria=" + categoria + ", tipo=" + tipo + ", kilos=" + kilos + '}';
    }
}
