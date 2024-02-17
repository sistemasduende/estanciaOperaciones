/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.Tarjeta;
import entidades.TarjetaPlan;
import general.BeanBase;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Usuario
 */
@ManagedBean(name="tarjetaController")
@SessionScoped
public class TarjetasController extends BeanBase implements Serializable{
    private Tarjeta registroSel, registroMod;
    private TarjetaPlan registroSelPlan, registroModPlan;
    private String modo = "";
    private String modoPlan = "";
    private String mensaje = "";
    private List<Tarjeta> tarjetas = new ArrayList<Tarjeta>();
    private TimeZone zona = TimeZone.getDefault();

    public TarjetasController() {
        getListaDatos();
    }
    
    public void getListaDatos(){
        FacesMessage msg;
        Session session =  HibernateUtil.getSessionFactory().openSession();
        try{
            session.beginTransaction();
            Query q = session.createQuery("from Tarjeta a");
            this.tarjetas = (List<Tarjeta>) q.list();
        }catch(Exception e){
            session.getTransaction().rollback();
            e.printStackTrace();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }finally{
            session.close();
        }
        this.registroSel = null;
        this.registroMod = null;
    }
    //Nuevo registro
    public String nuevo() {
        this.registroSel = new Tarjeta();
        this.registroMod = new Tarjeta();
        this.modo = "N";
        return "/vistas/tarjetas/Create";
    }
    public void nuevoPlan() {    
        this.registroModPlan = new TarjetaPlan();
        this.registroModPlan.setTarjeta(registroMod);
        this.modoPlan = "N";
    }
    public void editarPlan() {      
        registroModPlan = registroSelPlan;
    }
    
    public void actualizarPlan() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        try {
            this.registroMod.getTarjetaPlans().remove(this.registroSelPlan);
            this.registroMod.getTarjetaPlans().add(registroModPlan);
            /*int index = this.registroMod.getTarjetaPlans().indexOf(this.registroModPlan);
            this.planes.set(index, registroModPlan);*/
            /*msg = new FacesMessage("Plan actualizado con exito!");
            context.addMessage(null, msg);*/
        } catch (Exception e) {
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            context.addMessage(null, msg);
            return;
        }
    }
    
    public void agregarPlan() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        try {
            this.registroMod.getTarjetaPlans().add(registroModPlan);
            registroModPlan = new TarjetaPlan();
            /*msg = new FacesMessage("Plan agregado con exito!");
            context.addMessage(null, msg);*/
        } catch (Exception e) {
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            context.addMessage(null, msg);
            return;
        }
    }
    
    public String graba() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        if(registroMod.getNombre().equals("") || registroMod.getNombre()==null){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar un nombre para la tarjeta", "Tarjetas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction(); 
            session.saveOrUpdate(this.registroMod);
            session.getTransaction().commit();
            if (this.modo.equals("M")) {
                msg = new FacesMessage("Modificado con exito!");
                context.addMessage(null, msg);
            } else {
                msg = new FacesMessage("Guardado con exito!");
                context.addMessage(null, msg);
            }
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            session.close();
        }
        getListaDatos();
        this.registroSel = null;
        this.registroMod = null;
        this.registroSelPlan = null;
        this.registroModPlan = null;
        return "/vistas/tarjetas/Tarjetas";
    }
    //Obtiene los detalles del registro seleccionado
    public String edita() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        if (registroSel !=null) {
            Session session = null;
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod = (Tarjeta) session.get(Tarjeta.class, registroSel.getId());
                Hibernate.initialize(this.registroMod.getTarjetaPlans());           
                //this.registroMod.getTarjetaPlans().stream().collect(Collectors.toList());
                session.getTransaction().commit();
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                context.addMessage(null, msg);
                return null;
            } finally {
                session.close();
            }
            registroSelPlan = null;
            registroModPlan = null;
            this.modo = "M";
            return "/vistas/tarjetas/Edit";
        } else {
            return null;
        }
    }
    
    public void elimina() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        Tarjeta o = getRegistroSel();
        if(o!=null){
            Session session = null;
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                session.delete(o);
                session.getTransaction().commit();
                tarjetas.remove(o);
                msg = new FacesMessage("Eliminado con exito!");
                context.addMessage(null, msg);
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }finally{
                session.close();
            }
        }
    }
    
    public void eliminaPlan() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        try {
            this.registroMod.getTarjetaPlans().remove(this.registroSelPlan);
            /*msg = new FacesMessage("Plan eliminado con exito!");
            context.addMessage(null, msg);*/
        } catch (Exception e) {
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            context.addMessage(null, msg);
            return;
        }
    }
    
    public Tarjeta getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Tarjeta registroSel) {
        this.registroSel = registroSel;
    }

    public Tarjeta getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Tarjeta registroMod) {
        this.registroMod = registroMod;
    }

    public TarjetaPlan getRegistroSelPlan() {
        return registroSelPlan;
    }

    public void setRegistroSelPlan(TarjetaPlan registroSelPlan) {
        this.registroSelPlan = registroSelPlan;
    }

    public TarjetaPlan getRegistroModPlan() {
        return registroModPlan;
    }

    public void setRegistroModPlan(TarjetaPlan registroModPlan) {
        this.registroModPlan = registroModPlan;
    }

    public String getModoPlan() {
        return modoPlan;
    }

    public void setModoPlan(String modoPlan) {
        this.modoPlan = modoPlan;
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

    public List<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public TimeZone getZona() {
        return zona;
    }

    public void setZona(TimeZone zona) {
        this.zona = zona;
    }
    
    
}
