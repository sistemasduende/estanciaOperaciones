/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import controllers.HibernateUtil;
import entidades.Empresa;
import entidades.Localidad;
import entidades.Provincia;
import java.io.Serializable;
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
@ManagedBean(name="listalocalidades")
@SessionScoped

public class ListaLocalidadesTodas extends BeanBase {
    private List items=null;

    public ListaLocalidadesTodas() throws Exception{

    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
    
    public void getLista() throws Exception {
        SelectItem item;
        List resultados=new ArrayList();
        
        Session session;
        session= HibernateUtil.getSessionFactory().openSession();

        try{
            org.hibernate.Transaction tx =  session.beginTransaction();
            Query q=session.createSQLQuery("select a.id,a.nombre as nombreLoc,b.nombre as nombreProv from localidad a, provincia b where a.idProvincia = b.id "
                    + "                 order by b.nombre,a.nombre");

            item=new SelectItem();
            item.setValue(0);
            item.setLabel("Seleccione...");
            resultados.add(item);
            
            List<Object[]> listaLoc=(List<Object[]>) q.list();
            
            for (Object[] datos : listaLoc) {
                   item=new SelectItem();
                   item.setValue(datos[0]);
                   item.setLabel((String) datos[1] + "-" + (String) datos[2] );
                   resultados.add(item);

            }
            session.getTransaction().commit();    
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            session.close();            
        }        
        this.setItems(resultados);
    }
    
    
        
}
