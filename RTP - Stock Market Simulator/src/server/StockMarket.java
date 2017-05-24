package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class StockMarket {
	private  ArrayList<Stock> stockList = new ArrayList<Stock>();
	
	private ArrayList<Socket> clientSocketList = new ArrayList<Socket>();
    private  ServerSocket serverSocket;
        
	private  double positiveBias = 0.51;//0-1, higher for more positive
	private  double maxPercentageChange = 0.05;
	private final long initialSetupDelay = ((long) 20000000000.); //20 seconds
//	private final long initialSetupDelay = ((long) 20.); //nothing
//	private  long minimumTimeIntervalBetweenUpdates = ((long) 5000000000.);//nano seconds ie. 5 seconds
	private  long minimumTimeIntervalBetweenUpdates = ((long) 2000000.);//nano seconds ie. .001 seconds
	private final int updateLimit = 10000;
	
	public StockMarket() throws IOException{
		Stock stock1 = new Stock("aaa", 10.0);
		Stock stock2 = new Stock("bbb", 3.0);
		Stock stock3 = new Stock("ccc", 0.01);
		addStock(stock1);
		addStock(stock2);
		addStock(stock3);
		
		System.out.println("StockMarket Launched");
		
		int portNumber = 4003;
		serverSocket = new ServerSocket(portNumber);
        long startTime = System.nanoTime();
        
        Thread updater = new Thread(){
        	public void run(){
        		long lastUpdateTime = System.nanoTime();
        		long currentTime = System.nanoTime();
        		int iterations = 0;
                while(iterations <= updateLimit){
     			
        			currentTime = System.nanoTime();
        			if(currentTime - startTime > initialSetupDelay)
        			{
	        			if(currentTime - lastUpdateTime > minimumTimeIntervalBetweenUpdates)
	        			{
	        				lastUpdateTime = currentTime;
	        				updateMarket();
	        				notifyClientsOfPriceChange();
	        				printCurrentStockPrices();
	        				System.out.println("iterations = "+ iterations);
	        				iterations ++;
	        			}
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
	
	private void printCurrentStockPrices() {
		for(Stock stock : stockList)
		{
			System.out.println(stock.getName() + " - $" + stock.getPrice());
		}
		System.out.println("\n");
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
    	ArrayList<Socket> socketsToRemove = new ArrayList<Socket>();
        for(Socket clientSocket : clientSocketList)
        {
			try {
				write(message, clientSocket);
			} catch (SocketException e) {
				socketsToRemove.add(clientSocket);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        for(Socket clientSocket : socketsToRemove)
        {
        	clientSocketList.remove(clientSocket);
        }
    }
    
    public void write(String message, Socket socket) throws IOException{
    	//messages need to end with a new line, so add it here
    	message += "\n";
    	
    	DataOutputStream out;
		out = new DataOutputStream(socket.getOutputStream());
		out.writeBytes(message);
		out.flush();
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
			//Determine if the price should go up or down
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
