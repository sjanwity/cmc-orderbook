package com.cmcmarkets.cmcdevelopmenttask.control;

import com.cmcmarkets.cmcdevelopmenttask.model.Order;
import enums.Side;

public interface OrderHandler {
    void addOrder(Order order);

    void modifyOrder(OrderModification orderModification);

    void removeOrder(long orderId);

    double getCurrentPrice(String symbol, int quantity, Side side);

    /**
     * Please implement this method so we are able to create an instance
     * of your OrderHandler implementation.
     */
    static OrderHandler createInstance() {
        return OrderHandlerImpl.getInstance();
    }
}
