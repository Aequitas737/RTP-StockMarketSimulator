package client;

import java.io.*;
import java.net.*;
import java.util.*;

import server.Stock;

public class PercentageAI extends Trader
{
	private double buyPercentageThreshold = 0.9;
	private double sellGainPercentageThreshold = 1.2;
	private double sellLossPercentageThreshold = 0.8;
	private double purchaseLimit = 1000.0;//dollars
	
	public PercentageAI(double startingMoney) throws UnknownHostException, IOException {
		super(startingMoney);
	}
	
	public PercentageAI() throws UnknownHostException, IOException {
		super(10000.0);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		PercentageAI trader = new PercentageAI();
	}

	
	private double calculatePercentage(double initialPrice, double finalPrice)
	{
		return initialPrice / finalPrice;
	}
	
	private ArrayList<Double> calculatePercentages(ArrayList<Stock> stockList)
	{
		ArrayList<Double> percentages = new ArrayList<Double>();
		for (int i = 0; i < stockList.size(); i++)
		{
			percentages.add(calculatePercentage(initialStocks.get(i).getPrice(), stockList.get(i).getPrice()));
		}
		return percentages;
	}
	

	
	@Override
	public ArrayList<Stock> getStocksToBuy(ArrayList<Stock> stockList)
	{
		ArrayList<Stock> stocksToBuy = new ArrayList<Stock>();
		//sort which stocks are eligible
		for (int i = 0; i < stockList.size(); i++)
		{
//			System.out.println();
			double currentPrice = stockList.get(i).getPrice();
			double comparisonPrice = initialStocks.get(i).getPrice();
			double thresholdedPrice = comparisonPrice * buyPercentageThreshold;
//			System.out.println(buyPercentageThreshold);
//			System.out.println(currentPrice);
//			System.out.println(comparisonPrice);
//			System.out.println(thresholdedPrice);
//			System.out.println(currentPrice<thresholdedPrice);
			if (currentPrice < thresholdedPrice)
			{
				System.out.println("something to buy!!");
				System.out.println(stockList.get(i).getName());
				System.out.println(stockList.get(i).getPrice());
				stocksToBuy.add(stockList.get(i));
			}
		}
//		System.out.println("----------------\n\n");
		return stocksToBuy; //if empty don't buy
	}
	
	@Override
	public ArrayList<Stock> getStocksToSell(ArrayList<Stock> stockList)
	{
		ArrayList<Stock> result = new ArrayList<Stock>();
		for (int i = 0; i < stockList.size(); i++)
		{
			if ((stockList.get(i).getPrice() > initialStocks.get(i).getPrice() * sellGainPercentageThreshold) //if price has gone up since buying we sell
					|| (stockList.get(i).getPrice() > initialStocks.get(i).getPrice() * sellLossPercentageThreshold))//if it's gone down too much sell to cut losses
			{
				if(portfolio.contains(stockList.get(i)))
				{
					result.add(stockList.get(i));
				}				
			}
		}
		return result; //if empty don't sell
	}


	public double buyStocks(ArrayList<Stock> stockList) {
		double moneySpent = 0.0;
		ArrayList<Double> percentages = new ArrayList<Double>();
		percentages = calculatePercentages(stockList);
		for(int i = 0; i<stockList.size(); i++)
		{
			int indexOfNextLargestPercentage = percentages.indexOf(Collections.max(percentages));
			Stock stockToBuy = stockList.get(indexOfNextLargestPercentage);
			if (balance >= purchaseLimit)
			{
				//this will truncate, but is desired so we go under the purchaseLimit instead of going over
				int purchaseQuantity = (int) (purchaseLimit / stockToBuy.getPrice());
				moneySpent += buyStock(stockToBuy, purchaseQuantity);
			}
			else if(balance < purchaseLimit)
			{
				int purchaseQuantity = (int) (balance / stockToBuy.getPrice());
				moneySpent += buyStock(stockToBuy, purchaseQuantity);
			}
			percentages.remove(indexOfNextLargestPercentage);
			stockList.remove(stockToBuy);				
		}
		return moneySpent;
	}

	public double sellStocks(ArrayList<Stock> stockList)
	{
		double moneyGained = 0.0;
		for(Stock stock : stockList)
		{
			moneyGained += sellStock(stock, stock.getQuantity());//sell all
		}
		return moneyGained;
	}

	@Override
	protected void performTrading() 
	{
		this.buyPercentageThreshold = 0.9;
		this.sellGainPercentageThreshold = 1.1;
		this.sellLossPercentageThreshold = 0.8;
		this.purchaseLimit = 1000.0;
		
		if(!marketStocks.isEmpty())
		{
			ArrayList<Stock> stocksToSell = getStocksToSell(marketStocks);
			double moneyGained = sellStocks(stocksToSell);
			ArrayList<Stock> stocksToBuy = getStocksToBuy(marketStocks);
			double moneySpent = buyStocks(stocksToBuy);
			
			System.out.println("Gained " + moneyGained + " from selling stock");
			System.out.println("Spent" + moneySpent + " from buying stock");
		}
	}
}
