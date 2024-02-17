/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Chofer;
import entidades.Cliente;
import entidades.Localidad;
import entidades.Viaje;
import entidades.CostoFleteAnimales;
import general.BeanBase;
import general.ListaLocalidadesTodas;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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

@ManagedBean(name="viajecontroller")
@SessionScoped

public class ViajeController extends BeanBase implements Serializable {
    Viaje registroSel;
    Viaje registroMod;
    private String modo="";
    String mensaje="";
    private List <Viaje> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private List<Localidad> localidades= new ArrayList<>();
    private Cliente clienteSel;
    private CostoFleteAnimales regCostoSel;
    private String nombreCliente;
    private List <Cliente> listaClientes=new ArrayList<Cliente>();

    public ViajeController() {
        buscaListaDatos();
    }

    public Viaje getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Viaje registroSel) {
        this.registroSel = registroSel;
    }

    public Viaje getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Viaje registroMod) {
        this.registroMod = registroMod;
    }

    public List<Viaje> getLista() {
        return lista;
    }

    public void setLista(List<Viaje> lista) {
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

    public Cliente getClienteSel() {
        return clienteSel;
    }

    public void setClienteSel(Cliente clienteSel) {
        this.clienteSel = clienteSel;
    }

    public CostoFleteAnimales getRegCostoSel() {
        return regCostoSel;
    }

    public void setRegCostoSel(CostoFleteAnimales regCostoSel) {
        this.regCostoSel = regCostoSel;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }
    
    

    //Obtiene lista de todos los registros
    public void buscaListaDatos(){
        FacesMessage msg;
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Viaje a");
            this.lista=(List<Viaje>) q.list();
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
    public String nuevo() throws Exception{
        this.registroMod=new Viaje();        
        this.modo="N";
        
        //Obtengo el chofer o fletero relacionado con el usuario
        Chofer f=buscaChoferRelacionado(getUsuarioConectado().getIdUsuario());
        if (f!=null)
            this.registroMod.setChofer(f);
            
        this.registroMod.setTipoJaula('S'); //Jaula simple
        this.registroMod.setTipoProducto('A'); //Animales
        this.registroMod.setPorcIva(BigDecimal.ZERO);
        this.registroMod.setPorcUsado(BigDecimal.ZERO);
        this.registroMod.setDistancia(BigDecimal.ZERO);
        this.registroMod.setProcesado('0');
        
        //Obtengo la fecha de hoy para poner como fecha de carga
        java.util.Calendar fecha=java.util.Calendar.getInstance();
        java.sql.Date lda_fecha=new java.sql.Date(fecha.getTimeInMillis());
        this.registroMod.setFecCarga(lda_fecha);
        
        ListaLocalidadesTodas listaLoc= new  ListaLocalidadesTodas();
        listaLoc.getLista();
        localidades=listaLoc.getItems();
        return "/vistas/viajes/Create";
            
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
                this.registroMod =(Viaje) session.get(Viaje.class,registroSel.getId());
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
            ListaLocalidadesTodas listaLoc= new  ListaLocalidadesTodas();
            listaLoc.getLista();
            localidades=listaLoc.getItems();
            return "/vistas/viajes/Edit";
        }
        else
            return null;
    }

    //Obtiene los detalles del registro que se hace clic
    public String onClick(Viaje p) throws Exception{
        FacesMessage msg;
        registroSel=p;
        if (registroSel != null)
        {
            Session session = null;
            try{
                session=HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                this.registroMod =(Viaje) session.get(Viaje.class,registroSel.getId());
                //Hibernate.initialize(this.registroMod.getLocalCarnicerias());
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
            ListaLocalidadesTodas listaLoc= new  ListaLocalidadesTodas();
            listaLoc.getLista();
            localidades=listaLoc.getItems();
            return "/vistas/viajes/Edit";
        }
        else
            return null;
    }
    
    //Elimina registro seleccionado
    public void elimina() {
       FacesMessage msg;
       FacesContext context=FacesContext.getCurrentInstance();
       Viaje o=getRegistroSel();

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
    
        Viaje u=this.getRegistroMod();
        
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            this.setMensaje(e.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_INFO,"",e.getMessage()));
            return null;
            
        }
        finally {
            session.close();
        }
        buscaListaDatos();
        FacesMessage msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "Viajes";
    }
    
    public void calculaCostos(){
        double ld_valor_neto=0;
        double ld_costoxkm,ld_distancia,ld_porc_iva=0,ld_valor_iva=0,ld_valor_total=0,ld_KmArranque;
        double ld_toneladas,ld_costoxton=0,ld_seguro,ld_costo_arranque;
        Character ls_tipo_jaula,ls_tipo_prod;
        
        ls_tipo_prod=getRegistroMod().getTipoProducto();
        ls_tipo_jaula=getRegistroMod().getTipoJaula();

        //Actualizo IVA de acuerdo al cliente
        if (registroMod.getCliente().getPorcIvaFlete()!= null)
            registroMod.setPorcIva(registroMod.getCliente().getPorcIvaFlete());
        else
            registroMod.setPorcIva(BigDecimal.ZERO);

        
        switch (ls_tipo_prod){
            case  'A':
              //Producto transportado : Animales
              //Obtengo costos de acuerdo a cliente y tipo de jaula  
              if (getRegistroMod().getCliente()!= null){
                cargarRegistroCosto(getRegistroMod().getCliente(),ls_tipo_jaula);
                ld_valor_neto=0;
                if (regCostoSel!= null){
                    //Obtengo Km de arranque para no cobrar el seguro
                    ld_KmArranque=regCostoSel.getKmArranque().doubleValue();
                    //Costo del arranque
                    ld_costo_arranque=regCostoSel.getCostoArranque().doubleValue();
                    //Obtengo costo del km
                    ld_costoxkm=regCostoSel.getCostoxKm().doubleValue();
                    //Costo del seguro
                    ld_seguro=regCostoSel.getCostoSeguro().doubleValue();
                    
                    if (getRegistroMod().getDistancia()==null)
                        ld_distancia=0;
                    else
                        ld_distancia=registroMod.getDistancia().doubleValue();
                
                    if (ld_distancia > ld_KmArranque)
                        //No cobramos arranque
                        ld_valor_neto=ld_distancia*ld_costoxkm + ld_seguro;
                    else
                        //Cobramos arranque
                        ld_valor_neto=ld_distancia*ld_costoxkm + ld_costo_arranque + ld_seguro;
                
                }
        
                ld_porc_iva=0;
                if (registroMod.getPorcIva()!= null)
                    ld_porc_iva=registroMod.getPorcIva().doubleValue();
                ld_valor_iva=ld_valor_neto*ld_porc_iva/100;
        
                ld_valor_total=ld_valor_neto + ld_valor_iva;
        
            }  
            break;  
            case 'G':  
                //Producto transportado: Granos
                ld_valor_neto=0;
                //Toneladas trasnportadas
                if (getRegistroMod().getCantidad()==null)
                    ld_toneladas=0;
                else
                    ld_toneladas=registroMod.getCantidad().doubleValue();
            
                //Costo x tonelada
                if (getRegistroMod().getCliente()!= null){
                    ld_costoxton=getRegistroMod().getCliente().getCostoxTon().doubleValue();
                }
            
                ld_valor_neto=ld_toneladas*ld_costoxton;
                ld_porc_iva=0;
                if (registroMod.getPorcIva()!= null)
                    ld_porc_iva=registroMod.getPorcIva().doubleValue();
                ld_valor_iva=ld_valor_neto*ld_porc_iva/100;
                ld_valor_total=ld_valor_neto + ld_valor_iva;
                break;  
            case 'M':
                ld_valor_neto=registroMod.getValorNeto().doubleValue();
                ld_porc_iva=0;
                if (registroMod.getPorcIva()!= null)
                    ld_porc_iva=registroMod.getPorcIva().doubleValue();
                ld_valor_iva=ld_valor_neto*ld_porc_iva/100;
                ld_valor_total=ld_valor_neto + ld_valor_iva;
                break;  
               
        }
            
        registroMod.setValorNeto( new BigDecimal(ld_valor_neto));
        registroMod.setValorIva(new BigDecimal(ld_valor_iva));
        registroMod.setValorTotal(new BigDecimal(ld_valor_total));
        
    } 
    
    public void cargarRegistroCosto(Cliente cliente,Character tipoJaula){
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q=session.createQuery("from CostoFleteAnimales a where a.cliente = :clienteSel and tipoJaula= :tipoJaulaSel");
            q.setParameter("clienteSel",cliente);
            q.setParameter("tipoJaulaSel",tipoJaula);
            List<CostoFleteAnimales> lista=(List<CostoFleteAnimales>)q.list();
            if (lista.size()==1)
                regCostoSel=lista.get(0);
            else
                regCostoSel=null;
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            this.setMensaje(e.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_INFO,"",e.getMessage()));
            return;
            
        }
        finally {
            session.close();
        }
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
            this.setMensaje(e.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_INFO,"",e.getMessage()));
            return;
        }
        finally {
            session.close();
        }
    }
    
    //Busca el chofer o fletero relacionado con el usuario que carga el viaje
    public Chofer buscaChoferRelacionado(int idUsuario){
        Chofer f= null;        
        Session session=null;
        try{
            session=HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx=session.beginTransaction();
            Query q=session.createQuery("from Chofer a where a.idUsuario = :usuario");
            q.setParameter("usuario",idUsuario);
            if (q.list().size()==1)
                f=(Chofer) q.list().get(0);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            this.setMensaje(e.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_INFO,"",e.getMessage()));
            return null;
        }
        finally {
            session.close();
        }
        return f;        
    }
    
    
    public void seleccionaCliente(){
        this.getRegistroMod().setCliente(clienteSel);
        calculaCostos();
    }
   
}
