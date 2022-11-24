/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Producto;
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

/**
 *
 * @author rafael
 */
@ManagedBean(name="productocontroller")
@SessionScoped

public class ProductoController extends BeanBase implements Serializable {
    Producto registroSel;
    Producto registroMod;
    private String modo="";
    String mensaje="";
    private List <Producto> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private int idRubroSel;

    public ProductoController() {
        buscaListaDatos();
    }

    public Producto getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Producto registroSel) {
        this.registroSel = registroSel;
    }

    public Producto getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Producto registroMod) {
        this.registroMod = registroMod;
    }

    public List<Producto> getLista() {
        return lista;
    }

    public void setLista(List<Producto> lista) {
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

    public int getIdRubroSel() {
        return idRubroSel;
    }

    public void setIdRubroSel(int idRubroSel) {
        this.idRubroSel = idRubroSel;
    }

    //Obtiene lista de todos los registros
    public void buscaListaDatos(){
        Query q;
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            if (idRubroSel==0)
                q=session.createQuery("from Producto a order by a.nombre");
            else {
                q=session.createQuery("from Producto a where a.rubro.id = :idRubroSel order by a.nombre");
                q.setParameter("idRubroSel",idRubroSel);
            }
                
            this.lista=(List<Producto>) q.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            FacesMessage msg = new FacesMessage("Error: " + e.getLocalizedMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        finally {
            session.close();
        }
    }
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new Producto();
        this.registroMod=new Producto();        
        this.modo="N";
        this.registroMod.setEstado('1');  //Activo
        return "Producto";
    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita() throws Exception{
        this.registroMod=new Producto();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setNombre(registroSel.getNombre());
        registroMod.setRubro(registroSel.getRubro());
        registroMod.setUnidad(registroSel.getUnidad());
        registroMod.setDetalle(registroSel.getDetalle());
        registroMod.setMarca(registroSel.getMarca());
        registroMod.setPorcentajeIva(registroSel.getPorcentajeIva());
        registroMod.setStockMinimo(registroSel.getStockMinimo());
        registroMod.setEstado(registroSel.getEstado());
        registroMod.setTipo(registroSel.getTipo());
        registroMod.setElaborado(registroSel.getElaborado());
        this.modo="M";
        return "Producto";
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       Producto o=getRegistroSel();

       if (o!=null){
       Session session = null;
       try{
           session=HibernateUtil.getSessionFactory().openSession();
           session.beginTransaction();
           session.delete(o);
           session.getTransaction().commit();
           lista.remove(o);
           FacesMessage msg = new FacesMessage("Eliminación exitosa!");
           FacesContext.getCurrentInstance().addMessage(null, msg);
       }
       catch (HibernateException e){
            session.getTransaction().rollback();
            FacesMessage msg = new FacesMessage("Error: " + e.getLocalizedMessage());
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
        Producto u=this.getRegistroMod();
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
    
        
}
