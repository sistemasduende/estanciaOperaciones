/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.CondIva;
import general.BeanBase;
import general.ListaCondicionesIva;
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
import seguridad.LogIn;

/**
 *
 * @author rafael
 */
@ManagedBean(name="condicionivacontroller")
@SessionScoped

public class CondicionIvaController extends BeanBase implements Serializable {
    CondIva registroSel;
    CondIva registroMod;
    private String modo="";
    String mensaje="";
    private List <CondIva> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public CondicionIvaController() {
        getListaDatos();
    }

    public CondIva getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(CondIva registroSel) {
        this.registroSel = registroSel;
    }

    public CondIva getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(CondIva registroMod) {
        this.registroMod = registroMod;
    }

    public List<CondIva> getLista() {
        return lista;
    }

    public void setLista(List<CondIva> lista) {
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
            Query q=session.createQuery("from CondIva a");
            this.lista=(List<CondIva>) q.list();
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
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new CondIva();
        this.registroMod=new CondIva();        
        this.modo="N";
        //Obtengo la empresa actual
        FacesMessage msg;
        Session session = null;
        
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
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
        
        return "CondIva";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        this.registroMod=new CondIva();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
        return "CondIva";
    }

    //Elimina registro seleccionado
    public String elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       FacesMessage msg;
       CondIva o=getRegistroSel();

       if (o!=null){
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
            return null;
       }
       finally {
            session.close();
       }
       
       
       }
       return null;

    }
    
    public String graba(){
        FacesMessage msg;
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        CondIva u=this.getRegistroMod();
        
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            this.setMensaje(e.getMessage());
            return null;
            
        }
        finally {
            session.close();
        }
        getListaDatos();
        
        try {
            //Actualizo lista de condiciones iva
            ListaCondicionesIva listaCondIvas= new ListaCondicionesIva();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listacondicionesiva",listaCondIvas);
            
        } catch (Exception ex) {
            Logger.getLogger(CondicionIvaController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "CondIvas";
    }
}
