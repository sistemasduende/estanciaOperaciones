/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import controllers.HibernateUtil;
import entidades.Unidad;
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
@ManagedBean(name="listaunidades")
@SessionScoped

public class ListaUnidades extends BeanBase {
    private List items=null;

    public ListaUnidades() throws Exception{
        getLista();
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
    
    public void getLista() throws Exception {
        //Obtengo la empresa del usuario conectado
        LogIn login=getUsuarioConectado();
        login.getIdEmpresa();
        Session session;
        
        SelectItem item;
        List resultados=new ArrayList();
        session= HibernateUtil.getSessionFactory().openSession();

        try{
            org.hibernate.Transaction tx =  session.beginTransaction();
            Query q=session.createQuery("from Unidad a order by a.nombre");
            Iterator i=q.list().iterator();
            
            while (i.hasNext()){
                   Unidad p=(Unidad) i.next(); 
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
