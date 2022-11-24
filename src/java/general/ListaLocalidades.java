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

public class ListaLocalidades extends BeanBase {
    private List items=null;

    public ListaLocalidades() throws Exception{

    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
    
    public void getLista(int idProvincia) throws Exception {
        //Obtengo la provincia
        Session session;
        session= HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Provincia o=(Provincia) session.get(Provincia.class,idProvincia);
        session.getTransaction().commit();
        session.close();
        
        SelectItem item;
        List resultados=new ArrayList();
        session= HibernateUtil.getSessionFactory().openSession();

        try{
            org.hibernate.Transaction tx =  session.beginTransaction();
            Query q=session.createQuery("from Localidad a where a.provincia = :provinciaSel");
            q.setParameter("provinciaSel",o);

            item=new SelectItem();
            item.setValue(0);
            item.setLabel("Seleccione...");
            resultados.add(item);
            
            Iterator i=q.list().iterator();
            while (i.hasNext()){
                   Localidad p=(Localidad) i.next(); 
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
