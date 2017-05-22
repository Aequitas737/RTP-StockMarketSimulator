package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import client.Trader;

public class StockMarket {
	private  ArrayList<Stock> stockList = new ArrayList<Stock>();
	
	private  ArrayList<ConnectionToClient> clientList;
    private  LinkedBlockingQueue<Object> messages;
    private  ServerSocket serverSocket;
    
    
	private  double positiveBias = 0.51;//0-1, higher for more positive
	private  double maxPercentageChange = 0.05;
	private  long minimumTimeIntervalBetweenUpdates = ((long) 10000000000.);//nano seconds ie. 10 seconds
	
	public StockMarket() throws IOException{
		Stock stock1 = new Stock("aaa", 10.0);
		Stock stock2 = new Stock("bbb", 3.0);
		Stock stock3 = new Stock("ccc", 0.001);
		addStock(stock1);
		addStock(stock2);
		addStock(stock3);
		
		System.out.println("StockMarket Launched");
		
		int portNumber = 4003;
		long lastUpdateTime = System.nanoTime();
		long currentTime = System.nanoTime();
		
		clientList = new ArrayList<ConnectionToClient>();
        messages = new LinkedBlockingQueue<Object>();
        serverSocket = new ServerSocket(portNumber);
        
        
        Thread accept = new Thread() {
            public void run(){
                while(true){
                    try{
                        Socket s = serverSocket.accept();
                        clientList.add(new ConnectionToClient(s));
                        System.out.println("added client");
                    }
                    catch(IOException e){ e.printStackTrace(); }
                }
            }
        };
        
        accept.setDaemon(true);
        accept.start();
                
        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        Object message = messages.take();
                        // Do some handling here...
                        System.out.println("Message Received: " + message);
                    }
                    catch(InterruptedException e){ }
                }
            }
        };
        
        messageHandling.setDaemon(true);
        messageHandling.start();
        
        
        while(true){
			//read and handle messages from traders -> use separate threads?
//			socket.receive(packet);
//			handlePacket(packet, buffer);
			
			currentTime = System.nanoTime();
			if(currentTime - lastUpdateTime > minimumTimeIntervalBetweenUpdates){
				lastUpdateTime = currentTime;
				updateMarket();
				System.out.println("updated market");
				notifyClientsOfPriceChange();
			}
        }
	}
	
	public static void main(String[] args) throws IOException{


		StockMarket stockMarket = new StockMarket();
		
		//TODO handle exceptions with try catch don't just throw
		
        
        
//		byte[] buffer = new byte[2048];
//        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
         
		
		
		//open connection to allow traders to connect
//		DatagramSocket socket = new DatagramSocket(portNumber);

		
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

	/**
	 * Format of (String) message:
	 * "stockName1,priceInt,stockName2,priceInt2,stockName3,priceInt3,"
	 */
	private void notifyClientsOfPriceChange() {
		String message = "";
		for(Stock stock : stockList){
			message += stock.getName() + "," + stock.getPrice() + ",";
		}
		//remove last comma
		message = message.substring(0,message.length()-1);
		sendToAll(message);		
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
	
    public void sendToOne(int index, Object message)throws IndexOutOfBoundsException {
        clientList.get(index).write(message);
    }

    public void sendToAll(Object message){
        for(ConnectionToClient client : clientList)
            client.write(message);
    }
	
    //can this be static???
	private class ConnectionToClient {
		ObjectInputStream in;
		ObjectOutputStream out;
		Socket socket;
		
		ConnectionToClient(Socket sock) throws IOException {
			this.socket = sock;
			System.out.println("1");
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("2");
			out = new ObjectOutputStream(socket.getOutputStream());
			
			Thread read = new Thread(){
				public void run(){
					while(true){
						try{
							Object obj = in.readObject();
							messages.put(obj);
						}
						catch(IOException | ClassNotFoundException | InterruptedException e){ 
							e.printStackTrace(); 
						}
					}
				}
			};
			
			read.setDaemon(true); // terminate when main ends
			read.start();
		}
		public void write(Object obj) {
			try{
				out.writeObject(obj);
			}
			catch(IOException e){ e.printStackTrace(); }
		}
	}
}
