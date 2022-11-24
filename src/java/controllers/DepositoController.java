/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Localidad;
import entidades.Deposito;
import general.BeanBase;
import general.ListaComisionistas;
import general.ListaDepositos;
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
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import seguridad.LogIn;

/**
 *
 * @author rafael
 */
@ManagedBean(name="depositocontroller")
@SessionScoped

public class DepositoController extends BeanBase implements Serializable {
    Deposito registroSel;
    Deposito registroMod;
    private String modo="";
    String mensaje="";
    private List <Deposito> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private List<Localidad> localidades= new ArrayList<>();

    public DepositoController() {
        getListaDatos();
    }

    public Deposito getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Deposito registroSel) {
        this.registroSel = registroSel;
    }

    public Deposito getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Deposito registroMod) {
        this.registroMod = registroMod;
    }

    public List<Deposito> getLista() {
        return lista;
    }

    public void setLista(List<Deposito> lista) {
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
            Query q=session.createQuery("from Deposito a");
            this.lista=(List<Deposito>) q.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            this.setMensaje(e.getMessage());
            return ;
        }
        finally {
            session.close();
        }
    }
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new Deposito();
        this.registroMod=new Deposito();        
        this.modo="N";
        this.registroMod.setTipo('0'); //Seleccione tipo
        this.registroMod.setNombre(null);
        this.registroMod.setDireccion(null);
        this.registroMod.setTelefono(null);
        this.registroMod.setEmail(null);
        this.registroMod.getProvincia().setId(0);
        this.registroMod.getLocalidad().setId(0);
        this.registroMod.setTipoCobroFaena('K');  //Por Kilos
        localidades.clear();
        return "Deposito";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        this.registroMod=new Deposito();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setLocalidad(registroSel.getLocalidad());
        registroMod.setProvincia(registroSel.getProvincia());
        registroMod.setTipo(registroSel.getTipo());
        registroMod.setCuit(registroSel.getCuit());
        registroMod.setNombre(registroSel.getNombre());
        registroMod.setDireccion(registroSel.getDireccion());
        registroMod.setTelefono(registroSel.getTelefono());
        registroMod.setEmail(registroSel.getEmail());
        registroMod.setTipoCobroFaena(registroSel.getTipoCobroFaena());
        registroMod.setPrecioUnidad(registroSel.getPrecioUnidad());
        registroMod.setTropas(registroSel.getTropas());
        registroMod.setCcCostoTropa(registroSel.getCcCostoTropa());
        this.modo="M";
        
        ListaLocalidades listaLoc= new  ListaLocalidades();
        listaLoc.getLista(this.getRegistroMod().getProvincia().getId());
        localidades=listaLoc.getItems();

        return "Deposito";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Deposito o=getRegistroSel();

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
            this.setMensaje(e.getMessage());
            return;
       }
       finally {
            session.close();
       }
       
       
       }
       return;

    }
    
    public void graba(){
        FacesMessage msg;
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        Deposito u=this.getRegistroMod();
        
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            this.setMensaje(e.getMessage());
            return;
        }
        finally {
            session.close();
        }
        getListaDatos();
        
        try {
            //Actualizo lista de depósitos
            ListaDepositos listaDepositos= new ListaDepositos();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listadepositos",listaDepositos);
            
        } catch (Exception ex) {
            Logger.getLogger(DepositoController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
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
