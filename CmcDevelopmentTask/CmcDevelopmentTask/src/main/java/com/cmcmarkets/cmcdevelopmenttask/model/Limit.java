
package com.cmcmarkets.cmcdevelopmenttask.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author scja0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Limit {
    
    private Map<Long, Order> orders;
    
    public boolean isEmpty()
    {
        return orders.isEmpty();
    }
    
    public int getOrderCount()
    {
        return orders.size();
    }
    
    public int getOrderQuantity()
    {
        return orders.values().stream().mapToInt(Order::getQuantity).sum();
    }
    
    public boolean addOrder(Order order)
    {
        orders.put(order.getOrderId(),order);
        return true;
    }
    
    public boolean removeOrder(long orderId)
    {
        if (containsOrder(orderId) == false)
            return false;
        else orders.remove(orderId);
        return true;
    }
    
    public boolean containsOrder(long orderId)
    {
        return orders.get(orderId) != null;
    }
    
    public Order getOrder(long orderId)
    {
        return orders.get(orderId);
    }
    
}
