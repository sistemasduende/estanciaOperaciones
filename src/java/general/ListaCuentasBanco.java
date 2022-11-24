/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import controllers.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author rafael
 */
@ManagedBean(name="listacuentasbanco")
@SessionScoped

public class ListaCuentasBanco extends BeanBase {
    private List items=null;

    public ListaCuentasBanco() throws Exception{
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
        Session session;
        SelectItem item;
        List resultados=new ArrayList();
        session= HibernateUtil.getSessionFactory().openSession();

        try{
            org.hibernate.Transaction tx =  session.beginTransaction();
            Query q=session.createSQLQuery("select a.id,a.numeroCuenta as numeroCuenta,b.nombre as nombreBanco,a.tipoCuenta as tipoCuenta "
                    + "                     from cuenta_banco a,banco b where a.idBanco = b.id "
                    + "                 order by b.nombre,a.numeroCuenta");

            item=new SelectItem();
            item.setValue(0);
            item.setLabel("Seleccione...");
            resultados.add(item);

            String ls_tipoCuenta=null;
            List<Object[]> listaCuentas=(List<Object[]>) q.list();
            for (Object[] datos : listaCuentas) {
                   if ((datos[3]).equals('A'))
                       ls_tipoCuenta="Caja de Ahorro";
                   else
                       ls_tipoCuenta="Cuenta Corriente";
                   
                   item=new SelectItem();
                   item.setValue(datos[0]);
                   item.setLabel((String) datos[2] + " - " + ls_tipoCuenta + " - " + (String) datos[1] );
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
