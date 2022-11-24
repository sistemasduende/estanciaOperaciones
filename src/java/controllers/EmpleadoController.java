/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Localidad;
import entidades.Empleado;
import general.BeanBase;
import general.ListaLocalidades;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
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
@ManagedBean(name="empleadocontroller")
@SessionScoped

public class EmpleadoController extends BeanBase implements Serializable {
    Empleado registroSel;
    Empleado registroMod;
    private String modo="";
    String mensaje="";
    private List <Empleado> lista;
    private List <Empleado> listaFiltrado;
    private TimeZone Zona = TimeZone.getDefault();
    private List<Localidad> localidades= new ArrayList<>();

    public EmpleadoController() {
        buscaListaDatos();
    }

    public Empleado getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Empleado registroSel) {
        this.registroSel = registroSel;
    }

    public Empleado getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Empleado registroMod) {
        this.registroMod = registroMod;
    }

    public List<Empleado> getLista() {
        return lista;
    }

    public void setLista(List<Empleado> lista) {
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

    public List<Localidad> getLocalidades() {
        return localidades;
    }

    public void setLocalidades(List<Localidad> localidades) {
        this.localidades = localidades;
    }

    public List<Empleado> getListaFiltrado() {
        return listaFiltrado;
    }

    public void setListaFiltrado(List<Empleado> listaFiltrado) {
        this.listaFiltrado = listaFiltrado;
    }

    //Obtiene lista de todos los registros
    public void buscaListaDatos(){
        FacesMessage msg;
        Session session=null;
        try{session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Empleado a");
            this.lista=(List<Empleado>) q.list();
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
        this.registroSel=new Empleado();
        this.registroMod=new Empleado();        
        this.modo="N";
        this.registroMod.setNumId(null);
        this.registroMod.setNombre(null);
        this.registroMod.setDireccion(null);
        this.registroMod.setTelefono(null);
        this.registroMod.setEmail(null);
        this.registroMod.getProvincia().setId(0);
        this.registroMod.getLocalidad().setId(0);
        this.registroMod.setEstado('1');
        localidades.clear();
        return "Empleado";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public void edita() throws Exception{
        FacesMessage msg; 
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(Empleado) session.get(Empleado.class,registroSel.getId());
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
            this.modo="M";
            
            ListaLocalidades listaLoc= new  ListaLocalidades();
            listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
            localidades=listaLoc.getItems();
        }
    }

    //Obtiene los detalles del registro que se hace clic
    public void onClick(Empleado p) throws Exception{
        FacesMessage msg; 
        registroSel=p;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(Empleado) session.get(Empleado.class,registroSel.getId());
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
            this.modo="M";
            ListaLocalidades listaLoc= new  ListaLocalidades();
            listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
            localidades=listaLoc.getItems();
        }
    }
    
    
    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Empleado o=getRegistroSel();

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
        Empleado u=this.getRegistroMod();
        
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
        buscaListaDatos();
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
     //Procesa cambio de Provincia
    public void onChangeProvincia() {
            try {
                ListaLocalidades listaLoc= new  ListaLocalidades();
                listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
                localidades=listaLoc.getItems();
                this.getRegistroMod().getLocalidad().setId(0);
                
            }catch (Exception e) {
                this.setMensaje( "Error");
            }
   }
}
