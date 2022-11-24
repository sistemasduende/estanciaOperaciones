/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Categoria;
import entidades.CategoriaDet;
import entidades.TropaDet;
import general.BeanBase;
import general.ListaCategorias;
import java.io.Serializable;
import java.util.List;
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
 * @author rafael
 */
@ManagedBean(name="categoriacontroller")
@SessionScoped

public class CategoriaController extends BeanBase implements Serializable {
    Categoria registroSel;
    Categoria registroMod;
    CategoriaDet registroModDetalle;
    private String modo="";
    String mensaje="";
    private List <Categoria> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public CategoriaController() {
        getListaDatos();
    }

    public Categoria getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Categoria registroSel) {
        this.registroSel = registroSel;
    }

    public Categoria getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Categoria registroMod) {
        this.registroMod = registroMod;
    }

    public List<Categoria> getLista() {
        return lista;
    }

    public void setLista(List<Categoria> lista) {
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

    public CategoriaDet getRegistroModDetalle() {
        return registroModDetalle;
    }

    public void setRegistroModDetalle(CategoriaDet registroModDetalle) {
        this.registroModDetalle = registroModDetalle;
    }

    
    //Obtiene lista de todos los registros
    public void getListaDatos(){
        FacesMessage msg; 
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Categoria a");
            this.lista=(List<Categoria>) q.list();
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
    public void nuevoDetalle(){
        this.registroModDetalle=new CategoriaDet();
    } 
    
    public void agregaDetalleCategoria(){
        CategoriaDet detalle= new CategoriaDet();
        detalle=registroModDetalle;
        detalle.setCategoria(registroMod);
        this.getRegistroMod().getCategoriaDets().add(detalle);
    } 
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new Categoria();
        this.registroMod=new Categoria();        
        this.modo="N";
        //Obtengo la empresa actual
        return "/vistas/categorias/Create";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        FacesMessage msg; 
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(Categoria) session.get(Categoria.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getCategoriaDets());
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
            return "/vistas/categorias/Edit";
        }
        else
            return null;
    }
    
    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       Categoria o=getRegistroSel();
       FacesMessage msg;       

       if (o!=null){
        Session session = null;
        try{
           session=HibernateUtil.getSessionFactory().openSession();
           session.beginTransaction();
           session.delete(o);
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
        
        lista.remove(o);        
        getListaDatos();
        msg = new FacesMessage("Eliminación exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
       }

    }
    
    public String graba(){
        FacesMessage msg;       
        Categoria u=this.getRegistroMod();
        
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
            return null;
            
        }
        finally {
            session.close();
        }
        getListaDatos();
        
        try {
            //Actualizo lista de rubros
            ListaCategorias listaCategorias= new ListaCategorias();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listacategorias",listaCategorias);
            
        } catch (Exception ex) {
            Logger.getLogger(CategoriaController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "Categorias";
    }
    
    //Elimina detalle de categoría
    public void eliminaDetalle(CategoriaDet i) {
       this.getRegistroMod().getCategoriaDets().remove(i);
    }    
}
