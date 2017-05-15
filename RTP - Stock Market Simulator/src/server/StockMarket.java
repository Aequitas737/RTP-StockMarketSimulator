package server;

import java.util.ArrayList;
import java.util.Random;

public class StockMarket {
	private static ArrayList<Stock> stockList = new ArrayList<Stock>();
	private static double positiveBias = 0.51;//0-1, higher for more positive
	private static double maxPercentageChange = 0.05;
	public StockMarket(){
		
	}
	
	public static void main(String[] args){
		
	}
	
	
	
	
	
	
	public static void addStock(Stock stock){
		stockList.add(stock);
	}
	
	public static ArrayList<Stock> getStockList(){
		return stockList;
	}
	
	public static void updateMarket(){
		for(Stock stock : stockList){
			double currentPrice = stock.getPrice();
			//Determine if the price should go up or down, biased slightly towards up
			boolean positiveMultiplier = Math.random()<positiveBias ? true : false;
			//random positive number between 0 and 1 with distribution peaking at 0 and falling off towards 1
			double randomMultiplier = Math.abs(Math.random()-Math.random())*maxPercentageChange;
			double priceChange = currentPrice * randomMultiplier;
			//System.out.println("positive multiplier: " + positiveMultiplier);
			//System.out.println("randomMultiplier: " + randomMultiplier);
			//System.out.println("priceChange: " + priceChange);
			if(!positiveMultiplier){
				priceChange = priceChange * -1;
			}
			//System.out.println("priceChange: " + priceChange);
			double newPrice = currentPrice + priceChange;
			
			stock.updatePrice(newPrice);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
}
