package client;

import java.io.Serializable;
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

import server.Stock;

public abstract class Trader {
	protected static int balance;
	protected static ArrayList<Stock> portfolio;
	protected static ArrayList<Stock> marketStocks;

	protected static LinkedList<String> ownedStock; //used for quick finding stocks bought by name
	
	protected static InetAddress marketIPAdress;
	protected static LinkedBlockingQueue<Object> messages;
	protected static Socket socket;
	
	public abstract void buyStock(LinkedList<Stock> stockList);
	
	public abstract void sellStock(LinkedList<Stock> stockList);
	
	protected abstract void analyzeMarketForDecision();

	public Trader() throws UnknownHostException, IOException{
		System.out.println("Trader launched");
		int portNumber = 4003;
		String ipAddress = "127.0.0.1";
		socket = new Socket(ipAddress, portNumber);

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String fromServer;
            fromServer = in.readLine();
            String initialStocksAndPricesList = fromServer;
            marketStocks = parseStocksAndPricesString(initialStocksAndPricesList);
            
            while (true){
            	analyzeMarketForDecision();
            	displayInfo();
            	
            	fromServer = in.readLine();
            	String updatedPrices = fromServer;
            	List<String> newPriceList = parsePricesString(updatedPrices);
            	updateLocalPrices(newPriceList);
            }
	
	}

	private void displayInfo() {
		printCurrentLocalStockPrices();		
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
	
	/**
	 * Although Java does not allow static methods to be abstract, every subclass of Trader must implement the below entry point method:
	 */
	//public static abstract void main(String[] args) throws Exception;

}
