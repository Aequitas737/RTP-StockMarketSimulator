package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

import client.Trader;

public class StockMarket {
	private static ArrayList<Stock> stockList = new ArrayList<Stock>();
	
	private static double positiveBias = 0.51;//0-1, higher for more positive
	private static double maxPercentageChange = 0.05;
	private static long minimumTimeIntervalBetweenUpdates = ((long) 10000000000.);//nano seconds ie. 10 seconds
	
	public StockMarket(){
		Stock stock1 = new Stock("aaa", 10.0);
		Stock stock2 = new Stock("bbb", 3.0);
		Stock stock3 = new Stock("ccc", 0.001);
		addStock(stock1);
		addStock(stock2);
		addStock(stock3);
	}
	
	public static void main(String[] args) throws IOException{
		//TODO handle exceptions with try catch don't just throw
		int portNumber = 4003;
		byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
         
		long lastUpdateTime = System.nanoTime();
		long currentTime = System.nanoTime();
		
		StockMarket stockMarket = new StockMarket();
		
		//open connection to allow traders to connect
		DatagramSocket socket = new DatagramSocket(portNumber);

		while(true){
			//read and handle messages from traders -> use separate threads?
			socket.receive(packet);
			handlePacket(packet, buffer);
			
			currentTime = System.nanoTime();
			if(currentTime - lastUpdateTime > minimumTimeIntervalBetweenUpdates){
				lastUpdateTime = currentTime;
				updateMarket();
				notifyClientsOfPriceChange();
			}
		}
	}
	
	
	
	
	
	/**
	 * Handle a packet from a client,
	 * we'll need to be able to do things like:
	 * register - When a client comes online for the first time, they'll register with the server so the server knows where to send updates to.
	 * buy -
	 * sell
	 * @param packet
	 * @param buffer
	 */
	private static void handlePacket(DatagramPacket packet, byte[] buffer) {
		// TODO Auto-generated method stub
		
	}

	private static void notifyClientsOfPriceChange() {
		// TODO Auto-generated method stub
		
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
