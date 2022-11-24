/*
 * BeanBase.java
 *
 * Created on 10 de noviembre de 2005, 10:00
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package general;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import controllers.HibernateUtil;
import database.*;
import entidades.Empresa;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
//import javax.faces.context.*;
import javax.faces.model.SelectItem;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.hibernate.Query;
import org.hibernate.Session;
import seguridad.LogIn;


/**
 *
 * @author rafaelg
 */

//Bean b�sico con m�todos para la conexi�n a las bases de datos

public class BeanBase {
    private String tipo_exportacion;
    public static final String  URI_BACKEND=System.getProperty("URI_ESTANCIA_BACKEND");
    public static final String  URI_SERVICIO_ACTIVACION=System.getProperty("URI_SERVICIO_ACTIVACION"); //para activar usuario
    public static final String  URI_RESETEAR_CLAVE=System.getProperty("URI_ESTANCIA_RESETEAR_CLAVE"); //para resetear clave
    
    //public static final String  CARPETA_JASPERS=System.getProperty("CARPETA_JASPERS"); //para desarrollo
    
    public String getURI_BACKEND() {
        return URI_BACKEND;
    }

    public static String getURI_SERVICIO_ACTIVACION() {
        return URI_SERVICIO_ACTIVACION;
    }

    public static String getURI_RESETEAR_CLAVE() {
        return URI_RESETEAR_CLAVE;
    }

    
    
    private static final SelectItem[] TIPOS_ARCHIVOS = {new SelectItem("xls", "XLS"),
                                                        new SelectItem("pdf","PDF"),
                                                        new SelectItem("csv","CSV")};

    /** Creates a new instance of BeanBase */
     public BeanBase() {

    }

    public SelectItem[] getTipos() {
        return TIPOS_ARCHIVOS;
    }

    public String getTipo_exportacion() {
        return tipo_exportacion;
    }

    public void setTipo_exportacion(String tipo_exportacion) {
        this.tipo_exportacion = tipo_exportacion;
    }

    
    public static SelectItem[] getTIPOS_ARCHIVOS() {
        return TIPOS_ARCHIVOS;
    }
    
        
    //Obtiene datos del usuario conectado
    public LogIn getUsuarioConectado(){
       FacesContext context=FacesContext.getCurrentInstance();
       LogIn login=(LogIn) context.getExternalContext().getSessionMap().get("login");
       return login;
    }
    
    //Obtiene los datos de la empresa
    public Empresa cargaEmpresa(){
        Empresa empresa=null;
        Session session=HibernateUtil.getSessionFactory().openSession();
        try{
            session.beginTransaction();
            Query q=session.createQuery("from Empresa a where a.id = :idEmpresa");
            q.setParameter("idEmpresa",1);
            List<Empresa> lista=(List<Empresa>)q.list();
            if (lista.size()==1)
                empresa=lista.get(0);
            else
                empresa=null;
            session.getTransaction().commit();
            session.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return empresa;
    }
    
    public void preProcessPDF(Object document){
        if (this.getTipo_exportacion().compareTo("pdf")==0 )
        {
            Document doc= (Document) document;
            doc.setPageSize(PageSize.A4.rotate());
        }
    }
    
    //Obtiene datos del usuario
    public String obtenerNombreUsuario(int idUsuario) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_id;
        String ls_nombreUsuario,ls_clave,ls_apellido,ls_nombre,ls_tipo,ls_estado;
        //Conectamos a la base
        Conector conector = new Conector();  
        Connection conexion = conector.connect("administracion");
        
        try {

               s=conexion.prepareCall("{call sp_get_def_usuario_admin_id ( ? )}",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
               s.setInt(1,idUsuario);
               
               r=s.executeQuery();
               if (r.next()){
                   li_id=r.getInt("id");
                   ls_nombreUsuario=r.getString("nombreUsuario");
                   ls_clave=r.getString("clave");
                   
                   ls_apellido=r.getString("apellido");
                   if (ls_apellido==null)
                       ls_apellido="";
                   
                   ls_nombre=r.getString("nombre");
                   if (ls_nombre==null)
                       ls_nombre="";
                   
                   ls_estado=r.getString("estado");
                   ls_tipo=r.getString("tipo");
                   
                   return ls_apellido + " " + ls_nombre;
               }
               else
                   return null;
               
        } catch (SQLException e){
            e.getMessage();
            return null;            
        } finally {
             if (conexion != null) conexion.close();   
             if (r != null) r.close();   
        }
    
    }
    
    //Obtiene nombre de forma de pago
    public String obtenerFormaPago(int idFormaPago) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_id;
        String ls_nombre;
        //Conectamos a la base
        Conector conector = new Conector();  
        Connection conexion = conector.connect("contabilidad");
        
        try {

               s=conexion.prepareCall("{call sp_get_forma_pago ( ? )}",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
               s.setInt(1,idFormaPago);
               
               r=s.executeQuery();
               if (r.next()){
                   ls_nombre=r.getString("nombre");
                   return ls_nombre;
               }
               else
                   return null;
               
        } catch (SQLException e){
            e.getMessage();
            return null;            
        } finally {
             if (conexion != null) conexion.close();   
             if (r != null) r.close();   
        }
    
    }
    
    ////////////////////////////////////////////////////////////
    /////               Seguridad                ///////////////
    ////////////////////////////////////////////////////////////

    //Consume Web Service para grabar auditoría de la transaccción
    public boolean grabaAuditoria(int idUsuario,String codigoPermiso,String observaciones,String aplicacion,String nombreEquipo) throws UnsupportedEncodingException{
        
        observaciones = URLEncoder.encode(observaciones, "UTF-8");        
        aplicacion = URLEncoder.encode(aplicacion, "UTF-8");        
        nombreEquipo = URLEncoder.encode(nombreEquipo, "UTF-8");                
        
        JAXBContext jc;
        InputStream xml;
        String uri = String.format(getURI_BACKEND() + "/seguridad/grabaAuditoria?idUsuarioTrans=%1s&codigoPermiso=%1s&observacionesTrans=%1s&aplicacionTrans=%1s&nombreEquipoTrans=%1s&tipoRespuesta=XML",idUsuario,codigoPermiso,observaciones,aplicacion,nombreEquipo);
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
                jc = JAXBContext.newInstance(Auditoria.class);
                xml = connection.getInputStream();
                Auditoria auditoria = new Auditoria();
                auditoria = (Auditoria) jc.createUnmarshaller().unmarshal(xml);
                System.out.println("Usuario:" +  auditoria.getObservaciones());

                if (auditoria.getResultado()==1)
                    return true;
                else
                    return false;                            
                    
            }
            
         } catch (MalformedURLException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         } catch (IOException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         } catch (JAXBException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         }
        
    }    
    
    //Consume Web Service para chequear el permiso del usuario
    public boolean validaPermiso(String nombreUsuario,String codigoPermiso) throws UnsupportedEncodingException{
        nombreUsuario = URLEncoder.encode(nombreUsuario, "UTF-8");        
        
        JAXBContext jc;
        InputStream xml;
        String uri = String.format(getURI_BACKEND() + "/seguridad/validaPermiso?usuarioTrans=%1s&codigoPermiso=%1s&tipoRespuesta=XML",nombreUsuario,codigoPermiso);
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
                jc = JAXBContext.newInstance(Auditoria.class);
                xml = connection.getInputStream();
                Auditoria auditoria = new Auditoria();
                auditoria = (Auditoria) jc.createUnmarshaller().unmarshal(xml);
                if (auditoria.getResultado()==1)
                    return true;
                else
                    return false;                            
                    
            }
            
         } catch (MalformedURLException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         } catch (IOException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         } catch (JAXBException ex) {
             Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         }
    }

    //Obtiene el nombre de la PC
    public String obtieneNombreEquipo() {
        java.net.InetAddress localMachine; 
        try {
            localMachine = java.net.InetAddress.getLocalHost();
            System.out.println("Hostname of local machine: " + localMachine.getHostName());            
            return localMachine.getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(BeanBase.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }


    
    ////////////////////////////////////////////////////////////
    /////               Contabilidad             ///////////////
    ////////////////////////////////////////////////////////////
    
    //Obtiene id del ejercicio contable correspondiente de acuerdo a la fecha
    public int obtieneEjercicioContable(int idEmpresa,Date fec_mov) {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id_ejer;
        //Conectamos a la base
        Conector conector = new Conector();  
        Connection conexion = conector.connect("contabilidad");
        
        try {

               s=conexion.prepareCall("{call sp_get_ejercicio_fecha ( ? , ? , ? )}",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
               s.setInt(1,idEmpresa);
               s.setDate(2,new java.sql.Date(fec_mov.getTime()));
               s.registerOutParameter(3,java.sql.Types.INTEGER);
               r=s.executeQuery();
               
               li_id_ejer=s.getInt(3);
               
        } catch (SQLException e){
            e.getMessage();
            li_id_ejer=0;
        } finally {
             try {
                 if (conexion != null)  conexion.close();
                 if (r != null) r.close();   
                 if (s != null) s.close(); 
             } catch (SQLException ex) {
                 Logger.getLogger(BeanBase.class.getName()).log(Level.SEVERE, null, ex);
                 li_id_ejer=0;
             }   
        }
        return li_id_ejer;
    }
    
    //Consume Web Service que realiza el asiento contable
    public AsientoRealizado realizaAsiento(Asiento asiento) {
            String urlString = String.format(this.getURI_BACKEND()+ "/contabilidad/realizarAsiento");                      
            BufferedReader br=null;
            String resultadoJSon="";
            System.out.println(urlString);
            String resultado="";
            AsientoRealizado asientoRealizado = new AsientoRealizado();
            
            try {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                String JSONString=gson.toJson(asiento);
                System.out.println("Asiento: " + JSONString);
                
                URL url = new URL(urlString);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setReadTimeout(15000);
                httpConnection.setConnectTimeout(15000);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json;  charset=utf-8");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setInstanceFollowRedirects(true);
                httpConnection.setDoOutput(true);
                httpConnection.connect();
            
                DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
                wr.writeBytes(JSONString);
                wr.flush();
                wr.close();
                
                int responseCode = httpConnection.getResponseCode();
                System.out.println("ResponseCode=" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(),"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    resultadoJSon = sb.toString();
                }
                else
                    resultadoJSon=null;

                resultado="Ok";
                
                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                asientoRealizado = gson.fromJson(resultadoJSon, AsientoRealizado.class);
                System.out.println("Resultado Ws:" + resultadoJSon);                
                
            }catch(UnsupportedEncodingException e){
                System.out.println(resultadoJSon);
                asientoRealizado.setIdAsiento(0);
                asientoRealizado.setResultado("Error");
                asientoRealizado.setObservaciones(e.getLocalizedMessage());
            }catch(IOException e){
                System.out.println(resultadoJSon);
                asientoRealizado.setIdAsiento(0);
                asientoRealizado.setResultado("Error");
                asientoRealizado.setObservaciones(e.getLocalizedMessage());
            }     
            finally{
                if (br !=null){
                    try{
                        br.close();
                    }
                    catch (IOException e){
                        System.out.println(resultadoJSon);
                    }
                }
                System.out.println("Resultado Ws:" + resultadoJSon);
            }
            
            return asientoRealizado;
    }
    
    public AsientoRealizado eliminaAsiento(Asiento asiento) {
            String urlString = String.format(this.getURI_BACKEND()+ "/contabilidad/eliminarAsiento");                      
            BufferedReader br=null;
            String resultadoJSon="";
            System.out.println(urlString);
            String resultado="";
            AsientoRealizado asientoRealizado = new AsientoRealizado();
            
            try {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                String JSONString=gson.toJson(asiento);
                System.out.println("Asiento: " + JSONString);
                
                URL url = new URL(urlString);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setReadTimeout(15000);
                httpConnection.setConnectTimeout(15000);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json;  charset=utf-8");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setInstanceFollowRedirects(true);
                httpConnection.setDoOutput(true);
                httpConnection.connect();
            
                DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
                wr.writeBytes(JSONString);
                wr.flush();
                wr.close();
                
                int responseCode = httpConnection.getResponseCode();
                System.out.println("ResponseCode=" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(),"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    resultadoJSon = sb.toString();
                }
                else
                    resultadoJSon=null;

                resultado="Ok";
                
                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                asientoRealizado = gson.fromJson(resultadoJSon, AsientoRealizado.class);
                System.out.println("Resultado Ws:" + resultadoJSon);                
                
            }catch(UnsupportedEncodingException e){
                System.out.println(resultadoJSon);
                asientoRealizado.setIdAsiento(0);
                asientoRealizado.setResultado("Error");
                asientoRealizado.setObservaciones(e.getLocalizedMessage());
            }catch(IOException e){
                System.out.println(resultadoJSon);
                asientoRealizado.setIdAsiento(0);
                asientoRealizado.setResultado("Error");
                asientoRealizado.setObservaciones(e.getLocalizedMessage());
            }     
            finally{
                if (br !=null){
                    try{
                        br.close();
                    }
                    catch (IOException e){
                        System.out.println(resultadoJSon);
                    }
                }
                System.out.println("Resultado Ws:" + resultadoJSon);
            }
            
            return asientoRealizado;
    }
    
}