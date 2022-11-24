/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import java.util.Date;

/**
 *
 * @author Propietario
 */
public class ReporteEntregaItem {
    String numeroTropa;
    int idLocal;
    String nombreComercial;
    Date fec_ent;
    String categoria;
    double kilos;

    public ReporteEntregaItem() {
    }

    public ReporteEntregaItem(String numeroTropa, int idLocal, String nombreComercial,Date fec_ent,String categoria,double kilos) {
        this.numeroTropa=numeroTropa;
        this.idLocal=idLocal;
        this.nombreComercial=nombreComercial;
        this.fec_ent=fec_ent;
        this.categoria=categoria;
        this.kilos=kilos;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getKilos() {
        return kilos;
    }

    public void setKilos(double kilos) {
        this.kilos = kilos;
    }
    
    
    
}
