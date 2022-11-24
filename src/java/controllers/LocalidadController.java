/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Empresa;
import entidades.Localidad;
import entidades.Provincia;
import general.BeanBase;
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
@ManagedBean(name="localidadcontroller")
@SessionScoped

public class LocalidadController extends BeanBase implements Serializable {
    Localidad registroSel;
    Localidad registroMod;
    private String modo="";
    String mensaje="";
    private List <Localidad> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private int idProvincia;
    private Provincia provinciaSel;

    public LocalidadController() {
        cargarListaDatos();
    }

    public Localidad getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Localidad registroSel) {
        this.registroSel = registroSel;
    }

    public Localidad getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Localidad registroMod) {
        this.registroMod = registroMod;
    }

    public List<Localidad> getLista() {
        return lista;
    }

    public void setLista(List<Localidad> lista) {
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

    public int getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(int idProvincia) {
        this.idProvincia = idProvincia;
    }

    
    //Obtiene lista de todos los registros
    public void cargarListaDatos(){
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            provinciaSel=(Provincia) session.get(Provincia.class,idProvincia);
            Query q=session.createQuery("from Localidad a where a.provincia = :provinciaSel");
            q.setParameter("provinciaSel",provinciaSel);
            this.lista=(List<Localidad>) q.list();
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
    }
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new Localidad();
        this.registroMod=new Localidad();        
        this.modo="N";
        registroMod.setProvincia(provinciaSel);
        return "Localidad";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        this.registroMod=new Localidad();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setProvincia(registroSel.getProvincia());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
        return "Localidad";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       Localidad o=getRegistroSel();

       if (o!=null){
       Session session = null;
       try{
           session=HibernateUtil.getSessionFactory().openSession();
           session.beginTransaction();
           session.delete(o);
           session.getTransaction().commit();
           lista.remove(o);       
           FacesMessage msg = new FacesMessage("Eliminación exitosa!");
           FacesContext.getCurrentInstance().addMessage(null, msg);           
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
       
       
       }

    }
    
    public void graba(){
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        Localidad u=this.getRegistroMod();
        
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
        cargarListaDatos();
        
        FacesMessage msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
    
    //Procesa cambio de Provincia
    public void onChangeProvincia() {
            try {
                cargarListaDatos();
            }catch (Exception e) {
                this.setMensaje( "Error");
            }
   }
    
}
