/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;
import entidades.Rubro;
import general.BeanBase;
import general.ListaRubros;
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

@ManagedBean(name="rubrocontroller")
@SessionScoped

public class RubroController extends BeanBase implements Serializable {
    Rubro registroSel;
    Rubro registroMod;
    private String modo="";
    String mensaje="";
    private List <Rubro> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public RubroController() {
        getListaDatos();
    }

    public Rubro getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Rubro registroSel) {
        this.registroSel = registroSel;
    }

    public Rubro getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Rubro registroMod) {
        this.registroMod = registroMod;
    }

    public List<Rubro> getLista() {
        return lista;
    }

    public void setLista(List<Rubro> lista) {
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
            Query q=session.createQuery("from Rubro a");
            this.lista=(List<Rubro>) q.list();
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
        this.registroSel=new Rubro();
        this.registroMod=new Rubro();        
        this.modo="N";
        return "Rubro";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        this.registroMod=new Rubro();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        this.modo="M";
        return "Rubro";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Rubro o=getRegistroSel();

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
        Session session = null;
        Rubro u=this.getRegistroMod();
       
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
            //Actualizo lista de rubros
            ListaRubros listaRubros= new ListaRubros();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listarubros",listaRubros);
            
        } catch (Exception ex) {
            Logger.getLogger(RubroController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
}
