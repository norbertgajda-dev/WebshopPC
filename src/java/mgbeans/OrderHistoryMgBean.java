package mgbeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Query;
import org.hibernate.Session;
import pojos.Customers;
import pojos.Orderdetials;
import pojos.Orders;
import pojos.Products;

/**
 *
 * @author Noru
 */
@ManagedBean
@SessionScoped
public class OrderHistoryMgBean {

    private Customers customer;
    private List<Orders> orders;
    private Map<Orders, List<Products>> mapProducts;
    private List<Products> items;

    
    public OrderHistoryMgBean() {
        customer = (Customers) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedIn");  
        allOrders();
    }
    
    public String allOrders () {
        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Orders WHERE customers.customerId = :customerId");
        query.setParameter("customerId", customer.getCustomerId());
        orders = query.list();
        session.close();
        
        getProductsFromOrders();
        
        return "allOrder";
    }
    
    
    public void getProductsFromOrders() {
        items = new ArrayList<>();
        mapProducts = new HashMap<>();
        for (Orders order : orders) {
            for (Orderdetials od : order.getOrderdetialses()) {
                items.add(od.getProducts());
            }
            mapProducts.put(order, new ArrayList<>(items));
            items.clear();
        }
    }

    public Map<Orders, List<Products>> getMapProducts() {
        return mapProducts;
    }

    public void setMapProducts(Map<Orders, List<Products>> mapProducts) {
        this.mapProducts = mapProducts;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

}
