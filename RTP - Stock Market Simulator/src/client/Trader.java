package client;

import java.io.Serializable;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import server.Stock;

public abstract class Trader {
	protected double balance;
	protected double startingBalance;
	protected ArrayList<Stock> portfolio = new ArrayList<Stock>();
	protected ArrayList<Stock> marketStocks;
	protected ArrayList<Stock> initialStocks = new ArrayList<Stock>();
	protected Logger logger = Logger.getLogger("MyLog");  


	protected LinkedList<String> ownedStock; //used for quick finding stocks bought by name
	
	protected static Socket socket;
	
	public abstract ArrayList<Stock> getStocksToBuy(ArrayList<Stock> stockList);
	
	public abstract ArrayList<Stock> getStocksToSell(ArrayList<Stock> stockList);
	
	protected abstract void performTrading();

	public Trader(double startingMoney) throws UnknownHostException, IOException{
		this.startingBalance = startingMoney;
		this.balance = this.startingBalance;
		
		System.out.println("Trader launched");
		int portNumber = 4003;
		String ipAddress = "127.0.0.1";
		socket = new Socket(ipAddress, portNumber);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
	    FileHandler fh;  
	    fh = new FileHandler("trader.log"); 
	    logger.addHandler(fh);
	    SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  
        logger.info("Trader Initiated"); 
	    
        String fromServer;
        fromServer = in.readLine();
        String initialStocksAndPricesList = fromServer;
        marketStocks = parseStocksAndPricesString(initialStocksAndPricesList);
        initialStocks = parseStocksAndPricesString(initialStocksAndPricesList); 
        while (true){
        	performTrading();
        	displayInfo();
        	
        	fromServer = in.readLine();
        	String updatedPrices = fromServer;
        	List<String> newPriceList = parsePricesString(updatedPrices);
        	updateLocalPrices(newPriceList);
        }
	
	}

	private void displayInfo() 
	{
//		printCurrentLocalStockPrices();		
		System.out.println("Current balance: " + balance);
		double portfolioValue = calculateTotalPortfolioValue();
		System.out.println("Current portfolio value: " + portfolioValue);
		double netWorth = balance + portfolioValue;
		System.out.println("Net Worth: " + netWorth);
		double profit = netWorth - startingBalance;
		System.out.println("Net profit: " + profit);
		
		System.out.println("Portfolio details: ");
		for(Stock stock : portfolio)
		{
			System.out.println("Own " + stock.getQuantity() + " shares of " + stock.getName());			
		}
	}

	/*
	FileWriter fw = null;
	BufferedWriter bw = null;
	// Get current working directory and set up log
		String dir = Paths.get(".").toAbsolutePath().normalize().toString();
		String logDir = dir + "\\TraderLog.txt";
		File traderLog = new File(logDir);
		fw = new FileWriter(traderLog);
		bw = new BufferedWriter(fw);
	*/
	private void printCurrentLocalStockPrices() {
		for(Stock stock : marketStocks)
		{
			System.out.println(stock.getName() + " - $" + stock.getPrice());
		}
		System.out.println("\n");
	}

	public List<String> parsePricesString(String updatedPrices) {
		//separate the string with zero or more whitespace a comma and then zero or more whitespace
		List<String> updatedPricesList = Arrays.asList(updatedPrices.split("\\s*,\\s*"));
		return updatedPricesList;
	}
	
	public void updateLocalPrices(List<String> updatedPricesList)
	{
		for(int i = 1; i<=updatedPricesList.size(); i++)
		{
			marketStocks.get(i-1).updatePrice(Double.parseDouble(updatedPricesList.get(i-1)));
		}
	}
	/**
	 * Example input: "name1,price1,name2,price2"
	 * @param initialStocksAndPricesList
	 * @return
	 */
	public ArrayList<Stock> parseStocksAndPricesString(String initialStocksAndPricesList) {
		//separate the string with zero or more whitespace a comma and then zero or more whitespace
		List<String> stockNamesAndPrices = Arrays.asList(initialStocksAndPricesList.split("\\s*,\\s*"));
		ArrayList<Stock> stockList = new ArrayList<Stock>();
		int i = 1;
		while(i<stockNamesAndPrices.size())
		{
			Stock stock = new Stock(stockNamesAndPrices.get(i-1), Double.parseDouble(stockNamesAndPrices.get(i)));
			i += 2;//add 2 since one stock takes up two spaces (one for name, one for price)
			stockList.add(stock);
		}
		return stockList;
	}
	
	public double calculateTotalPortfolioValue()
	{
		double value = 0.0;
		for(Stock stock : portfolio)
		{
			value+=stock.getPrice() * stock.getQuantity();
		}
		return value;
	}
	//TODO might only need this for calculating portfolio value so change stockList to be this.portfolio
	protected double calculateTotalValue(ArrayList<Stock> stockList)
	{
		double result = 0.0;
		for(Stock stock : stockList)
		{
			result += stock.getPrice() * stock.getQuantity();
		}
		return result;
	}
	
	
	public double buyStock(Stock stockToBuy, int quantityToBuy)
    {
		int stockIndex = portfolio.indexOf(stockToBuy);
		int ownedQuantity = 0;
		if(stockIndex>=0)
		{
			ownedQuantity = portfolio.get(stockIndex).getQuantity();
		}
		
		portfolio.add(stockToBuy);
		stockToBuy.setQuantity(ownedQuantity + quantityToBuy);
//		ownedStock.add(stockToBuy.getName());
		balance -= stockToBuy.getPrice() * quantityToBuy;
		int index = indexOfStockFromName(initialStocks, stockToBuy.getName());
		initialStocks.get(index).updatePrice(stockToBuy.getPrice());
		
		double moneySpent = stockToBuy.getPrice() * quantityToBuy;
		logger.info(this.getClass().getName() + " has purchased " + quantityToBuy + " shares of " + stockToBuy.getName() + " at $"+stockToBuy.getPrice());  
		return moneySpent;
    }
 
	public int indexOfStockFromName(ArrayList<Stock> stockList, String stockName)
	{
		int i = 0;
		for(Stock stock : stockList)
		{
			if(stock.getName().equals(stockName))
			{
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public double sellStock(Stock stockToSell, int quantityToSell)
    {
		int ownedQuantity = portfolio.get(portfolio.indexOf(stockToSell)).getQuantity();
		if(ownedQuantity == quantityToSell)
		{
			portfolio.remove(stockToSell);
//			ownedStock.remove(stockToSell.getName());
			balance += stockToSell.getPrice() * stockToSell.getQuantity();
			int index = indexOfStockFromName(initialStocks, stockToSell.getName());
			initialStocks.get(index).updatePrice(stockToSell.getPrice());
			portfolio.remove(stockToSell);
		}
		else if(ownedQuantity > quantityToSell)
		{
			portfolio.get(portfolio.indexOf(stockToSell)).setQuantity(ownedQuantity-quantityToSell);
			balance += stockToSell.getPrice() * (quantityToSell);
		}
		
		logger.info(this.getClass().getName() + " has purchased " + quantityToSell + " shares of " + stockToSell.getName() + " at $"+stockToSell.getPrice());  

		double moneymade = stockToSell.getPrice() * quantityToSell;
		return moneymade;
	}
	
	/**
	 * Although Java does not allow static methods to be abstract, every subclass of Trader must implement the below entry point method:
	 */
	//public static abstract void main(String[] args) throws Exception;

}
