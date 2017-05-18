package client;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import server.Stock;

public abstract class Trader {
	protected static int balance;
	protected static LinkedList<Stock> portfolio;
	protected static LinkedList<String> ownedStock; //used for quick finding stocks bought by name
	
	protected static InetAddress marketIPAdress;
	protected static DatagramSocket socket;
	
	public abstract void buyStock(LinkedList<Stock> stockList);
	
	public abstract void sellStock(LinkedList<Stock> stockList);
	
	public static Stock deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
	
	public void main(String[] args) throws Exception
	{	
		FileWriter fw;
		BufferedWriter bw;
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
                    DatagramSocket receiverSocket;
					socket.receive(receivePacket);
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }
				LinkedList<Stock> stockList = deserialize(receivePacket.getData());
				
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
		}
	}
}
