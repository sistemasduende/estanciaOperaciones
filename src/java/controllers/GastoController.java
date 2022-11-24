/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Gasto;
import general.BeanBase;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;
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
@ManagedBean(name="gastocontroller")
@SessionScoped

public class GastoController extends BeanBase implements Serializable {
    Gasto registroSel;
    Gasto registroMod;
    private String modo="";
    String mensaje="";
    private List <Gasto> lista=null;
    private TimeZone Zona = TimeZone.getDefault();

    public GastoController() {
        buscaListaDatos();
    }

    public Gasto getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Gasto registroSel) {
        this.registroSel = registroSel;
    }

    public Gasto getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Gasto registroMod) {
        this.registroMod = registroMod;
    }

    public List<Gasto> getLista() {
        return lista;
    }

    public void setLista(List<Gasto> lista) {
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
            Query q=session.createQuery("from Gasto a");
            this.lista=(List<Gasto>) q.list();
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
        this.registroSel=new Gasto();
        this.registroMod=new Gasto();        
        this.modo="N";
        //Obtengo la fecha de hoy para poner como fecha de carga y fecha de movimiento
        java.util.Calendar fecha=java.util.Calendar.getInstance();
        java.sql.Date lda_fecha=new java.sql.Date(fecha.getTimeInMillis());
        this.registroMod.setFecCarga(lda_fecha);
        this.registroMod.setFecMov(lda_fecha);        
        this.registroMod.setIdUsuario(getUsuarioConectado().getIdUsuario());
        this.registroMod.setCantidad(BigDecimal.ONE);
        this.registroMod.setValorGravado(BigDecimal.ZERO);
        this.registroMod.setValorNoGravado(BigDecimal.ZERO);
        this.registroMod.setPorcIva(BigDecimal.ZERO);
        this.registroMod.setValorIva(BigDecimal.ZERO);
        this.registroMod.setValorTotal(BigDecimal.ZERO);
        this.registroMod.setProcesado('0');
        
        return "Gasto";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        this.registroMod=new Gasto();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setTipoGasto(registroSel.getTipoGasto());
        registroMod.setUnidad(registroSel.getUnidad());
        registroMod.setIdUsuario(registroSel.getIdUsuario());
        registroMod.setFecMov(registroSel.getFecMov());
        registroMod.setFecCarga(registroSel.getFecCarga());
        registroMod.setCantidad(registroSel.getCantidad());
        registroMod.setValorGravado(registroSel.getValorGravado());
        registroMod.setPorcIva(registroSel.getPorcIva());
        registroMod.setValorIva(registroSel.getValorIva());
        registroMod.setValorNoGravado(registroSel.getValorNoGravado());
        registroMod.setValorTotal(registroSel.getValorTotal());
        registroMod.setObservaciones(registroSel.getObservaciones());
        registroMod.setProcesado(registroSel.getProcesado());
        this.modo="M";
        return "Gasto";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Gasto o=getRegistroSel();

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
        double ld_cantidad=0;
        FacesMessage msg;
        FacesContext context = FacesContext.getCurrentInstance();
        
        Session session = null;
        Gasto u=this.getRegistroMod();
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
        return;
    }
    
    //Calcula valores de gasto
    public void calculaValoresGasto(){
        double ld_porc_iva=0,ld_valor_iva=0,ld_valor_gravado=0,ld_valor_no_gravado=0,ld_valor_total=0;
        if (registroMod!= null){
            
            if (registroMod.getPorcIva()!= null)
                ld_porc_iva=registroMod.getPorcIva().doubleValue();
            
            ld_valor_gravado=registroMod.getValorGravado().doubleValue();
            ld_valor_no_gravado=registroMod.getValorNoGravado().doubleValue();
            ld_valor_iva=ld_valor_gravado*ld_porc_iva/100;
            ld_valor_total=ld_valor_gravado + ld_valor_no_gravado + ld_valor_iva;
            
            registroMod.setValorIva(new BigDecimal(ld_valor_iva));            
            registroMod.setValorTotal(new BigDecimal(ld_valor_total));            
        }
           
        return;
    }
    
}
