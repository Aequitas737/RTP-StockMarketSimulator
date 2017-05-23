package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import client.Trader;

public class StockMarket {
	private  ArrayList<Stock> stockList = new ArrayList<Stock>();
	
	private ArrayList<Socket> clientSocketList = new ArrayList<Socket>();
    private  ServerSocket serverSocket;
        
	private  double positiveBias = 0.45;//0-1, higher for more positive
	private  double maxPercentageChange = 0.05;
	private  long minimumTimeIntervalBetweenUpdates = ((long) 5000000000.);//nano seconds ie. 5 seconds
	
	public StockMarket() throws IOException{
		Stock stock1 = new Stock("aaa", 10.0);
		Stock stock2 = new Stock("bbb", 3.0);
		Stock stock3 = new Stock("ccc", 0.001);
		addStock(stock1);
		addStock(stock2);
		addStock(stock3);
		
		System.out.println("StockMarket Launched");
		
		int portNumber = 4003;
		serverSocket = new ServerSocket(portNumber);
        
        Thread updater = new Thread(){
        	public void run(){
        		long lastUpdateTime = System.nanoTime();
        		long currentTime = System.nanoTime();
                while(true){
     			
        			currentTime = System.nanoTime();
        			if(currentTime - lastUpdateTime > minimumTimeIntervalBetweenUpdates){
        				lastUpdateTime = currentTime;
        				updateMarket();
//        				System.out.println("updated market");
        				notifyClientsOfPriceChange();
        			}
                }
        	}
        };
        updater.start();
        
        while(true){
        	System.out.println("waiting to accept");
        	Socket socket = serverSocket.accept();
        	System.out.println("accepted");
        	clientSocketList.add(socket);
        	
        	Thread accepter = new Thread(){
        		public void run(){
        			DataOutputStream out = null;
        			Socket sock = socket;
        			try {
        				out = new DataOutputStream(sock.getOutputStream());
        		        } catch (IOException e) {
        		            return;
        		        }
        		        String message = "";
    		            try {
    		            	//Send a message on accept to the client detailing the stock names and prices
    		        		for(Stock stock : stockList){
    		        			message += stock.getName() + "," + stock.getPrice() + ",";
    		        		}
    		        		//remove last comma
    		        		message = message.substring(0,message.length()-1);
    		        		//add a new line
    		        		message += "\n";
    		        		out.writeBytes(message);
    		        		out.flush();
    		            } catch (IOException e) {
    		                e.printStackTrace();
    		                return;
    		            }
        		}
        	};
        	accepter.start();
        }
	}
	
	public static void main(String[] args) throws IOException{
		StockMarket stockMarket = new StockMarket();
	}

	/**
	 * No need for stock names as prices are always in the same order
	 * Format of (String) message:
	 * "priceDouble,priceDouble2,priceDouble3"
	 */
	private void notifyClientsOfPriceChange() {
		String message = "";
		for(Stock stock : stockList){
			message += stock.getPrice() + ",";
		}
		//remove last comma
		message = message.substring(0,message.length()-1);
		sendToAllClients(message);		
	}

    public void sendToAllClients(String message){
        for(Socket clientSocket : clientSocketList)
            write(message, clientSocket);
    }
    
    public void write(String message, Socket socket){
    	//messages need to end with a new line, so add it here
    	message += "\n";
    	
    	DataOutputStream out;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public void addStock(Stock stock){
		stockList.add(stock);
	}
	
	public ArrayList<Stock> getStockList(){
		return stockList;
	}
	
	public void updateMarket(){
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
