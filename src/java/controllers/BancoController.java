/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Banco;
import general.BeanBase;
import general.ListaBancos;
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
@ManagedBean(name="bancocontroller")
@SessionScoped

public class BancoController extends BeanBase implements Serializable {
    Banco registroSel;
    Banco registroMod;
    private String modo="";
    String mensaje="";
    private List <Banco> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public BancoController() {
        getListaDatos();
    }

    public Banco getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Banco registroSel) {
        this.registroSel = registroSel;
    }

    public Banco getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Banco registroMod) {
        this.registroMod = registroMod;
    }

    public List<Banco> getLista() {
        return lista;
    }

    public void setLista(List<Banco> lista) {
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
            Query q=session.createQuery("from Banco a");
            this.lista=(List<Banco>) q.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e){
                session.getTransaction().rollback();
                e.printStackTrace();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
        }
        finally{
            session.close();
        }
    }
    
    //Nuevo registro
    public void nuevo(){
        this.registroSel=new Banco();
        this.registroMod=new Banco();        
        this.modo="N";
        //Obtengo la empresa actual
    }    
    
    //Obtiene los detalles del registro seleccionado
    public void edita(){
        this.registroMod=new Banco();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       Banco o=getRegistroSel();
       FacesMessage msg;
        
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
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        Banco u=this.getRegistroMod();
        try{
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
            ListaBancos listaBancos= new ListaBancos();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listabancos",listaBancos);
            
        } catch (Exception ex) {
            Logger.getLogger(BancoController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
