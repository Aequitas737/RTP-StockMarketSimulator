import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import server.Stock;

public class PercentageAI extends Trader
{
	private LinkedList<Stock> initialStocks = new LinkedList<Stock>();
	
	private double calculatePercentage(double initialPrice, double finalPrice)
	{
		return initialPrice / finalPrice;
	}
	
	private double calculateTotalPrice(LinkedList<Integer> indexList, LinkedList<Stock> stockList)
	{
		double result = 0.0;
		for (int i = 0; i < indexList.size(); i++)
		{
			result += stockList.get(indexList.get(i)).getPrice();
		}
		return result;
	}
	
	private LinkedList<Integer> getStocksToBuy(LinkedList<Stock> stockList)
	{
		LinkedList<Integer> result = new LinkedList<Integer>();
		LinkedList<Double> percentages = new LinkedList<Double>();
		//sort which stocks are eligible
		for (int i = 0; i < stockList.size(); i++)
		{
			if (stockList.get(i).getPrice() < initialStocks.get(i).getPrice() * 0.8) //change percentages here
			{
				result.add(i);
				percentages.add(calculatePercentage(initialStocks.get(i).getPrice(), stockList.get(i).getPrice()));
			}
		}
		//check to see if everything can be afforded
		if (balance < calculateTotalPrice(result, stockList))
		{
			//rearrange percentages to ascending order
			LinkedList<Double> sortedPercentages = percentages;
			Collections.sort(sortedPercentages);
			double copyBalance = balance;
			LinkedList<Integer> copyResult = result;
			for (int i = 0; i < sortedPercentages.size(); i++)
			{
				//Oh my goooooooood
				if (stockList.get(result.get(percentages.indexOf(sortedPercentages.get(i)))).getPrice() > copyBalance)
					copyResult.remove(percentages.indexOf(sortedPercentages.get(i)));
				else
					copyBalance -= stockList.get(result.get(percentages.indexOf(sortedPercentages.get(i)))).getPrice();
			}
			result = copyResult;
		}
		return result; //if empty don't buy
	}
	
	private LinkedList<Integer> getStocksToSell(LinkedList<Stock> stockList)
	{
		LinkedList<Integer> result = new LinkedList<Integer>();
		for (int i = 0; i < stockList.size(); i++)
		{
			if ((stockList.get(i).getPrice() > initialStocks.get(i).getPrice() * 1.2) || (stockList.get(i).getPrice() > initialStocks.get(i).getPrice() * 0.9))
				result.add(stockList.get(i));
		}
		return result; //if empty don't sell
	}
	//original
	/*
	private LinkedList<Stock> getStocksToBuy(LinkedList<Stock> stockList)
	{
		LinkedList<Stock> result = new LinkedList<Stock> result;
		for (int i = 0; i < stockList.size(); i++)
		{
			if (stockList[i].getPrice() > initialStocks.getPrice() * 1.1)
				result.add(stockList[i]);
		}
		return result; //if empty don't buy
	}
	private LinkedList<Stock> getStocksToSell(LinkedList<Stock> stockList)
	{
		LinkedList<Stock> result = new LinkedList<Stock> result;
		for (int i = 0; i < stockList.size(); i++)
		{
			if (stockList[i].getPrice() < initialStocks.getPrice() * -1.2)
				result.add(stockList[i]);
		}
		return result; //if empty don't sell
	}
	*/
	public void buyStock(Stock stockToBuy)
    {
		//check to see if initial stock list is empty before doing anything
		/*if (initialStocks.isEmpty())
		{
			initialStocks = stockList;
		}
        else
		{*/
//			LinkedList stocksToBuy = getStocksToBuy(stockList);
//			if (!stocksToBuy.isEmpty())
//			{
				//send request to buy from stock marketIPAdress
				//right now assume just returning ints
				//add to portfolio and update stock history for next decision
//				for (int i = 0; i < stocksToBuy.size(); i++)
//				{
					portfolio.add(stockToBuy);
					ownedStock.add(stockToBuy.getName());
					balance -= stockToBuy.getPrice();
//				}
//				for (int i = 0; i < stockList.size(); i++)
//					initialStocks.get(i) = stockList.get(i);
				//write to log
    }

 
	
	public void sellStock(Stock stockToBuy) throws Exception
    {
        if (initialStocks.isEmpty())
		{
			initialStocks = stockList;
		}
        else
		{
			LinkedList stocksToSell = getStocksToSell(stockList);
			if (!stocksToSell.isEmpty())
			{
				//send request to sell stocks to stock marketIPAdress
				//right now assume just returning ints
				//add to portfolio and update stock history for next decision
				for (int i = 0; i < stocksToBuy.size(); i++)
				{
					portfolio.remove(stockList.get(i));
					ownedStock.remove(stockList.get(i).getName());
					balance += stockList.get(i).getPrice();
				}
				for (int i = 0; i < stockList.size(); i++)
					initialStocks.get(i) = stockList.get(i);
			}
		}
}

	@Override
	public void buyStock(LinkedList<Stock> stockList) {
		for(Stock stock : stockList)
		{
			buyStock(stock);
		}
		
	}

	@Override
	public void sellStock(LinkedList<Stock> stockList) {
		for(Stock stock : stockList)
		{
			sellStock(stock);
		}
		
		
	}

	@Override
	protected void analyzeMarketForDecision() {
		// TODO Auto-generated method stub
		
	}
