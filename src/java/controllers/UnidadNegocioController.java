/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.UnidadNegocio;
import general.BeanBase;
import general.ListaUnidadesNegocio;
import java.io.Serializable;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author rafael
 */

@ManagedBean(name="unidadnegociocontroller")
@SessionScoped

public class UnidadNegocioController extends BeanBase implements Serializable {
    UnidadNegocio registroSel;
    UnidadNegocio registroMod;
    private String modo="";
    String mensaje="";
    private List <UnidadNegocio> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public UnidadNegocioController() {
        getListaDatos();
    }

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

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public TimeZone getZona() {
        return Zona;
    }

    public void setZona(TimeZone Zona) {
        this.Zona = Zona;
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
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new UnidadNegocio();
        this.registroMod=new UnidadNegocio();        
        this.modo="N";
        return "UnidadNegocio";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        this.registroMod=new UnidadNegocio();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
        return "UnidadNegocio";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       UnidadNegocio o=getRegistroSel();

       if (o!=null) {
       Session session = null;
       try{
           session=HibernateUtil.getSessionFactory().openSession();
           session.beginTransaction();
           session.delete(o);
           session.getTransaction().commit();
           lista.remove(o);       
           msg = new FacesMessage("Eliminación exitosa!");
           FacesContext.getCurrentInstance().addMessage(null, msg);           
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

    }
    
    public void graba(){
        FacesMessage msg;
        Session session = null;
        UnidadNegocio u=this.getRegistroMod();
       
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(u);
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
        getListaDatos();
        
        try {
            //Actualizo lista de rubros
            ListaUnidadesNegocio listaUnidadesNegocio= new ListaUnidadesNegocio();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listaunidadesnegocio",listaUnidadesNegocio);
            
        } catch (Exception ex) {
            Logger.getLogger(UnidadNegocioController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
}
