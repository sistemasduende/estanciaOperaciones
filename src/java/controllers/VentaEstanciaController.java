/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.Cliente;
import entidades.Empresa;
import entidades.LocalCarniceria;
import entidades.RemitoVenta;
import entidades.RemitoVentaDet;
import entidades.TropaViaje;
import entidades.VentaEstancia;
import entidades.VentaEstanciaDet;
import entidades.VentaEstanciaRemito;
import general.Asiento;
import general.AsientoCuentaAdicional;
import general.AsientoRealizado;
import general.BeanBase;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
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
 * @author rafael
 */
@ManagedBean(name="ventaestanciacontroller")
@SessionScoped

public class VentaEstanciaController extends BeanBase{
    private TimeZone zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private java.util.Date fec_desde=null;
    private java.util.Date fec_hasta=null;
    private VentaEstancia registroSel;
    private VentaEstancia registroMod;
    private List <VentaEstancia> lista=null;
    private List <VentaEstancia> listaFiltrada=null;
    private List <VentaEstanciaDet> ventaEstanciaDetalle = null;
    private String modo="";
    private List <RemitoVenta> listaRemitos=new ArrayList<>();
    private List <RemitoVenta> listaRemitosSeleccionados=new ArrayList<>();
    private Cliente clienteSel;
    private String nombreCliente;
    private List <Cliente> listaClientes=new ArrayList<Cliente>();
    private int idLocalSel;
    private List<AsientoRealizado> listaAsientos= new ArrayList<AsientoRealizado>();
    
    public VentaEstanciaController() {
        ventaEstanciaDetalle = new ArrayList<VentaEstanciaDet>();
    }

    public VentaEstancia getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(VentaEstancia registroSel) {
        this.registroSel = registroSel;
    }

    public VentaEstancia getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(VentaEstancia registroMod) {
        this.registroMod = registroMod;
    }

    public List<VentaEstancia> getLista() {
        return lista;
    }

    public void setLista(List<VentaEstancia> lista) {
        this.lista = lista;
    }

    public TimeZone getZona() {
        return zona;
    }

    public void setZona(TimeZone zona) {
        this.zona = zona;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public List<VentaEstancia> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<VentaEstancia> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
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

    public List<RemitoVenta> getListaRemitos() {
        return listaRemitos;
    }

    public void setListaRemitos(List<RemitoVenta> listaRemitos) {
        this.listaRemitos = listaRemitos;
    }

    public List<RemitoVenta> getListaRemitosSeleccionados() {
        return listaRemitosSeleccionados;
    }

    public void setListaRemitosSeleccionados(List<RemitoVenta> listaRemitosSeleccionados) {
        this.listaRemitosSeleccionados = listaRemitosSeleccionados;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Cliente getClienteSel() {
        return clienteSel;
    }

    public void setClienteSel(Cliente clienteSel) {
        this.clienteSel = clienteSel;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public int getIdLocalSel() {
        return idLocalSel;
    }

    public void setIdLocalSel(int idLocalSel) {
        this.idLocalSel = idLocalSel;
    }

    public List<VentaEstanciaDet> getVentaEstanciaDetalle() {
        return ventaEstanciaDetalle;
    }

    public void setVentaEstanciaDetalle(List<VentaEstanciaDet> ventaEstanciaDetalle) {
        this.ventaEstanciaDetalle = ventaEstanciaDetalle;
    } 
    
    //Obtiene lista de todos las entregas
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
        
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from VentaEstancia a where fecMov >= :fec_desde and fecMov <= :fec_hasta order by fecMov,id");
            q.setParameter("fec_desde",lda_fecha_desde);
            q.setParameter("fec_hasta",lda_fecha_hasta);
            this.lista=(List<VentaEstancia>) q.list();
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
    
    //Nueva venta
    public String nuevo(){
        this.registroMod=new VentaEstancia();  
        this.modo="N";
        this.registroMod.setIdUsuario(getUsuarioConectado().getIdUsuario());
        //Obtengo la fecha de hoy para poner como fecha de carga y fecha de movimiento
        java.util.Calendar fecha=java.util.Calendar.getInstance();
        java.sql.Date lda_fecha=new java.sql.Date(fecha.getTimeInMillis());
        this.registroMod.setFecCarga(lda_fecha);
        this.registroMod.setFecMov(lda_fecha);
        this.registroMod.setTipoCompro("V");  //Venta
        this.registroMod.setSubtotalSinIva(BigDecimal.ZERO);
        this.registroMod.setPorcIva(BigDecimal.ZERO);
        this.registroMod.setValorIva(BigDecimal.ZERO);
        this.registroMod.setValorTotal(BigDecimal.ZERO);
        this.listaRemitos.clear();
        this.listaRemitosSeleccionados.clear();
        idLocalSel=0;
        return "/vistas/ventas/Create";
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
                this.registroMod =(VentaEstancia) session.get(VentaEstancia.class,registroSel.getId());
                //Hibernate.initialize(this.registroMod.getVentaEstanciaDets());
                //linea agregada por diego
                //this.registroMod.getVentaEstanciaDets().stream().forEach(x->x.getUnidad().getNombre());
                //Hibernate.initialize(this.registroMod.getVentaEstanciaRemitos());
                //linea agregada por diego
                //this.registroMod.getVentaEstanciaRemitos().stream().forEach(x->x.getRemitoVenta().getIdUsuario());
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
            idLocalSel=registroMod.getLocalCarniceria().getId();
            this.modo="M";
            return "/vistas/ventas/Edit";
        }
        else
            return null;
    }
 
    
    //Obtiene los detalles del registro que se hace clic
    public String onClick(VentaEstancia p) throws Exception{
        FacesMessage msg;
        registroSel=p;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(VentaEstancia) session.get(VentaEstancia.class,registroSel.getId());
                //Hibernate.initialize(this.registroMod.getVentaEstanciaDets());
                //linea agregada por diego
                //this.registroMod.getVentaEstanciaDets().stream().forEach(x->x.getUnidad().getNombre());
                //Hibernate.initialize(this.registroMod.getVentaEstanciaRemitos());
                //linea agregada por diego
                //this.registroMod.getVentaEstanciaRemitos().stream().forEach(x->x.getRemitoVenta().getIdUsuario());
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
            idLocalSel=registroMod.getLocalCarniceria().getId();
            this.modo="M";
            return "/vistas/ventas/Edit";
        }
        else
            return null;
    }
    
    //Busco remitos del cliente que no han sido facturados
    public void buscaRemitos(){
        listaRemitos.clear();
        listaRemitosSeleccionados.clear();
        
        Session session = null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();        
            Query q=session.createQuery("from RemitoVenta a where a.localCarniceria.id = :idLocalSel and estado='0'");
            q.setParameter("idLocalSel",idLocalSel);
            listaRemitos=(List<RemitoVenta>) q.list();
            for (RemitoVenta remito: listaRemitos){
                Hibernate.initialize(remito.getRemitoVentaDets());
                //linea agregada por diego
                //remito.getRemitoVentaDets().stream().forEach(x->x.getUnidad().getNombre());
            }
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
    
    //Agrega los remitos seleccionados a la venta
    public void agregaRemitos(){
        ArrayList<VentaEstanciaDet> listaDetalle= new ArrayList<VentaEstanciaDet>();
        
        for (RemitoVenta remito: listaRemitosSeleccionados) {
                VentaEstanciaRemito e= new VentaEstanciaRemito();
                e.setRemitoVenta(remito);
                e.setVentaEstancia(registroMod);
                boolean lb_remito_existente=false;
                for (VentaEstanciaRemito k: getRegistroMod().getVentaEstanciaRemitos()) {
                    if (k.getRemitoVenta().getId()==remito.getId()){
                        lb_remito_existente=true;
                        break;
                    }
                }
                
                boolean lb_producto_existente=false;                
                if (!lb_remito_existente){
                   //Remito no ha sido agregado 
                    this.getRegistroMod().getVentaEstanciaRemitos().add(e);
                    //Agrego los productos de cada remito a la venta

                    double ld_precio_final=0,ld_valor_final=0,ld_cantidad=0;
                    Set<RemitoVentaDet> listaProductos=remito.getRemitoVentaDets();
                    for (RemitoVentaDet d: listaProductos) {
                        lb_producto_existente=false;
                        int li_idProducto=0;
                        int li_idTropa=0;
                        li_idProducto=d.getProducto().getId().intValue();
                        li_idTropa=d.getInventarioTropa().getTropa().getId().intValue();
                        System.out.println("Producto a agregar: " + li_idProducto);
                        VentaEstanciaDet detVenta= null;
                        //Chequeo si existe ya el producto en el detalle de venta
                        listaDetalle=new ArrayList<VentaEstanciaDet>(getRegistroMod().getVentaEstanciaDets());
                        for (int i=0;i < listaDetalle.size(); i++) {
                            VentaEstanciaDet g= (VentaEstanciaDet) listaDetalle.get(i);
                            System.out.println("Detalle: " + g.getProducto().getId().intValue() + " " +  g.getProducto().getNombre());
                            if (g.getProducto().getId().intValue() == li_idProducto && g.getTropa().getId().intValue()== li_idTropa){
                                //Ya existe entonces sumo la cantidad
                                ld_cantidad=g.getCantidad().doubleValue() + d.getCantidad().doubleValue();
                                g.setCantidad(new BigDecimal(ld_cantidad));
                                //Precio final unitario con iva incluído
//                                ld_precio_final=g.getProducto().getPrecio().doubleValue();
//                                g.setPrecioFinal(g.getProducto().getPrecio());
//                                g.setPrecio(g.getProducto().getPrecio());
//                                //Precio final total de la cantidad de productos
//                                ld_valor_final=ld_cantidad*g.getProducto().getPrecio().doubleValue();
                                g.setValorFinal(new BigDecimal(ld_valor_final));
                                detVenta=g;
                                lb_producto_existente=true;
                                listaDetalle.set(i,g);
                                break;
                            }
                        }
                        
                        if (!lb_producto_existente){
                            //Producto nuevo
                            detVenta= new VentaEstanciaDet();
                            detVenta.setVentaEstancia(registroMod);
                            detVenta.setUnidad(d.getUnidad());
                            detVenta.setTropa(d.getInventarioTropa().getTropa());
                            detVenta.setProducto(d.getProducto());
                            detVenta.setCantidad(d.getCantidad());
                            detVenta.setPorcDesc(BigDecimal.ZERO);
                            detVenta.setValorDesc(BigDecimal.ZERO);

                            //Precio final unitario con iva incluído
                            //ld_precio_final=d.getProducto().getPrecio().doubleValue();
                            detVenta.setPrecioFinal(new BigDecimal(ld_precio_final));
                            detVenta.setPrecio(new BigDecimal(ld_precio_final));
                            
                            //Precio final total de la cantidad de productos
                            ld_valor_final=d.getCantidad().doubleValue()*ld_precio_final;
                            detVenta.setValorFinal(new BigDecimal(ld_valor_final));
                            listaDetalle.add(detVenta);
                        }
                        this.getRegistroMod().setVentaEstanciaDets(new HashSet<>(listaDetalle));
                        this.ventaEstanciaDetalle = listaDetalle.stream().collect(Collectors.toCollection(ArrayList::new));
                    }
                } 
        }    
        
        //Actualizo totales
        actualizaTotales();
    }     
    
    public void actualizaTotales(){
        double ld_total_venta=0,ld_valor=0;
        for (VentaEstanciaDet g: getRegistroMod().getVentaEstanciaDets()) {
            if (g.getPrecioFinal()!=null)
                ld_valor=g.getValorFinal().doubleValue();
            ld_total_venta=ld_total_venta + ld_valor;
        }
        getRegistroMod().setSubtotalSinIva(new BigDecimal(ld_total_venta));
        getRegistroMod().setValorIva(BigDecimal.ZERO);
        getRegistroMod().setValorTotal(new BigDecimal(ld_total_venta));
    }
    
    //Elimina remito
    public void eliminaRemito(VentaEstanciaRemito i) {
       this.getRegistroMod().getVentaEstanciaRemitos().remove(i);
        //Actualizo valores de los efectivos
        //actualizaValorEfectivos();
        //Calculo los valores de la liquidación
    }    
    
    public void buscaClientes(){
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Cliente a where a.nombre like :nombreCliente");
            q.setParameter("nombreCliente","%" + nombreCliente + "%");
            listaClientes=(List<Cliente>) q.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_INFO,"",e.getMessage()));
            return;
        }
        finally {
            session.close();
        }
    }
    
    public void seleccionaCliente(){
        this.getRegistroMod().setCliente(clienteSel);
        //calculaCostos();
    }
    
    //Graba la venta
    public String graba(){
        String ls_tipo_compro;
        String ls_pdv,ls_num_fact;
        FacesMessage msg;
        this.registroMod.setVentaEstanciaDets(ventaEstanciaDetalle.stream().collect(Collectors.toCollection(HashSet<VentaEstanciaDet>::new)));
        listaAsientos.clear();
                
        if (idLocalSel==0){
            msg = new FacesMessage("Debe seleccionar un local");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        
        ls_tipo_compro=registroMod.getTipoCompro();
        if (ls_tipo_compro.equals("F")){
            //Valido punto de venta
            ls_pdv=registroMod.getPuntoVenta();
            if(ls_pdv==null || ls_pdv.isEmpty()){
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe ingresar el punto de venta","Ventas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
            
            //Valido número de factura
            ls_num_fact=registroMod.getNumFact();
            if(ls_num_fact==null || ls_num_fact.isEmpty()){
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe ingresar el número de factura","Ventas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
        }
        
        if (registroMod.getVentaEstanciaDets().size()==0){
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe ingresar los productos de la venta","Ventas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        
        for (VentaEstanciaDet d : registroMod.getVentaEstanciaDets()){
            if (d.getPrecioFinal()==null){
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe ingresar el precio","Ventas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
            
            if (d.getPrecioFinal().doubleValue() <= 0){
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe ingresar el precio","Ventas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
            
        }
        
        
        LocalCarniceria local=obtieneLocal(idLocalSel);
        registroMod.setLocalCarniceria(local);
        
        Session session=HibernateUtil.getSessionFactory().openSession();
        
        
        try{
            Transaction tx = session.beginTransaction();
            //Cambio el estado de los remitos relacionados
            for (VentaEstanciaRemito rv : registroMod.getVentaEstanciaRemitos()){   
                RemitoVenta remito= rv.getRemitoVenta();
                remito.setEstado('1');
                session.saveOrUpdate(remito);
            }
            
            //Grabo la venta
            session.saveOrUpdate(registroMod);
            
            boolean lb_resul=false;
            lb_resul=contabilizaVenta();
            if (!lb_resul){
                session.getTransaction().rollback();
                for (AsientoRealizado asiento : listaAsientos){
                    Asiento ast= new Asiento();
                    ast.setId(asiento.getIdAsiento());
                    AsientoRealizado resul=eliminaAsiento(ast);  
                }
                return null;
            }
            else{
                if (!contabilizaCostosMV()){
                    session.getTransaction().rollback();
                    for (AsientoRealizado asiento : listaAsientos){
                        Asiento ast= new Asiento();
                        ast.setId(asiento.getIdAsiento());
                        AsientoRealizado resul=eliminaAsiento(ast);  
                    }
                }
            }
            if (!tx.wasCommitted()){
                tx.commit();
            }
            ventaEstanciaDetalle.clear();
            msg = new FacesMessage("Actualización exitosa!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "Ventas";
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_INFO,"",e.getMessage()));
            return null;
        }
        finally {
            session.close();
        }
        
        
        
        
    }
    
    public void calculaValorIva(){
        double ld_subTotal = 0,ld_porcIva = 0,ld_valorIva,ld_total=0;
        String ls_tipo_compro;
        ls_tipo_compro=registroMod.getTipoCompro();
        
        if (registroMod.getValorTotal() != null)
               ld_total=registroMod.getValorTotal().doubleValue();
        
        if (ls_tipo_compro.equals("F")){
            //Tipo de Comprobante: Factura
            //Obtengo porc. de IVA ventas a cobrar
            ld_porcIva=obtienePorcIvaVentas();
            System.out.println("IVA(%): " + ld_porcIva);
            if (ld_porcIva > 0){
                //Calculo subtotal antes de IVA
                ld_subTotal=ld_total /( 1 + ld_porcIva/100.00);
                System.out.println("SubTotal: " + ld_subTotal);
                ld_subTotal=Math.round(ld_subTotal*100d)/ 100d;
            }
            else
                ld_subTotal=ld_total;
            
            ld_valorIva=ld_total - ld_subTotal;
            System.out.println("IVA: " +  ld_valorIva);
        }
        else{
            //Tipo de Comprobante: Venta
            ld_porcIva=0;
            ld_valorIva=0;
            ld_subTotal=ld_total;
            registroMod.setPuntoVenta(null);
            registroMod.setNumFact(null);
            
        }
        
        registroMod.setSubtotalSinIva(new BigDecimal(ld_subTotal));
        registroMod.setPorcIva(new BigDecimal(ld_porcIva));
        registroMod.setValorIva(new BigDecimal(ld_valorIva));
        
    }
    
    //Obtien porc. iva ventas
    public double obtienePorcIvaVentas(){
        double ld_valor=0;
        Empresa e=cargaEmpresa();
        if (e.getPorcIvaVentas()!= null)
          ld_valor=e.getPorcIvaVentas().doubleValue();
        return ld_valor;
    }
    
    //Obtiene local
    public LocalCarniceria obtieneLocal(int codLocal){
            LocalCarniceria local = null;
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                local=(LocalCarniceria) session.get(LocalCarniceria.class,codLocal);
                registroMod.setLocalCarniceria(local);
                session.getTransaction().commit();
            }
            catch (HibernateException e){
                session.getTransaction().rollback();
                return null;
            }
            finally {
                session.close();
            }
            return local;
    }
    
    //Selecciono local
    public void cambiaLocal() {
        obtieneLocal(idLocalSel);
        this.getListaRemitos().clear();
        registroMod.getVentaEstanciaRemitos().clear();
        registroMod.getVentaEstanciaDets().clear();
    }
    
    //Calcula precio total del remanente del producto
    public void calculaPrecioTotalProducto(VentaEstanciaDet det){
        System.out.println("Prod: " +  det.getProducto().getNombre()) ;
        double ld_precio_unitario=0,ld_cantidad,ld_precio_total;
        ld_cantidad=det.getCantidad().doubleValue();
        ld_precio_unitario=det.getPrecioFinal().doubleValue();
        ld_precio_total=ld_cantidad*ld_precio_unitario;
        det.setValorFinal(new BigDecimal(ld_precio_total));
        actualizaTotalVenta();
        calculaValorIva();
    }
    
    //Actualiza total de remanentes
    public void actualizaTotalVenta(){
        this.registroMod.setVentaEstanciaDets(ventaEstanciaDetalle.stream().collect(Collectors.toCollection(HashSet<VentaEstanciaDet>::new)));
        double ld_total=0;
        Iterator i= this.getRegistroMod().getVentaEstanciaDets().iterator();
        while (i.hasNext()){
                VentaEstanciaDet g= (VentaEstanciaDet) i.next();
                ld_total=ld_total + g.getValorFinal().doubleValue();
        }
        registroMod.setValorTotal(new BigDecimal(ld_total));
    } 
    
    //Realizo asientos de venta
    public boolean contabilizaVenta(){
        //Obtengo la empresa para tener las cuentas contables
        Empresa empresa=cargaEmpresa();
        
        for (VentaEstanciaDet det : registroMod.getVentaEstanciaDets()){
            Asiento asiento= new Asiento();
            asiento.setIdEmpresa(1);        
            asiento.setNumPlantilla(0);  //No tiene plantilla relacionada
            asiento.setDescripcion("Venta Local " + registroMod.getLocalCarniceria().getNombreComercial() +
                    " Tropa " + det.getTropa().getNumeroTropa() + " Kilos: " + det.getCantidad() + " Precio: " + det.getPrecioFinal());
        
            //Fecha del movimiento
            java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
            fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
            fecha_mov.setTime(new Date());
        
            //Convierto la fecha a String para no tener problemas con JSON
            TimeZone gmtZone = TimeZone.getTimeZone("America/Buenos_Aires");
            DateFormat destDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            destDateFormat.setTimeZone(gmtZone);
            String ls_fecha;
            ls_fecha=destDateFormat.format(fecha_mov.getTime());
            System.out.println("Fecha string: " + ls_fecha);
            asiento.setFecMov(ls_fecha);
        
            asiento.setIdUsuario(getUsuarioConectado().getIdUsuario());
        
            //Todas las cuentas contables del asiento se pasan desde aquí
            AsientoCuentaAdicional cuentaAdic=null;
        
            //Agrego cuenta contable de venta del local
            cuentaAdic= new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(registroMod.getLocalCarniceria().getCcVentaLocal());
            cuentaAdic.setDc("D");
            cuentaAdic.setValor(Math.round(det.getValorFinal().doubleValue()*100.0)/100.0);
            asiento.getListaCuentasAdic().add(cuentaAdic);
        
            //Agrego cuenta contable de Ingresos Brutos
            cuentaAdic= new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(empresa.getCcVentaCarne());
            cuentaAdic.setDc("C");
            cuentaAdic.setValor( Math.round(det.getValorFinal().doubleValue()*100.0)/100.0);
            asiento.getListaCuentasAdic().add(cuentaAdic);
                
            AsientoRealizado asientoRealizado=realizaAsiento(asiento);
            if (asientoRealizado.getIdAsiento() == 0){
                FacesMessage msg;
                msg = new FacesMessage("Error al contabilizar asientos de venta: " + asientoRealizado.getObservaciones());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
            else 
                listaAsientos.add(asientoRealizado);
        }
        return true;

    }    
    
    //Realizo asientos de costo de mercadería vendida
    public boolean contabilizaCostosMV(){
        //Obtengo la empresa para tener las cuentas contables
        Empresa empresa=cargaEmpresa();

        for (VentaEstanciaDet det : registroMod.getVentaEstanciaDets()){
            
            Asiento asiento= new Asiento();
            asiento.setIdEmpresa(1);        
            asiento.setNumPlantilla(0);  //No tiene plantilla relacionada
            asiento.setDescripcion("Costo mercaderia vendida " + registroMod.getLocalCarniceria().getNombreComercial() +
                    " Tropa " + det.getTropa().getNumeroTropa() + " Kilos: " + det.getCantidad() + " Costo Unitario: " + det.getTropa().getCostoUnitario().doubleValue());
        
            //Fecha del movimiento
            java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
            fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
            fecha_mov.setTime(new Date());
        
            //Convierto la fecha a String para no tener problemas con JSON
            TimeZone gmtZone = TimeZone.getTimeZone("America/Buenos_Aires");
            DateFormat destDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            destDateFormat.setTimeZone(gmtZone);
            String ls_fecha;
            ls_fecha=destDateFormat.format(fecha_mov.getTime());
            System.out.println("Fecha string: " + ls_fecha);
            asiento.setFecMov(ls_fecha);
        
            asiento.setIdUsuario(getUsuarioConectado().getIdUsuario());
        
            //Todas las cuentas contables del asiento se pasan desde aquí
            AsientoCuentaAdicional cuentaAdic=null;
            
            double ld_costo_mv=0;
            
            ld_costo_mv=Math.round((det.getCantidad().doubleValue()*det.getTropa().getCostoUnitario().doubleValue())*100.0)/100.0;
        
            //Agrego cuenta contable de venta del local
            cuentaAdic= new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(empresa.getCcCostoMercVendida());
            cuentaAdic.setDc("D");
            cuentaAdic.setValor(ld_costo_mv);
            asiento.getListaCuentasAdic().add(cuentaAdic);
        
            //Agrego cuenta contable de Ingresos Brutos
            cuentaAdic= new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(empresa.getCcTropasCamara());
            cuentaAdic.setDc("C");
            cuentaAdic.setValor(ld_costo_mv);
            asiento.getListaCuentasAdic().add(cuentaAdic);
                
            AsientoRealizado asientoRealizado=realizaAsiento(asiento);
            if (asientoRealizado.getIdAsiento() == 0){
                FacesMessage msg;
                msg = new FacesMessage("Error al contabilizar asiento de costo: " + asientoRealizado.getObservaciones());
                FacesContext.getCurrentInstance().addMessage(null, msg);

                return false;
            }
            else 
                listaAsientos.add(asientoRealizado);
        }
        
        return true;

    }   
    public void cancelar(){
        ventaEstanciaDetalle.clear();
        //this.getRegistroMod().getVentaEstanciaRemitos().clear();
        nuevo();
    }
}
