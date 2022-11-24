/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.TipoGasto;
import entidades.UnidadNegocio;
import entidades.UnidadNegocioGasto;
import general.BeanBase;
import general.ListaLocales;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author Propietario
 */

@ManagedBean(name="tiposGastoxunidadController")
@SessionScoped

public class TiposGastoxUnidadController extends BeanBase implements Serializable {
    UnidadNegocio registroSel;
    UnidadNegocio registroMod;
    private List <UnidadNegocio> lista=null;
    private TimeZone Zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private String modo="";
    private List <TipoGasto> listaGastos=new ArrayList<>();
    private List <TipoGasto> listaGastosSeleccionados=new ArrayList<>();
    
    public UnidadNegocio getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(UnidadNegocio registroSel) {
        this.registroSel = registroSel;
    }

    public UnidadNegocio getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(UnidadNegocio registroMod) {
        this.registroMod = registroMod;
    }

    public List<UnidadNegocio> getLista() {
        return lista;
    }

    public void setLista(List<UnidadNegocio> lista) {
        this.lista = lista;
    }

    public TimeZone getZona() {
        return Zona;
    }

    public void setZona(TimeZone Zona) {
        this.Zona = Zona;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public List<TipoGasto> getListaGastos() {
        return listaGastos;
    }

    public void setListaGastos(List<TipoGasto> listaGastos) {
        this.listaGastos = listaGastos;
    }

    public List<TipoGasto> getListaGastosSeleccionados() {
        return listaGastosSeleccionados;
    }

    public void setListaGastosSeleccionados(List<TipoGasto> listaGastosSeleccionados) {
        this.listaGastosSeleccionados = listaGastosSeleccionados;
    }

    public TiposGastoxUnidadController() {
        getListaDatos();
    }
    
    //Obtiene lista de todos los registros
    public void getListaDatos(){
        FacesMessage msg;
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from UnidadNegocio a");
            this.lista=(List<UnidadNegocio>) q.list();
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
                this.registroMod =(UnidadNegocio) session.get(UnidadNegocio.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getUnidadNegocioGastos());
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
            return "/vistas/unidadesNegocio/EditUnidadGastos";
        }
        else
            return null;
    }
    
    //Agrega tipos de gasto a Unidad de negocio
    public void agregaTipoGasto(){
        //Chequeo que no esté agregado
        for (TipoGasto gasto: listaGastosSeleccionados) {
                UnidadNegocioGasto e= new UnidadNegocioGasto();
                e.setTipoGasto(gasto);
                e.setUnidadNegocio(registroMod);
                this.getRegistroMod().getUnidadNegocioGastos().add(e);
        }
    } 
    
    //Elimina tipo de gasto
    public void eliminaTipoGasto(UnidadNegocioGasto i) {
       this.getRegistroMod().getUnidadNegocioGastos().remove(i);
    }    
    
    //Busco tipo de gasto para poder agregar
    public void buscaTiposGastos(){
        FacesMessage msg;
        listaGastos.clear();
        listaGastosSeleccionados.clear();
        
        Session session=null;      
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();   
            Query q=session.createQuery("from TipoGasto");
            listaGastos=(List<TipoGasto>) q.list();
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
    
    public void graba(){
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        UnidadNegocio u=this.getRegistroMod();
        
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            FacesMessage msg = new FacesMessage("Error: " + e.getLocalizedMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        finally {
            session.close();
        }
        
        getListaDatos();
        
        FacesMessage msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
    
    //Elimina gasto de la lista
    public void eliminaGasto(UnidadNegocioGasto i) {
       registroMod.getUnidadNegocioGastos().remove(i);
    }
    
}
