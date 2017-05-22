package client;

import java.io.Serializable;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import server.Stock;

public abstract class Trader {
	protected static int balance;
	protected static LinkedList<Stock> portfolio;
	protected static LinkedList<String> ownedStock; //used for quick finding stocks bought by name
	
	protected static InetAddress marketIPAdress;
//	protected static DatagramSocket socket;
	protected static ConnectionToServer server;
	protected static LinkedBlockingQueue<Object> messages;
	protected static Socket socket;
	
	public abstract void buyStock(LinkedList<Stock> stockList);
	
	public abstract void sellStock(LinkedList<Stock> stockList);
	
//	public static Stock deserialize(byte[] data) throws IOException, ClassNotFoundException {
		
		//ByteArrayInputStream in = new ByteArrayInputStream(data);
		//ObjectInputStream is = new ObjectInputStream(in);
		//return is.readObject();
//		Stock desStock = (Stock) SerializationUtils.deserialize(data);
//		return desStock;
//	}
	
	public Trader() throws UnknownHostException, IOException{
		System.out.println("Trader launched");
		int portNumber = 4003;
		String ipAddress = "127.0.0.1";
		socket = new Socket(ipAddress, portNumber);
		messages = new LinkedBlockingQueue<Object>();
		System.out.println("2");
		server = new ConnectionToServer(socket);
		System.out.println("1");

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
	}
	
	public static void main(String[] args) throws Exception
	{	
		
        /*
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			//Set up connection to Stock Market
			socket = new DatagramSocket(4003);
			marketIPAdress = InetAddress.getByName("127.0.0.1"); //change IP address
			
			//initialize class variables
			balance = 0;
			portfolio = new LinkedList<Stock>();
			
			// Get current working directory and set up log
			String dir = Paths.get(".").toAbsolutePath().normalize().toString();
			String logDir = dir + "\\TraderLog.txt";
			File traderLog = new File(logDir);
			fw = new FileWriter(traderLog);
			bw = new BufferedWriter(fw);
			
			//tell server (stock market) that trader has gone online
			
			
			while (true)
			{
				byte[] receiveData = new byte[1024];
				
				// Recieve a packet
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try
                {
                    //DatagramSocket receiverSocket;
					socket.receive(receivePacket);
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }
                
				LinkedList<Stock> stockList = new LinkedList<Stock>();
				stockList.add(deserialize(receivePacket.getData()));
				
				buyStock(stockList);
				sellStock(stockList);
					
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}*/
	}
	
	private class ConnectionToServer {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        ConnectionToServer(Socket sock) throws IOException {
            this.socket = sock;
            System.out.println("12");
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("1234");
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("141");

            Thread read = new Thread(){
                public void run(){
                    while(true){
                        try{
                            Object obj = in.readObject();
                            messages.put(obj);
                        }
                        catch(IOException | ClassNotFoundException | InterruptedException e){ e.printStackTrace(); }
                    }
                }
            };
            System.out.println("6");

            read.setDaemon(true);
            read.start();
        }

        private void write(Object obj) {
            try{
                out.writeObject(obj);
            }
            catch(IOException e){ e.printStackTrace(); }
        }


    }

    public void send(Object obj) {
        server.write(obj);
    }
}
