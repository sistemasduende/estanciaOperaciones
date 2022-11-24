/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.CuentaBanco;
import general.BeanBase;
import general.ListaCuentasBanco;
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
@ManagedBean(name="cuentabancocontroller")
@SessionScoped

public class CuentaBancoController extends BeanBase implements Serializable {
    CuentaBanco registroSel;
    CuentaBanco registroMod;
    private String modo="";
    String mensaje="";
    private List <CuentaBanco> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public CuentaBancoController() {
        getListaDatos();
    }

    public CuentaBanco getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(CuentaBanco registroSel) {
        this.registroSel = registroSel;
    }

    public CuentaBanco getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(CuentaBanco registroMod) {
        this.registroMod = registroMod;
    }

    public List<CuentaBanco> getLista() {
        return lista;
    }

    public void setLista(List<CuentaBanco> lista) {
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
            Query q=session.createQuery("from CuentaBanco a");
            this.lista=(List<CuentaBanco>) q.list();
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
        this.registroSel=new CuentaBanco();
        this.registroMod=new CuentaBanco();        
        this.modo="N";
        this.registroMod.getBanco().setId(0);
        this.registroMod.getMoneda().setId(0);
        this.registroMod.setEstado('1');
        return "CuentaBanco";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        this.registroMod=new CuentaBanco();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNumeroCuenta(registroSel.getNumeroCuenta());
        registroMod.setBanco(registroSel.getBanco());
        registroMod.setTipoCuenta(registroSel.getTipoCuenta());
        registroMod.setMoneda(registroSel.getMoneda());
        registroMod.setEstado(registroSel.getEstado());
        this.modo="M";
        return "CuentaBanco";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       CuentaBanco o=getRegistroSel();

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
        CuentaBanco u=this.getRegistroMod();
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
            ListaCuentasBanco listaCuentasBanco= new ListaCuentasBanco();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listacuentasbanco",listaCuentasBanco);
            
        } catch (Exception ex) {
            Logger.getLogger(CuentaBancoController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
    
}
