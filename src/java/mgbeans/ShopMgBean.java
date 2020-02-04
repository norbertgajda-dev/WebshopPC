package mgbeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;

import javax.faces.context.FacesContext;
import org.hibernate.Session;
import pojos.Customers;
import pojos.Orderdetials;
import pojos.Orders;
import pojos.Productlines;
import pojos.Products;

@ManagedBean
@SessionScoped
public class ShopMgBean {

    //auth-reg
    private String email;
    private String pass;
    private Customers logInCustomer;
    private Customers newCustomer;

    //list products
    private List<Products> products;
    private List<Productlines> productLines;
    private List<Orders> orders;
    private List<Orderdetials> orderDetials;

    //filter
    private List<String> filterOptions;
    Map<String, Object> sessionMap;
    

    public ShopMgBean() {
        logInCustomer = null;
        newCustomer = new Customers();
        filterOptions = new ArrayList<>();

        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        products = session.createQuery("FROM Products").list();
        productLines = session.createQuery("FROM Productlines").list();
        session.close();
    }

    public Map<String, Customers> mapCustomers() {
        List<Customers> customers = new ArrayList<>();
        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        customers = session.createQuery("FROM Customers").list();
        session.close();

        Map<String, Customers> customersMap = new HashMap<>();
        for (Customers c : customers) {
            customersMap.put(c.getEmail(), c);
        }
        return customersMap;
    }

    public void authuser() {
        
        Map<String, Customers> customersMap = mapCustomers();
        if (customersMap.get(email) != null && customersMap.get(email).getPass().equals(pass)) {
            logInCustomer = customersMap.get(email);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            sessionMap = externalContext.getSessionMap();
            sessionMap.put("loggedIn", logInCustomer);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username or password is incorrect!"));
        }
    }

    public void logOut() {
        logInCustomer = null;
    }

    public String register() {
        Map<String, Customers> customersMap = mapCustomers();

        if (!customersMap.containsKey(newCustomer.getEmail())
                && !newCustomer.getName().equals("")
                && !newCustomer.getEmail().equals("")
                && !newCustomer.getPass().equals("")
                && !newCustomer.getPhone().equals("")
                && !newCustomer.getCity().equals("")
                && !newCustomer.getAddress().equals("")) {

            Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(newCustomer);
            session.getTransaction().commit();
            session.close();
            newCustomer = new Customers();
            return "regSuccess";
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please fill all fields!"));
        return null;
    }

    public void filterProducts() {

        if (!filterOptions.contains("null")) {
            Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
            productLines = session.createQuery("FROM Productlines").list();
            session.close();

            products.clear();
            for (String foption : filterOptions) {
                for (Productlines pl : productLines) {
                    if (pl.getProductlinesId().equals(foption)) {
                        products.addAll(pl.getProductses());
                    }
                }
            }
        } else {
            Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
            products = session.createQuery("FROM Products").list();
            session.close();
        }
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public List<Orderdetials> getOrderDetials() {
        return orderDetials;
    }

    public void setOrderDetials(List<Orderdetials> orderDetials) {
        this.orderDetials = orderDetials;
    }

    public Customers getLogInCustomer() {
        return logInCustomer;
    }

    public void setLogInCustomer(Customers logInCustomer) {
        this.logInCustomer = logInCustomer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Customers getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customers newCustomer) {
        this.newCustomer = newCustomer;
    }

    public List<Productlines> getProductLines() {
        return productLines;
    }

    public void setProductLines(List<Productlines> productLines) {
        this.productLines = productLines;
    }

    public List<String> getFilterOptions() {
        return filterOptions;
    }

    public void setFilterOptions(List<String> filterOptions) {
        this.filterOptions = filterOptions;
    }

}
