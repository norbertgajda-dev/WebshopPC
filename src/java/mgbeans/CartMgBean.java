package mgbeans;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import pojos.Customers;
import pojos.Orderdetials;
import pojos.Orders;
import pojos.Products;

@ManagedBean
@SessionScoped
public class CartMgBean {

    private List<Orderdetials> orderedItems;
    private int itemsInCart;
    private int itemsPricesInCart;

    public CartMgBean() {
        orderedItems = new ArrayList<>();
    }

    public void addToCart(Products item) {
        boolean newItem = true;

        for (Orderdetials orderedItem : orderedItems) {
            if (orderedItem.getProducts().equals(item)) {
                newItem = false;
                orderedItem.setOrderQty(orderedItem.getOrderQty() + 1);
            }
        }
        if (newItem) {
            orderedItems.add(new Orderdetials(null, item, 1));
        }
        sumOrderItems();
    }

    public void sumOrderItems() {
        itemsInCart = 0;
        itemsPricesInCart = 0;
        for (Orderdetials orderedItem : orderedItems) {
            itemsInCart += orderedItem.getOrderQty();
            itemsPricesInCart += orderedItem.getOrderQty() * orderedItem.getProducts().getPrice();
        }
    }

    public void increaseQty(Orderdetials item) {
        item.setOrderQty(item.getOrderQty() + 1);
        sumOrderItems();
    }

    public void decreaseQty(Orderdetials item) {
        if (item.getOrderQty() == 1) {
            orderedItems.remove(item);
        } else {
            item.setOrderQty(item.getOrderQty() - 1);
        }
        sumOrderItems();
    }

    public String saveOrder() {
        Customers customer = (Customers) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedIn");
        Orders order = new Orders(customer, java.util.Calendar.getInstance().getTime(), true, "-");

        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.save(order);
        for (Orderdetials item : orderedItems) {
            item.setOrders(order);
            session.save(item);        
        }
        session.getTransaction().commit();
        session.close();

        orderedItems.clear();
        sumOrderItems();
                
        return "orderSuccess";
    }

    public void deleteFromCart(Orderdetials orderedItem) {
        itemsInCart -= orderedItem.getOrderQty();
        itemsPricesInCart -= orderedItem.getOrderQty() * orderedItem.getProducts().getPrice();
        orderedItems.remove(orderedItem);
    }

    public int getItemsPricesInCart() {
        return itemsPricesInCart;
    }

    public void setItemsPricesInCart(int itemsPricesInCart) {
        this.itemsPricesInCart = itemsPricesInCart;
    }

    public int getItemsInCart() {
        return itemsInCart;
    }

    public void setItemsInCart(int itemsInCart) {
        this.itemsInCart = itemsInCart;
    }

    public List<Orderdetials> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<Orderdetials> orderedItems) {
        this.orderedItems = orderedItems;
    }

}
