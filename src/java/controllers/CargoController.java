/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Cargo;
import general.BeanBase;
import general.ListaCargos;
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
@ManagedBean(name="cargocontroller")
@SessionScoped

public class CargoController extends BeanBase implements Serializable {
    Cargo registroSel;
    Cargo registroMod;
    private String modo="";
    String mensaje="";
    private List <Cargo> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public CargoController() {
        getListaDatos();
    }

    public Cargo getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Cargo registroSel) {
        this.registroSel = registroSel;
    }

    public Cargo getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Cargo registroMod) {
        this.registroMod = registroMod;
    }

    public List<Cargo> getLista() {
        return lista;
    }

    public void setLista(List<Cargo> lista) {
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
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Cargo a");
            this.lista=(List<Cargo>) q.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            this.setMensaje(e.getMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Error:" + e.getLocalizedMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        finally {
            session.close();
        }

    }
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new Cargo();
        this.registroMod=new Cargo();        
        this.modo="N";
        return "Cargo";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        this.registroMod=new Cargo();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
        return "Cargo";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       Cargo o=getRegistroSel();
       
       Session session;
       session=HibernateUtil.getSessionFactory().openSession();
       try{
           if (o!=null){
            session.beginTransaction();
            session.delete(o);
            session.getTransaction().commit();
            lista.remove(o);
            FacesMessage msg = new FacesMessage("Eliminación exitosa!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
           }
       }
       catch (HibernateException e){
            session.getTransaction().rollback();
            this.setMensaje(e.getMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Error:" + e.getLocalizedMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);
       }
        finally {
            session.close();
       }
       getListaDatos();
    }
    
    public String graba(){
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        Cargo u=this.getRegistroMod();
        
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            this.setMensaje(e.getMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Error:" + e.getLocalizedMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
            
        }
        finally {
            session.close();
        }
        getListaDatos();
        
        try {
            //Actualizo lista de rubros
            ListaCargos listaCargos= new ListaCargos();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listacargos",listaCargos);
            
        } catch (Exception ex) {
            Logger.getLogger(CargoController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        FacesMessage msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null,msg);
        return "Cargos";
    }
}
