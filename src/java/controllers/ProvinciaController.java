/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Empresa;
import entidades.Provincia;
import general.BeanBase;
import general.ListaProvincias;
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
@ManagedBean(name="provinciacontroller")
@SessionScoped

public class ProvinciaController extends BeanBase implements Serializable {
    Provincia registroSel;
    Provincia registroMod;
    private String modo="";
    String mensaje="";
    private List <Provincia> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public ProvinciaController() {
        getListaDatos();
    }

    public Provincia getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Provincia registroSel) {
        this.registroSel = registroSel;
    }

    public Provincia getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Provincia registroMod) {
        this.registroMod = registroMod;
    }

    public List<Provincia> getLista() {
        return lista;
    }

    public void setLista(List<Provincia> lista) {
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
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Provincia a");
            this.lista=(List<Provincia>) q.list();
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
        this.registroSel=new Provincia();
        this.registroMod=new Provincia();        
        this.modo="N";
        return "Provincia";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        this.registroMod=new Provincia();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
        return "Provincia";
    }

    //Elimina registro seleccionado
    public void elimina() {
        FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Provincia o=getRegistroSel();

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
            return;
       }
       finally {
            session.close();
       }

       }
    }
    
    public void graba(){
        FacesMessage msg;
        Session session=null;
    
        Provincia u=this.getRegistroMod();
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
            //Actualizo lista de provincias
            ListaProvincias listaProvincias= new ListaProvincias();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listaprovincias",listaProvincias);
            
        } catch (Exception ex) {
            Logger.getLogger(ProvinciaController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
