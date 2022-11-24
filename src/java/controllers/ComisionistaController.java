/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Localidad;
import entidades.Comisionista;
import general.BeanBase;
import general.ListaComisionistas;
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
@ManagedBean(name="comisionistacontroller")
@SessionScoped

public class ComisionistaController extends BeanBase implements Serializable {
    Comisionista registroSel;
    Comisionista registroMod;
    private String modo="";
    String mensaje="";
    private List <Comisionista> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private List<Localidad> localidades= new ArrayList<>();

    public ComisionistaController() {
        getListaDatos();
    }

    public Comisionista getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Comisionista registroSel) {
        this.registroSel = registroSel;
    }

    public Comisionista getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Comisionista registroMod) {
        this.registroMod = registroMod;
    }

    public List<Comisionista> getLista() {
        return lista;
    }

    public void setLista(List<Comisionista> lista) {
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
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Comisionista a");
            this.lista=(List<Comisionista>) q.list();
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
        this.registroSel=new Comisionista();
        this.registroMod=new Comisionista();        
        this.modo="N";
        this.registroMod.setTipo("CM");  //Comisionista
        this.registroMod.setNumId(null);
        this.registroMod.setNombre(null);
        this.registroMod.setDireccion(null);
        this.registroMod.setTelefono(null);
        this.registroMod.setEmail(null);
        this.registroMod.getProvincia().setId(0);
        this.registroMod.getLocalidad().setId(0);
        this.registroMod.setEstado('1');
        localidades.clear();
        return "Comisionista";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        this.registroMod=new Comisionista();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setTipo(registroSel.getTipo());
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

        return "Comisionista";
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClick(Comisionista p) throws Exception{
        this.registroMod=new Comisionista();
        //Pongo los datos a modificar del registro seleccionado
        registroSel=p;
        registroMod.setId(registroSel.getId());
        registroMod.setTipo(registroSel.getTipo());        
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

        return "Comisionista";
    }
    //Elimina registro seleccionado
    public String elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       FacesMessage msg;
       Comisionista o=getRegistroSel();

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
            return null;
        }
        finally {
            session.close();
        }
       
       }
       return null;

    }
    
    public String graba(){
        FacesMessage msg;
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        Comisionista u=this.getRegistroMod();
        try{
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
            //Actualizo lista de comisionistas
            ListaComisionistas listaComisionistas= new ListaComisionistas();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listacomisionistas",listaComisionistas);
            
        } catch (Exception ex) {
            Logger.getLogger(ComisionistaController.class.getName()).log(Level.SEVERE, null, ex);
        }        

        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "Comisionistas";
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
