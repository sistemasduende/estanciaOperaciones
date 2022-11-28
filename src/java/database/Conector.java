package database;

import general.BeanBase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 *
 * @author francisco
 */
public class Conector {
    String dbUrl_Estancia="jdbc/estancia";
    String dbUrl_administracion="jdbc/administracion";
    String usuario = "dba";
    String password = "adminduende";
    
    Connection conexion; 
    String servidor;
    String puerto;

    public Conector() {
    }
   
    public Connection connect(String baseDatos){
      String ls_data_source="";  
      switch (baseDatos)
      {
          case "administracion":
              ls_data_source="jdbc/administracion";
                              
              break;
          case "estancia":
              ls_data_source="jdbc/estancia"; 
                              
              break;
          case "contabilidad":
              ls_data_source="jdbc/contabilidad";
                              
              break;
              
          default: 
              ls_data_source=null;
      }
          
      Connection con_base = null;
      try{
         InitialContext ic= new InitialContext(); 
         DataSource ds = (DataSource) ic.lookup(ls_data_source);
         con_base=ds.getConnection();
         
      }catch (Exception e) {
         e.getMessage();
      }
      return con_base;
      
    }
    
    
    

 
    }
 
 
