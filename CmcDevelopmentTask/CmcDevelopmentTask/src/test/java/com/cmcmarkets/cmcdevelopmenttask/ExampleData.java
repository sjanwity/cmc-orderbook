package com.cmcmarkets.cmcdevelopmenttask;

import com.cmcmarkets.cmcdevelopmenttask.control.OrderHandlerImpl;
import com.cmcmarkets.cmcdevelopmenttask.control.OrderModification;
import com.cmcmarkets.cmcdevelopmenttask.model.Order;
import enums.Side;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
        
        //Build list from readme
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
        System.out.println("Done adding from readme file");
        orderHandler.addOrder(new Order(11L, "MSFT", Side.SELL, 20, 8));
        orderHandler.addOrder(new Order(10L, "MSFT", Side.BUY, 19, 8));
        
    }
    @After
    public void cleanUp() throws Exception {
        systemOutRule.clearLog();
    }
         
    @Test
    public void testExampleOrderBookFromReadMe() {
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getSellLimits().get(19).containsOrder(2L));
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getSellLimits().get(19).containsOrder(1L));
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(15).containsOrder(6L));
        Assert.assertFalse(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(15).containsOrder(7L));
    }
    @Test
    public void addOrderTest(){
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(19).containsOrder(10L));
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getSellLimits().get(20).containsOrder(11L));
    }
    
    public void removeOrderTest(){
        orderHandler.addOrder(new Order(15L, "MSFT", Side.BUY, 19, 8));
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(19).containsOrder(15L));
        orderHandler.removeOrder(15L);
        Assert.assertFalse(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(19).containsOrder(15L));
    }
    @Test
    //When we remove the last price on a limit, the limit should get deleted too.
    public void removeOrderLastLimitTest(){
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(19).containsOrder(10L));
        orderHandler.removeOrder(10L);
        Assert.assertNull(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(19));
    }
    
    @Test
    public void modifyOrderTest(){
        orderHandler.addOrder(new Order(12L, "MSFT", Side.BUY, 22, 11));
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(22).containsOrder(12L));
        orderHandler.modifyOrder((new OrderModification(12L, 22, 10)));
        Assert.assertTrue(orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(22).containsOrder(12L));
        Assert.assertEquals(10, orderHandler.getOrderbooks().get("MSFT").getBuyLimits().get(22).getOrder(12L).getQuantity());
    }
    
    @Test
    public void multipleOrderbookAddTest(){
        orderHandler.addOrder(new Order(13L, "TSLA", Side.BUY, 23, 1));
        orderHandler.addOrder(new Order(14L, "HSBA", Side.BUY, 41, 41));
        orderHandler.addOrder(new Order(15L, "PYPL", Side.BUY, 222,51));
        orderHandler.addOrder(new Order(16L, "TSLA", Side.BUY, 23, 2));
        orderHandler.addOrder(new Order(17L, "HSBA", Side.BUY, 42, 40));
        orderHandler.addOrder(new Order(18L, "PYPL", Side.BUY, 222,50));
        Assert.assertTrue(orderHandler.getOrderbooks().get("TSLA").getBuyLimits().get(23).containsOrder(13L));
        Assert.assertTrue(orderHandler.getOrderbooks().get("HSBA").getBuyLimits().get(42).containsOrder(17L));
        Assert.assertTrue(orderHandler.getOrderbooks().get("PYPL").getBuyLimits().get(222).containsOrder(18L));
        Assert.assertEquals(2, orderHandler.getOrderbooks().get("TSLA").getBuyLimits().get(23).getOrderCount());
        Assert.assertEquals(4, orderHandler.getOrderbooks().size());
    }
    @Test
    public void netAcrossOrderbookTest()
    {
        Assert.assertTrue(orderHandler.getOrderbooks().get("HSBA").getBuyLimits().get(41).containsOrder(14L));
        orderHandler.modifyOrder(new OrderModification(14L, 40, 41));
        Assert.assertTrue(orderHandler.getOrderbooks().get("HSBA").getBuyLimits().get(40).containsOrder(14L));
    }
    @Test
    public void testQuantity()
    {
        Assert.assertEquals(44, orderHandler.getOrderbooks().get("MSFT").getSellQuantity());
        Assert.assertEquals(46, orderHandler.getOrderbooks().get("MSFT").getBuyQuantity());
        orderHandler.modifyOrder(new OrderModification(9L, 10, 14));
        Assert.assertEquals(47, orderHandler.getOrderbooks().get("MSFT").getBuyQuantity());
    }
    
    @Test
    public void testGetBestPriceNaN()
    {
        Assert.assertEquals(Double.NaN, orderHandler.getBestBuyPriceForQuantity("HSBA", 9999), 0);
        Assert.assertEquals(Double.NaN, orderHandler.getBestSellPriceForQuantity("MSFT", 9999), 0);
    }
    
    @Test
    public void ztestGetBestBuyPrice()
    {
        orderHandler.addOrder(new Order(19L, "HSBA", Side.BUY, 43, 12));
        orderHandler.addOrder(new Order(20L, "HSBA", Side.BUY, 45, 14));
        Assert.assertEquals(43.317, orderHandler.getBestBuyPriceForQuantity("HSBA", 41), 0.001);
    }
    
    @Test
    public void ztestGetBestSellPrice()
    {
        Assert.assertEquals(19.9334, orderHandler.getBestSellPriceForQuantity("MSFT", 30), 0.001);
    }
    
}
