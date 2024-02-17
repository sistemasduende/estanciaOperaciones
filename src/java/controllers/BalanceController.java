/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;
import database.Conector;
import entidades.Categoria;
import entidades.Balance;
import entidades.BalanceCobranza;
import entidades.BalanceDifPesada;
import entidades.BalanceEfectivo;
import entidades.BalanceGastoExtra;
import entidades.BalanceGastoPersonal;
import entidades.BalanceGastoVario;
import entidades.BalanceIngreso;
import entidades.BalanceInvIni;
import entidades.BalancePorCobrar;
import entidades.BalanceRemanente;
import entidades.Cliente;
import entidades.Empleado;
import entidades.LocalCarniceria;
import entidades.LocalEmpleado;
import entidades.Producto;
import entidades.Proveedor;
import entidades.TipoGasto;
import general.BeanBase;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author rafael
 */

@ManagedBean(name="balancecontroller")
@SessionScoped

public class BalanceController extends BeanBase implements Serializable {
    Balance registroSel;
    Balance registroMod;
    BalanceGastoExtra registroModGastoExtra;
    BalanceGastoVario registroModGastoVario;
    BalanceGastoPersonal registroModGastoPersonal;
    BalanceCobranza registroModCobranza;
    BalanceEfectivo registroModEfectivo;
    BalanceInvIni registroModInvIni;
    BalanceIngreso registroModIngreso;
    BalanceRemanente registroModRemanente;
    BalancePorCobrar registroModVentaCobrar;
    BalanceDifPesada registroModDiferencia;
    private String modo="";
    private String tipoBusqStock="T";
    String mensaje="";
    private int idLocalSel;
    private List <Balance> lista=null;
    private TimeZone Zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private java.util.Date fec_desde=null;
    private java.util.Date fec_hasta=null;
    private static final char PENDIENTE='P';
    private static final char FINALIZADO='F';
    private static final char APROBADO='A';
    private char estadoActual;
    private List listaEmpleados;
    private boolean noEditable=false;
    private boolean fechasNoEditables=false;
    private List<BalanceInvIni> listaInvIni= new ArrayList<BalanceInvIni>();
    private List listaRemanente= new ArrayList<BalanceRemanente>();
    private List<BalanceIngreso> listaIngresos= new ArrayList<BalanceIngreso>();
    
    public BalanceController() {
        
    }

    public Balance getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Balance registroSel) {
        this.registroSel = registroSel;
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

    public List<Balance> getLista() {
        return lista;
    }

    public void setLista(List<Balance> lista) {
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
    
    public Balance getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Balance registroMod) {
        this.registroMod = registroMod;
    }

    public String getTipoBusqStock() {
        return tipoBusqStock;
    }

    public void setTipoBusqStock(String tipoBusqStock) {
        this.tipoBusqStock = tipoBusqStock;
    }

    public char getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(char estadoActual) {
        this.estadoActual = estadoActual;
    }

    public BalanceGastoExtra getRegistroModGastoExtra() {
        return registroModGastoExtra;
    }

    public void setRegistroModGastoExtra(BalanceGastoExtra registroModGastoExtra) {
        this.registroModGastoExtra = registroModGastoExtra;
    }

    public BalanceGastoVario getRegistroModGastoVario() {
        return registroModGastoVario;
    }

    public void setRegistroModGastoVario(BalanceGastoVario registroModGastoVario) {
        this.registroModGastoVario = registroModGastoVario;
    }

    public BalanceGastoPersonal getRegistroModGastoPersonal() {
        return registroModGastoPersonal;
    }

    public void setRegistroModGastoPersonal(BalanceGastoPersonal registroModGastoPersonal) {
        this.registroModGastoPersonal = registroModGastoPersonal;
    }

    public BalanceCobranza getRegistroModCobranza() {
        return registroModCobranza;
    }

    public void setRegistroModCobranza(BalanceCobranza registroModCobranza) {
        this.registroModCobranza = registroModCobranza;
    }

    public BalanceInvIni getRegistroModInvIni() {
        return registroModInvIni;
    }

    public void setRegistroModInvIni(BalanceInvIni registroModInvIni) {
        this.registroModInvIni = registroModInvIni;
    }

    public BalanceIngreso getRegistroModIngreso() {
        return registroModIngreso;
    }

    public void setRegistroModIngreso(BalanceIngreso registroModIngreso) {
        this.registroModIngreso = registroModIngreso;
    }

    public BalanceRemanente getRegistroModRemanente() {
        return registroModRemanente;
    }

    public void setRegistroModRemanente(BalanceRemanente registroModRemanente) {
        this.registroModRemanente = registroModRemanente;
    }

    public BalancePorCobrar getRegistroModVentaCobrar() {
        return registroModVentaCobrar;
    }

    public void setRegistroModVentaCobrar(BalancePorCobrar registroModVentaCobrar) {
        this.registroModVentaCobrar = registroModVentaCobrar;
    }

    public BalanceEfectivo getRegistroModEfectivo() {
        return registroModEfectivo;
    }

    public void setRegistroModEfectivo(BalanceEfectivo registroModEfectivo) {
        this.registroModEfectivo = registroModEfectivo;
    }

    public BalanceDifPesada getRegistroModDiferencia() {
        return registroModDiferencia;
    }

    public void setRegistroModDiferencia(BalanceDifPesada registroModDiferencia) {
        this.registroModDiferencia = registroModDiferencia;
    }

    public List getListaEmpleados() {
        return listaEmpleados;
    }

    public void setListaEmpleados(List listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
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

    public List<BalanceInvIni> getListaInvIni() {
        return listaInvIni;
    }

    public void setListaInvIni(List<BalanceInvIni> listaInvIni) {
        this.listaInvIni = listaInvIni;
    }

    public List<BalanceRemanente> getListaRemanente() {
        return listaRemanente;
    }

    public void setListaRemanente(List<BalanceRemanente> listaRemanente) {
        this.listaRemanente = listaRemanente;
    }

    public List<BalanceIngreso> getListaIngresos() {
        return listaIngresos;
    }

    public void setListaIngresos(List<BalanceIngreso> listaIngresos) {
        this.listaIngresos = listaIngresos;
    }

    public int getIdLocalSel() {
        return idLocalSel;
    }

    public void setIdLocalSel(int idLocalSel) {
        this.idLocalSel = idLocalSel;
    }
    
   
    //Obtiene lista de todos las tropas
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
            Query q;
            if (idLocalSel==0)
                q=session.createQuery("from Balance a where fecCarga >= :fec_desde and fecCarga <= :fec_hasta");
            else {
                q=session.createQuery("from Balance a where fecCarga >= :fec_desde and fecCarga <= :fec_hasta"
                        + " and  localCarniceria.id = :localSel");
                q.setParameter("localSel",idLocalSel);
            }
            
            q.setParameter("fec_desde",lda_fecha_desde);
            q.setParameter("fec_hasta",lda_fecha_hasta);
            this.lista=(List<Balance>) q.list();
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
        listaInvIni= new ArrayList<BalanceInvIni>();
        listaRemanente= new ArrayList<BalanceRemanente>();
        listaIngresos= new ArrayList<BalanceIngreso>();
        
        fechasNoEditables=false;
        //Me fijo si el usuario es encargado de un local
        LocalCarniceria local=buscaLocalEncargado(getUsuarioConectado().getIdUsuario());
        if (local != null){
            //Es encargado de un local
            this.registroMod=new Balance();     
            this.registroModCobranza=new BalanceCobranza();
            this.registroModGastoExtra=new BalanceGastoExtra();
            this.registroModGastoPersonal=new BalanceGastoPersonal();
            this.registroModIngreso=new BalanceIngreso();
            this.registroModInvIni=new BalanceInvIni();
            this.registroModRemanente=new BalanceRemanente();
            this.modo="N";
            this.registroMod.setIdUsuario(getUsuarioConectado().getIdUsuario());
            this.registroMod.setLocalCarniceria(local);
            
            //Obtengo la fecha de hoy para poner como fecha de carga y fecha de movimiento
            java.util.Calendar fecha=java.util.Calendar.getInstance();
            java.sql.Date lda_fecha=new java.sql.Date(fecha.getTimeInMillis());
            this.registroMod.setFecCarga(lda_fecha);
            
            registroMod.setValorCobradoEfectivo(BigDecimal.ZERO);
            registroMod.setValorCobrado(BigDecimal.ZERO);
            registroMod.setValorEfectivoCaja(BigDecimal.ZERO);
            registroMod.setValorPorCobrar(BigDecimal.ZERO);
            registroMod.setValorGastos(BigDecimal.ZERO);
            registroMod.setValorRemanente(BigDecimal.ZERO);
            estadoActual=PENDIENTE; //Balance nueva por default EN TRAMITE
            registroMod.setEstado(PENDIENTE);
        
            //Cargo los datos del local
            seleccionaLocal();     
            System.out.println("Fecha fin:" + registroMod.getFecFin());
            
            //Agrego por default todos los productos de la lista a remanentes
            List<Producto> listaProductos=buscaListaProductos(local);
            for(Producto producto: listaProductos){
                double ld_precio=0;
                try {
                    //Obtengo el precio del producto
                    ld_precio=obtienePrecio(registroMod.getLocalCarniceria().getId(),producto.getId(),registroMod.getFecFin());
                } catch (SQLException ex) {
                    Logger.getLogger(BalanceController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                BalanceRemanente prodRemanente= new BalanceRemanente();
                prodRemanente.setBalance(registroMod);
                prodRemanente.setProducto(producto);
                prodRemanente.setUnidad(producto.getUnidad());
                prodRemanente.setCantidad(BigDecimal.ZERO);
                prodRemanente.setPrecioUnitario(new BigDecimal(ld_precio));
                prodRemanente.setPrecioTotal(BigDecimal.ZERO);
                registroMod.getBalanceRemanentes().add(prodRemanente);
            }
            //Cargo lista de remanentes para mostrar agrupados
            cargaListaRemanentes();
            
            
            return "/vistas/balances/Create";
        }
        else {
            FacesMessage msg;
            msg = new FacesMessage("El usuario no es encargado de ningún local");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

    }    
    
    //Lista de productos de los rubros que vende el local
    public List<Producto> buscaListaProductos(LocalCarniceria local){
        FacesMessage msg;
        List listaProductos;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("select a from Producto a,LocalRubro b where a.estado = '1' "
                    + "and b.localCarniceria = :local "
                    + "and b.rubro=a.rubro");
            q.setParameter("local",local);
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
    
    //Obtien el local del encargado
    public LocalCarniceria buscaLocalEncargado(int idUsuarioEncargado){
        FacesMessage msg;
        List listaUsuarios;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from LocalCarniceria a where idUsuarioEncargado = :idUsuarioEncargado");
            q.setParameter("idUsuarioEncargado",idUsuarioEncargado);
            listaUsuarios=(List<LocalCarniceria>) q.list();
            session.getTransaction().commit();
            
            if(listaUsuarios.size()==1)
                return (LocalCarniceria) listaUsuarios.get(0);
            else
                return null;
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
    
    //Lista de empleados del local
    public List<LocalEmpleado> buscaListaEmpleados(){
        FacesMessage msg;
        List<LocalEmpleado> listaEmpleadosLocal;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from LocalEmpleado a where localCarniceria = :local");
            q.setParameter("local",getRegistroMod().getLocalCarniceria());
            listaEmpleadosLocal=(List<LocalEmpleado>) q.list();
            session.getTransaction().commit();
            return listaEmpleadosLocal;
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
    
    //Obtiene remanentes de un balance
    public List<BalanceRemanente> buscaListaRemanentes(){
        FacesMessage msg;
        List<BalanceRemanente> listaRemanentesLocal;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from BalanceRemanente a where balance = :balanceLocal");
            q.setParameter("balanceLocal",getRegistroMod());
            listaRemanentesLocal=(List<BalanceRemanente>) q.list();
            session.getTransaction().commit();
            return listaRemanentesLocal;
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
    

    //Agrego por default todos los empleados del local para los gastos personales
    public void seleccionaLocal(){
        TipoGasto tipoGasto= new TipoGasto();
        //Obtengo la instancia del tipo de gasto por default
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        TipoGasto c =(TipoGasto) session.get(TipoGasto.class,1);
        session.getTransaction().commit();
        session.close();
        
        SelectItem item;
        List resultados=new ArrayList();
                
        registroMod.getBalanceGastoPersonals().clear();
        List<LocalEmpleado> listaEmpleados=buscaListaEmpleados();
        for(LocalEmpleado empleado : listaEmpleados){
            BalanceGastoPersonal gasto = new BalanceGastoPersonal();
            gasto.setEmpleado(empleado.getEmpleado());
            gasto.setTipoGasto(c);
            gasto.setBalance(registroMod);
            gasto.setValor(BigDecimal.ZERO);
            gasto.setFecha(new Date());
            registroMod.getBalanceGastoPersonals().add(gasto);
            
            //Armo lista para selección de empleados
            item=new SelectItem();
            item.setValue(empleado.getEmpleado().getId());
            item.setLabel(empleado.getEmpleado().getNombre() + " " + empleado.getEmpleado().getApellido());
            resultados.add(item);
        }
        setListaEmpleados(resultados);
        
        //Obtengo el último balance
        Balance ultimoBalance=null;
        try {
            ultimoBalance=obtieneUltimoBalance(registroMod.getLocalCarniceria().getId().intValue());
        } catch (SQLException ex) {
            Logger.getLogger(BalanceController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Si existe un balance anterior, traigo su remanente y lo cargo commo inventario inicial
        if (ultimoBalance != null){
            fechasNoEditables=true;
            
            Iterator i= ultimoBalance.getBalanceRemanentes().iterator();
            while (i.hasNext()){
                BalanceRemanente g= (BalanceRemanente) i.next();
                
                BalanceInvIni h= new BalanceInvIni();
                h.setBalance(registroMod);
                h.setProducto(g.getProducto());
                h.setCantidad(g.getCantidad());
                h.setUnidad(g.getUnidad());
                registroMod.getBalanceInvInis().add(h);
                listaInvIni.add(h);
            }
        }
    }
    
    public void cambiaTipo(BalanceGastoPersonal item){
        //Obtengo la instancia del tipo de gasto seleccionado para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        TipoGasto c =(TipoGasto) session.get(TipoGasto.class,item.getTipoGasto().getId());
        System.out.println("C: " + c);
        session.getTransaction().commit();
        session.close();
        item.setTipoGasto(c);
        
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
                this.registroMod =(Balance) session.get(Balance.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getBalanceCobranzas());
                Hibernate.initialize(this.registroMod.getBalanceGastoExtras());
                Hibernate.initialize(this.registroMod.getBalanceGastoPersonals());
                Hibernate.initialize(this.registroMod.getBalanceGastoVarios());
                Hibernate.initialize(this.registroMod.getBalanceIngresos());
                Hibernate.initialize(this.registroMod.getBalanceInvInis());
                Hibernate.initialize(this.registroMod.getBalancePorCobrars());
                Hibernate.initialize(this.registroMod.getBalanceRemanentes());
                Hibernate.initialize(this.registroMod.getBalanceEfectivos());
                Hibernate.initialize(this.registroMod.getBalanceDifPesadas());
                session.getTransaction().commit();
                
                //Lista de inventario inicial para mostrar agrupada
                cargaListaInvIni();
                
                //Cargo lista de remanentes para mostrar agrupados
                cargaListaRemanentes();

                //Lista de empleados del local para seleccionar
                SelectItem item;
                List resultados= new ArrayList();
                List<LocalEmpleado> listaEmpleados=buscaListaEmpleados();
                for(LocalEmpleado empleado : listaEmpleados){
                    //Armo lista para selección de empleados
                    item=new SelectItem();
                    item.setValue(empleado.getEmpleado().getId());
                    item.setLabel(empleado.getEmpleado().getNombre() + " " + empleado.getEmpleado().getApellido());
                    resultados.add(item);
                }
                setListaEmpleados(resultados);
                
                //No se puede modificar las fechas
                fechasNoEditables=true;
                estadoActual=registroMod.getEstado();
                if (estadoActual==FINALIZADO || estadoActual==APROBADO)
                    noEditable=true;
                else
                    noEditable=false;
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
            return "/vistas/balances/Edit";
        }
        else
            return null;
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClick(Balance p) throws Exception{
        FacesMessage msg;
        registroSel=p;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(Balance) session.get(Balance.class,registroSel.getId());
                Hibernate.initialize(this.registroMod.getBalanceCobranzas());
                Hibernate.initialize(this.registroMod.getBalanceGastoExtras());
                Hibernate.initialize(this.registroMod.getBalanceGastoPersonals());
                Hibernate.initialize(this.registroMod.getBalanceGastoVarios());
                Hibernate.initialize(this.registroMod.getBalanceIngresos());
                Hibernate.initialize(this.registroMod.getBalanceInvInis());
                Hibernate.initialize(this.registroMod.getBalancePorCobrars());
                Hibernate.initialize(this.registroMod.getBalanceRemanentes());
                Hibernate.initialize(this.registroMod.getBalanceEfectivos());
                Hibernate.initialize(this.registroMod.getBalanceDifPesadas());
                session.getTransaction().commit();
                
                //Lista de inventario inicial para mostrar agrupada
                cargaListaInvIni();
                
                //Cargo lista de remanentes para mostrar agrupados
                cargaListaRemanentes();
                
                //Cargo lista de ingresos
                cargaListaIngresos();
                
                //Lista de empleados del local para seleccionar
                SelectItem item;
                List resultados= new ArrayList();
                List<LocalEmpleado> listaEmpleados=buscaListaEmpleados();
                for(LocalEmpleado empleado : listaEmpleados){
                    //Armo lista para selección de empleados
                    item=new SelectItem();
                    item.setValue(empleado.getEmpleado().getId());
                    item.setLabel(empleado.getEmpleado().getNombre() + " " + empleado.getEmpleado().getApellido());
                    resultados.add(item);
                }
                setListaEmpleados(resultados);      

                //No se puede modificar las fechas
                fechasNoEditables=true;
                estadoActual=registroMod.getEstado();
                if (estadoActual==FINALIZADO || estadoActual==APROBADO)
                    noEditable=true;
                else
                    noEditable=false;

                
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
            return "/vistas/balances/Edit";
        }
        else
            return null;
    }
    
    //Lista de inventario inicial para mostrar agrupada
    public void cargaListaInvIni(){
        listaInvIni= new ArrayList<BalanceInvIni>();
        Iterator i= this.getRegistroMod().getBalanceInvInis().iterator();
        while (i.hasNext()){
            BalanceInvIni g= (BalanceInvIni) i.next();
            listaInvIni.add(g);
        }
    }
    
    //Lista de remanentes para mostrar agrupada
    public void cargaListaRemanentes(){
        listaRemanente= new ArrayList<BalanceRemanente>();
        Iterator i= this.getRegistroMod().getBalanceRemanentes().iterator();
        while (i.hasNext()){
            BalanceRemanente g= (BalanceRemanente) i.next();
            listaRemanente.add(g);
        }
        
        //Ordeno la lista por tipo y nombre
        Collections.sort(listaRemanente,Comparator.comparing((BalanceRemanente p)->p.getProducto().getRubro().getNombre())
          .thenComparing(p->p.getProducto().getNombre()));

    }
    
    //Lista de ingresos para mostrar agrupada
    public void cargaListaIngresos(){
        listaIngresos= new ArrayList<BalanceIngreso>();
        Iterator i= this.getRegistroMod().getBalanceIngresos().iterator();
        while (i.hasNext()){
            BalanceIngreso g= (BalanceIngreso) i.next();
            listaIngresos.add(g);
        }
        
        //Ordeno la lista por tipo y nombre
        Collections.sort(listaIngresos,Comparator.comparing((BalanceIngreso p)->p.getProducto().getRubro().getNombre())
          .thenComparing(p->p.getProducto().getNombre()));

    }
    
    //Elimina registro seleccionado
    public void elimina() {
       FacesContext context=FacesContext.getCurrentInstance();
       FacesMessage msg;       
       Balance o=getRegistroSel();

       if (o!=null){
           if (o.getEstado()=='A' || o.getEstado()=='F'){
               msg = new FacesMessage("Balance no puede ser eliminado!");
               FacesContext.getCurrentInstance().addMessage(null, msg);
           }    
           else{
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
       }
    }
    
    //Agrega gasto extra
    public void agregaGastoExtra(){
        FacesMessage msg;
        BalanceGastoExtra det= new BalanceGastoExtra();
        det=registroModGastoExtra;
        
        if (registroModGastoExtra.getTipoGasto().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar el tipo de gasto","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (registroModGastoExtra.getProveedor().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar un proveedor","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        //Obtengo la instancia del tipo de gasto seleccionado para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        TipoGasto c =(TipoGasto) session.get(TipoGasto.class,registroModGastoExtra.getTipoGasto().getId());
        Proveedor p= (Proveedor) session.get(Proveedor.class,registroModGastoExtra.getProveedor().getId());
        session.getTransaction().commit();
        session.close();
        det.setTipoGasto(c);
        det.setProveedor(p);
        det.setBalance(registroMod);
        
        this.getRegistroMod().getBalanceGastoExtras().add(det);
        //Actualizo valor de gastos 
        actualizaValorGastos();
       
    } 
    
    //Agrega gasto vario
    public void agregaGastoVario(){
        FacesMessage msg;
        BalanceGastoVario det= new BalanceGastoVario();
        det=registroModGastoVario;
        
        if (registroModGastoVario.getTipoGasto().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar el tipo de gasto","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (registroModGastoVario.getProveedor().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar un proveedor","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        //Obtengo la instancia del tipo de gasto seleccionado para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        TipoGasto c =(TipoGasto) session.get(TipoGasto.class,registroModGastoVario.getTipoGasto().getId());
        Proveedor p= (Proveedor) session.get(Proveedor.class,registroModGastoVario.getProveedor().getId());
        session.getTransaction().commit();
        session.close();
        det.setTipoGasto(c);
        det.setProveedor(p);
        det.setBalance(registroMod);
        
        this.getRegistroMod().getBalanceGastoVarios().add(det);
        //Actualizo valor de gastos 
        actualizaValorGastos();
       
    } 
    
    //Agrega gasto personal
    public void agregaGastoPersonal(){
        FacesMessage msg;        
        BalanceGastoPersonal det= new BalanceGastoPersonal();
        det=registroModGastoPersonal;
        
        if (registroModGastoPersonal.getTipoGasto().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar el tipo de gasto","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        //Obtengo la instancia del tipo de gasto seleccionado para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        TipoGasto c =(TipoGasto) session.get(TipoGasto.class,registroModGastoPersonal.getTipoGasto().getId());
        Empleado e= (Empleado)session.get(Empleado.class,registroModGastoPersonal.getEmpleado().getId());
        session.getTransaction().commit();
        session.close();
        det.setTipoGasto(c);
        det.setEmpleado(e);
        det.setBalance(registroMod);
        
        this.getRegistroMod().getBalanceGastoPersonals().add(det);
        //Actualizo valor de gastos 
        actualizaValorGastos();
       
    } 
    
    //Agrega cobranza
    public void agregaCobranza(){
        BalanceCobranza det= new BalanceCobranza();
        det=registroModCobranza;
        det.setBalance(registroMod);
        this.getRegistroMod().getBalanceCobranzas().add(det);
        //Actualizo valor de cobranzas
        actualizaValorCobranzas();
       
    } 
    
    //Agrega ingreso
    public void agregaIngreso(){
        FacesMessage msg;        
        if (registroModIngreso.getProducto().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar el producto","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }        
        
        BalanceIngreso det= new BalanceIngreso();
        det=registroModIngreso;
        det.setPrecioTotal(new BigDecimal(det.getPrecioUnitario().doubleValue()*det.getCantidad().doubleValue()));
        
        //Obtengo la instancia de producto para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Producto c =(Producto) session.get(Producto.class,registroModIngreso.getProducto().getId());
        Proveedor d = (Proveedor) session.get(Proveedor.class,registroModIngreso.getProveedor().getId());
        session.getTransaction().commit();
        session.close();
        
        det.setProducto(c);
        det.setProveedor(d);
        det.setBalance(registroMod);
        this.getRegistroMod().getBalanceIngresos().add(det);
        
        //Actualizo lista de ingresos para mostrar agrupadas
        cargaListaIngresos();
    } 
    
    //Agrega inventario inicial del balance
    public void agregaInvIni(){
        FacesMessage msg;        
        if (registroModInvIni.getProducto().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar el producto","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }        
        
        BalanceInvIni det= new BalanceInvIni();
        det=registroModInvIni;
        
        //Obtengo la instancia de producto para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Producto c =(Producto) session.get(Producto.class,registroModInvIni.getProducto().getId());
        session.getTransaction().commit();
        session.close();
        
        det.setProducto(c);
        det.setBalance(registroMod);
        this.getRegistroMod().getBalanceInvInis().add(det);
        //Actualizo lista de inventarios iniciales para mostrar agrupadas
        cargaListaInvIni();
    } 
    
    //Agrega remanente
    public void agregaRemanente(){
        FacesMessage msg;        
        if (registroModRemanente.getProducto().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar el producto","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }        
        
        BalanceRemanente det= new BalanceRemanente();
        det=registroModRemanente;
        //det.setPrecioTotal(new BigDecimal(det.getPrecioUnitario().doubleValue()*det.getCantidad().doubleValue()));
        
        //Obtengo la instancia de producto para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Producto c =(Producto) session.get(Producto.class,registroModRemanente.getProducto().getId());
        session.getTransaction().commit();
        session.close();
        
        det.setProducto(c);
        det.setBalance(registroMod);
        this.getRegistroMod().getBalanceRemanentes().add(det);
        
        //Actualizo lista de remanentes para mostrar agrupadas
        cargaListaRemanentes();
        
        //Actualizo valor de remanentes
        actualizaValorRemanente();
    } 
    
    //Agrega venta por cobrar
    public void agregaVentaCobrar(){
        FacesMessage msg;
        BalancePorCobrar det= new BalancePorCobrar();
        det=registroModVentaCobrar;
        
        if (registroModVentaCobrar.getCliente().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar un cliente","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        //Obtengo la instancia del cliente seleccionado para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Cliente p= (Cliente) session.get(Cliente.class,registroModVentaCobrar.getCliente().getId());
        session.getTransaction().commit();
        session.close();
        det.setCliente(p);
        
        det.setBalance(registroMod);
        this.getRegistroMod().getBalancePorCobrars().add(det);
        //Actualizo valor de ventas por cobrar
        actualizaValorVentasCobrar();
       
    } 
    
    //Agrega entrega de efectivo
    public void agregaEfectivo(){
        BalanceEfectivo det= new BalanceEfectivo();
        det=registroModEfectivo;
        det.setBalance(registroMod);
        this.getRegistroMod().getBalanceEfectivos().add(det);
    } 
    
    //Agrega diferencia de pesada
    public void agregaDiferencia(){
        FacesMessage msg;
        BalanceDifPesada det= new BalanceDifPesada();
        det=registroModDiferencia;
        
        if (registroModDiferencia.getCliente().getId()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar un cliente","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        //Obtengo la instancia del cliente seleccionado para que muestre bien en la pantalla
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Cliente p= (Cliente) session.get(Cliente.class,registroModDiferencia.getCliente().getId());
        session.getTransaction().commit();
        session.close();
        det.setCliente(p);

        det.setBalance(registroMod);
        this.getRegistroMod().getBalanceDifPesadas().add(det);
    } 
    
    //Elimina gasto extra
    public void eliminaGastoExtra(BalanceGastoExtra i) {
       this.getRegistroMod().getBalanceGastoExtras().remove(i);
       //Actualizo valor de gastos extras
        actualizaValorGastos();
    }    

    //Elimina gasto vario
    public void eliminaGastoVario(BalanceGastoVario i) {
       this.getRegistroMod().getBalanceGastoVarios().remove(i);
       //Actualizo valor de gastos extras
        actualizaValorGastos();
    }   
    
    //Elimina gasto personal
    public void eliminaGastoPersonal(BalanceGastoPersonal i) {
       this.getRegistroMod().getBalanceGastoPersonals().remove(i);
       //Actualizo valor de gastos extras
        actualizaValorGastos();
    }   
    
    //Elimina cobranza
    public void eliminaCobranza(BalanceCobranza i) {
       this.getRegistroMod().getBalanceCobranzas().remove(i);
       //Actualizo valor de cobranzas
        actualizaValorCobranzas();
    }   
    
    //Elimina registro de inventario inicial
    public void eliminaInvIni(BalanceInvIni i) {
       this.getRegistroMod().getBalanceInvInis().remove(i);
       cargaListaInvIni();
    }   
    
    //Elimina ingreso
    public void eliminaIngreso(BalanceIngreso i) {
       this.getRegistroMod().getBalanceIngresos().remove(i);
       cargaListaIngresos();
    } 
    
    //Elimina remanente
    public void eliminaRemanente(BalanceRemanente i) {
       this.getRegistroMod().getBalanceRemanentes().remove(i);
       //Actualizo valor del remanente
       actualizaValorRemanente();
       cargaListaRemanentes();
    } 
    
    //Elimina venta por cobrar
    public void eliminaVentaCobrar(BalancePorCobrar i) {
       this.getRegistroMod().getBalancePorCobrars().remove(i);
       //Actualizo valor de ventas por cobrar
       actualizaValorVentasCobrar();
    }  
    
    //Elimina entrega de efectivo
    public void eliminaEfectivo(BalanceEfectivo i) {
       this.getRegistroMod().getBalanceEfectivos().remove(i);
    } 
    
    //Elimina diferencia de pesada
    public void eliminaDiferencia(BalanceDifPesada i) {
       this.getRegistroMod().getBalanceDifPesadas().remove(i);
    } 
    
    //Actualiza total de gastos extras
    public void actualizaValorGastos(){
        double ld_gastos_extras=0,ld_gastos_varios=0,ld_gastos_personal=0;
        Iterator i= this.getRegistroMod().getBalanceGastoExtras().iterator();
        while (i.hasNext()){
                BalanceGastoExtra g= (BalanceGastoExtra) i.next();
                ld_gastos_extras=ld_gastos_extras + g.getValor().doubleValue();
        }
        
        i= this.getRegistroMod().getBalanceGastoPersonals().iterator();
        while (i.hasNext()){
                BalanceGastoPersonal g= (BalanceGastoPersonal) i.next();
                ld_gastos_personal=ld_gastos_personal + g.getValor().doubleValue();
        }
        
        i= this.getRegistroMod().getBalanceGastoVarios().iterator();
        while (i.hasNext()){
                BalanceGastoVario g= (BalanceGastoVario) i.next();
                ld_gastos_varios=ld_gastos_varios + g.getValor().doubleValue();
        }
        
        registroMod.setValorGastos(new BigDecimal(ld_gastos_extras + ld_gastos_personal + ld_gastos_varios));
       
    } 
    
    //Actualiza total de cobranzas
    public void actualizaValorCobranzas(){
        double ld_cobranzas=0;
        Iterator i= this.getRegistroMod().getBalanceCobranzas().iterator();
        while (i.hasNext()){
                BalanceCobranza g= (BalanceCobranza) i.next();
                ld_cobranzas=ld_cobranzas + g.getValor().doubleValue();
        }
        registroMod.setValorCobrado(new BigDecimal(ld_cobranzas));
    } 
    
    //Actualiza total de remanentes
    public void actualizaValorRemanente(){
        double ld_total=0;
        Iterator i= this.getRegistroMod().getBalanceRemanentes().iterator();
        while (i.hasNext()){
                BalanceRemanente g= (BalanceRemanente) i.next();
                ld_total=ld_total + g.getPrecioTotal().doubleValue();
        }
        registroMod.setValorRemanente(new BigDecimal(ld_total));
    } 
    
    public void actualizaValorVentasCobrar(){
        double ld_ventas_cobrar=0;
        Iterator i= this.getRegistroMod().getBalancePorCobrars().iterator();
        while (i.hasNext()){
                BalancePorCobrar g= (BalancePorCobrar) i.next();
                ld_ventas_cobrar=ld_ventas_cobrar + g.getValor().doubleValue();
        }
        registroMod.setValorPorCobrar(new BigDecimal(ld_ventas_cobrar));
    } 
    
    //Nuevo gasto extra
    public void nuevoGastoExtra(){
        BalanceGastoExtra detalle=new BalanceGastoExtra();
        //Obtengo la fecha de hoy para poner como fecha predeterminada del gasto
        java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
        fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_mov.setTime(new Date());
        fecha_mov.set(Calendar.HOUR_OF_DAY, 0);
        fecha_mov.set(Calendar.MINUTE, 0);
        fecha_mov.set(Calendar.SECOND, 0);
        fecha_mov.set(Calendar.MILLISECOND, 0);        
        detalle.setFecha(fecha_mov.getTime());
        
        this.registroModGastoExtra=detalle;
    }    
    
    //Nuevo gasto vario
    public void nuevoGastoVario(){
        BalanceGastoVario detalle=new BalanceGastoVario();
        //Obtengo la fecha de hoy para poner como fecha predeterminada del gasto
        java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
        fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_mov.setTime(new Date());
        fecha_mov.set(Calendar.HOUR_OF_DAY, 0);
        fecha_mov.set(Calendar.MINUTE, 0);
        fecha_mov.set(Calendar.SECOND, 0);
        fecha_mov.set(Calendar.MILLISECOND, 0);        
        detalle.setFecha(fecha_mov.getTime());
        
        this.registroModGastoVario=detalle;
    }    
    
    //Nuevo gasto personal
    public void nuevoGastoPersonal(){
        BalanceGastoPersonal detalle=new BalanceGastoPersonal();
        //Obtengo la fecha de hoy para poner como fecha predeterminada del gasto
        java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
        fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_mov.setTime(new Date());
        fecha_mov.set(Calendar.HOUR_OF_DAY, 0);
        fecha_mov.set(Calendar.MINUTE, 0);
        fecha_mov.set(Calendar.SECOND, 0);
        fecha_mov.set(Calendar.MILLISECOND, 0);        
        detalle.setFecha(fecha_mov.getTime());
        
        this.registroModGastoPersonal=detalle;
    }    
    
    //Nueva venta por cobrar
    public void nuevaVentaCobrar(){
        BalancePorCobrar detalle=new BalancePorCobrar();
        //Obtengo la fecha de hoy para poner como fecha predeterminada del gasto
        java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
        fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_mov.setTime(new Date());
        fecha_mov.set(Calendar.HOUR_OF_DAY, 0);
        fecha_mov.set(Calendar.MINUTE, 0);
        fecha_mov.set(Calendar.SECOND, 0);
        fecha_mov.set(Calendar.MILLISECOND, 0);        
        detalle.setFecha(fecha_mov.getTime());
        
        this.registroModVentaCobrar=detalle;
    }    
    
    //Nueva cobranza
    public void nuevaCobranza(){
        BalanceCobranza detalle=new BalanceCobranza();
        this.registroModCobranza=detalle;
    }    
    
    //Nuevo inv inicial
    public void nuevoInvIni(){
        BalanceInvIni detalle=new BalanceInvIni();
        this.registroModInvIni=detalle;
    }    
    
    //Nuevo ingreso
    public void nuevoIngreso(){
        BalanceIngreso detalle=new BalanceIngreso();
        detalle.setPrecioUnitario(BigDecimal.ZERO);
        detalle.setPrecioTotal(BigDecimal.ZERO);
        this.registroModIngreso=detalle;
    } 
    
    //Nuevo remanente
    public void nuevoRemanente(){
        BalanceRemanente detalle=new BalanceRemanente();
        this.registroModRemanente=detalle;
    } 
    
    //Nueva entrega de efectivo
    public void nuevoEfectivo(){
        BalanceEfectivo detalle=new BalanceEfectivo();
        this.registroModEfectivo=detalle;
    }    
    
    //Nueva diferencia de pesada
    public void nuevaDiferencia(){
        BalanceDifPesada detalle=new BalanceDifPesada();
        this.registroModDiferencia=detalle;
    }   
    
    //Edita gasto extra
    public void editaGastoExtra(BalanceGastoExtra d){
        if (d != null)
            registroModGastoExtra=d;
    }
    
    //Edita gasto vario
    public void editaGastoVario(BalanceGastoVario d){
        if (d != null)
            registroModGastoVario=d;
    }

    //Edita gasto personal
    public void editaGastoPersonal(BalanceGastoPersonal d){
        if (d != null)
            registroModGastoPersonal=d;
    }
    
    //Edita cobranza
    public void editaCobranza(BalanceCobranza d){
        if (d != null)
            registroModCobranza=d;
    }
    
    //Edita inventario inicial
    public void editaInvIni(BalanceInvIni d){
        if (d != null)
            registroModInvIni=d;
    }
    
    //Edita ingreso
    public void editaIngreso(BalanceIngreso d){
        if (d != null)
            registroModIngreso=d;
    }
    
    //Edita remanente
    public void editaRemanente(BalanceRemanente d){
        if (d != null)
            registroModRemanente=d;
    }
    
    //Edita venta por cobrar
    public void editaVentaCobrar(BalancePorCobrar d){
        if (d != null)
            registroModVentaCobrar=d;
    }
    
    //Edita entrega de efectivo
    public void editaEfectivo(BalanceEfectivo d){
        if (d != null)
            registroModEfectivo=d;
    }
    
    //Edita diferencia de pesada
    public void editaDiferencia(BalanceDifPesada d){
        if (d != null)
            registroModDiferencia=d;
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
        
        if (registroMod.getFecIni().compareTo(registroMod.getFecCarga()) > 0){
            msg = new FacesMessage("La fecha de inicio no puede ser mayor que la fecha del sistema");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (registroMod.getFecFin().compareTo(registroMod.getFecCarga()) > 0){
            msg = new FacesMessage("La fecha de fin no puede ser mayor que la fecha del sistema");
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
        
        int li_local;
        li_local=registroMod.getLocalCarniceria().getId();
        if (li_local==0)
        {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar un local","Balances");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        System.out.println("LLegó 2");
        
        Session session=null;
        Balance u=this.getRegistroMod();
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
        catch (Exception e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        
        finally {
            session.close();
        }
        
        System.out.println("LLegó 3");
        if (registroMod.getEstado()==FINALIZADO || registroMod.getEstado()==APROBADO)
            noEditable=true;
        
        //Grabo la auditoría de la transacción
//        try {
//            grabaAuditoria(getUsuarioConectado().getIdUsuario(),ResourceBundle.getBundle("general/Permisos").getString("ModificarBalance"),
//                    "Actualización de Balance Nro: " + registroMod.getId(),"Módulo de Balances",obtieneNombreEquipo());
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(BalanceController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
   
    //Obtiene categoria
    public Categoria obtieneCategoria(int codCategoria){
            FacesMessage msg;
            Categoria cat = null;
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                cat=(Categoria) session.get(Categoria.class,codCategoria);
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
            return cat;
    }
    
    //Obtiene balance
    public Balance obtieneBalance(int idBalance){
            FacesMessage msg;
            Balance balance = null;
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                balance=(Balance) session.get(Balance.class,idBalance);
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
            return balance;
    }
    
    
    
    //Selecciona producto y seteo la unidad de medida
    public void onSeleccionaProductoInvIni() {
        //Obtengo la instancia de producto
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Producto c =(Producto) session.get(Producto.class,registroModInvIni.getProducto().getId());
        session.getTransaction().commit();
        session.close();
        registroModInvIni.setUnidad(c.getUnidad());
    }
    
    //Selecciona producto y seteo la unidad de medida
    public void onSeleccionaProductoIngreso() {
        //Obtengo la instancia de producto
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Producto c =(Producto) session.get(Producto.class,registroModIngreso.getProducto().getId());
        session.getTransaction().commit();
        session.close();
        registroModIngreso.setUnidad(c.getUnidad());
    }
    
    //Selecciona producto y seteo la unidad de medida
    public void onSeleccionaProductoRemanente() {
        //Obtengo la instancia de producto
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Producto c =(Producto) session.get(Producto.class,registroModRemanente.getProducto().getId());
        session.getTransaction().commit();
        session.close();
        registroModRemanente.setUnidad(c.getUnidad());
        registroModRemanente.setCantidad(BigDecimal.ZERO);
        registroModRemanente.setPrecioUnitario(BigDecimal.ZERO);
    }
    
    //Calcula precio total del remanente del producto
    public void calculaPrecioTotalRemanente(BalanceRemanente remanente){
        System.out.println("Prod: " +  remanente.getProducto().getNombre()) ;
        double ld_precio_unitario=0,ld_cantidad,ld_precio_total;
        ld_cantidad=remanente.getCantidad().doubleValue();
        ld_precio_unitario=remanente.getPrecioUnitario().doubleValue();
        ld_precio_total=ld_cantidad*ld_precio_unitario;
        remanente.setPrecioTotal(new BigDecimal(ld_precio_total));
        actualizaValorRemanente();
    }
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Fila Edited", String.valueOf(event.getComponent()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
    
    //Obtiene fecha del último balance del local
    public Balance obtieneUltimoBalance(int idLocal) throws SQLException{
        Date lda_fecha = null;
        CallableStatement s=null;
        ResultSet r=null;
        int li_resultado=0;
        int li_idBalance=0;
        FacesMessage msg=null;
        
        //Conectamos a la base
        Conector conector = new Conector();  
        Connection conexion = conector.connect("estancia");
        
        try {      
             //Los parámetros que tienen blanco o nulo son porque no los actualiza
             s=conexion.prepareCall("{call sp_get_datos_ultimo_balance ( ? , ? , ? )}"); 
             s.setInt(1,idLocal); 
             s.registerOutParameter(2,java.sql.Types.INTEGER);
             s.registerOutParameter(3,java.sql.Types.DATE);
             r=s.executeQuery();

             //Obtengo el id y la fecha de fin del último balance
             li_idBalance=s.getInt(2);
             lda_fecha=s.getDate(3);             

        }catch (SQLException e){
            System.out.println("Error al obtener la fecha de último balance SQL: " + e.getMessage() );
        }catch (Exception e){
            System.out.println("Error al obtener la fecha de último balance: " + e.getMessage() );
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
        if (li_idBalance > 0)
        {
            //Calculo la fecha final del balance actual
            if (lda_fecha != null){
                registroMod.setFecIni(lda_fecha);
                //Sumo 7 días y pongo la fecha de fin
                int li_dias=7;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
                calendar.setTime(lda_fecha); 
                calendar.add(Calendar.DAY_OF_YEAR, li_dias);  
                calendar.set(Calendar.HOUR_OF_DAY,11);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                registroMod.setFecFin(calendar.getTime());
                
            }
            
            Balance balance = null;
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                balance=(Balance) session.get(Balance.class,li_idBalance);
                Hibernate.initialize(balance.getBalanceCobranzas());
                Hibernate.initialize(balance.getBalanceGastoExtras());
                Hibernate.initialize(balance.getBalanceGastoPersonals());
                Hibernate.initialize(balance.getBalanceGastoVarios());
                Hibernate.initialize(balance.getBalanceIngresos());
                Hibernate.initialize(balance.getBalanceInvInis());
                Hibernate.initialize(balance.getBalancePorCobrars());
                Hibernate.initialize(balance.getBalanceRemanentes());
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
            return balance;            
        }
        else
            return null;
        
    }
    
    //Obtiene total por tipo de producto para la lista de inventarios iniciales
    public double getSumaInvIniTipoProducto(char tipoProd) {
        return registroMod.getBalanceInvInis().stream().filter(comprobante -> tipoProd == comprobante.getProducto().getTipo()).mapToDouble(BalanceInvIni::getCantidadDouble).sum();
    }
    
    //Obtiene total de cantidades por ramo para la lista de remanentes
    public double getSumaCantidadRemanentesRubro(int idRubro) {
        return registroMod.getBalanceRemanentes().stream().filter(comprobante -> idRubro == comprobante.getProducto().getRubro().getId()).mapToDouble(BalanceRemanente::getCantidadDouble).sum();
    }
    
    //Obtiene total de precios totales por ramo para la lista de remanentes
    public double getSumaPrecioTotalRemanentesRubro(int idRubro) {
        return registroMod.getBalanceRemanentes().stream().filter(comprobante -> idRubro == comprobante.getProducto().getRubro().getId()).mapToDouble(BalanceRemanente::getPrecioTotalDouble).sum();
    }

    //Obtiene total de cantidades por ramo para la lista de ingresos
    public double getSumaCantidadIngresosRubro(int idRubro) {
        return registroMod.getBalanceIngresos().stream().filter(comprobante -> idRubro == comprobante.getProducto().getRubro().getId()).mapToDouble(BalanceIngreso::getCantidadDouble).sum();
    }
    
    public double obtienePrecio(int idLocal,int idProducto,Date fecha) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        double ld_precio=0;
        FacesMessage msg=null;
        
        //Conectamos a la base
        Conector conector = new Conector();  
        Connection conexion = conector.connect("estancia");
        
        try {      
             //Los parámetros que tienen blanco o nulo son porque no los actualiza
             s=conexion.prepareCall("{call sp_get_precio_producto_local ( ? , ? , ? , ? )}"); 
             s.setInt(1,idLocal); 
             s.setInt(2,idProducto); 
             s.setTimestamp(3,new java.sql.Timestamp(fecha.getTime()));
             s.registerOutParameter(4,java.sql.Types.DOUBLE);
             r=s.executeQuery();

             //Obtengo el id y la fecha de fin del último balance
             ld_precio=s.getDouble(4);             

        }catch (SQLException e){
            System.out.println("Error al obtener precio del producto" + e.getMessage() );
        }catch (Exception e){
            System.out.println("Error al obtener precio del producto" + e.getMessage() );
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        return ld_precio;
    }
    
    
}
