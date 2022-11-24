/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import controllers.HibernateUtil;
import database.Conector;
import entidades.Producto;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.hibernate.Query;
import org.hibernate.Session;
import seguridad.LogIn;

/**
 *
 * @author rafael
 */
@ManagedBean(name="listaformaspago")
@SessionScoped

public class ListaFormasPago extends BeanBase {
    private List items=null;

    public ListaFormasPago() throws Exception{
        getLista();
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
    
    public void getLista() throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        Connection conexion=null;
        int li_id;
        String ls_nombre;
        
        SelectItem item;
        List resultados=new ArrayList();

        try{
            
            Conector conector = new Conector();  
            conexion = conector.connect("contabilidad");
            s=conexion.prepareCall("{call sp_get_formas_pago ( )}");
            r=s.executeQuery();
            
            while (r.next()){
                   li_id=r.getInt("id");
                   ls_nombre=r.getString("nombre");

                   item=new SelectItem();
                   item.setValue(li_id);
                   item.setLabel(ls_nombre);
                   resultados.add(item);
            }
            s.close();            
        } catch (SQLException e){
                    e.getMessage();
                    return;
        }
        finally{
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        this.setItems(resultados);
    }
    
    
        
}
