package client;

import java.io.*;
import java.net.*;
import java.util.*;

import server.Stock;

public class TrendingAI extends Trader
{
	private double purchaseLimit;
	private int historySize;
	private ArrayList<ArrayList<Double>> stockHistory;
	private ArrayList<Double> stockAverages;
	private double buyPercentageThreshold;
	private double sellPercentageThreshold;
	
	
	public TrendingAI(double startingMoney) throws UnknownHostException, IOException {
		super(startingMoney);
	}
	
	public TrendingAI() throws UnknownHostException, IOException {
		super(10000.0);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		TrendingAI trader = new TrendingAI();
	}

	private ArrayList<Double> calculateAverages()
	{
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < stockHistory.get(0).size(); i++)//for each of the stocks in a moment of history
		{
			double average = 0.0;
			for(int j = 0; j<stockHistory.size(); j++)//for each of the moments of history
			{
				average += stockHistory.get(j).get(i);
			}
			average /= stockHistory.size();
			result.add(average);
		}
		return result;
	}
	
	private ArrayList<Double> calculateAveragePercentageDifference(ArrayList<Stock> stockList)
	{
		ArrayList<Double> percentages = new ArrayList<Double>();
		for (int i = 0; i < stockList.size(); i++)
		{
			percentages.add(stockAverages.get(i) / stockList.get(i).getPrice());
		}
		return percentages;
	}
	
	@Override
	public ArrayList<Stock> getStocksToBuy(ArrayList<Stock> stockList)
	{
		ArrayList<Stock> result = new ArrayList<Stock>();
		//sort which stocks are eligible
		for (int i = 0; i < stockList.size(); i++)
		{
			if (stockList.get(i).getPrice() < (stockAverages.get(i) * buyPercentageThreshold))
			{
				result.add(stockList.get(i));
			}
		}
		return result; //if empty don't buy
	}

	@Override
	public ArrayList<Stock> getStocksToSell(ArrayList<Stock> stockList)
	{
		ArrayList<Stock> result = new ArrayList<Stock>();
		for (int i = 0; i < stockList.size(); i++)
		{
			if (stockList.get(i).getPrice() > (stockAverages.get(i) * sellPercentageThreshold))
			{
				if(portfolio.contains(stockList.get(i)))
				{
					result.add(stockList.get(i));
				}	
			}
		}
		return result; //if empty don't sell
	}
	
	public double buyStocks(ArrayList<Stock> stockList) 
	{
		double moneySpent = 0.0;
		ArrayList<Double> percentages = new ArrayList<Double>();
		percentages = calculateAveragePercentageDifference(stockList);
		for(int i = 0; i<stockList.size(); i++)
		{
			int indexOfNextSmallestPercentage = percentages.indexOf(Collections.min(percentages));
			Stock stockToBuy = stockList.get(indexOfNextSmallestPercentage);
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
			percentages.remove(indexOfNextSmallestPercentage);
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
	protected void performTrading() {
		this.buyPercentageThreshold = 0.95;
		this.sellPercentageThreshold = 0.95;
//		this.sellGainPercentageThreshold = 1.1;
//		this.sellLossPercentageThreshold = 0.8;
		this.purchaseLimit = 1000.0;
		this.historySize = 10;
		
		if(stockHistory == null)
		{
			stockHistory = new ArrayList<ArrayList<Double>>();
		}
		if(stockAverages == null)
		{
			stockAverages = new ArrayList<Double>();
		}
		if(!marketStocks.isEmpty())
		{
			setStockHistory(marketStocks);
			stockAverages = calculateAverages();
			
			ArrayList<Stock> stocksToSell = getStocksToSell(marketStocks);
			double moneyGained = sellStocks(stocksToSell);
			ArrayList<Stock> stocksToBuy = getStocksToBuy(marketStocks);
			double moneySpent = buyStocks(stocksToBuy);
			
			System.out.println("\n\nGained " + moneyGained + " from selling stock");
			System.out.println("Spent " + moneySpent + " from buying stock");
		}
		
	}

	private void setStockHistory(ArrayList<Stock> stockList) 
	{
		ArrayList<Double> stockPricesToAdd = new ArrayList<Double>();
		if(stockHistory.size()<=historySize)
		{
			for(Stock stock : stockList)
			{
				stockPricesToAdd.add(stock.getPrice());
			}
			stockHistory.add(stockPricesToAdd);
		}
		else
		{
			for(Stock stock : stockList)
			{
				stockPricesToAdd.add(stock.getPrice());
			}
			stockHistory.add(stockPricesToAdd);
			stockHistory.remove(0);//remove the oldest entry
		}
	}
}
