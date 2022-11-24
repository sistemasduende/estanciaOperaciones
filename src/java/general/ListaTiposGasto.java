/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import controllers.HibernateUtil;
import entidades.TipoGasto;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.model.SelectItem;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author rafael
 */

public class ListaTiposGasto extends BeanBase {
    private List items=null;

    public ListaTiposGasto() throws Exception{
        getLista();
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
    
    public void getLista() throws Exception {
        Session session;
        SelectItem item;
        List resultados=new ArrayList();
        session= HibernateUtil.getSessionFactory().openSession();

//        item=new SelectItem();
//        item.setValue(0);
//        item.setLabel("Seleccione...");
//        resultados.add(item);

        try{
            org.hibernate.Transaction tx =  session.beginTransaction();
            Query q=session.createQuery("from TipoGasto a order by a.nombre");
            Iterator i=q.list().iterator();
            
            while (i.hasNext()){
                   TipoGasto p=(TipoGasto) i.next(); 
                   item=new SelectItem();
                   item.setValue(p.getId());
                   item.setLabel(p.getNombre());

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
