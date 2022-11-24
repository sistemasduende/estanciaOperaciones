/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.EfectivoFlete;
import entidades.Empresa;
import entidades.Gasto;
import entidades.LiquidacionEfectivo;
import entidades.LiquidacionFlete;
import entidades.LiquidacionGasto;
import entidades.LiquidacionViaje;
import entidades.Viaje;
import general.BeanBase;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 * @author rafael
 */
@ManagedBean(name="liqfletecontroller")
@SessionScoped

public class LiqFleteController extends BeanBase implements Serializable {
    LiquidacionFlete registroSel;
    LiquidacionFlete registroMod;
    LiquidacionGasto registroModGasto;
    private String modo="";
    String mensaje="";
    private List <LiquidacionFlete> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private List <Viaje> listaViajes=new ArrayList<>();
    private List <Viaje> listaViajesSeleccionados=new ArrayList<>();
    private List <EfectivoFlete> listaEfectivos=new ArrayList<>();
    private List <EfectivoFlete> listaEfectivosSeleccionados=new ArrayList<>();
    private List <Gasto> listaGastos=new ArrayList<>();
    private List <Gasto> listaGastosSeleccionados=new ArrayList<>();
    
    
    private Viaje viajeSel;
    private String nombreChofer;

    public LiqFleteController() {
        buscaListaDatos();
    }

    public LiquidacionFlete getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(LiquidacionFlete registroSel) {
        this.registroSel = registroSel;
    }

    public LiquidacionFlete getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(LiquidacionFlete registroMod) {
        this.registroMod = registroMod;
    }

    public List<LiquidacionFlete> getLista() {
        return lista;
    }

    public void setLista(List<LiquidacionFlete> lista) {
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

    public LiquidacionGasto getRegistroModGasto() {
        return registroModGasto;
    }

    public void setRegistroModGasto(LiquidacionGasto registroModGasto) {
        this.registroModGasto = registroModGasto;
    }

    public List<Viaje> getListaViajes() {
        return listaViajes;
    }

    public void setListaViajes(List<Viaje> listaViajes) {
        this.listaViajes = listaViajes;
    }

    public Viaje getViajeSel() {
        return viajeSel;
    }

    public void setViajeSel(Viaje viajeSel) {
        this.viajeSel = viajeSel;
    }

    public String getNombreChofer() {
        return nombreChofer;
    }

    public void setNombreChofer(String nombreChofer) {
        this.nombreChofer = nombreChofer;
    }

    public List<Viaje> getListaViajesSeleccionados() {
        return listaViajesSeleccionados;
    }

    public void setListaViajesSeleccionados(List<Viaje> listaViajesSeleccionados) {
        this.listaViajesSeleccionados = listaViajesSeleccionados;
    }

    public List<EfectivoFlete> getListaEfectivos() {
        return listaEfectivos;
    }

    public void setListaEfectivos(List<EfectivoFlete> listaEfectivos) {
        this.listaEfectivos = listaEfectivos;
    }

    public List<EfectivoFlete> getListaEfectivosSeleccionados() {
        return listaEfectivosSeleccionados;
    }

    public void setListaEfectivosSeleccionados(List<EfectivoFlete> listaEfectivosSeleccionados) {
        this.listaEfectivosSeleccionados = listaEfectivosSeleccionados;
    }

    public List<Gasto> getListaGastos() {
        return listaGastos;
    }

    public void setListaGastos(List<Gasto> listaGastos) {
        this.listaGastos = listaGastos;
    }

    public List<Gasto> getListaGastosSeleccionados() {
        return listaGastosSeleccionados;
    }

    public void setListaGastosSeleccionados(List<Gasto> listaGastosSeleccionados) {
        this.listaGastosSeleccionados = listaGastosSeleccionados;
    }

    
    //Obtiene lista de todos los registros
    public void buscaListaDatos(){
        FacesMessage msg;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from LiquidacionFlete a");
            this.lista=(List<LiquidacionFlete>) q.list();
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
        this.registroMod=new LiquidacionFlete();  
        this.registroModGasto= new LiquidacionGasto();
        this.modo="N";
        this.registroMod.setIdUsuario(getUsuarioConectado().getIdUsuario());
        //Obtengo la fecha de hoy para poner como fecha de carga y fecha de movimiento
        java.util.Calendar fecha=java.util.Calendar.getInstance();
        java.sql.Date lda_fecha=new java.sql.Date(fecha.getTimeInMillis());
        this.registroMod.setFecCarga(lda_fecha);
        this.registroMod.setFecMov(lda_fecha);
        this.registroMod.setValorViajesSimples(BigDecimal.ZERO);
        this.registroMod.setValorViajesDobles(BigDecimal.ZERO);
        this.registroMod.setVueltoCaja(BigDecimal.ZERO);        
        this.registroMod.setCargasSociales(BigDecimal.ZERO);       
        this.registroMod.setProcesada('0'); 
        
        return "/vistas/liqFletes/Create";
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
                this.registroMod =(LiquidacionFlete) session.get(LiquidacionFlete.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getLiquidacionGastos());
                Hibernate.initialize(this.registroMod.getLiquidacionEfectivos());
                Hibernate.initialize(this.registroMod.getLiquidacionViajes());
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
            nombreChofer=this.getRegistroMod().getChofer().getNombre();
            return "/vistas/liqFletes/Edit";
        }
        else
            return null;
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClick(LiquidacionFlete p) throws Exception{
        FacesMessage msg;
        registroSel=p;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(LiquidacionFlete) session.get(LiquidacionFlete.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getLiquidacionGastos());
                Hibernate.initialize(this.registroMod.getLiquidacionEfectivos());
                Hibernate.initialize(this.registroMod.getLiquidacionViajes());
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
            nombreChofer=this.getRegistroMod().getChofer().getNombre();
            return "/vistas/liqFletes/Edit";
        }
        else
            return null;
    }
    
    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       LiquidacionFlete o=getRegistroSel();

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
    
    public String graba(){
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
    
        LiquidacionFlete u=this.getRegistroMod();
        
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            FacesMessage msg = new FacesMessage("Error: " + e.getLocalizedMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
            
        }
        finally {
            session.close();
        }
        buscaListaDatos();
        FacesMessage msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "LiqFletes";
    }
    
    //Elimina efectivo
    public void eliminaEfectivo(LiquidacionEfectivo i) {
       this.getRegistroMod().getLiquidacionEfectivos().remove(i);
        //Actualizo valores de los efectivos
        actualizaValorEfectivos();
        //Calculo los valores de la liquidación
        calculaValoresLiq();        
    }    

    //Elimina gasto
    public void eliminaGasto(LiquidacionGasto i) {
       this.getRegistroMod().getLiquidacionGastos().remove(i);
        //Actualizo valores de los gastos
        actualizaValoresGastos();
        //Calculo los valores de la liquidación
        calculaValoresLiq();        
    }    
    
    //Elimina viaje
    public void eliminaViaje(LiquidacionViaje i) {
       this.getRegistroMod().getLiquidacionViajes().remove(i);
        //Actualizo valores de los viajes
        actualizaValoresViajes();
        //Calculo los valores de la liquidación
        calculaValoresLiq();        
    }    
    
    
    //Calcula valores de viajes
    public void calculaValoresLiq(){
        double ld_total_viajesSimples=0,ld_valor_viajesSimples,ld_total_viajesDobles=0,ld_valor_viajesDobles=0;
        double ld_total_viajes=0,ld_valor_viajes=0;
        double ld_efectivos=0,ld_gastos=0;
        double ld_vuelto_caja=0,ld_cargas_sociales=0,ld_total_pagar=0;
        double ld_porc_viajes_simples=0,ld_porc_viajes_dobles=0;
        
        //Obtengo los porcentajes de viajes cargados en la empresa
        Empresa empresa= cargaEmpresa();
        if (empresa != null){
            ld_porc_viajes_simples=empresa.getPorcViajesSimples().doubleValue();
            ld_porc_viajes_dobles=empresa.getPorcViajesDobles().doubleValue();
        }
                
        if (registroMod.getTotalViajesSimples() != null)
           ld_total_viajesSimples=registroMod.getTotalViajesSimples().doubleValue();
            
        ld_valor_viajesSimples=(ld_total_viajesSimples*ld_porc_viajes_simples/100.00);
            
        if (registroMod.getTotalViajesDobles()!= null)
           ld_total_viajesDobles=registroMod.getTotalViajesDobles().doubleValue();
            
        ld_valor_viajesDobles=(ld_total_viajesDobles*ld_porc_viajes_dobles/100.00);
        
        if (registroMod.getVueltoCaja()!= null)
           ld_vuelto_caja=registroMod.getVueltoCaja().doubleValue();
        
        if (registroMod.getCargasSociales()!= null)
           ld_cargas_sociales=registroMod.getCargasSociales().doubleValue();
        
        ld_total_viajes=ld_total_viajesSimples + ld_total_viajesDobles;    
        ld_valor_viajes=ld_valor_viajesSimples + ld_valor_viajesDobles;  
        
        if (registroMod.getValorEfectivos()!= null)
           ld_efectivos=registroMod.getValorEfectivos().doubleValue();
        
        if (registroMod.getValorGastos()!= null)
           ld_gastos=registroMod.getValorGastos().doubleValue();
        
        ld_vuelto_caja=ld_efectivos - ld_gastos;
        
        ld_total_pagar=ld_valor_viajes - ld_vuelto_caja - ld_cargas_sociales;
        registroMod.setValorViajesSimples(new BigDecimal(ld_valor_viajesSimples));
        registroMod.setValorViajesDobles(new BigDecimal(ld_valor_viajesDobles));
        registroMod.setTotalViajes(new BigDecimal(ld_total_viajes));
        registroMod.setValorViajes(new BigDecimal(ld_valor_viajes));
        registroMod.setValorEfectivos(new BigDecimal(ld_efectivos));
        registroMod.setValorGastos(new BigDecimal(ld_gastos));
        registroMod.setVueltoCaja(new BigDecimal(ld_vuelto_caja));
        registroMod.setTotalPagar(new BigDecimal(ld_total_pagar));
        return;
    }

    
    //Busco viajes del chofer que no han sido liquidados
    public void buscaViajes(){
        listaViajes.clear();
        listaViajesSeleccionados.clear();
        
        nombreChofer=registroMod.getChofer().getNombre();
        System.out.println("Chofer: " +  nombreChofer);
        Session session = null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();        
            Query q=session.createQuery("from Viaje a where a.chofer = :chofer and procesado='0'");
            q.setParameter("chofer",registroMod.getChofer());
            listaViajes=(List<Viaje>) q.list();
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
    
    //Busco efectivos del chofer que no han sido liquidados
    public void buscaEfectivos(){
        listaEfectivos.clear();
        listaEfectivosSeleccionados.clear();
        
        Session session = null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();        
            Query q=session.createQuery("from EfectivoFlete a where a.fletero = :chofer and procesado='0'");
            q.setParameter("chofer",this.getRegistroMod().getChofer());
            listaEfectivos=(List<EfectivoFlete>) q.list();
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
    
    //Busco gastos del chofer que no han sido liquidados
    public void buscaGastos(){
        listaGastos.clear();
        listaGastosSeleccionados.clear();
        
        Session session = null;               
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction(); 
            Query q=session.createQuery("from Gasto a where procesado='0'");
            listaGastos=(List<Gasto>) q.list();
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
    
    public void cambiaChofer(){
        listaViajes.clear();
        listaViajesSeleccionados.clear();
        this.getRegistroMod().getLiquidacionViajes().clear();
        this.getRegistroMod().getLiquidacionEfectivos().clear();
        registroMod.setValorEfectivos(BigDecimal.ZERO);
        registroMod.setValorViajes(BigDecimal.ZERO);
        registroMod.setTotalViajesSimples(BigDecimal.ZERO);
        registroMod.setTotalViajesDobles(BigDecimal.ZERO);
        calculaValoresLiq();

    }

    //Agrega gasto a liquidación    
    public void agregaGastos(){
        for (Gasto gasto: listaGastosSeleccionados) {
                LiquidacionGasto e= new LiquidacionGasto();
                e.setGasto(gasto);
                e.setLiquidacionFlete(registroMod);
                this.getRegistroMod().getLiquidacionGastos().add(e);
        }
        //Actualizo valores de los gastos
        actualizaValoresGastos();
        //Calculo los valores de la liquidación
        calculaValoresLiq();        
    } 
    
    //Agrega viajes seleccionados a liquidación    
    public void agregaViajes(){
        for (Viaje viaje: listaViajesSeleccionados) {
                LiquidacionViaje v= new LiquidacionViaje();
                v.setViaje(viaje);
                v.setLiquidacionFlete(registroMod);
                this.getRegistroMod().getLiquidacionViajes().add(v);
        }
        //Actualizo valores de los viajes
        actualizaValoresViajes();
        //Calculo los valores de la liquidación
        calculaValoresLiq();   

    } 

    //Agrega efectivos seleccionados a liquidación    
    public void agregaEfectivos(){
        for (EfectivoFlete efec: listaEfectivosSeleccionados) {
                LiquidacionEfectivo e= new LiquidacionEfectivo();
                e.setEfectivoFlete(efec);
                e.setLiquidacionFlete(registroMod);
                this.getRegistroMod().getLiquidacionEfectivos().add(e);
        }
        //Actualizo valor de efectivos
        actualizaValorEfectivos();
        //Calculo los valores de la liquidación
        calculaValoresLiq();        
    }     
    
    //Actualiza total de gastos en liquidación
    public void actualizaValoresGastos(){
        double ld_gastos=0;
        Iterator i= this.getRegistroMod().getLiquidacionGastos().iterator();
        while (i.hasNext()){
                LiquidacionGasto g= (LiquidacionGasto) i.next();
                ld_gastos=ld_gastos + g.getGasto().getValorTotal().doubleValue();
        }
        registroMod.setValorGastos(new BigDecimal(ld_gastos));
    } 
    
    //Actualiza total de viajes simples y dobles en liquidación
    public void actualizaValoresViajes(){
        double ld_viajes_simples=0,ld_viajes_dobles=0;
        double ld_distancia=0,ld_distancia_total=0;
        Iterator i= this.getRegistroMod().getLiquidacionViajes().iterator();
        while (i.hasNext()){
                //Totales de viajes simples y dobles
                LiquidacionViaje v= (LiquidacionViaje) i.next();
                if (v.getViaje().getTipoJaula().equals('S'))
                    ld_viajes_simples=ld_viajes_simples + v.getViaje().getValorTotal().doubleValue();
                else
                    ld_viajes_dobles=ld_viajes_dobles + v.getViaje().getValorTotal().doubleValue();
                
                //Distancia total recorrida
                if (v.getViaje().getDistancia()!=null)
                    ld_distancia=v.getViaje().getDistancia().doubleValue();
                ld_distancia_total=ld_distancia_total + ld_distancia;
                
        }
        registroMod.setTotalViajesSimples(new BigDecimal(ld_viajes_simples));
        registroMod.setTotalViajesDobles(new BigDecimal(ld_viajes_dobles));
        registroMod.setKilometrosRecorridos( Double.valueOf(ld_distancia_total).intValue() );
    } 
    
    //Actualiza total de efectivos en liquidación
    public void actualizaValorEfectivos(){
        double ld_efectivos=0;
        Iterator i= this.getRegistroMod().getLiquidacionEfectivos().iterator();
        while (i.hasNext()){
                LiquidacionEfectivo v= (LiquidacionEfectivo) i.next();
                ld_efectivos=ld_efectivos + v.getEfectivoFlete().getValor().doubleValue();
        }
        registroMod.setValorEfectivos(new BigDecimal(ld_efectivos));
    } 
    

}
