/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.ListaPrecios;
import entidades.ListaPreciosDet;
import entidades.ListaPreciosLocal;
import entidades.ListaPreciosLocalDet;
import entidades.LocalCarniceria;
import entidades.LocalRubro;
import entidades.Producto;
import general.BeanBase;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
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
 * @author Propietario
 */

@ManagedBean(name="listapreciocontroller")
@SessionScoped

public class ListaPrecioController extends BeanBase implements Serializable {
    ListaPrecios registroSel;
    ListaPrecios registroMod;
    ListaPreciosDet registroModDet;
    private List <ListaPrecios> lista=null;
    private TimeZone Zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private java.util.Date fec_desde=null;
    private java.util.Date fec_hasta=null;
    private List<ListaPreciosDet> listaPreciosDet= new ArrayList<ListaPreciosDet>();
    private String modo="";
    String mensaje="";
    private boolean noEditable=false;
    private boolean fechasNoEditables=false;
    private double porcAumento;

    public ListaPrecioController() {
    }

    public ListaPrecios getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(ListaPrecios registroSel) {
        this.registroSel = registroSel;
    }

    public ListaPrecios getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(ListaPrecios registroMod) {
        this.registroMod = registroMod;
    }

    public ListaPreciosDet getRegistroModDet() {
        return registroModDet;
    }

    public void setRegistroModDet(ListaPreciosDet registroModDet) {
        this.registroModDet = registroModDet;
    }

    public List<ListaPrecios> getLista() {
        return lista;
    }

    public void setLista(List<ListaPrecios> lista) {
        this.lista = lista;
    }

    public TimeZone getZona() {
        return Zona;
    }

    public void setZona(TimeZone Zona) {
        this.Zona = Zona;
    }

    public Date getFec_desde() {
        return fec_desde;
    }

    public void setFec_desde(Date fec_desde) {
        this.fec_desde = fec_desde;
    }

    public Date getFec_hasta() {
        return fec_hasta;
    }

    public void setFec_hasta(Date fec_hasta) {
        this.fec_hasta = fec_hasta;
    }

    public List<ListaPreciosDet> getListaPreciosDet() {
        return listaPreciosDet;
    }

    public void setListaPreciosDet(List<ListaPreciosDet> listaPreciosDet) {
        this.listaPreciosDet = listaPreciosDet;
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

    public boolean isNoEditable() {
        return noEditable;
    }

    public void setNoEditable(boolean noEditable) {
        this.noEditable = noEditable;
    }

    public boolean isFechasNoEditables() {
        return fechasNoEditables;
    }

    public void setFechasNoEditables(boolean fechasNoEditables) {
        this.fechasNoEditables = fechasNoEditables;
    }

    public double getPorcAumento() {
        return porcAumento;
    }

    public void setPorcAumento(double porcAumento) {
        this.porcAumento = porcAumento;
    }
    
    
    //Obtiene lista de todas las listas de precios
    public void buscaListaDatos(){
        FacesMessage msg;
        if (fec_desde==null){
            msg = new FacesMessage("Debe ingresar la fecha inicial de la búsqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        if (fec_hasta==null){
            msg = new FacesMessage("Debe ingresar la fecha final de la búsqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        java.util.Calendar fecha_desde = java.util.Calendar.getInstance();
        fecha_desde.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_desde.setTime(fec_desde);
        fecha_desde.set(Calendar.HOUR_OF_DAY, 0);
        fecha_desde.set(Calendar.MINUTE, 0);
        fecha_desde.set(Calendar.SECOND, 0);
        fecha_desde.set(Calendar.MILLISECOND, 0);
        java.util.Date lda_fecha_desde = new java.sql.Date(fecha_desde.getTimeInMillis());

        java.util.Calendar fecha_hasta = java.util.Calendar.getInstance();
        fecha_hasta.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_hasta.setTime(fec_hasta);
        fecha_hasta.set(Calendar.HOUR_OF_DAY, 23);
        fecha_hasta.set(Calendar.MINUTE, 59);
        fecha_hasta.set(Calendar.SECOND, 59);
        fecha_hasta.set(Calendar.MILLISECOND, 0);
        java.util.Date lda_fecha_hasta = new java.sql.Date(fecha_hasta.getTimeInMillis());        
        
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from ListaPrecios a where fecCarga >= :fec_desde and fecCarga <= :fec_hasta");
            q.setParameter("fec_desde",lda_fecha_desde);
            q.setParameter("fec_hasta",lda_fecha_hasta);
            this.lista=(List<ListaPrecios>) q.list();
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
    
    //Lista de productos
    public List<Producto> buscaListaProductos(){
        FacesMessage msg;
        List listaProductos;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Producto a where estado = '1'");
            listaProductos=(List<Producto>) q.list();
            session.getTransaction().commit();
            return listaProductos;
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
    
    //Nueva lista de precios
    public String nuevo(){
        listaPreciosDet= new ArrayList<ListaPreciosDet>();
        this.registroMod=new ListaPrecios();
        this.modo="N";
        this.registroMod.setIdUsuario(getUsuarioConectado().getIdUsuario());
            
        //Obtengo la fecha de hoy para poner como fecha de carga y fecha de movimiento
        java.util.Calendar fecha=java.util.Calendar.getInstance();
        java.sql.Date lda_fecha=new java.sql.Date(fecha.getTimeInMillis());
        this.registroMod.setFecCarga(lda_fecha);
        
        //Obtengo última lista de precios
        ListaPrecios ultimaLista=obtenerUltimaListaPrecios();
        
        if (ultimaLista != null){
            //Existe una lista de precios anterior
            //Calculo la fecha final de la lista de precios
            Date lda_fec_ini=ultimaLista.getFecFin();
            if (lda_fec_ini != null){
                registroMod.setFecIni(lda_fec_ini);
                //Sumo 7 días y pongo la fecha de fin
                int li_dias=7;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lda_fec_ini); 
                calendar.add(Calendar.DAY_OF_YEAR, li_dias);  
                registroMod.setFecFin(calendar.getTime());
            }
            fechasNoEditables=true;
        }
        else
            fechasNoEditables=false;
        
        //Agrego por default todos los productos de la lista a remanentes
        List<Producto> listaProductos=buscaListaProductos();
        for(Producto producto: listaProductos){
            ListaPreciosDet listaDet= new ListaPreciosDet();
            listaDet.setListaPrecios(registroMod);
            listaDet.setProducto(producto);
            
            if (ultimaLista != null){
                //Busco el precio del producto en la última lista de precios
                listaDet.setPrecio(BigDecimal.ZERO);
                Iterator i= ultimaLista.getListaPreciosDets().iterator();
                while (i.hasNext()){
                    ListaPreciosDet k= (ListaPreciosDet) i.next();
                    if (k.getProducto().getId()==producto.getId() ){
                        listaDet.setPrecio(k.getPrecio());
                        break;
                    }
                }
            }
            registroMod.getListaPreciosDets().add(listaDet);
        }
        
        //Cargo lista de productos ordenada
        cargaListaDetalles();
            
        return "/vistas/listaPrecios/Create";
       

    }    
    
    //Obtiene los detalles del registro seleccionado
    public String edita(){
        FacesMessage msg;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(ListaPrecios) session.get(ListaPrecios.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getListaPreciosDets());
                session.getTransaction().commit();
                
                //Cargo lista de precios ordenada
                cargaListaDetalles();

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
            fechasNoEditables=true;
            return "/vistas/listaPrecios/Edit";
        }
        else
            return null;
    }
    
    //Obtiene los detalles del registro seleccionado
    public String onClick(ListaPrecios p){
        FacesMessage msg;
        registroSel=p;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(ListaPrecios) session.get(ListaPrecios.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getListaPreciosDets());
                session.getTransaction().commit();
                
                //Cargo lista de precios ordenada
                cargaListaDetalles();

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
            return "/vistas/listaPrecios/Edit";
        }
        else
            return null;
    }
    
    
    //Lista de detalle de productos de la lista para mostrar agrupada
    public void cargaListaDetalles(){
        listaPreciosDet= new ArrayList<ListaPreciosDet>();
        Iterator i= this.getRegistroMod().getListaPreciosDets().iterator();
        while (i.hasNext()){
            ListaPreciosDet g= (ListaPreciosDet) i.next();
            listaPreciosDet.add(g);
        }
        
        //Ordeno la lista por rubro y nombre
        Collections.sort(listaPreciosDet,Comparator.comparing((ListaPreciosDet p)->p.getProducto().getRubro().getNombre())
          .thenComparing(p->p.getProducto().getNombre()));

    }

    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       FacesMessage msg;       
       ListaPrecios o=getRegistroSel();

       Session session;
       session=HibernateUtil.getSessionFactory().openSession();
       try{
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
    
    //Obtiene cantidad de registros por rubro
    public double getCantidadRegistrosRubro(int idRubro) {
        return registroMod.getListaPreciosDets().stream().filter(comprobante -> idRubro == comprobante.getProducto().getRubro().getId()).mapToInt(ListaPreciosDet::getId).count();
    }
    
    public void graba(){
        FacesMessage msg;        

        //Valido permiso para la transacción        
//        try {
//            if (!validaPermiso(getUsuarioConectado().getNombreUsuario(),ResourceBundle.getBundle("general/Permisos").getString("ModificarBalance"))){
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Transacción no autorizada","Balances");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//                return null;
//            }
//                
//        } catch (UnsupportedEncodingException ex) {
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getLocalizedMessage(),"Balances");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//            return null;
//        };

        if (registroMod.getFecIni()==null){
            msg = new FacesMessage("Debe ingresar la fecha de inicio");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        if (registroMod.getFecFin()==null){
            msg = new FacesMessage("Debe ingresar la fecha de fin");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        if (registroMod.getFecIni().compareTo(registroMod.getFecFin()) > 0){
            msg = new FacesMessage("La fecha de inicio debe ser menor que la fecha de fin");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        java.util.Calendar fecha_ini = java.util.Calendar.getInstance();
        fecha_ini.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_ini.setTime(registroMod.getFecIni());
        fecha_ini.set(Calendar.HOUR_OF_DAY, 12);
        fecha_ini.set(Calendar.MINUTE, 0);
        fecha_ini.set(Calendar.SECOND, 0);
        fecha_ini.set(Calendar.MILLISECOND, 0);
        registroMod.setFecIni(fecha_ini.getTime());
        
        java.util.Calendar fecha_fin = java.util.Calendar.getInstance();
        fecha_fin.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_fin.setTime(registroMod.getFecFin());
        fecha_fin.set(Calendar.HOUR_OF_DAY, 11);
        fecha_fin.set(Calendar.MINUTE, 59);
        fecha_fin.set(Calendar.SECOND, 0);
        fecha_fin.set(Calendar.MILLISECOND, 0);
        registroMod.setFecFin(fecha_fin.getTime());
        
        //Valido que haya un precio ingresado
        Iterator i= this.getRegistroMod().getListaPreciosDets().iterator();
//        while (i.hasNext()){
//            ListaPreciosDet g= (ListaPreciosDet) i.next();
//            double ld_precio=(g.getPrecio() != null ? g.getPrecio().doubleValue() : 0);
//            if (ld_precio <= 0 ){
//               msg = new FacesMessage("El producto " + g.getProducto().getNombre() + " no tiene precio");
//               FacesContext.getCurrentInstance().addMessage(null, msg);
//               return; 
//            }
//        }
        
        //Obtengo lista de locales
        List<LocalCarniceria> listaLocales=buscaListaLocales();
        
        //Grabo lista general
        Session session=null;
        ListaPrecios listaGeneral=this.getRegistroMod();
        try{
            session=HibernateUtil.getSessionFactory().openSession();            
            session.beginTransaction();
            session.saveOrUpdate(listaGeneral);
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            session.close();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        if (this.getModo().equals("N")){
            //Lista de precios nueva
            //Grabo las listas de precios para cada uno de los locales
            for(LocalCarniceria local : listaLocales){
                ListaPreciosLocal listaLocal= new ListaPreciosLocal();
                listaLocal.setLocalCarniceria(local);
                listaLocal.setFecCarga(registroMod.getFecCarga());
                listaLocal.setFecIni(registroMod.getFecIni());
                listaLocal.setFecFin(registroMod.getFecFin());
                listaLocal.setIdUsuario(registroMod.getIdUsuario());
                listaLocal.setObservaciones(registroMod.getObservaciones());
                
                i= this.getRegistroMod().getListaPreciosDets().iterator();
                while (i.hasNext()){
                    boolean lb_rubro_vendido=false;
                    ListaPreciosDet g= (ListaPreciosDet) i.next();
                    
                    //Reviso si el local vende el rubro del producto
                    Iterator k= local.getLocalRubros().iterator();
                    while (k.hasNext()){
                        LocalRubro r= (LocalRubro) k.next();
                        if (r.getRubro().getId()==g.getProducto().getRubro().getId()){
                            lb_rubro_vendido=true;
                            break;
                        }
                    }
                    
                    //Si el local vende el rubro entonces lo agrego a la lista de precios
                    if (lb_rubro_vendido){
                        ListaPreciosLocalDet q = new ListaPreciosLocalDet();
                        q.setListaPreciosLocal(listaLocal);
                        q.setPrecio(g.getPrecio());
                        q.setProducto(g.getProducto());
                        listaLocal.getListaPreciosLocalDets().add(q);
                    }
                }
        
                try{
                    session.saveOrUpdate(listaLocal);
                }
                catch (HibernateException e){
                    session.getTransaction().rollback();
                    session.close();
                    msg = new FacesMessage("Error: " + e.getCause().getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return;
                }
            }
        }

        //Grabo la auditoría de la transacción
//        try {
//            grabaAuditoria(getUsuarioConectado().getIdUsuario(),ResourceBundle.getBundle("general/Permisos").getString("ModificarBalance"),
//                    "Actualización de Balance Nro: " + registroMod.getId(),"Módulo de Balances",obtieneNombreEquipo());
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(BalanceController.class.getName()).log(Level.SEVERE, null, ex);
//        }

        session.getTransaction().commit();
        session.close();

        if (getModo().equals("N")) setModo("M");
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    //Elimina producto
    public void eliminaProducto(ListaPreciosDet i) {
       this.getRegistroMod().getListaPreciosDets().remove(i);
       //Elimino de la lista ordenada
       for (ListaPreciosDet det : listaPreciosDet){
           if (i.getProducto().equals(det.getProducto())){
               System.out.println("Producto eliminado: " + i.getProducto().getNombre());
               listaPreciosDet.remove(i);
               return;
           }
       }
    }    
    
    //Nuevo producto
    public void nuevoProducto(){
        ListaPreciosDet detalle=new ListaPreciosDet();
        this.registroModDet=detalle;
    }      
    
    //Agrega producto
    public void agregaProducto(){
        FacesMessage msg;        
        if (registroModDet.getProducto().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar el producto","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        ListaPreciosDet det= new ListaPreciosDet();
        det=registroModDet;
        //det.setPrecioTotal(new BigDecimal(det.getPrecioUnitario().doubleValue()*det.getCantidad().doubleValue()));
        
        //Obtengo la instancia de producto para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Producto c =(Producto) session.get(Producto.class,registroModDet.getProducto().getId());
        session.getTransaction().commit();
        session.close();
        
        det.setProducto(c);
        det.setListaPrecios(registroMod);
        this.getRegistroMod().getListaPreciosDets().add(det);
        listaPreciosDet.add(det);
        
    } 
    
    //Lista de locales
    public List<LocalCarniceria> buscaListaLocales(){
        FacesMessage msg;
        List<LocalCarniceria> listaLocales;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from LocalCarniceria a where estado = '1'");
            listaLocales=(List<LocalCarniceria>) q.list();
            for(LocalCarniceria local : listaLocales){
                Hibernate.initialize(local.getLocalRubros());
            }
            
            session.getTransaction().commit();
            return listaLocales;
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
    
    private ListaPrecios obtenerUltimaListaPrecios() {
        Session session = HibernateUtil.getSessionFactory().openSession();        
        try {
            //Obtengo el id del último ejercicio de la empresa

            session.beginTransaction();
            Query q=session.createQuery("select max(a.id) from ListaPrecios a");
            List resultList = q.list();
            
            ListaPrecios ultimaLista = null;
            int idUltimaLista=0;
            if (resultList.get(0) != null){
                //Hay una lista de precios
                idUltimaLista=((Integer) resultList.get(0)).intValue();
                System.out.println("Lista: " + idUltimaLista);
                q=session.createQuery("from ListaPrecios where id = :idLista ");
                q.setParameter("idLista",idUltimaLista);
                resultList = q.list();
                if (resultList.size() == 1){
                    ultimaLista=(ListaPrecios) resultList.get(0);
                    Hibernate.initialize(ultimaLista.getListaPreciosDets());
                    
                }
                session.getTransaction().commit();
            }
            return ultimaLista;
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        finally {
            session.close();
        }
        return null;
        
    }
    
    public void aplicaPorcentaje(){
        FacesMessage msg;
        if (porcAumento <= 0){
            msg = new FacesMessage("Debe ingresar el porcentaje de aumento");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        double ld_valor=0;
        Iterator i= registroMod.getListaPreciosDets().iterator();
            while (i.hasNext()){
                ListaPreciosDet k= (ListaPreciosDet) i.next();
                ld_valor=k.getPrecio().doubleValue();
                ld_valor=ld_valor*(1 + porcAumento/100);
                ld_valor = Math.round(ld_valor*100.0)/100.0;
                k.setPrecio(new BigDecimal(ld_valor));
                registroMod.getListaPreciosDets().add(k);
        }
        cargaListaDetalles();
        msg = new FacesMessage("Porcentaje de aumento aplicado!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
            
    }
    
}
