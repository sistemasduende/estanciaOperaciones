/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Empresa;
import entidades.Tipoid;
import general.BeanBase;
import general.ListaTiposId;
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
@ManagedBean(name="tipoidcontroller")
@SessionScoped

public class TipoIdController extends BeanBase implements Serializable {
    Tipoid registroSel;
    Tipoid registroMod;
    private String modo="";
    String mensaje="";
    private List <Tipoid> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public TipoIdController() {
        getListaDatos();
    }

    public Tipoid getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Tipoid registroSel) {
        this.registroSel = registroSel;
    }

    public Tipoid getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Tipoid registroMod) {
        this.registroMod = registroMod;
    }

    public List<Tipoid> getLista() {
        return lista;
    }

    public void setLista(List<Tipoid> lista) {
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
            Query q=session.createQuery("from Tipoid a");
            this.lista=(List<Tipoid>) q.list();
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
        this.registroSel=new Tipoid();
        this.registroMod=new Tipoid();        
        this.modo="N";
        return "Tipoid";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        this.registroMod=new Tipoid();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
        return "Tipoid";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Tipoid o=getRegistroSel();

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
       }
       finally {
            session.close();
       }
       
       }

    }
    
    public void graba(){
        FacesMessage msg;
        Session session = null;
        Tipoid u=this.getRegistroMod();
        
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
        }
        finally {
            session.close();
        }
        
        getListaDatos();
        try {
            //Actualizo lista de rubros
            ListaTiposId listaTiposId= new ListaTiposId();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listatiposid",listaTiposId);
            
        } catch (Exception ex) {
            Logger.getLogger(TipoIdController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
}
