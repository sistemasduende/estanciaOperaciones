/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import controllers.HibernateUtil;
import controllers.TropaStock;
import database.Conector;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.hibernate.Query;
import org.hibernate.Session;
import seguridad.LogIn;
import seguridad.UsuarioAdmin;

/**
 *
 * @author rafael
 */
@ManagedBean(name="listausuarios")
@SessionScoped

public class ListaUsuarios extends BeanBase {
    private List items=null;

    public ListaUsuarios() throws Exception{
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
        SelectItem item;
        List resultados=new ArrayList();
        int li_id;
        String ls_nombre_usuario;
        
        item=new SelectItem();
        item.setValue(0);
        item.setLabel("Seleccione...");
        resultados.add(item);

        try{
            //Conectamos a la base
            Conector conector = new Conector();  
            conexion = conector.connect("administracion");
            s=conexion.prepareCall("{call sp_get_lista_usuarios_admin ( )}");
            r=s.executeQuery();
            while (r.next()){
                li_id=r.getInt("id");
                ls_nombre_usuario=r.getString("nombreUsuario");
                item=new SelectItem();
                item.setValue(li_id);
                item.setLabel(ls_nombre_usuario);
                resultados.add(item);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally{
            if (conexion != null) conexion.close();   
            if (r != null) r.close();  
        }
        this.setItems(resultados);

    }
    
    
        
}
