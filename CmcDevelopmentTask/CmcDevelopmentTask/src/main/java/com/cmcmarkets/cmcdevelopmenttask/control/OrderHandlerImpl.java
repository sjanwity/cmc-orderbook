
package com.cmcmarkets.cmcdevelopmenttask.control;

import com.cmcmarkets.cmcdevelopmenttask.model.Limit;
import com.cmcmarkets.cmcdevelopmenttask.model.Order;
import enums.Side;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 *
 * @author scja0
 */
public class OrderHandlerImpl implements OrderHandler {
    
    @Getter
    private final Map<String, Orderbook> orderbooks = new HashMap<>();
    private static OrderHandlerImpl INSTANCE = null;
    //TODO: cache of all recent orders

            
    private OrderHandlerImpl()
    {
        //Singleton
    }
    
    public static OrderHandlerImpl getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new OrderHandlerImpl();
  
        return INSTANCE;
    }
    
    
    @Override
    public void addOrder(Order order) {
        final String symbol = order.getSymbol();
        if (orderbooks.containsKey(symbol))
        {
            orderbooks.get(symbol).addOrder(order);
        } else
        {
            Orderbook orderbook = new Orderbook(symbol);
            orderbook.addOrder(order);
            orderbooks.put(symbol, orderbook);
            
        }
    }

    @Override
    public void modifyOrder(OrderModification orderModification) {
        Long orderId = orderModification.getOrderId();
        for (Orderbook orderbook : orderbooks.values())
        {
            Limit limit = orderbook.findOrder(orderId);
            
            if (limit != null) //if a limit is assigned means an order is found
            {
               Order order = limit.getOrder(orderId); 
               System.out.println("Found order from the " + orderbook.getSymbol() + " orderbook");
               System.out.println("Current order price is: " + order.getPrice() + " for quantity: " + order.getQuantity());
               Side side = order.getSide();
               String symbol = order.getSymbol();
               
               //remove and replace, as price maybe different so we may need a different level.
               System.out.println("Deleting order...");
               orderbook.deleteOrder(orderId);
               
               System.out.println("Creating new order...");
               Order newOrder = new Order(orderId, symbol, side, orderModification.getNewPrice(), orderModification.getNewQuantity());
               System.out.println("Adding new order...");
               orderbook.addOrder(newOrder);
            }
        }
        //if for loop completes without breaking, that means no order was deleted
        System.out.println("Order ID not found, nothing was changed");
    }

    @Override
    public void removeOrder(long orderId) {
        for (Orderbook orderbook : orderbooks.values())
        {
            if (orderbook.deleteOrder(orderId))
            {
               
               System.out.println("Order successfully deleted from the " + orderbook.getSymbol() + " orderbook");
               return; //immediately break as there shouldn't be more than 1 order with that ID
            }
        }
        //if for loop completes without breaking, that means no order was deleted
        System.out.println("Order ID not found, nothing was deleted");
        
             
    }

    @Override
    public double getCurrentPrice(String symbol, int quantity, Side side) {
         return 0;
    }
    

    
}
