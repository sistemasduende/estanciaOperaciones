/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.Entrega;
import entidades.Entrega;
import entidades.EntregaDet;
import general.BeanBase;
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
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author rafael
 */
@ManagedBean(name="entregacontroller")
@SessionScoped

public class EntregaController extends BeanBase{
    private TimeZone zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private java.util.Date fec_desde=null;
    private java.util.Date fec_hasta=null;
    private Entrega registroSel;
    private Entrega registroMod;
    private List <Entrega> lista=null;
    private List <Entrega> listaFiltrada=null;
    private List<EntregaDet> listaEntregaDet= new ArrayList<EntregaDet>();
    private List <EntregaDet> listaFiltradaDet=null;    
    private String modo="";
    private double totalKilos;
    
    public EntregaController() {
        
    }

    public Entrega getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Entrega registroSel) {
        this.registroSel = registroSel;
    }

    public Entrega getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Entrega registroMod) {
        this.registroMod = registroMod;
    }

    public List<Entrega> getLista() {
        return lista;
    }

    public void setLista(List<Entrega> lista) {
        this.lista = lista;
    }

    public TimeZone getZona() {
        return zona;
    }

    public void setZona(TimeZone zona) {
        this.zona = zona;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public List<Entrega> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<Entrega> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
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

    public List<EntregaDet> getListaEntregaDet() {
        return listaEntregaDet;
    }

    public void setListaEntregaDet(List<EntregaDet> listaEntregaDet) {
        this.listaEntregaDet = listaEntregaDet;
    }

    public List<EntregaDet> getListaFiltradaDet() {
        return listaFiltradaDet;
    }

    public void setListaFiltradaDet(List<EntregaDet> listaFiltradaDet) {
        this.listaFiltradaDet = listaFiltradaDet;
    }

    public double getTotalKilos() {
        return totalKilos;
    }

    public void setTotalKilos(double totalKilos) {
        this.totalKilos = totalKilos;
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
        
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Entrega a where fecEnt >= :fec_desde and fecEnt <= :fec_hasta order by fecEnt,id");
            q.setParameter("fec_desde",lda_fecha_desde);
            q.setParameter("fec_hasta",lda_fecha_hasta);
            this.lista=(List<Entrega>) q.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
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
                this.registroMod =(Entrega) session.get(Entrega.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getEntregaDets());
                session.getTransaction().commit();
                listaEntregaDet.clear();
                listaEntregaDet.addAll(this.registroMod.getEntregaDets());
                calculaSumaKilos();
                
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
            return "/vistas/entregas/Edit";
        }
        else
            return null;
    }
    
    public void calculaSumaKilos() {
        System.out.println("Entra calcular");
        totalKilos=listaEntregaDet.stream().mapToDouble(EntregaDet::getKilosDouble).sum();
        
        if (listaFiltradaDet != null){
            System.out.println("Filtrados: " + listaFiltradaDet.size());
            totalKilos=totalKilos - listaFiltradaDet.stream().mapToDouble(EntregaDet::getKilosDouble).sum();
        }
    }
    
}
