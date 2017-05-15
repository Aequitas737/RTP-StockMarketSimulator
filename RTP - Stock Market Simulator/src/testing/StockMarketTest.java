package testing;

import static org.junit.Assert.*;

import org.junit.Test;

import server.Stock;
import server.StockMarket;

public class StockMarketTest {

	@Test
	public void test() {
		StockMarket market = new StockMarket();
		double initialPrice = 10.00;
		
		Stock stock = new Stock("AWC", initialPrice);
		market.addStock(stock);
		assertEquals(initialPrice, market.getStockList().get(0).getPrice(), 0.00001);
		
	}
	
	@Test
	public void test2() {
		StockMarket market = new StockMarket();
		int timesPositive = 0;
		int timesNegative = 0;
		double initialPrice = 10.00;
		
		Stock stock = new Stock("AWC", initialPrice);
		market.addStock(stock);
		for(int i=0;i<10000;i++){
			for(int j=0;j<1000;j++){
				//System.out.println("\n\n");
				market.updateMarket();
				//System.out.println(market.getStockList().get(0).getPrice());
			}
			double price = market.getStockList().get(0).getPrice();
			if(price > 10.00){
				timesPositive++;
			}else{
				timesNegative++;
			}
			market.getStockList().get(0).updatePrice(10.0);
		}
		System.out.println("times positive = " +timesPositive);
		System.out.println("times negative = " + timesNegative);
	}
}