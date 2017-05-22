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
		StockMarket.addStock(stock);
		assertEquals(initialPrice, StockMarket.getStockList().get(0).getPrice(), 0.00001);
		
	}
	
	@Test
	public void test2() {
		StockMarket market = new StockMarket();
		int timesPositive = 0;
		int timesNegative = 0;
		double initialPrice = 10.00;
		
		Stock stock = new Stock("AWC", initialPrice);
		StockMarket.addStock(stock);
		for(int i=0;i<1000;i++){
			for(int j=0;j<1000;j++){
				//System.out.println("\n\n");
				StockMarket.updateMarket();
				//System.out.println(market.getStockList().get(0).getPrice());
			}
			double price = StockMarket.getStockList().get(0).getPrice();
			if(price > 10.00){
				timesPositive++;
			}else{
				timesNegative++;
			}
			StockMarket.getStockList().get(0).updatePrice(10.0);
		}
		System.out.println("times positive = " +timesPositive);
		System.out.println("times negative = " + timesNegative);
	}
}
