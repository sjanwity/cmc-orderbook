/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cmcmarkets.cmcdevelopmenttask.control;

import com.cmcmarkets.cmcdevelopmenttask.model.Limit;
import com.cmcmarkets.cmcdevelopmenttask.model.Order;
import enums.Side;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 *
 * @author scja0
 */

public class Orderbook {
    
    @Getter
    private SortedMap<Integer, Limit> sellLimits;
    @Getter
    private SortedMap<Integer, Limit> buyLimits;
    @Getter
    private String symbol;
    @Getter
    private int sellQuantity; //helper for looking at ask book volume
    @Getter
    private int buyQuantity; //helper for looking at bid book volume
    
    public Orderbook(String symbol) {
        this.symbol = symbol;
        sellLimits = new TreeMap<>();
        buyLimits = new TreeMap<>(Collections.reverseOrder());
        sellQuantity = 0;
        buyQuantity = 0;
}
    public boolean addOrder(Order order)
    {
        if (order.getSide() == Side.BUY)
        {
            int basePrice = order.getPrice();
            if (buyLimits.containsKey(basePrice))
            {
                if (buyLimits.get(basePrice).addOrder(order) == true)
                System.out.println("Successfully added order to buyLimits @ " + basePrice);
                else
                    throw new IllegalStateException("Failed to add order to buyLimits");
            } else
            {
                Map<Long, Order> orders = new HashMap();
                orders.put(order.getOrderId(), order);
                
                Limit limit = new Limit(orders, order.getPrice());
                System.out.println("Successfully created new buyLimits Limit @ " + basePrice);
                buyLimits.put(basePrice, limit);
                System.out.println("Successfully added order to buyLimits @ " + basePrice);
            }
            buyQuantity = buyQuantity + order.getQuantity();
        } else if (order.getSide() == Side.SELL)
        {
            int basePrice = order.getPrice();
            if (sellLimits.containsKey(basePrice))
            {
                if (sellLimits.get(basePrice).addOrder(order) == true )
                System.out.println("Successfully added order to sellLimits @ " + basePrice);
                else
                    throw new IllegalStateException("Failed to add order to sellLimits");
            } else
            {
                Map<Long, Order> orders = new HashMap();
                orders.put(order.getOrderId(), order);
                Limit limit = new Limit(orders, basePrice);
                System.out.println("Successfully created new sellLimits Limit @ " + basePrice);
                sellLimits.put(basePrice, limit);
                System.out.println("Successfully added order to sellLimits @ " + basePrice);
            }
            sellQuantity = sellQuantity + order.getQuantity();
        } else return false; //something went wrong, side is null
        
        return true;
    }
    
    
    /**
     * Attempt to find an order, return true if successful, false if order not found. Does this by iterating through 
     * each of the Limits in sellLimits and buyLimits. Not the most elegant way, but should be reasonably quick as 
     * orders are stored as maps so lookup is quick.
     * @param orderId
     * @return the Limit object if order is found in that limit, null if not.
     */
    public Limit findOrder(long orderId)
    {
        for (Limit limit : sellLimits.values())
        {
            if (limit.containsOrder(orderId))
                return limit;
        }
        for (Limit limit : buyLimits.values())
        {
            if (limit.containsOrder(orderId))
                return limit;
        }
        //if both for loops complete execution without breaking, that means orderId is not in this book therefore return null.
        return null;
    }
   
    
    /**
     * Works the same way as find order, but seperate implementation as we want the method to remove the limit level from the SortedMap
     * if the limit is empty (no more orders exist at that price level)
     * @param orderId
     * @return true if an order was deleted, false if not
     */
    public boolean deleteOrder(long orderId)
    {
       for (Limit limit : sellLimits.values())
        {
            if (limit.containsOrder(orderId))
            {
                Order order = limit.getOrder(orderId);
                limit.removeOrder(orderId);
                sellQuantity = sellQuantity - order.getQuantity();
                if (limit.isEmpty())
                    sellLimits.remove(order.getPrice());
                return true;
            }
                
        }
        for (Limit limit : buyLimits.values())
        {
            if (limit.containsOrder(orderId))
            {
                Order order = limit.getOrder(orderId);
                limit.removeOrder(orderId);
                buyQuantity = buyQuantity - order.getQuantity();
                if (limit.isEmpty())
                    buyLimits.remove(order.getPrice());
                return true;
            }
        }
        //if both for loops complete execution without breaking, that means orderId is not in this book therefore return false.
        return false;
    }
    
/*    private class sellLimitComparator implements Comparator<Limit>{

        @Override
        public int compare(Limit o1, Limit o2) {
            return o1.getPrice() > o2.getPrice() ? 1 : -1;
        }
        
    }
    
    private class buyLimitComparator implements Comparator<Limit>{

        @Override
        public int compare(Limit o1, Limit o2) {
            return o1.getPrice() < o2.getPrice() ? 1 : -1;
        }
        
    }*/
    
}
