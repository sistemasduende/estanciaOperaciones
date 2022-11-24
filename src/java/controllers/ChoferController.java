/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Chofer;
import general.BeanBase;
import general.ListaChoferes;
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
@ManagedBean(name="chofercontroller")
@SessionScoped

public class ChoferController extends BeanBase implements Serializable {
    Chofer registroSel;
    Chofer registroMod;
    private String modo="";
    String mensaje="";
    private List <Chofer> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public ChoferController() {
        getListaDatos();
    }

    public Chofer getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Chofer registroSel) {
        this.registroSel = registroSel;
    }

    public Chofer getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Chofer registroMod) {
        this.registroMod = registroMod;
    }

    public List<Chofer> getLista() {
        return lista;
    }

    public void setLista(List<Chofer> lista) {
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
            Query q=session.createQuery("from Chofer a");
            this.lista=(List<Chofer>) q.list();
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
    public void nuevo(){
        this.registroSel=new Chofer();
        this.registroMod=new Chofer();        
        this.modo="N";
        this.registroMod.setTipo('L');  //Larga distancia
    }    
    
    //Obtiene los detalles del registro seleccionado
    public void edita(){
        this.registroMod=new Chofer();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setTipoid(registroSel.getTipoid());
        registroMod.setNumId(registroSel.getNumId());
        registroMod.setNombre(registroSel.getNombre());
        registroMod.setTipo(registroSel.getTipo());
        registroMod.setIdUsuario(registroSel.getIdUsuario());
        registroMod.setCcCostoTropa(registroSel.getCcCostoTropa());
        this.modo="M";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       FacesMessage msg;
       
       Chofer o=getRegistroSel();
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
        Chofer u=this.getRegistroMod();
        
        Session session = null;        
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
            ListaChoferes listaChoferes= new ListaChoferes();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listachoferes",listaChoferes);
            
        } catch (Exception ex) {
            Logger.getLogger(ChoferController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
