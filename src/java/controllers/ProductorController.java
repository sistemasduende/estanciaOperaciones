/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Localidad;
import entidades.Productor;
import general.BeanBase;
import general.ListaLocalidades;
import general.ListaProductores;
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
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import seguridad.LogIn;

/**
 *
 * @author rafael
 */
@ManagedBean(name="productorcontroller")
@SessionScoped

public class ProductorController extends BeanBase implements Serializable {
    Productor registroSel;
    Productor registroMod;
    private String modo="";
    String mensaje="";
    private List <Productor> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private List<Localidad> localidades= new ArrayList<>();

    public ProductorController() {
        getListaDatos();
    }

    public Productor getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Productor registroSel) {
        this.registroSel = registroSel;
    }

    public Productor getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Productor registroMod) {
        this.registroMod = registroMod;
    }

    public List<Productor> getLista() {
        return lista;
    }

    public void setLista(List<Productor> lista) {
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

    
    
    //Obtiene lista de todos los registros
    public void getListaDatos(){
        FacesMessage msg;
        Session session=null;
        try{session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Productor a");
            this.lista=(List<Productor>) q.list();
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
        this.registroSel=new Productor();
        this.registroMod=new Productor();        
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
        return "Productor";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        this.registroMod=new Productor();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setCondIva(registroSel.getCondIva());
        registroMod.setLocalidad(registroSel.getLocalidad());
        registroMod.setProvincia(registroSel.getProvincia());
        registroMod.setTipoid(registroSel.getTipoid());
        registroMod.setNumId(registroSel.getNumId());
        registroMod.setNombre(registroSel.getNombre());
        registroMod.setDireccion(registroSel.getDireccion());
        registroMod.setTelefono(registroSel.getTelefono());
        registroMod.setEmail(registroSel.getEmail());
        registroMod.setEstado(registroSel.getEstado());
        registroMod.setCcCostoTropa(registroSel.getCcCostoTropa());
        this.modo="M";
        
        ListaLocalidades listaLoc= new  ListaLocalidades();
        listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
        localidades=listaLoc.getItems();

        return "Productor";
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClick(Productor p) throws Exception{
        this.registroMod=new Productor();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(p.getId());
        registroMod.setCondIva(p.getCondIva());
        registroMod.setLocalidad(p.getLocalidad());
        registroMod.setProvincia(p.getProvincia());
        registroMod.setTipoid(p.getTipoid());
        registroMod.setNumId(p.getNumId());
        registroMod.setNombre(p.getNombre());
        registroMod.setDireccion(p.getDireccion());
        registroMod.setTelefono(p.getTelefono());
        registroMod.setEmail(p.getEmail());
        registroMod.setEstado(p.getEstado());
        registroMod.setCcCostoTropa(p.getCcCostoTropa());
        registroSel=p;        
        this.modo="M";
        ListaLocalidades listaLoc= new  ListaLocalidades();
        listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
        localidades=listaLoc.getItems();

        return "Productor";
    }
    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Productor o=getRegistroSel();

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
        Productor u=this.getRegistroMod();
        
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
            //Actualizo lista de productores
            ListaProductores listaProductores= new ListaProductores();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listaproductores",listaProductores);
            
        } catch (Exception ex) {
            Logger.getLogger(ProductorController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
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
