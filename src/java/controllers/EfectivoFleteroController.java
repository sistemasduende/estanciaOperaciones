/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.EfectivoFlete;
import general.BeanBase;
import java.io.Serializable;
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
@ManagedBean(name="efectivofleterocontroller")
@SessionScoped

public class EfectivoFleteroController extends BeanBase implements Serializable {
    EfectivoFlete registroSel;
    EfectivoFlete registroMod;
    private String modo="";
    String mensaje="";
    private List <EfectivoFlete> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public EfectivoFleteroController() {
        buscaListaDatos();
    }

    public EfectivoFlete getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(EfectivoFlete registroSel) {
        this.registroSel = registroSel;
    }

    public EfectivoFlete getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(EfectivoFlete registroMod) {
        this.registroMod = registroMod;
    }

    public List<EfectivoFlete> getLista() {
        return lista;
    }

    public void setLista(List<EfectivoFlete> lista) {
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
    public void buscaListaDatos(){
        FacesMessage msg;
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from EfectivoFlete a");
            this.lista=(List<EfectivoFlete>) q.list();
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
    }
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new EfectivoFlete();
        this.registroMod=new EfectivoFlete();        
        this.modo="N";
        this.registroMod.setIdUsuario(getUsuarioConectado().getIdUsuario());
        this.registroMod.setProcesado('0');
        return "EfectivoFlete";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        this.registroMod=new EfectivoFlete();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setIdUsuario(registroSel.getIdUsuario());
        registroMod.setFecha(registroSel.getFecha());
        registroMod.setChofer(registroSel.getChofer());
        registroMod.setValor(registroSel.getValor());
        registroMod.setProcesado(registroSel.getProcesado());
        this.modo="M";
        return "EfectivoFlete";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       EfectivoFlete o=getRegistroSel();

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
    
        EfectivoFlete u=this.getRegistroMod();
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
        
        buscaListaDatos();
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
    
}
