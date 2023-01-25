/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import antlr.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.Conector;
import entidades.Categoria;
import entidades.Comisionista;
import entidades.Deposito;
import entidades.Empresa;
import entidades.Entrega;
import entidades.InventarioTropa;
import entidades.LiquidacionEfectivo;
import entidades.Tropa;
import entidades.TropaDet;
import entidades.TropaDetGarron;
import entidades.TropaPagoCiva;
import entidades.TropaViaje;
import entidades.Viaje;
import general.Asiento;
import general.AsientoCuentaAdicional;
import general.AsientoRealizado;
import general.BeanBase;
import general.ParametroAsiento;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import jxl.Sheet;
import jxl.Workbook;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import seguridad.LogIn;

/**
 *
 * @author rafael
 */
@ManagedBean(name = "tropacontroller")
@SessionScoped

public class TropaController extends BeanBase implements Serializable {

    Tropa registroSel;
    Tropa registroMod;
    TropaDet registroModDetalle;
    private String origen="";
    private String numTropa = "";
    private String modo = "";
    private String tipoBusqStock = "T";
    String mensaje = "";
    private List<Tropa> lista = null;
    private TimeZone Zona = TimeZone.getTimeZone("America/Buenos_Aires");
    private java.util.Date fec_desde = null;
    private java.util.Date fec_hasta = null;
    private List<Viaje> listaViajes = new ArrayList<>();
    private List<Viaje> listaViajesSeleccionados = new ArrayList<>();
    private List<TropaDetGarron> listaGarrones = new ArrayList<TropaDetGarron>();
    private double totalKilosGarrones;
    private int codDepositoSel;
    private Deposito depositoSel;
    private List<TropaStock> listaTropasStock = new ArrayList<>();
    private List<InventarioTropa> listaGarronesStock = new ArrayList<InventarioTropa>();
    private static final char EN_TRAMITE = '0';
    private static final char PROCESADA = '1';
    private static final char EN_STOCK = '2';
    private char estadoActual;
    private String nombreComisionista;
    private List<Comisionista> listaComisionistas = new ArrayList<Comisionista>();
    private Comisionista comisionistaSel;

    //Stock
    TropaStock registroStockSel;

    public TropaController() {

    }

    public String getNumTropa() {
        return numTropa;
    }

    public void setNumTropa(String numTropa) {
        this.numTropa = numTropa;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
    
    public Tropa getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Tropa registroSel) {
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

    public List<Tropa> getLista() {
        return lista;
    }

    public void setLista(List<Tropa> lista) {
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

    public Tropa getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Tropa registroMod) {
        this.registroMod = registroMod;
    }

    public TropaDet getRegistroModDetalle() {
        return registroModDetalle;
    }

    public void setRegistroModDetalle(TropaDet registroModDetalle) {
        this.registroModDetalle = registroModDetalle;
    }

    public List<Viaje> getListaViajes() {
        return listaViajes;
    }

    public void setListaViajes(List<Viaje> listaViajes) {
        this.listaViajes = listaViajes;
    }

    public List<Viaje> getListaViajesSeleccionados() {
        return listaViajesSeleccionados;
    }

    public void setListaViajesSeleccionados(List<Viaje> listaViajesSeleccionados) {
        this.listaViajesSeleccionados = listaViajesSeleccionados;
    }

    public List<TropaDetGarron> getListaGarrones() {
        return listaGarrones;
    }

    public void setListaGarrones(List<TropaDetGarron> listaGarrones) {
        this.listaGarrones = listaGarrones;
    }

    public double getTotalKilosGarrones() {
        return totalKilosGarrones;
    }

    public void setTotalKilosGarrones(double totalKilosGarrones) {
        this.totalKilosGarrones = totalKilosGarrones;
    }

    public int getCodDepositoSel() {
        return codDepositoSel;
    }

    public void setCodDepositoSel(int codDepositoSel) {
        this.codDepositoSel = codDepositoSel;
    }

    public Deposito getDepositoSel() {
        return depositoSel;
    }

    public void setDepositoSel(Deposito depositoSel) {
        this.depositoSel = depositoSel;
    }

    public List<TropaStock> getListaTropasStock() {
        return listaTropasStock;
    }

    public void setListaTropasStock(List<TropaStock> listaTropasStock) {
        this.listaTropasStock = listaTropasStock;
    }

    public String getTipoBusqStock() {
        return tipoBusqStock;
    }

    public void setTipoBusqStock(String tipoBusqStock) {
        this.tipoBusqStock = tipoBusqStock;
    }

    public TropaStock getRegistroStockSel() {
        return registroStockSel;
    }

    public void setRegistroStockSel(TropaStock registroStockSel) {
        this.registroStockSel = registroStockSel;
    }

    public List<InventarioTropa> getListaGarronesStock() {
        return listaGarronesStock;
    }

    public void setListaGarronesStock(List<InventarioTropa> listaGarronesStock) {
        this.listaGarronesStock = listaGarronesStock;
    }

    public char getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(char estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getNombreComisionista() {
        return nombreComisionista;
    }

    public void setNombreComisionista(String nombreComisionista) {
        this.nombreComisionista = nombreComisionista;
    }

    public List<Comisionista> getListaComisionistas() {
        return listaComisionistas;
    }

    public void setListaComisionistas(List<Comisionista> listaComisionistas) {
        this.listaComisionistas = listaComisionistas;
    }

    public Comisionista getComisionistaSel() {
        return comisionistaSel;
    }

    public void setComisionistaSel(Comisionista comisionistaSel) {
        this.comisionistaSel = comisionistaSel;
    }

    //Obtiene lista de todos las tropas
    public void buscaListaDatos() {
        FacesMessage msg;
        if (fec_desde == null) {
            msg = new FacesMessage("Debe ingresar la fecha inicial de la búsqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (fec_hasta == null) {
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
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery("from Tropa a where fecIng >= :fec_desde and fecIng <= :fec_hasta order by a.fecIng desc");
            q.setParameter("fec_desde", lda_fecha_desde);
            q.setParameter("fec_hasta", lda_fecha_hasta);
            this.lista = (List<Tropa>) q.list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } finally {
            session.close();
        }
    }

    //Obtiene lista de todos las tropas con inventario disponible
    public void buscaStockTropas() throws SQLException {
        FacesMessage msg;
        CallableStatement s = null;
        ResultSet r = null;
        int li_idTropa;
        Date lda_fec_ing;
        String ls_num_tropa, ls_tipo, ls_productor, ls_deposito;
        double ld_kilos_disp, ld_kilos_ent, ld_total_kilos;
        ArrayList resultados = new ArrayList();
        Connection conexion = null;

        try {
            //Conectamos a la base
            Conector conector = new Conector();
            conexion = conector.connect("estancia");

            s = conexion.prepareCall("{call sp_get_inventario_tropas ( ? )}");
            s.setString(1, tipoBusqStock);
            r = s.executeQuery();
            while (r.next()) {
                li_idTropa = r.getInt("id");
                ls_num_tropa = r.getString("numeroTropa");
                ls_tipo = r.getString("tipo");
                lda_fec_ing = r.getDate("fec_ing");
                ls_productor = r.getString("productor");
                ls_deposito = r.getString("deposito");
                ld_kilos_disp = r.getDouble("disponible");
                ld_kilos_ent = r.getDouble("entregado");
                ld_total_kilos = ld_kilos_disp + ld_kilos_ent;

                TropaStock registro = new TropaStock(li_idTropa, ls_num_tropa, ls_tipo, lda_fec_ing, ls_productor,
                        ls_deposito, ld_kilos_disp, ld_kilos_ent, ld_total_kilos);
                resultados.add(registro);
            }
            s.close();
            this.setListaTropasStock(resultados);
        } catch (SQLException e) {
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } finally {
            if (conexion != null) {
                conexion.close();
            }
            if (r != null) {
                r.close();
            }
        }

    }

    //Nuevo registro
    public String nuevo() {
        this.registroMod = new Tropa();
        this.modo = "N";
        this.registroMod.setIdUsuario(getUsuarioConectado().getIdUsuario());
        //Obtengo la fecha de hoy para poner como fecha de carga y fecha de movimiento
        java.util.Calendar fecha = java.util.Calendar.getInstance();
        java.sql.Date lda_fecha = new java.sql.Date(fecha.getTimeInMillis());
        this.registroMod.setFecCarga(lda_fecha);
        this.registroMod.setFecIng(lda_fecha);
        this.registroMod.setTipo('F');  //Faena por default
        this.registroMod.setProcesada('0'); //En trámite
        this.registroMod.setValorFlete(BigDecimal.ZERO);
        this.registroMod.setImporteComision(BigDecimal.ZERO);
        this.registroMod.setImporteCostoTotal(BigDecimal.ZERO);
        this.registroMod.setImporteCostoTotalTropa(BigDecimal.ZERO);
        listaGarrones.clear();
        codDepositoSel = 0;
        depositoSel = new Deposito();
        depositoSel.setPrecioUnidad(BigDecimal.ZERO);
        this.registroMod.setDeposito(depositoSel);
        estadoActual = EN_TRAMITE; //Tropa nueva por default EN TRAMITE
        return "/vistas/tropas/Create";

    }
    public String cancelar(){
        this.registroSel = null;
        return this.origen;
    }
    //Obtiene los detalles del registro seleccionado
    public String edita(String origen) {
        this.origen = origen;
        FacesMessage msg;
        if (registroSel != null) {
            Session session = null;
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod = (Tropa) session.get(Tropa.class, registroSel.getId());
                Hibernate.initialize(this.registroMod.getTropaDets());
                Hibernate.initialize(this.registroMod.getTropaDetGarrons());
                Hibernate.initialize(this.registroMod.getTropaViajes());
                Hibernate.initialize(this.registroMod.getTropaPagoCivas());
                session.getTransaction().commit();
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            } finally {
                session.close();
            }

            this.modo = "M";
            //Guardo el deposito seleccionado
            codDepositoSel = registroMod.getDeposito().getId();
            obtieneDeposito(codDepositoSel);

            //Cargo lista de detalles de garrón
            listaGarrones.clear();
            totalKilosGarrones = 0;
            Iterator i = this.getRegistroMod().getTropaDetGarrons().iterator();
            while (i.hasNext()) {
                TropaDetGarron g = (TropaDetGarron) i.next();
                //Calculo el total de kilos de los garrones
                double ld_kilos_garron = g.getKilos().doubleValue();
                ld_kilos_garron = Math.round(ld_kilos_garron * 100.0) / 100.0;
                totalKilosGarrones = totalKilosGarrones + ld_kilos_garron;
                listaGarrones.add(g);
            }
            totalKilosGarrones = Math.round(totalKilosGarrones * 100.0) / 100.0;
            estadoActual = registroMod.getProcesada();  //Guardo el estado actual
            return "/vistas/tropas/Edit";
        } else {
            return null;
        }
    }

    public String setearTropa() {
        if (numTropa == null) {
                return null;
        }
        FacesMessage msg;
        if (numTropa.equals("")) {
                msg = new FacesMessage("Debe ingresar un número de tropa valido!");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
        }
        registroSel = obtenerTropaPorNumero();
        if (registroSel == null) {
                msg = new FacesMessage("No existe ese número de tropa!");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
        }
        return edita("/vistas/tropas/TropasPorNumero");
    }
    public Tropa obtenerTropaPorNumero() {
        FacesMessage msg;
        Session session = null;
        Tropa tropa = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String sql = "From Tropa T where T.numeroTropa = '" + numTropa + "'";
            tropa = (Tropa) session.createQuery(sql).uniqueResult();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } finally {
            session.close();
        }
        return tropa;
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClick(Tropa p) throws Exception {
        this.origen = "/vistas/tropas/Tropas";
        FacesMessage msg;
        registroSel = p;
        if (registroSel != null) {
            Session session = null;
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod = (Tropa) session.get(Tropa.class, registroSel.getId());
                Hibernate.initialize(this.registroMod.getTropaDets());
                Hibernate.initialize(this.registroMod.getTropaDetGarrons());
                Hibernate.initialize(this.registroMod.getTropaViajes());
                Hibernate.initialize(this.registroMod.getTropaPagoCivas());
                session.getTransaction().commit();
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            } finally {
                session.close();
            }
            this.modo = "M";
            //Guardo el deposito seleccionado
            codDepositoSel = registroMod.getDeposito().getId();
            obtieneDeposito(codDepositoSel);

            //Cargo lista de detalles de garrón
            listaGarrones.clear();
            totalKilosGarrones = 0;
            Iterator i = this.getRegistroMod().getTropaDetGarrons().iterator();
            while (i.hasNext()) {
                TropaDetGarron g = (TropaDetGarron) i.next();
                //Calculo el total de kilos de los garrones
                double ld_kilos_garron = g.getKilos().doubleValue();
                ld_kilos_garron = Math.round(ld_kilos_garron * 100.0) / 100.0;
                totalKilosGarrones = totalKilosGarrones + ld_kilos_garron;
                listaGarrones.add(g);
            }
            totalKilosGarrones = Math.round(totalKilosGarrones * 100.0) / 100.0;
            estadoActual = registroMod.getProcesada();  //Guardo el estado actual
            return "/vistas/tropas/Edit";
        } else {
            return null;
        }
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClickStock(TropaStock p) throws Exception {
        FacesMessage msg;
        registroStockSel = p;
        if (registroStockSel != null) {
            Session session = null;
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod = (Tropa) session.get(Tropa.class, registroStockSel.getId());
                Hibernate.initialize(this.registroMod.getInventarioTropas());
                session.getTransaction().commit();
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            } finally {
                session.close();
            }

            this.modo = "M";
            //Cargo lista de detalles de garrones en stock
            listaGarronesStock.clear();
            Iterator i = this.getRegistroMod().getInventarioTropas().iterator();
            while (i.hasNext()) {
                InventarioTropa g = (InventarioTropa) i.next();
                listaGarronesStock.add(g);
            }

            //Guardo el deposito seleccionado
            codDepositoSel = registroMod.getDeposito().getId();
            obtieneDeposito(codDepositoSel);
            return "/vistas/tropas/ConsultaStockTropa";
        } else {
            return null;
        }
    }

    public void onClickPagoCIva() {
    }

    //Elimina registro seleccionado
    public void elimina() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        Tropa o = getRegistroSel();

        if (o != null) {
            if (o.getProcesada() == '1') {
                msg = new FacesMessage("Tropa no puede ser eliminada. Ya fué procesada.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                Session session;
                session = HibernateUtil.getSessionFactory().openSession();
                try {
                    session.beginTransaction();
                    session.delete(o);
                    session.getTransaction().commit();
                    lista.remove(o);
                    msg = new FacesMessage("Eliminación exitosa!");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } catch (HibernateException e) {
                    session.getTransaction().rollback();
                    msg = new FacesMessage("Error: " + e.getCause().getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return;
                } finally {
                    session.close();
                }

            }
        }
    }

    //Elimina detalle de tropa
    public void eliminaDetalle(TropaDet i) {
        this.getRegistroMod().getTropaDets().remove(i);
        //Actualizo totales de tropa
        actualizaValoresTropa();

    }

    //Elimina detalle de garrones
    public void eliminaDetalleGarron(TropaDetGarron i) {
        this.getRegistroMod().getTropaDetGarrons().remove(i);
        //Actualizo el total de kilos de garrones
        double ld_kilos_garron = i.getKilos().doubleValue();
        totalKilosGarrones = totalKilosGarrones - ld_kilos_garron;
        //Elimino el registro de la lista de garrones
        listaGarrones.remove(i);
    }

    //Agraga detalle de tropa
    public void agregaDetalle() {
        TropaDet det = new TropaDet();
        det = registroModDetalle;
        //Obtengo la instancia de la categoría seleccionada para que muestre bien en la pantalla
        Session session;
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Categoria c = (Categoria) session.get(Categoria.class, registroModDetalle.getCategoria().getId());
        session.getTransaction().commit();
        session.close();
        det.setCategoria(c);

        det.setTropa(registroMod);
        this.getRegistroMod().getTropaDets().add(det);
        //Actualizo totales de tropa
        actualizaValoresTropa();
    }

    //Agrega viajes a tropa
    public void agregaViajes() {
        //Chequeo que no esté agregado
        for (Viaje viaje : listaViajesSeleccionados) {
            TropaViaje e = new TropaViaje();
            e.setViaje(viaje);
            e.setTropa(registroMod);
            e.setPorcAfectado(BigDecimal.ZERO);
            e.setValorAfectado(BigDecimal.ZERO);
            this.getRegistroMod().getTropaViajes().add(e);
        }
    }

    //Elimina viaje
    public void eliminaViaje(TropaViaje i) {
        this.getRegistroMod().getTropaViajes().remove(i);
        //Actualizo valor de total de fletes
        actualizaValorFlete();
        //Actualizo totales de tropa
        actualizaValoresTropa();
    }

    //Calcula valor afectado en base a porcentaje viaje
    public void calculaValorAfectadoViaje(TropaViaje i) {
        double ld_total_viaje = 0, ld_porc_afec = 0, ld_valor_afec = 0;
        TropaViaje viajeSel = i;
        if (viajeSel.getViaje().getValorTotal() != null) {
            ld_total_viaje = viajeSel.getViaje().getValorTotal().doubleValue();
        }

        if (viajeSel.getPorcAfectado() != null) {
            ld_porc_afec = viajeSel.getPorcAfectado().doubleValue();
        }

        ld_valor_afec = ld_total_viaje * ld_porc_afec / 100.00;
        ld_valor_afec = Math.round(ld_valor_afec * 100d) / 100d;

        viajeSel.setValorAfectado(new BigDecimal(ld_valor_afec));
        //Actualizo valor de total de fletes
        actualizaValorFlete();
        //Actualizo totales de tropa
        actualizaValoresTropa();

    }

    //Calcula porcentaje afectado en base a valor afectado del viaje
    public void calculaPorcAfectadoViaje(TropaViaje i) {
        double ld_total_viaje = 0, ld_porc_afec = 0, ld_valor_afec = 0;
        TropaViaje viajeSel = i;

        if (viajeSel.getViaje().getValorTotal() != null) {
            ld_total_viaje = viajeSel.getViaje().getValorTotal().doubleValue();
        }

        if (viajeSel.getValorAfectado() != null) {
            ld_valor_afec = viajeSel.getValorAfectado().doubleValue();
        }

        ld_porc_afec = (ld_valor_afec / ld_total_viaje) * 100.00;
        ld_porc_afec = Math.round(ld_porc_afec * 100d) / 100d;
        viajeSel.setPorcAfectado(new BigDecimal(ld_porc_afec));
        //Actualizo valor de total de fletes
        actualizaValorFlete();
    }

    //Busco viajes pendientes
    public void buscaViajes() {
        FacesMessage msg;
        listaViajes.clear();
        listaViajesSeleccionados.clear();
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q = session.createQuery("from Viaje a where procesado='0'");
            listaViajes = (List<Viaje>) q.list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } finally {
            session.close();
        }

    }

    //Actualiza total de fletes afectados a tropa
    public void actualizaValorFlete() {
        double ld_fletes = 0, ld_costo_total = 0, ld_costo_total_tropa = 0, ld_costo_unitario = 0, ld_kilos_faenados = 0;
        Iterator i = this.getRegistroMod().getTropaViajes().iterator();
        while (i.hasNext()) {
            TropaViaje g = (TropaViaje) i.next();
            ld_fletes = ld_fletes + g.getValorAfectado().doubleValue();
        }
        registroMod.setValorFlete(new BigDecimal(ld_fletes));

        if (registroMod.getImporteCostoTotal() != null) {
            ld_costo_total = registroMod.getImporteCostoTotal().doubleValue();
        }

        if (registroMod.getKilosFaenados() != null) {
            ld_kilos_faenados = registroMod.getKilosFaenados().doubleValue();
        }

        //Importe total de la tropa incluyendo flete
        ld_costo_total_tropa = ld_costo_total + ld_fletes;
        registroMod.setImporteCostoTotalTropa(new BigDecimal(ld_costo_total_tropa));

        ld_costo_unitario = Math.round(ld_costo_total_tropa / ld_kilos_faenados * 10000d) / 10000d;
        registroMod.setCostoUnitario(new BigDecimal(ld_costo_unitario));

    }

    //Actualiza totales a nivel de tropa
    public void actualizaValoresTropa() {
        double ld_kilos_faenados = 0, ld_kilos_decomisados = 0, ld_kilos_vivos = 0, ld_importe_kilos_vivos = 0;
        double ld_importe_faena = 0, ld_rendimiento = 0;
        double ld_valor_ing_brutos = 0, ld_pago_cuenta_iva = 0;
        double ld_importe_comision = 0;
        double ld_importe_flete = 0, ld_costo_total = 0, ld_costo_total_tropa = 0;
        int li_cabezas_vivas = 0, li_cabezas_dte = 0, li_decomisos = 0, li_cabezas_muertas = 0, li_cabezas_faenadas = 0;
        short li_medias_reces = 0;
        double ld_costo_unitario = 0;

        Iterator i = this.getRegistroMod().getTropaDets().iterator();
        while (i.hasNext()) {
            TropaDet g = (TropaDet) i.next();
            li_cabezas_vivas = li_cabezas_vivas + g.getCabezasVivas();
            li_cabezas_dte = li_cabezas_dte + g.getCabezasDte();
            li_decomisos = li_decomisos + g.getDecomisos();
            li_cabezas_muertas = li_cabezas_muertas + g.getCabezasMuertas();
            li_cabezas_faenadas = li_cabezas_faenadas + g.getCabezasFaenadas();
            li_medias_reces = (short) (li_medias_reces + g.getMediasReces());

            ld_kilos_vivos = ld_kilos_vivos + Math.round(g.getKilosVivos().doubleValue() * 100d) / 100d;
            ld_kilos_faenados = ld_kilos_faenados + Math.round(g.getKilosFaenados().doubleValue() * 100d) / 100d;
            ld_kilos_decomisados = ld_kilos_decomisados + Math.round(g.getKilosDecomisados().doubleValue() * 100d) / 100d;
            ld_importe_kilos_vivos = ld_importe_kilos_vivos + Math.round(g.getImporteKiloVivo().doubleValue() * 100d) / 100d;
            ld_importe_faena = ld_importe_faena + Math.round(g.getImporteFaena().doubleValue() * 100d) / 100d;
            ld_valor_ing_brutos = ld_valor_ing_brutos + Math.round(g.getValorIngBrutos().doubleValue() * 100d) / 100d;
            ld_pago_cuenta_iva = ld_pago_cuenta_iva + Math.round(g.getPagoCuentaIva().doubleValue() * 100d) / 100d;
            ld_importe_comision = ld_importe_comision + Math.round(g.getImporteComision().doubleValue() * 100d) / 100d;
            ld_costo_total = ld_costo_total + Math.round(g.getImporteCostoTotal().doubleValue() * 100d) / 100d;
        }

        //Calculo el rendimiento global de la tropa
        if (ld_kilos_vivos > 0) {
            ld_rendimiento = (ld_kilos_faenados / ld_kilos_vivos) * 100.00;
            ld_rendimiento = Math.round(ld_rendimiento * 1000d) / 1000d;
        }

        registroMod.setCabezasVivas(li_cabezas_vivas);
        registroMod.setCabezasDte(li_cabezas_dte);
        registroMod.setDecomisos(li_decomisos);
        registroMod.setCabezasMuertas(li_cabezas_muertas);
        registroMod.setCabezasFaenadas(li_cabezas_faenadas);
        registroMod.setMediasReces(li_medias_reces);

        registroMod.setKilosVivos(new BigDecimal(ld_kilos_vivos));
        registroMod.setKilosFaenados(new BigDecimal(ld_kilos_faenados));
        registroMod.setKilosDecomisados(new BigDecimal(ld_kilos_decomisados));
        registroMod.setImporteKiloVivo(new BigDecimal(ld_importe_kilos_vivos));
        registroMod.setImporteFaena(new BigDecimal(ld_importe_faena));
        registroMod.setValorIngBrutos(new BigDecimal(ld_valor_ing_brutos));
        registroMod.setPagoCuentaIva(new BigDecimal(ld_pago_cuenta_iva));
        registroMod.setImporteComision(new BigDecimal(ld_importe_comision));
        registroMod.setImporteCostoTotal(new BigDecimal(ld_costo_total));

        if (registroMod.getValorFlete() != null) {
            ld_importe_flete = registroMod.getValorFlete().doubleValue();
        }

        //Importe total de la tropa incluyendo flete
        ld_costo_total_tropa = ld_costo_total + ld_importe_flete;
        registroMod.setImporteCostoTotalTropa(new BigDecimal(ld_costo_total_tropa));

        ld_costo_unitario = Math.round(ld_costo_total_tropa / ld_kilos_faenados * 10000d) / 10000d;
        registroMod.setCostoUnitario(new BigDecimal(ld_costo_unitario));

        registroMod.setRendimiento(new BigDecimal(ld_rendimiento));

    }

    //Nuevo detalle de tropa
    public void nuevoDetalle() {
        TropaDet detalle = new TropaDet();
        detalle.setPorcIngBrutos(BigDecimal.ZERO);
        detalle.setPorcComision(BigDecimal.ZERO);
        detalle.setImporteKiloVivo(BigDecimal.ZERO);
        detalle.setKilosFaenados(BigDecimal.ZERO);
        detalle.setKilosDecomisados(BigDecimal.ZERO);
        detalle.setKilosVivos(BigDecimal.ZERO);
        detalle.setImporteFaena(BigDecimal.ZERO);
        detalle.setValorIngBrutos(BigDecimal.ZERO);
        detalle.setPagoCuentaIva(BigDecimal.ZERO);
        detalle.setImporteComision(BigDecimal.ZERO);
        detalle.setImporteCostoTotal(BigDecimal.ZERO);
        detalle.setRendimiento(BigDecimal.ZERO);
        detalle.setPrecioKiloVivo(BigDecimal.ZERO);
        detalle.setPrecioKiloFaenado(BigDecimal.ZERO);
        //Obtengo el porcentaje de ingresos brutos
        double ld_porc_ing_brutos = obtienePorcIngBrutos();
        detalle.setPorcIngBrutos(new BigDecimal(ld_porc_ing_brutos));

        //Obtengo los datos del depósito para ver como cobrar la faena
        detalle.setPrecioKiloFaenado(depositoSel.getPrecioUnidad());
        this.registroModDetalle = detalle;
    }

    //Edita un detalle de tropa
    public void editaDetTropa(TropaDet d) {
        if (d != null) {
            registroModDetalle = d;
        }
    }

    public String graba() {
        FacesMessage msg;
        //Valido permiso para la transacción        
        try {
            if (!validaPermiso(getUsuarioConectado().getNombreUsuario(), ResourceBundle.getBundle("general/Permisos").getString("ModificarTropa"))) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Transacción no autorizada", "Tropas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }

        } catch (UnsupportedEncodingException ex) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage(), "Tropas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        };

        int li_productor, li_deposito, li_producto;
        char ls_procesada;

        //Estado de procesamiento de la tropa
        ls_procesada = registroMod.getProcesada();

        //Si la tropa está EN_TRAMITE solo puede ser puesta EN_STOCK
        if (estadoActual == EN_TRAMITE) {
            if (ls_procesada == PROCESADA) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "La tropa debe ser puesta en stock primero!", "Tropas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
        }

        if (estadoActual == EN_STOCK) {
            if (ls_procesada == EN_TRAMITE) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Estado de la tropa no válido.", "Tropas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
        }

        li_productor = registroMod.getProductor().getId();
        if (li_productor == 0) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un productor", "Tropas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        li_producto = registroMod.getCategoria().getId();
        if (li_producto == 0) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un producto", "Tropas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (registroMod.getDeposito().getId() != null) {
            li_deposito = registroMod.getDeposito().getId().intValue();
            if (li_deposito == 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un depósito", "Tropas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un depósito", "Tropas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (registroMod.getComisionista() != null) {
            double ld_valor_comision = 0;
            ld_valor_comision = (registroMod.getImporteComision() != null ? registroMod.getImporteComision().doubleValue() : 0);
            if (ld_valor_comision == 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar el valor de la comisión", "Tropas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
        }

        //Valido detalle de pagos a cuenta de iva
        if (!validaTotalPagosCuentaIva()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pagos a cuenta de Iva no coinciden con la tropa", "Tropas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        //Valido detalle de romaneo con total de kilos faenados
        if (!validaTotalRomaneo()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Total de kilos faenados no coincide con el romaneo", "Tropas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        Session session = null;
        Tropa u = this.getRegistroMod();
        AsientoRealizado asientoRealizadoFaena = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(u);

            //Si se pasa la tropa a estado EN_STOCK actualizo el inventario y contabilizo
            if (estadoActual == EN_TRAMITE && ls_procesada == EN_STOCK) {
                //Actualizo inventario
                List<InventarioTropa> listaInv = actualizaInventario();
                for (InventarioTropa invDetalle : listaInv) {
                    session.saveOrUpdate(invDetalle);
                }
            }

            //Si se pasa la tropa a estado EN_STOCK actualizo el inventario y contabilizo
            if (estadoActual == EN_STOCK && ls_procesada == PROCESADA) {

                //Realizo los asientos contables
                //Asiento de faena
                asientoRealizadoFaena = contabilizaCostoTropa();
                if (asientoRealizadoFaena.getIdAsiento() == 0) {
                    session.getTransaction().rollback();
                    msg = new FacesMessage("Error al contabilizar: " + asientoRealizadoFaena.getObservaciones());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return null;
                }
            }

            session.getTransaction().commit();

        } catch (HibernateException e) {
            session.getTransaction().rollback();
            if (estadoActual == EN_STOCK && ls_procesada == PROCESADA) {
                //Entonces elimino el asiento de faena
                Asiento asiento = new Asiento();
                asiento.setId(asientoRealizadoFaena.getIdAsiento());
                AsientoRealizado resul = eliminaAsiento(asiento);
            }
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            session.close();
        }

        estadoActual = registroMod.getProcesada(); //Actualizo el estado actual
        if(!this.origen.equals("/vistas/tropas/TropasPorNumero")){
            buscaListaDatos();
        }
        

        //Grabo la auditoría de la transacción
        try {
            grabaAuditoria(getUsuarioConectado().getIdUsuario(), ResourceBundle.getBundle("general/Permisos").getString("ModificarTropa"),
                    "Actualización de Tropa Nro: " + registroMod.getNumeroTropa(), "Módulo de Tropas", obtieneNombreEquipo());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TropaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return this.origen;
    }

    //Calcula medias reces
    public void calculaMediasReces() {
        int li_cabezas_faenadas = 0;
        short li_medias_reces = 0;

        li_cabezas_faenadas = registroModDetalle.getCabezasFaenadas();
        li_medias_reces = (short) (li_cabezas_faenadas * 2);
        registroModDetalle.setMediasReces(li_medias_reces);
    }

    //Calcula valores de costo total del detalle de tropa
    public void calculaValoresTropa() {
        int li_cabezas_faenadas = 0, li_cabezas_decomisadas = 0;
        double ld_kilos_faenados = 0, ld_kilos_decomisados = 0, ld_kilos_vivos = 0, ld_precio_kilo_vivo = 0, ld_importe_kilos_vivos = 0;
        double ld_precio_kilo_faenado = 0, ld_importe_faena = 0, ld_rendimiento = 0;
        double ld_porc_ing_brutos = 0, ld_valor_ing_brutos = 0, ld_pago_cuenta_iva = 0;
        double ld_porc_comision = 0, ld_importe_comision = 0;
        double ld_importe_flete = 0, ld_costo_total = 0;
        int li_cabezas_dte = 0;

        //Obtengo los datos del depósito para ver como cobrar la faena
        li_cabezas_faenadas = registroModDetalle.getCabezasFaenadas();
        li_cabezas_decomisadas = registroModDetalle.getDecomisos();

        if (registroModDetalle.getKilosFaenados() != null) {
            ld_kilos_faenados = registroModDetalle.getKilosFaenados().doubleValue();
        }

        if (registroModDetalle.getKilosDecomisados() != null) {
            ld_kilos_decomisados = registroModDetalle.getKilosDecomisados().doubleValue();
        }

        if (registroModDetalle.getKilosVivos() != null) {
            ld_kilos_vivos = registroModDetalle.getKilosVivos().doubleValue();
        }

        if (ld_kilos_vivos > 0) {
            ld_rendimiento = (ld_kilos_faenados / ld_kilos_vivos) * 100.00;
            ld_rendimiento = Math.round(ld_rendimiento * 1000d) / 1000d;
        }

        if (registroModDetalle.getPrecioKiloVivo() != null) {
            ld_precio_kilo_vivo = registroModDetalle.getPrecioKiloVivo().doubleValue();
            ld_precio_kilo_vivo = Math.round(ld_precio_kilo_vivo * 1000d) / 1000d;
        }

        ld_importe_kilos_vivos = ld_kilos_vivos * ld_precio_kilo_vivo;
        ld_importe_kilos_vivos = Math.round(ld_importe_kilos_vivos * 100d) / 100d;

        if (registroModDetalle.getPrecioKiloFaenado() != null) {
            ld_precio_kilo_faenado = registroModDetalle.getPrecioKiloFaenado().doubleValue();
            ld_precio_kilo_faenado = Math.round(ld_precio_kilo_faenado * 10000d) / 10000d;
        }

        //Calculo el importe de la faena de acuerdo a como cobra el productor
        switch (depositoSel.getTipoCobroFaena()) {
            case 'K':
                //Por kilo
                //Importe de faena incluye faenados + decomisos
                ld_importe_faena = (ld_kilos_faenados + ld_kilos_decomisados) * ld_precio_kilo_faenado;
                ld_importe_faena = Math.round(ld_importe_faena * 100d) / 100d;
                break;
            case 'C':
                //Cobra por cabeza
                ld_importe_faena = (li_cabezas_faenadas + li_cabezas_decomisadas) * ld_precio_kilo_faenado;
                ld_importe_faena = Math.round(ld_importe_faena * 100d) / 100d;
                break;
            default:
                ld_importe_faena = 0.00;
        }

        if (registroModDetalle.getPorcIngBrutos() != null) {
            ld_porc_ing_brutos = registroModDetalle.getPorcIngBrutos().doubleValue();
        }

        ld_valor_ing_brutos = ld_importe_kilos_vivos * ld_porc_ing_brutos / 100.00;
        ld_valor_ing_brutos = Math.round(ld_valor_ing_brutos * 100d) / 100d;

        li_cabezas_dte = registroModDetalle.getCabezasDte();
        //Cargo el valor por cabeza para el pago a cuenta de iva
        double ld_pago_cuenta_iva_cabeza = obtieneValorPagoCuentaIva();
        ld_pago_cuenta_iva = li_cabezas_dte * ld_pago_cuenta_iva_cabeza;
        ld_pago_cuenta_iva = Math.round(ld_pago_cuenta_iva * 100d) / 100d;

        if (registroModDetalle.getPorcComision() != null) {
            ld_porc_comision = registroModDetalle.getPorcComision().doubleValue();
        }

        ld_importe_comision = ld_importe_kilos_vivos * ld_porc_comision / 100.00;

        if (registroMod.getValorFlete() != null) {
            ld_importe_flete = registroMod.getValorFlete().doubleValue();
        }

        //Calculo el costo total
        ld_costo_total = ld_importe_kilos_vivos + ld_importe_faena + ld_valor_ing_brutos + ld_pago_cuenta_iva
                + ld_importe_comision;

        registroModDetalle.setRendimiento(new BigDecimal(ld_rendimiento));
        registroModDetalle.setImporteKiloVivo(new BigDecimal(ld_importe_kilos_vivos));
        registroModDetalle.setImporteFaena(new BigDecimal(ld_importe_faena));
        registroModDetalle.setValorIngBrutos(new BigDecimal(ld_valor_ing_brutos));
        registroModDetalle.setPagoCuentaIva(new BigDecimal(ld_pago_cuenta_iva));
        registroModDetalle.setImporteComision(new BigDecimal(ld_importe_comision));
        registroModDetalle.setImporteCostoTotal(new BigDecimal(ld_costo_total));
        return;
    }

    //Actualiza el costo total debido a actualización del pago a cuenta de iva
    public void actualizaValorPagoCuentaIva() {
        double ld_pago_cuenta_iva = 0, ld_importe_kilos_vivos = 0, ld_importe_faena = 0, ld_valor_ing_brutos = 0;
        double ld_importe_comision = 0, ld_costo_total = 0;

        if (registroModDetalle.getPagoCuentaIva() != null) {
            ld_pago_cuenta_iva = registroModDetalle.getPagoCuentaIva().doubleValue();
        }

        if (registroModDetalle.getImporteKiloVivo() != null) {
            ld_importe_kilos_vivos = registroModDetalle.getImporteKiloVivo().doubleValue();
        }

        if (registroModDetalle.getImporteFaena() != null) {
            ld_importe_faena = registroModDetalle.getImporteFaena().doubleValue();
        }

        if (registroModDetalle.getValorIngBrutos() != null) {
            ld_valor_ing_brutos = registroModDetalle.getValorIngBrutos().doubleValue();
        }

        if (registroModDetalle.getImporteComision() != null) {
            ld_importe_comision = registroModDetalle.getImporteComision().doubleValue();
        }

        //Calculo el costo total
        ld_costo_total = ld_importe_kilos_vivos + ld_importe_faena + ld_valor_ing_brutos
                + ld_pago_cuenta_iva + ld_importe_comision;

        registroModDetalle.setImporteCostoTotal(new BigDecimal(ld_costo_total));
        return;

    }

    //Actualiza el costo total debido a actualización del valor de ingresos brutos
    public void actualizaValorIngBrutos() {
        double ld_pago_cuenta_iva = 0, ld_importe_kilos_vivos = 0, ld_importe_faena = 0, ld_valor_ing_brutos = 0;
        double ld_importe_comision = 0, ld_costo_total = 0, ld_porc_ing_brutos;

        if (registroModDetalle.getPagoCuentaIva() != null) {
            ld_pago_cuenta_iva = registroModDetalle.getPagoCuentaIva().doubleValue();
        }

        if (registroModDetalle.getImporteKiloVivo() != null) {
            ld_importe_kilos_vivos = registroModDetalle.getImporteKiloVivo().doubleValue();
        }

        if (registroModDetalle.getImporteFaena() != null) {
            ld_importe_faena = registroModDetalle.getImporteFaena().doubleValue();
        }

        if (registroModDetalle.getValorIngBrutos() != null) {
            ld_valor_ing_brutos = registroModDetalle.getValorIngBrutos().doubleValue();
        }

        //Calculo el nuevo porcentaje de ingresos brutos
        ld_porc_ing_brutos = (ld_valor_ing_brutos / ld_importe_kilos_vivos) * 100;

        if (registroModDetalle.getImporteComision() != null) {
            ld_importe_comision = registroModDetalle.getImporteComision().doubleValue();
        }

        //Calculo el costo total
        ld_costo_total = ld_importe_kilos_vivos + ld_importe_faena + ld_valor_ing_brutos
                + ld_pago_cuenta_iva + ld_importe_comision;

        registroModDetalle.setImporteCostoTotal(new BigDecimal(ld_costo_total));
        registroModDetalle.setPorcIngBrutos(new BigDecimal(ld_porc_ing_brutos));
        return;

    }

    //Obtien el valor de pago a cuenta de iva por cabeza
    public double obtieneValorPagoCuentaIva() {
        double ld_valor = 0;
        Empresa e = cargaEmpresa();
        if (e.getPagoCuentaIva() != null) {
            ld_valor = e.getPagoCuentaIva().doubleValue();
        }
        return ld_valor;
    }

    //Obtien porc. ingresos brutos
    public double obtienePorcIngBrutos() {
        double ld_valor = 0;
        Empresa e = cargaEmpresa();
        if (e.getPorcIngBrutos() != null) {
            ld_valor = e.getPorcIngBrutos().doubleValue();
        }
        return ld_valor;
    }

    //Método para generar un archivo para poder procesar los datos del archvo subido ya que el formato subido por el uploaded del primefaces no se procesa directamente    
    public File generarArchivo(UploadedFile prmFile) {

        File excelFile = new File("outFile.java");
        InputStream inputStream;
        try { // 
            inputStream = prmFile.getInputstream();
            OutputStream out = new FileOutputStream(excelFile);
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return excelFile;
    }

    //Valida si el archivo de entrada esta con formato correcto
    public boolean validarArchivo(File prmFile) {
        int rowError;
        String ls_num_garron_ant;
        int li_contador_garron = 0;
        try {
            Workbook workbook = Workbook.getWorkbook(prmFile);
            Sheet sheet = workbook.getSheets()[0];

            for (int row = 1; row < sheet.getRows(); row++) {

                String garron_actual = sheet.getCell(2, row).getContents();
                if (garron_actual.isEmpty()) { // || fNac.isEmpty() para cada fila del archivo pregunto si son titulares
                    rowError = row;
                    return false;
                } else {
                }
                //Guardo el número de garrón para confirmar que sean 2
                ls_num_garron_ant = garron_actual;

            }
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean armadoArchivoTropa(FileUploadEvent event) {
        int rowSheet = 1;
        UploadedFile file = event.getFile();
        File excelFile = generarArchivo(file); // llamo a la funcion que convierte el archivo subido por primefaces a un archivo tipo File que se puede procesar
        int li_num_garron_ant = 0, li_garron_actual = 0;
        double ld_kilos_garron;
        System.out.println("Entra a procesar");

        //Borro todos los garrones
        for (TropaDetGarron dg : listaGarrones) {
            this.getRegistroMod().getTropaDetGarrons().remove(dg);
        }
        listaGarrones.clear();
        totalKilosGarrones = 0;
        try {
            //obtengo los datos del archivo
            Workbook workbook = Workbook.getWorkbook(excelFile);
            Sheet sheet = workbook.getSheets()[0];
            String ls_kilos_garron, ls_categoria;
            Categoria cat = null;
            int li_categoria = 0;
            String ls_tipo;

            for (int row = rowSheet; row < sheet.getRows(); row++) {
                System.out.println("Entra a procesar filas.." + row);
                String ls_garron_actual = sheet.getCell(1, row).getContents();
                if (ls_garron_actual == null) {
                    li_garron_actual = 0;
                } else {
                    li_garron_actual = Integer.valueOf(ls_garron_actual);
                }

                //Pongo tipo de garrón derecho o izquierdo
                if (li_garron_actual == li_num_garron_ant) {
                    ls_tipo = "GD";
                } else {
                    ls_tipo = "GI";
                }

                //Kilos del garrón
                ls_kilos_garron = sheet.getCell(2, row).getContents();

                DecimalFormat df = new DecimalFormat("#,##0.##", new DecimalFormatSymbols(new Locale("es", "AR")));
                //ld_kilos_garron=(double) df.parse(ls_kilos_garron);
                Number number = df.parse(ls_kilos_garron);
                ld_kilos_garron = number.doubleValue();
                double kilosRedo = Math.round(ld_kilos_garron * 100.0) / 100.0;
                ld_kilos_garron = kilosRedo;
                //ld_kilos_garron=Double.valueOf(ls_kilos_garron);

                //Guardo el número de garrón para confirmar que sean 2
                li_num_garron_ant = li_garron_actual;

                //Categoría o denominación del garrón
                ls_categoria = sheet.getCell(3, row).getContents();
                if (ls_categoria != null) {
                    li_categoria = Integer.valueOf(ls_categoria).intValue();
                }
                if (li_categoria > 0) {
                    cat = obtieneCategoria(li_categoria);
                }

                TropaDetGarron dg = new TropaDetGarron();
                dg.setTropa(registroMod);
                dg.setNumGarron(li_garron_actual);
                dg.setCategoria(cat);
                dg.setTipo(ls_tipo);
                dg.setKilos(new BigDecimal(ld_kilos_garron));

                totalKilosGarrones = totalKilosGarrones + ld_kilos_garron;

                //Agrego a la lista
                listaGarrones.add(dg);
                System.out.println("Reg: " + row);
                System.out.println("Garrón: " + ls_garron_actual);
                System.out.println("Tipo: " + ls_tipo);
                System.out.println("Kilos: " + ld_kilos_garron);

            }
            totalKilosGarrones = Math.round(totalKilosGarrones * 100.0) / 100.0;
            //Ahora cargo la lista de garrones
            for (TropaDetGarron dg : listaGarrones) {
                TropaDetGarron e = new TropaDetGarron();
                e = dg;
                this.getRegistroMod().getTropaDetGarrons().add(e);
            }

            FacesMessage msg = new FacesMessage("Archivo cargado exitosamente!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    //Arma lista de garrones par actualizar el inventario 
    public List<InventarioTropa> actualizaInventario() {
        int li_idGarron;
        String ls_tipo = "";
        double ld_kilos = 0;
        List<InventarioTropa> listaInventario = new ArrayList<>();

        Iterator i = this.getRegistroMod().getTropaDetGarrons().iterator();
        while (i.hasNext()) {
            TropaDetGarron detGarron = (TropaDetGarron) i.next();
            li_idGarron = detGarron.getId();
            ls_tipo = String.valueOf(detGarron.getTipo());
            if (detGarron.getKilos() != null) {
                ld_kilos = detGarron.getKilos().doubleValue();
            }

            InventarioTropa inv = new InventarioTropa();
            inv.setTropa(registroMod);
            inv.setTropaDetGarron(detGarron);
            inv.setTipo(ls_tipo);
            inv.setKilos(new BigDecimal(ld_kilos));
            inv.setEstado('D'); //Disponible para entrega
            listaInventario.add(inv);

        }
        return listaInventario;

    }

    //Obtiene deposito
    public void obtieneDeposito(int codDeposito) {
        FacesMessage msg;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            setDepositoSel((Deposito) session.get(Deposito.class, codDeposito));
            registroMod.setDeposito(depositoSel);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } finally {
            session.close();
        }
    }

    //Selecciono depósito
    public void cambiaDeposito() {
        obtieneDeposito(this.getCodDepositoSel());
    }

    //Obtiene categoria
    public Categoria obtieneCategoria(int codCategoria) {
        FacesMessage msg;
        Categoria cat = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            cat = (Categoria) session.get(Categoria.class, codCategoria);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            session.close();
        }
        return cat;
    }

    //Agrega pago a cuenta de Iva
    public void agregaPagoCuentaIva() {
        TropaPagoCiva pagoCIva = new TropaPagoCiva();
        pagoCIva.setTropa(registroMod);
        registroMod.getTropaPagoCivas().add(pagoCIva);

    }

    public void validaPagosCIva() {
        Iterator i = this.getRegistroMod().getTropaPagoCivas().iterator();
        while (i.hasNext()) {
            TropaPagoCiva pago = (TropaPagoCiva) i.next();
            if (pago.getCuentaBanco().getId() == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar una cuenta", "Tropas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
        }
    }

    //Valida que el total de los pagos a cuenta de Iva coincida con el valor de la tropa
    public boolean validaTotalPagosCuentaIva() {
        double ld_total_pagos_civa = 0;
        Iterator i = this.getRegistroMod().getTropaPagoCivas().iterator();
        while (i.hasNext()) {
            TropaPagoCiva pago = (TropaPagoCiva) i.next();
            if (pago.getValor() != null) {
                ld_total_pagos_civa = ld_total_pagos_civa + pago.getValor().doubleValue();
            }
        }

        if (registroMod.getPagoCuentaIva() != null) {
            if (ld_total_pagos_civa == registroMod.getPagoCuentaIva().doubleValue()) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    //Valida que el detalle de romaneo de tropa coincida con los kilos faenados
    public boolean validaTotalRomaneo() {
        double ld_total_kilos_faenados = 0;

        if (registroMod.getKilosFaenados() != null) {
            ld_total_kilos_faenados = registroMod.getKilosFaenados().doubleValue();
            ld_total_kilos_faenados = Math.round(ld_total_kilos_faenados * 100.0) / 100.0;
        }

        if (totalKilosGarrones == ld_total_kilos_faenados) {
            return true;
        } else {
            return false;
        }
    }

    public void eliminaPagoCIva(TropaPagoCiva o) {
        this.getRegistroMod().getTropaPagoCivas().remove(o);
    }

    //Realizo asiento de faena
    public AsientoRealizado contabilizaCostoTropa() {
        //Cargo la empresa para obtener las cuentas contables
        Empresa empresa = cargaEmpresa();

        Asiento asiento = new Asiento();
        asiento.setIdEmpresa(1);
        asiento.setNumPlantilla(0);  //No tiene plantilla relacionada
        asiento.setDescripcion("Costo de faena Tropa: " + registroMod.getNumeroTropa() + " Frigorífico: " + registroMod.getDeposito().getNombre());
//        java.util.Calendar fecha=java.util.Calendar.getInstance();
//        java.sql.Date lda_fecha=new java.sql.Date(fecha.getTimeInMillis());
//        asiento.setFecMov(lda_fecha);

        //Fecha del movimiento
        java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
        fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_mov.setTime(new Date());

        //Convierto la fecha a String para no tener problemas con JSON
        TimeZone gmtZone = TimeZone.getTimeZone("America/Buenos_Aires");
        DateFormat destDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        destDateFormat.setTimeZone(gmtZone);
        String ls_fecha;
        ls_fecha = destDateFormat.format(fecha_mov.getTime());
        System.out.println("Fecha string: " + ls_fecha);
        asiento.setFecMov(ls_fecha);

        asiento.setIdUsuario(getUsuarioConectado().getIdUsuario());

        //Todas las cuentas contables del asiento se pasan desde aquí
        AsientoCuentaAdicional cuentaAdic = null;

        //Agrego cuenta contable del costo total de tropa
        cuentaAdic = new AsientoCuentaAdicional();
        cuentaAdic.setNumeroCuenta(empresa.getCcCostoTropa());
        cuentaAdic.setDc("D");
        cuentaAdic.setValor(Math.round(registroMod.getImporteCostoTotalTropa().doubleValue() * 100d) / 100d);
        asiento.getListaCuentasAdic().add(cuentaAdic);

        //Agrego cuenta contable de Ingresos Brutos
        cuentaAdic = new AsientoCuentaAdicional();
        cuentaAdic.setNumeroCuenta(empresa.getCcIngBrutos());
        cuentaAdic.setDc("C");
        cuentaAdic.setValor(Math.round(registroMod.getValorIngBrutos().doubleValue() * 100d) / 100d);
        asiento.getListaCuentasAdic().add(cuentaAdic);

        //Agrego cuenta contable de VEP
        cuentaAdic = new AsientoCuentaAdicional();
        cuentaAdic.setNumeroCuenta(empresa.getCcPagoVep());
        cuentaAdic.setDc("C");
        cuentaAdic.setValor(Math.round(registroMod.getPagoCuentaIva().doubleValue() * 100d) / 100d);
        asiento.getListaCuentasAdic().add(cuentaAdic);

        if (registroMod.getProductor() != null) {
            //Agrego cuenta contable del productor
            cuentaAdic = new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(registroMod.getProductor().getCcCostoTropa());
            cuentaAdic.setDc("C");
            cuentaAdic.setValor(Math.round(registroMod.getImporteKiloVivo().doubleValue() * 100d) / 100d);
            asiento.getListaCuentasAdic().add(cuentaAdic);
        }

        if (registroMod.getComisionista() != null) {
            //Agrego cuenta contable del comisionista
            cuentaAdic = new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(registroMod.getComisionista().getCcCostoTropa());
            cuentaAdic.setDc("C");
            cuentaAdic.setValor(Math.round(registroMod.getImporteComision().doubleValue() * 100d) / 100d);
            asiento.getListaCuentasAdic().add(cuentaAdic);
        }

        if (registroMod.getDeposito() != null) {
            //Agrego cuenta contable del costo de faena o depósito
            cuentaAdic = new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(registroMod.getDeposito().getCcCostoTropa());
            cuentaAdic.setDc("C");
            cuentaAdic.setValor(Math.round(registroMod.getImporteFaena().doubleValue() * 100d) / 100d);
            asiento.getListaCuentasAdic().add(cuentaAdic);
        }

        //Agrego cuenta contable por cada uno de los fletes
        Iterator i = this.getRegistroMod().getTropaViajes().iterator();
        while (i.hasNext()) {
            TropaViaje viaje = (TropaViaje) i.next();
            cuentaAdic = new AsientoCuentaAdicional();
            cuentaAdic.setNumeroCuenta(viaje.getViaje().getChofer().getCcCostoTropa());
            cuentaAdic.setDc("C");
            cuentaAdic.setValor(Math.round(viaje.getValorAfectado().doubleValue() * 100d) / 100d);
            asiento.getListaCuentasAdic().add(cuentaAdic);
        }

        AsientoRealizado resul = realizaAsiento(asiento);
        return resul;
    }

    public void buscaComisionista() {
        FacesMessage msg;
        comisionistaSel = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery("from Comisionista a where a.nombre like :nombreComisionista");
            q.setParameter("nombreComisionista", "%" + nombreComisionista + "%");
            listaComisionistas = (List<Comisionista>) q.list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        } finally {
            session.close();
        }
    }

    public void seleccionaComisionista() {
        if (comisionistaSel != null) {
            this.getRegistroMod().setComisionista(comisionistaSel);
        }
    }

    public void eliminaComisionista() {
        this.getRegistroMod().setComisionista(null);
    }

    //Obtiene el id de la entrega del garrón
    public int obtieneEntregaGarron(int idGarronStock) throws SQLException {
        //Obtiene nombre de forma de pago
        CallableStatement s = null;
        ResultSet r = null;
        int li_idEntrega;
        String ls_nombre;
        //Conectamos a la base
        Conector conector = new Conector();
        Connection conexion = conector.connect("estancia");

        try {

            s = conexion.prepareCall("{call sp_get_entrega_garron ( ? , ? )}", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            s.setInt(1, idGarronStock);
            s.registerOutParameter(2, java.sql.Types.INTEGER);
            r = s.executeQuery();
            //Obtengo el id del registro insertado
            li_idEntrega = s.getInt(2);
            return li_idEntrega;

        } catch (SQLException e) {
            e.getMessage();
            return 0;
        } finally {
            if (conexion != null) {
                conexion.close();
            }
            if (r != null) {
                r.close();
            }
        }

    }

    //Obtiene entrega
    public Entrega obtieneEntregaDeGarron(InventarioTropa itemInventario) {
        int li_idEntrega = 0;
        try {
            li_idEntrega = obtieneEntregaGarron(itemInventario.getId().intValue());
        } catch (SQLException ex) {
            Logger.getLogger(TropaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Entrega entrega = new Entrega();
        if (li_idEntrega > 0) {
            FacesMessage msg;
            Session session = null;
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                entrega = (Entrega) session.get(Entrega.class, li_idEntrega);
                session.getTransaction().commit();
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                msg = new FacesMessage("Error: " + e.getCause().getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } finally {
                session.close();
            }
        }
        return entrega;
    }

    public String onClickEntregaDeGarron(InventarioTropa itemInventario) {

        int li_idEntrega = 0;
        try {
            li_idEntrega = obtieneEntregaGarron(itemInventario.getId().intValue());
        } catch (SQLException ex) {
            Logger.getLogger(TropaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        FacesMessage msg;
        Session session = null;
        Entrega entrega = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            entrega = (Entrega) session.get(Entrega.class, li_idEntrega);
            Hibernate.initialize(entrega.getEntregaDets());
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            session.close();
        }

        try {
            EntregaController entregaController = new EntregaController();
            entregaController.setRegistroSel(entrega);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("entregacontroller", entregaController);
            return entregaController.edita();

        } catch (Exception ex) {
            Logger.getLogger(ProductorController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
