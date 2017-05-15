import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class TrendingAI implements Trader
{
	private LinkedList<LinkedList<double>> stockHistory = new LinkedList<LinkedList<double>>();
	private LinkedList<double> stockAverages = new LinkedList<double>();
	
	private double calculateTotalPrice(LinkedList<int> indexList, LinkedList<Stock> stockList)
	{
		double result = 0.0;
		for (int i = 0; i < indexList.size(); i++)
		{
			result += stockList.get(indexList.get(i)).getPrice();
		}
		return result;
	}
	
	private LinkedList<double> calculateAverages()
	{
		LinkedList<double> result = new LinkedList<double>();
		for (int i = 0; i < stockHistory.size(); i++)
		{
			double average = 0.0;
			for (int j = 0; j < stockHistory.get(i).size(); j++)
			{
				average += stockHistory.get(i).get(j);
			}
			average /= stockHistory.get(i).size();
			result.add(average);
		}
		return result;
	}
	
	private LinkedList<int> getStocksToBuy(LinkedList<Stock> stockList)
	{
		LinkedList<int> result = new LinkedList<int>();
		LinkedList<double> profits = new LinkedList<double>();
		//sort which stocks are eligible
		for (int i = 0; i < stockList.size(); i++)
		{
			if (stockList.get(i).getPrice() < stockAverage.get(i))
			{
				result.add(i);
				profits.add(stockAverage.get(i) - stockList.get(i).getPrice());
			}
		}
		//check to see if everything can be afforded
		if (balance < calculateTotalPrice(result, stockList))
		{
			//rearrange profits to descending order
			LinkedList<double> sortedProfits = profits;
			Collections.sort(sortedProfits);
			Collections.reverse(sortedProfits);
			double copyBalance = balance;
			LinkedList<int> copyResult = result;
			for (int i = 0; i < sortedProfits.size(); i++)
			{
				//Oh my goooooooood
				if (stockList.get(result.get(profits.indexOf(sortedProfits.get(i)))).getPrice() > copyBalance)
					copyResult.remove(profits.indexOf(sortedProfits.get(i)));
				else
					copyBalance -= stockList.get(result.get(profits.indexOf(sortedProfits.get(i)))).getPrice();
			}
			result = copyResult;
		}
		return result; //if empty don't buy
	}

	
	public void buyStock(LinkedList<Stock> stockList) throws Exception
	{
    		if (stockHistory.isEmpty())
		{
			for (int i = 0; i < stockList.size(); ++i)
			{
				stockHistory.add(new LinkedList<double>());
				//I saw this online
				stockHistory.get(i).add(stockList[i].getPrice());
			}
		}
		else {
			//update history
			for (int i = 0; i < stockList.size(); ++i)
			{
				stockHistory.get(i).add(stockList[i].getPrice());
			}
			//all queues are of the same size
			if (!(stockHistory.get(0).size() < 6))
			{
				stockHistory.get(i).remove();
				stockAverages = calculateAverages();
				LinkedList stocksToBuy = getStocksToBuy(stockList);
				if (!stocksToBuy.isEmpty())
				{
					//send request to buy from stock marketIPAdress
					//right now assume just returning ints
					//add to portfolio
					for (int i = 0; i < stocksToBuy.size(); i++)
					{
						portfolio.add(stockList.get(i));
						ownedStock.add(stockList.get(i).getName());
						balance -= stockList.get(i).getPrice();
					}
					//write to log
				}
			}
		}
	}
	
	public void sellStock(Stock stockToBuy) throws Exception
    {
        if (sellAlgorithm(stockToBuy))
		{
			
		}
		else
		{
			//update stocks
		}
    
}
