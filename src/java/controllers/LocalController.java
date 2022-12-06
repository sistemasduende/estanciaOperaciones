/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Empleado;
import entidades.ListaPrecios;
import entidades.ListaPreciosDet;
import entidades.ListaPreciosLocal;
import entidades.ListaPreciosLocalDet;
import entidades.Localidad;
import entidades.LocalCarniceria;
import entidades.LocalEmpleado;
import entidades.LocalRubro;
import entidades.Rubro;
import general.BeanBase;
import general.ListaLocales;
import general.ListaLocalidades;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
@ManagedBean(name="localcontroller")
@SessionScoped

public class LocalController extends BeanBase implements Serializable {
    LocalCarniceria registroSel;
    LocalCarniceria registroMod;
    ListaPreciosLocal registroModListaPrecios;
    private String modo="";
    String mensaje="";
    private List <LocalCarniceria> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private List<Localidad> localidades= new ArrayList<>();
    private List<Empleado> listaEmpleados=new ArrayList<Empleado>();
    private List<Empleado> listaEmpleadosSeleccionados=new ArrayList<Empleado>();
    private List<Rubro> listaRubros=new ArrayList<Rubro>();
    private List<Rubro> listaRubrosSeleccionados=new ArrayList<Rubro>();
    private List<ListaPreciosLocalDet> listaPreciosDet= new ArrayList<ListaPreciosLocalDet>();
    private boolean noEditable=false;
    private String intervaloListaPreciosLocal;

    public LocalController() {
        getListaDatos();
    }

    public LocalCarniceria getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(LocalCarniceria registroSel) {
        this.registroSel = registroSel;
    }

    public LocalCarniceria getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(LocalCarniceria registroMod) {
        this.registroMod = registroMod;
    }

    public List<LocalCarniceria> getLista() {
        return lista;
    }

    public void setLista(List<LocalCarniceria> lista) {
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

    public List<Empleado> getListaEmpleados() {
        return listaEmpleados;
    }

    public void setListaEmpleados(List<Empleado> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    public List<Empleado> getListaEmpleadosSeleccionados() {
        return listaEmpleadosSeleccionados;
    }

    public void setListaEmpleadosSeleccionados(List<Empleado> listaEmpleadosSeleccionados) {
        this.listaEmpleadosSeleccionados = listaEmpleadosSeleccionados;
    }

    public ListaPreciosLocal getRegistroModListaPrecios() {
        return registroModListaPrecios;
    }

    public void setRegistroModListaPrecios(ListaPreciosLocal registroModListaPrecios) {
        this.registroModListaPrecios = registroModListaPrecios;
    }

    public List<ListaPreciosLocalDet> getListaPreciosDet() {
        return listaPreciosDet;
    }

    public void setListaPreciosDet(List<ListaPreciosLocalDet> listaPreciosDet) {
        this.listaPreciosDet = listaPreciosDet;
    }

    public boolean isNoEditable() {
        return noEditable;
    }

    public void setNoEditable(boolean noEditable) {
        this.noEditable = noEditable;
    }

    public String getIntervaloListaPreciosLocal() {
        return intervaloListaPreciosLocal;
    }

    public void setIntervaloListaPreciosLocal(String intervaloListaPreciosLocal) {
        this.intervaloListaPreciosLocal = intervaloListaPreciosLocal;
    }

    public List<Rubro> getListaRubros() {
        return listaRubros;
    }

    public void setListaRubros(List<Rubro> listaRubros) {
        this.listaRubros = listaRubros;
    }

    public List<Rubro> getListaRubrosSeleccionados() {
        return listaRubrosSeleccionados;
    }

    public void setListaRubrosSeleccionados(List<Rubro> listaRubrosSeleccionados) {
        this.listaRubrosSeleccionados = listaRubrosSeleccionados;
    }

    
    //Obtiene lista de todos los registros
    public void getListaDatos(){
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from LocalCarniceria a order by orden");
            this.lista=(List<LocalCarniceria>) q.list();
            session.getTransaction().commit();
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
    
    //Nuevo registro
    public String nuevo(){
        this.registroSel=new LocalCarniceria();
        this.registroMod=new LocalCarniceria();        
        this.modo="N";
        this.registroMod.setNombreComercial(null);
        this.registroMod.setResponsable(null);
        this.registroMod.setDireccion(null);
        this.registroMod.setTelefono(null);
        this.registroMod.setEmail(null);
        this.registroMod.getProvincia().setId(0);
        this.registroMod.getLocalidad().setId(0);
        this.registroMod.setOrden(0);
        this.registroMod.setEstado('1');
        localidades.clear();
        return "/vistas/locales/Create";
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
                this.registroMod =(LocalCarniceria) session.get(LocalCarniceria.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getLocalEmpleados());
                Hibernate.initialize(this.registroMod.getListaPreciosLocals());
                Hibernate.initialize(this.registroMod.getLocalRubros());
                System.out.println("Lista rubros: " + this.registroMod.getLocalRubros());
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
            return "/vistas/locales/Edit";
        }
        else
            return null;
    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       LocalCarniceria o=getRegistroSel();

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
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        LocalCarniceria u=this.getRegistroMod();
        
        if (u.getIdUsuarioEncargado().intValue()==0)
            u.setIdUsuarioEncargado(null);
        
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            FacesMessage msg = new FacesMessage("Error: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        finally {
            session.close();
        }
        
        getListaDatos();
        try {
            //Actualizo lista de depósitos
            ListaLocales listaLocales= new ListaLocales();
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("listalocales",listaLocales);
            
        } catch (Exception ex) {
            Logger.getLogger(LocalController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        FacesMessage msg = new FacesMessage("Actualización exitosa!");
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
    
    //Busco empleados
    public void buscaEmpleados(){
        FacesMessage msg;
        listaEmpleados.clear();
        listaEmpleadosSeleccionados.clear();
        Session session=null;      
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();   
            Query q=session.createQuery("from Empleado a where estado='1'");
            listaEmpleados=(List<Empleado>) q.list();
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
    
    //Agrega empleados a local
    public void agregaEmpleados(){
        //Chequeo que no esté agregado
        for (Empleado empleado: listaEmpleadosSeleccionados) {
                LocalEmpleado e= new LocalEmpleado();
                e.setEmpleado(empleado);
                e.setLocalCarniceria(registroMod);
                
                boolean lb_existente=false;
                for (LocalEmpleado k: getRegistroMod().getLocalEmpleados()) {
                    if (k.getEmpleado().getId()==empleado.getId()){
                        lb_existente=true;
                        break;
                    }
                }
                
                if (!lb_existente)
                    this.getRegistroMod().getLocalEmpleados().add(e);
        }
    } 
    
    //Elimina empleado
    public void eliminaEmpleado(LocalEmpleado i) {
       this.getRegistroMod().getLocalEmpleados().remove(i);
    }    

    //Busco rubros
    public void buscaRubros(){
        FacesMessage msg;
        listaRubros.clear();
        listaRubrosSeleccionados.clear();
        Session session=null;      
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();   
            Query q=session.createQuery("from Rubro");
            listaRubros=(List<Rubro>) q.list();
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
    
    //Agrega rubros a local
    public void agregaRubros(){
        //Chequeo que no esté agregado
        for (Rubro rubro: listaRubrosSeleccionados) {
                LocalRubro e= new LocalRubro();
                e.setRubro(rubro);
                e.setLocalCarniceria(registroMod);
                
                boolean lb_existente=false;
                for (LocalRubro k: getRegistroMod().getLocalRubros()) {
                    if (k.getRubro().getId()==rubro.getId()){
                        lb_existente=true;
                        break;
                    }
                }
                
                if (!lb_existente)
                    this.getRegistroMod().getLocalRubros().add(e);
        }
        
        
        
    }
    
    //Elimina rubro
    public void eliminaRubro(LocalRubro i) {
       this.getRegistroMod().getLocalRubros().remove(i);
    }  
    
    //Edita lista de precios
    public void editaListaPrecios(ListaPreciosLocal l){
        FacesMessage msg;
        if (l != null){
            TimeZone gmtZone = TimeZone.getTimeZone("America/Buenos_Aires");
            DateFormat destDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            destDateFormat.setTimeZone(gmtZone);
            String ls_fecha;
            ls_fecha=destDateFormat.format(l.getFecIni());
            intervaloListaPreciosLocal="Desde: " + ls_fecha;
            ls_fecha=destDateFormat.format(l.getFecFin());
            intervaloListaPreciosLocal=intervaloListaPreciosLocal + " Hasta: " + ls_fecha;
            System.out.println("Intervalo: " + intervaloListaPreciosLocal);
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroModListaPrecios =(ListaPreciosLocal) session.get(ListaPreciosLocal.class,l.getId());
                Hibernate.initialize(this.registroModListaPrecios.getListaPreciosLocalDets());
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
            cargaListaDetalles();
        }
            
    }
    
    //Lista de detalle de productos de la lista para mostrar agrupada
    public void cargaListaDetalles(){
        listaPreciosDet= new ArrayList<ListaPreciosLocalDet>();
        Iterator i= this.getRegistroModListaPrecios().getListaPreciosLocalDets().iterator();
        while (i.hasNext()){
            ListaPreciosLocalDet g= (ListaPreciosLocalDet) i.next();
            listaPreciosDet.add(g);
        }
        
        //Ordeno la lista por rubro y nombre
        Collections.sort(listaPreciosDet,Comparator.comparing((ListaPreciosLocalDet p)->p.getProducto().getRubro().getNombre())
          .thenComparing(p->p.getProducto().getNombre()));

    }
    
    //Elimina producto
    public void eliminaProducto(ListaPreciosLocalDet i) {
       this.getRegistroModListaPrecios().getListaPreciosLocalDets().remove(i);
       //Elimino de la lista ordenada
       for (ListaPreciosLocalDet det : listaPreciosDet){
           if (i.getProducto().equals(det.getProducto())){
               System.out.println("Producto eliminado: " + i.getProducto().getNombre());
               listaPreciosDet.remove(i);
               return;
           }
       }
    }    
    
    public void grabaListaPrecios(){
        FacesMessage msg;        

        //Valido que haya un precio ingresado
        Iterator i= this.registroModListaPrecios.getListaPreciosLocalDets().iterator();
        while (i.hasNext()){
            ListaPreciosLocalDet g= (ListaPreciosLocalDet) i.next();
            double ld_precio=(g.getPrecio() != null ? g.getPrecio().doubleValue() : 0);
//            if (ld_precio <= 0 ){
//               msg = new FacesMessage("El producto " + g.getProducto().getNombre() + " no tiene precio");
//               FacesContext.getCurrentInstance().addMessage(null, msg);
//               return; 
//            }
        }
        
        //Grabo lista general
        Session session=null;
        ListaPreciosLocal listaGeneral=this.getRegistroModListaPrecios();
        try{
            session=HibernateUtil.getSessionFactory().openSession();            
            session.beginTransaction();
            session.saveOrUpdate(listaGeneral);
            session.getTransaction().commit();            
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        finally{
            session.close();
        }
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
}
