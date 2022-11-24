/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import controllers.TropaStock;
import database.Conector;
import entidades.Balance;
import general.BeanBase;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Propietario
 */
@ManagedBean(name="reporteentregacontroller")
@SessionScoped

public class ReporteEntregaController extends BeanBase{
    private List <ReporteEntregaItem> lista=null;
    private TimeZone Zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private java.util.Date fec_desde=null;
    private java.util.Date fec_hasta=null;

    public List<ReporteEntregaItem> getLista() {
        return lista;
    }

    public void setLista(List<ReporteEntregaItem> lista) {
        this.lista = lista;
    }

    public TimeZone getZona() {
        return Zona;
    }

    public void setZona(TimeZone Zona) {
        this.Zona = Zona;
    }

    public Date getFec_desde() {
        return fec_desde;
    }

    public void setFec_desde(Date fec_desde) {
        this.fec_desde = fec_desde;
    }

    public Date getFec_hasta() {
        return fec_hasta;
    }

    public void setFec_hasta(Date fec_hasta) {
        this.fec_hasta = fec_hasta;
    }

    //Obtiene lista de todos las tropas con inventario disponible
    public void buscaListaDatos() throws SQLException{
        FacesMessage msg;
        CallableStatement s=null;
        ResultSet r=null;
        String ls_numTropa,ls_nombreComercial,ls_categoria;
        Date lda_fec_ent;
        int li_idLocal;
        double ld_kilos;
        ArrayList resultados = new ArrayList();
        Connection conexion=null;
        
        if (fec_desde==null){
            msg = new FacesMessage("Debe ingresar la fecha inicial de la búsqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        if (fec_hasta==null){
            msg = new FacesMessage("Debe ingresar la fecha final de la búsqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        java.util.Calendar fecha_desde = java.util.Calendar.getInstance();
        fecha_desde.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_desde.setTime(fec_desde);
        fecha_desde.set(Calendar.HOUR_OF_DAY, 0);
        fecha_desde.set(Calendar.MINUTE, 0);
        fecha_desde.set(Calendar.SECOND, 0);
        fecha_desde.set(Calendar.MILLISECOND, 0);
        java.util.Date lda_fecha_desde = new java.sql.Date(fecha_desde.getTimeInMillis());

        java.util.Calendar fecha_hasta = java.util.Calendar.getInstance();
        fecha_hasta.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_hasta.setTime(fec_hasta);
        fecha_hasta.set(Calendar.HOUR_OF_DAY, 23);
        fecha_hasta.set(Calendar.MINUTE, 59);
        fecha_hasta.set(Calendar.SECOND, 59);
        fecha_hasta.set(Calendar.MILLISECOND, 0);
        java.util.Date lda_fecha_hasta = new java.sql.Date(fecha_hasta.getTimeInMillis());        

        try {
               //Conectamos a la base
               Conector conector = new Conector();  
               conexion = conector.connect("estancia");

               s=conexion.prepareCall("{call sp_rep_entrega_tropas_local ( ? , ? )}");
               s.setDate(1, (java.sql.Date) lda_fecha_desde);
               s.setDate(2, (java.sql.Date) lda_fecha_hasta);
               
               r=s.executeQuery();
               while (r.next()){
                   ls_numTropa=r.getString("numeroTropa");
                   li_idLocal=r.getInt("idLocal");
                   ls_nombreComercial=r.getString("nombreComercial");
                   lda_fec_ent=r.getDate("fec_ent");
                   ls_categoria=r.getString("categoria");
                   ld_kilos=r.getDouble("kilos");
                   
                   ReporteEntregaItem registro= new ReporteEntregaItem(ls_numTropa,li_idLocal,ls_nombreComercial,  
                           lda_fec_ent,ls_categoria,ld_kilos);
                   resultados.add(registro);
               }
               s.close();
               this.setLista(resultados);
        } catch (SQLException e){
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } finally {
             if (conexion != null) conexion.close();   
             if (r != null) r.close();   
        }
        
    }
}
