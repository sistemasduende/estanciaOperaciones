/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Localidad;
import entidades.Cliente;
import entidades.CostoFleteAnimales;
import general.BeanBase;
import general.ListaClientes;
import general.ListaLocalidades;
import java.io.Serializable;
import java.util.ArrayList;
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
@ManagedBean(name="clientecontroller")
@SessionScoped

public class ClienteController extends BeanBase implements Serializable {
    Cliente registroSel;
    Cliente registroMod;
    CostoFleteAnimales registroModCostoAnimales;
    private String modo="";
    String mensaje="";
    private List <Cliente> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private List<Localidad> localidades= new ArrayList<>();

    public ClienteController() {
        buscaListaDatos();
    }

    public Cliente getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Cliente registroSel) {
        this.registroSel = registroSel;
    }

    public Cliente getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Cliente registroMod) {
        this.registroMod = registroMod;
    }

    public List<Cliente> getLista() {
        return lista;
    }

    public void setLista(List<Cliente> lista) {
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

    public CostoFleteAnimales getRegistroModCostoAnimales() {
        return registroModCostoAnimales;
    }

    public void setRegistroModCostoAnimales(CostoFleteAnimales registroModCostoAnimales) {
        this.registroModCostoAnimales = registroModCostoAnimales;
    }

    //Obtiene lista de todos los registros
    public void buscaListaDatos(){
        FacesMessage msg; 
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Cliente a");
            this.lista=(List<Cliente>) q.list();
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
        this.registroMod=new Cliente();     
        this.registroModCostoAnimales= new CostoFleteAnimales();
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
        return "/vistas/clientes/Create";
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
                this.registroMod =(Cliente) session.get(Cliente.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getLocalCarnicerias());
                Hibernate.initialize(this.registroMod.getCostoFleteAnimaleses());
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
            
            ListaLocalidades listaLoc= new  ListaLocalidades();
            listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
            localidades=listaLoc.getItems();
            return "/vistas/clientes/Edit";
        }
        else
            return null;
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClick(Cliente p) throws Exception{
        FacesMessage msg; 
        registroSel=p;
        if (registroSel != null)
        {
            Session session=null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(Cliente) session.get(Cliente.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getLocalCarnicerias());
                Hibernate.initialize(this.registroMod.getCostoFleteAnimaleses());
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
            ListaLocalidades listaLoc= new  ListaLocalidades();
            listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
            localidades=listaLoc.getItems();
            return "/vistas/clientes/Edit";
        }
        else
            return null;
    }
    
    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg; 
       FacesContext context=FacesContext.getCurrentInstance();
       Cliente o=getRegistroSel();
       if (o!=null){
           Session session=null;
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
    
    public String graba(){
        FacesMessage msg; 
        Cliente u=this.getRegistroMod();
        
        Session session=null;
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
        buscaListaDatos();
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        //Actualizo lista de rubros
        ListaClientes listaClientes = null;
        try {
            listaClientes = new ListaClientes();
        } catch (Exception ex) {
            Logger.getLogger(ClienteController.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesContext context=FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("listaclientes",listaClientes);

        return "Clientes";
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

    
    public void agregaCostoFleteAnimales(){
        CostoFleteAnimales costo= new CostoFleteAnimales();
        costo=registroModCostoAnimales;
        costo.setCliente(registroMod);
        this.getRegistroMod().getCostoFleteAnimaleses().add(costo);
    } 
   
    //Nuevo registro
    public void nuevoCA(){
        this.registroModCostoAnimales=new CostoFleteAnimales();
    }    
    
    public void eliminaCostoKm(CostoFleteAnimales o) {
       this.getRegistroMod().getCostoFleteAnimaleses().remove(o);
    }    
    
    
}
