package com.cmcmarkets.cmcdevelopmenttask;

import com.cmcmarkets.cmcdevelopmenttask.control.OrderHandlerImpl;
import com.cmcmarkets.cmcdevelopmenttask.control.OrderModification;
import com.cmcmarkets.cmcdevelopmenttask.model.Order;
import enums.Side;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class ExampleData {
    /**
     * Submits a series of orders for MSFT.The resulting Order Book for MSFT
 is the one shown in Table 1 of the ReadMe document.
     *
     */
    static OrderHandlerImpl orderHandler;
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();    
    @BeforeClass
    public static void setUp() throws Exception {
        orderHandler = OrderHandlerImpl.getInstance();
        
    }
    @After
    public void cleanUp() throws Exception {
        systemOutRule.clearLog();
    }
         
    @Test
    public void buildExampleOrderBookFromReadMe() {
        orderHandler.addOrder(new Order(1L, "MSFT", Side.SELL, 19, 8));
        orderHandler.addOrder(new Order(2L, "MSFT", Side.SELL, 19, 4));
        orderHandler.addOrder(new Order(3L, "MSFT", Side.SELL, 21, 16));
        orderHandler.addOrder(new Order(4L, "MSFT", Side.SELL, 21, 1));
        orderHandler.addOrder(new Order(5L, "MSFT", Side.SELL, 22, 7));

        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        orderHandler.modifyOrder(new OrderModification(6L, 15, 10));

        orderHandler.addOrder(new Order(7L, "MSFT", Side.BUY, 15, 20));
        orderHandler.removeOrder(7L);

        orderHandler.addOrder(new Order(8L, "MSFT", Side.BUY, 10, 13));
        orderHandler.addOrder(new Order(9L, "MSFT", Side.BUY, 10, 13));
        System.out.println("Done");
    }
    @Test
    @Ignore
    public void addOrderTest(){
        orderHandler.addOrder(new Order(10L, "MSFT", Side.BUY, 19, 8));
        
        //Test if the correct message is displayed
        Assert.assertEquals("Successfully created new buyLimits Limit @ 19", systemOutRule.getLog().trim());
        systemOutRule.clearLog();
        orderHandler.addOrder(new Order(11L, "MSFT", Side.SELL, 20, 8));
        Assert.assertEquals("Successfully created new sellLimits Limit @ 20", systemOutRule.getLog().trim());
    }
    
    @Test
    @Ignore
    public void removeOrderTest(){
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(19).containsOrder(10L));
        orderHandler.removeOrder(10L);
        Assert.assertFalse(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(19).containsOrder(10L));
    }
}
