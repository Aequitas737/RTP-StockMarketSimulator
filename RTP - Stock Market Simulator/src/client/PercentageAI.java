import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class PercentageAI implements Trader
{
	private LinkedList<Stock> initialStocks = new LinkedList<Stock>();
	
	private double calculatePercentage(double initialPrice, double finalPrice)
	{
		return initialPrice / finalPrice;
	}
	
	private double calculateTotalPrice(LinkedList<int> indexList, LinkedList<Stock> stockList)
	{
		double result = 0.0;
		for (int i = 0; i < indexList.size(); i++)
		{
			result += stockList.get(indexList.get(i)).getPrice();
		}
		return result;
	}
	
	private LinkedList<int> getStocksToBuy(LinkedList<Stock> stockList)
	{
		LinkedList<int> result = new LinkedList<int>();
		LinkedList<double> percentages = new LinkedList<double>();
		//sort which stocks are eligible
		for (int i = 0; i < stockList.size(); i++)
		{
			if (stockList.get(i).getPrice() < initialStocks.getPrice() * 0.8) //change percentages here
			{
				result.add(i);
				percentages.add(calculatePercentage(initialStocks.get(i).getPrice(), stockList.get(i).getPrice()));
			}
		}
		//check to see if everything can be afforded
		if (balance < calculateTotalPrice(result, stockList))
		{
			//rearrange percentages to ascending order
			LinkedList<double> sortedPercentages = percentages;
			Collections.sort(sortedPercentages);
			double copyBalance = balance;
			LinkedList<int> copyResult = result;
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
	
	private LinkedList<int> getStocksToSell(LinkedList<Stock> stockList)
	{
		LinkedList<int> result = new LinkedList<int>();
		for (int i = 0; i < stockList.size(); i++)
		{
			if ((stockList.get(i).getPrice() > initialStocks.getPrice() * 1.2) || (stockList.get(i).getPrice() > initialStocks.getPrice() * 0.9))
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
	public void buyStock(LinkedList<Stock> stockList) throws Exception
    {
		//check to see if initial stock list is empty before doing anything
		if (initialStocks.isEmpty())
		{
			initialStocks = stockList;
		}
        else
		{
			LinkedList stocksToBuy = getStocksToBuy(stockList);
			if (!stocksToBuy.isEmpty())
			{
				//send request to buy from stock marketIPAdress
				//right now assume just returning ints
				//add to portfolio and update stock history for next decision
				for (int i = 0; i < stocksToBuy.size(); i++)
				{
					initialStocks.get(i) = stockList.get(i);
					portfolio.add(stockList.get(i));
					ownedStock.add(stockList.get(i).getName());
					balance -= stockList.get(i).getPrice();
				}
				//write to log
			}
		}
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
