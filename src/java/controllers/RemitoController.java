/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.RemitoVenta;
import general.BeanBase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author rafael
 */
@ManagedBean(name="remitocontroller")
@SessionScoped

public class RemitoController extends BeanBase{
    private TimeZone zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private java.util.Date fec_desde=null;
    private java.util.Date fec_hasta=null;
    private RemitoVenta registroSel;
    private RemitoVenta registroMod;
    private List <RemitoVenta> lista=null;
    private List<RemitoDetTotal> listaTotalizada=new ArrayList<RemitoDetTotal>();
    private String modo="";
    private double totalKilos;

    public RemitoController() {
        
    }

    public RemitoVenta getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(RemitoVenta registroSel) {
        this.registroSel = registroSel;
    }

    public RemitoVenta getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(RemitoVenta registroMod) {
        this.registroMod = registroMod;
    }

    public List<RemitoVenta> getLista() {
        return lista;
    }

    public void setLista(List<RemitoVenta> lista) {
        this.lista = lista;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public List<RemitoDetTotal> getListaTotalizada() {
        return listaTotalizada;
    }

    public void setListaTotalizada(List<RemitoDetTotal> listaTotalizada) {
        this.listaTotalizada = listaTotalizada;
    }

    public double getTotalKilos() {
        return totalKilos;
    }

    public void setTotalKilos(double totalKilos) {
        this.totalKilos = totalKilos;
    }

    public TimeZone getZona() {
        return zona;
    }

    public void setZona(TimeZone zona) {
        this.zona = zona;
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

    
    //Obtiene lista de todos las entregas
    public void buscaListaDatos(){
        FacesMessage msg;
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
        
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from RemitoVenta a where a.fecMov >= :fec_desde and a.fecMov <= :fec_hasta order by a.fecMov,id");
            q.setParameter("fec_desde",lda_fecha_desde);
            q.setParameter("fec_hasta",lda_fecha_hasta);
            this.lista=(List<RemitoVenta>) q.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        finally {
            session.close();
        }
    }
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        FacesMessage msg;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(RemitoVenta) session.get(RemitoVenta.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getRemitoVentaDets());
                //Obtengo el detalle agrupado por producto y unidad
                List<RemitoDetTotal> listaDetTotal=new ArrayList<>();
                Query q=session.createSQLQuery("SELECT b.nombre as producto,c.nombre as unidad,sum(cantidad) as cantidad " +
                        "from  remito_venta_det a,producto b,unidad c " +
                        "where idRemito= :idRemitoSel " +
                        "and   b.id=a.idProducto " +
                        "and   c.id=a.idUnidad " +
                        "group by b.nombre,c.nombre order by b.nombre,c.nombre");
                q.setParameter("idRemitoSel",registroMod.getId());
                List<Object[]> listaDetalle=(List<Object[]>) q.list();
                totalKilos=0;
                double ld_cantidad;
                for (Object[] datos : listaDetalle) {
                   RemitoDetTotal item=new RemitoDetTotal();
                   item.setProducto((String) datos[0]);
                   item.setUnidad((String) datos[1]);
                   ld_cantidad=((BigDecimal) datos[2]).doubleValue();
                   item.setCantidad(ld_cantidad);
                   listaDetTotal.add(item);
                   totalKilos=totalKilos + ld_cantidad;
                }
                listaTotalizada=listaDetTotal;
                session.getTransaction().commit();
            }
            catch (HibernateException e){
                session.getTransaction().rollback();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
            finally {
                session.close();
            }
            this.modo="M";
            return "/vistas/remitos/Edit";
        }
        else
            return null;
    }
    
}
