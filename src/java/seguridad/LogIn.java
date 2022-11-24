/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seguridad;

import controllers.HibernateUtil;
import entidades.Empresa;
import general.BeanBase;
import general.StringMD;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author rafael
 */
public class LogIn extends BeanBase implements Serializable {
     private int idUsuario;
     private String nombreUsuario;
     private String claveUsuario;
     private String claveUsuario_reing;
     private String nombreUsuarioCompleto;
     private String estado;
     private String observaciones;
     private int idEmpresa;
     private String nombreEmpresa;
     private String nombrePais;
     private String moneda;
     private String simboloMoneda;
     private boolean conectado;
     
     private String nombreUsuarioEnc; //Nombre de usuarioe encriptado para cambiar la clave

     JAXBContext jc;
     InputStream xml;
     

    public LogIn() {
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuarioCompleto() {
        return nombreUsuarioCompleto;
    }

    public void setNombreUsuarioCompleto(String nombreUsuarioCompleto) {
        this.nombreUsuarioCompleto = nombreUsuarioCompleto;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getSimboloMoneda() {
        return simboloMoneda;
    }

    public void setSimboloMoneda(String simboloMoneda) {
        this.simboloMoneda = simboloMoneda;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getClaveUsuario_reing() {
        return claveUsuario_reing;
    }

    public void setClaveUsuario_reing(String claveUsuario_reing) {
        this.claveUsuario_reing = claveUsuario_reing;
    }

    public String getNombreUsuarioEnc() {
        return nombreUsuarioEnc;
    }

    public void setNombreUsuarioEnc(String nombreUsuarioEnc) {
        this.nombreUsuarioEnc = nombreUsuarioEnc;
    }

    
    //Iniciar sesión de usuario administración
    public String iniciarSesion() {
        FacesMessage msg;
        
        //Valido permiso para la transacción        
        try {
            if (!validaPermiso(getUsuarioConectado().getNombreUsuario(),ResourceBundle.getBundle("general/Permisos").getString("IngresarModulo"))){
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Transacción no autorizada","Estancia");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
                
        } catch (UnsupportedEncodingException ex) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getLocalizedMessage(),"Tropas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        };
        
        //Consumo el servicio web de LogIn de usuario administrativo
        String uri = String.format(this.getURI_BACKEND() + "/seguridad/validaLoginUsuarioAdmin?nombreUsuario=%1s&clave=%1s&tipoUsuario=A&tipoRespuesta=XML",nombreUsuario,claveUsuario);
        System.out.println("URI:" + uri);
        String ls_resultado=null;
        URL url;
         try {
            url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");
             
            if (connection.getResponseCode() != 200) {
                    throw new RuntimeException("Error: HTTP error code : " + connection.getResponseCode());
            }else
            {
                jc = JAXBContext.newInstance(UsuarioAdmin.class);
                xml = connection.getInputStream();
                UsuarioAdmin usuario = new UsuarioAdmin();
                usuario = (UsuarioAdmin) jc.createUnmarshaller().unmarshal(xml);
                System.out.println("Usuario:" +  usuario.getObservaciones());

                if (usuario.getEstado().equals("HABILITADO")){
                    
                    //Obtengo datos de la empresa
                    Session session;
                    session= HibernateUtil.getSessionFactory().openSession();
                    org.hibernate.Transaction tx =  session.beginTransaction();
                    Query q=session.createQuery("from Empresa a");
                    Iterator i=q.list().iterator();
                    Empresa p=null;
                    if (i.hasNext()){
                        p=(Empresa) i.next(); 
                    }
                    
                    Hibernate.initialize(p.getProvincia());
                    Hibernate.initialize(p.getPais());
                    Hibernate.initialize(p.getPais().getMoneda());

                    session.getTransaction().commit();    
                    session.close();
                    this.setIdEmpresa(p.getId());
                    this.setNombreEmpresa(p.getNombreFantasia());
                    this.setNombrePais(p.getPais().getNombre());
                    this.setMoneda(p.getPais().getMoneda().getNombre());
                    this.setSimboloMoneda(p.getPais().getMoneda().getSimbolo());

                    this.setIdUsuario(usuario.getId());
                    this.setNombreUsuario(nombreUsuario);
                    this.setNombreUsuarioCompleto(usuario.getNombreCompletoUsuario());                            
                    this.setEstado(usuario.getEstado());
                    this.setConectado(true);
                    FacesContext context=FacesContext.getCurrentInstance();
                    context.getExternalContext().getSessionMap().put("login",this);
                    ls_resultado="Principal";
                }
                else{
                    this.setConectado(false);
                    this.setObservaciones(usuario.getObservaciones());
                }

            }
            
             
         } catch (MalformedURLException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
         } catch (JAXBException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
         }
         
         return ls_resultado;
        
        
    }
    
    //Método para que el usuario cierre la sesi�n
    public void cerrarSesion() throws Exception {
            try {
             //Obtengo la sesion
             FacesContext context=FacesContext.getCurrentInstance();
             HttpSession session=(HttpSession)context.getExternalContext().getSession(false);
             
             this.setConectado(false);
             //Invalido la sesion
             session.invalidate();
             
             FacesContext.getCurrentInstance().getExternalContext().redirect("/estancia/LogIn.xhtml");
             return;
             //return("/LogIn");
            }
             catch (Exception e){
              //return("error");   
            }
            
        
    }
    
    //Solicita cambio de clave para usuario
    public String solicitarCambioClave() {
        //Consumo el servicio web de LogIn de administrador
        String uri = String.format(this.getURI_BACKEND() + "/seguridad/solicitaCambioClaveAdmin?nombreUsuario=%1s&tipoRespuesta=XML",nombreUsuario);
        System.out.println("URI:" + uri);
        String ls_resultado=null;
        URL url;
         try {
            url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");
             
            if (connection.getResponseCode() != 200) {
                    throw new RuntimeException("Error: HTTP error code : " + connection.getResponseCode());
            }else
            {
                jc = JAXBContext.newInstance(UsuarioAdmin.class);
                xml = connection.getInputStream();
                UsuarioAdmin usuario = new UsuarioAdmin();
                usuario = (UsuarioAdmin) jc.createUnmarshaller().unmarshal(xml);
                System.out.println("Usuario:" +  usuario.getObservaciones());

                if (usuario.getEstado().equals("Ok")){
                    ls_resultado="CambioClaveSolicitado";
                }
                else
                {
                    FacesMessage msg = new FacesMessage("No existe ese nombre de usuario!");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
            
         } catch (MalformedURLException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
         } catch (JAXBException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
         }
         return ls_resultado;
    }
    
    //Cambia clave de usuario administrativo
    public String cambiaClave() throws Exception {
        String ls_resul="";
        if (this.getClaveUsuario() != null && this.getClaveUsuario_reing() != null ){
            if (!this.getClaveUsuario().equals(this.getClaveUsuario_reing())){
                //Claves no coinciden
                setObservaciones("Claves no coinciden");
                return null;
            }
        }
        
        String uri = String.format(this.getURI_BACKEND()+ "/seguridad/cambiaClaveAdmin?nombreUsuario=%1s&claveUsuarioNueva=%1s&tipoRespuesta=%1s",                      
                    this.getNombreUsuarioEnc(),this.getClaveUsuario(),"XML");
        UsuarioAdmin respuesta;
        System.err.println("WebService: " + uri);
        try {        
            URL url = new URL(uri);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");            
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }
            
            JAXBContext jc = JAXBContext.newInstance(UsuarioAdmin.class);
            InputStream xml = connection.getInputStream();
            respuesta = (UsuarioAdmin) jc.createUnmarshaller().unmarshal(xml);     

        } catch (MalformedURLException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (JAXBException ex) {
            return null;
        }
           
        if (respuesta.getEstado().equals("MODIFICADO")){
            System.out.println("Todo oK");
            return "ClaveCambiada";
            }
        else
        {
            return null;
        }

    }
}
