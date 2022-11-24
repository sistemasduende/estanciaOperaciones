/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import entidades.Empresa;
import general.BeanBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author rafael
 */
@ManagedBean(name="empresacontroller")
@SessionScoped

public class EmpresaController extends BeanBase implements Serializable {
    Empresa registroSel;
    Empresa registroMod;
    private String modo="";
    String mensaje="";
    private List <Empresa> lista=null;
    private TimeZone Zona = TimeZone.getDefault();
    private UploadedFile archivoSubido;
    private StreamedContent foto;
    
    public EmpresaController() {
        getListaDatos();
    }

    public Empresa getRegistroSel() {
        return registroSel;
    }

    public void setRegistroSel(Empresa registroSel) {
        this.registroSel = registroSel;
    }

    public Empresa getRegistroMod() {
        return registroMod;
    }

    public void setRegistroMod(Empresa registroMod) {
        this.registroMod = registroMod;
    }

    public List<Empresa> getLista() {
        return lista;
    }

    public void setLista(List<Empresa> lista) {
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

    public UploadedFile getArchivoSubido() {
        return archivoSubido;
    }

    public void setArchivoSubido(UploadedFile archivoSubido) {
        this.archivoSubido = archivoSubido;
    }

    public StreamedContent getFoto() {
        return foto;
    }

    public void setFoto(StreamedContent foto) {
        this.foto = foto;
    }

    
    
    //Obtiene lista de todos los registros
    //En este caso sola la empresa en la cual está trabajando
    public void getListaDatos(){
        FacesMessage msg;
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            org.hibernate.Transaction tx=session.beginTransaction();
            Empresa u=(Empresa) session.get(Empresa.class,getUsuarioConectado().getIdEmpresa());
            System.out.println("Empresa: " + u.getNombreFantasia());
            this.lista=new ArrayList<Empresa>();
            this.lista.add(u);
            session.getTransaction().commit();
            registroSel=u;
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            this.setMensaje(e.getMessage());
            return;
        }
        finally {
            session.close();
        }
    }
    
    //Obtiene los detalles del registro seleccionado
    public void edita(){
        this.registroMod=new Empresa();
        //Pongo los datos a modificar del registro seleccionado
        registroMod.setId(registroSel.getId());
        registroMod.setProvincia(registroSel.getProvincia());
        registroMod.setPais(registroSel.getPais());
        registroMod.setNombreFantasia(registroSel.getNombreFantasia());
        registroMod.setRazonSocial(registroSel.getRazonSocial());
        registroMod.setObservaciones(registroSel.getObservaciones());
        registroMod.setCuit(registroSel.getCuit());
        registroMod.setDireccion(registroSel.getDireccion());        
        registroMod.setTelefonoFijo(registroSel.getTelefonoFijo());        
        registroMod.setTelefonoMovil(registroSel.getTelefonoMovil());        
        registroMod.setContacto(registroSel.getContacto());                
        registroMod.setEmail(registroSel.getEmail());                
        registroMod.setSitioWeb(registroSel.getSitioWeb());                
        registroMod.setSitioNoticias(registroSel.getSitioNoticias());                        
        registroMod.setTextoPromociones(registroSel.getTextoPromociones());       
        registroMod.setEstado(registroSel.getEstado());     
        registroMod.setMpPublicKey(registroSel.getMpPublicKey());
        registroMod.setMpAccessToken(registroSel.getMpAccessToken());
        registroMod.setLogo(registroSel.getLogo());     
        registroMod.setPorcViajesSimples(registroSel.getPorcViajesSimples());
        registroMod.setPorcViajesDobles(registroSel.getPorcViajesDobles());
        registroMod.setPagoCuentaIva(registroSel.getPagoCuentaIva());
        registroMod.setPorcIngBrutos(registroSel.getPorcIngBrutos());
        registroMod.setCostoFleteEntrega(registroSel.getCostoFleteEntrega());
        registroMod.setPorcIvaVentas(registroSel.getPorcIvaVentas());
        registroMod.setEmailSistemas(registroSel.getEmailSistemas());
        registroMod.setClaveEmail(registroSel.getClaveEmail());
        registroMod.setHostSmtp(registroSel.getHostSmtp());
        registroMod.setPuertoEmail(registroSel.getPuertoEmail());
        registroMod.setStartTls(registroSel.getStartTls());
        registroMod.setAuth(registroSel.getAuth());
        registroMod.setCcCostoTropa(registroSel.getCcCostoTropa());
        registroMod.setCcIngBrutos(registroSel.getCcIngBrutos());
        registroMod.setCcPagoVep(registroSel.getCcPagoVep());
        registroMod.setCcVentaCarne(registroSel.getCcVentaCarne());
        registroMod.setCcCostoMercVendida(registroSel.getCcCostoMercVendida());
        registroMod.setCcTropasCamara(registroSel.getCcTropasCamara());
        
        this.modo="M";
        
        if (registroSel.getLogo()!= null){
            InputStream myInputStream = new ByteArrayInputStream(getRegistroMod().getLogo());
            setFoto(new DefaultStreamedContent( myInputStream, "image/jpg"));
        }
        
    }

    public void graba(){
        FacesMessage msg;
        Session session;
        session=HibernateUtil.getSessionFactory().openSession();
        Empresa u=this.getRegistroMod();
        try{
            session.beginTransaction();
            session.saveOrUpdate(u);
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            session.getTransaction().rollback();
            msg = new FacesMessage("Error: " + e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            this.setMensaje(e.getMessage());
            return;
        }
        finally {
            session.close();
        }
        getListaDatos();
        msg = new FacesMessage("Actualización exitosa!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return;
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        archivoSubido = event.getFile();
        System.out.println("Archivo:" + archivoSubido.getFileName());
        String contentType = archivoSubido.getContentType();
        byte[] contents = archivoSubido.getContents(); // Or getInputStream()
        this.getRegistroMod().setLogo(contents);
        try {
            this.setFoto(new DefaultStreamedContent(archivoSubido.getInputstream(),"image/jpg"));
            System.out.println("Relaciona foto");
        } catch (IOException ex) {
            Logger.getLogger(EmpresaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesMessage msg = new FacesMessage("Successful", archivoSubido.getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public StreamedContent getFileToDisplay() throws IOException {
    if(archivoSubido != null) {
        return new DefaultStreamedContent(archivoSubido.getInputstream(), "image/jpg");
    }
    else {
        return null;
    }
    }
    
    public StreamedContent getLogoToDisplay() throws IOException {
        if (getRegistroSel().getLogo()!=null){
            InputStream myInputStream = new ByteArrayInputStream(getRegistroSel().getLogo());
            return new DefaultStreamedContent( myInputStream, "image/jpg");
        }
        else
            return new DefaultStreamedContent();
    }
    
    
}
