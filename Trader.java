import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public abstract class Trader
{
	protected int balance;
	protected LinkedList<Stock> portfolio;
	protected LinkedList<String> ownedStock; //used for quick finding stocks bought by name
	
	public abstract void buyStock(Stock stockToBuy);
	
	public abstract void sellStock(Stock stockToBuy);
	
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
	
	public static void main(String[] args) throws Exception
	{	
		try {
			//Set up connection to Stock Market
			DatagramSocket socket = new DatagramSocket(4003);
			InetAddress marketIPAdress = InetAddress.getByName("Herpderp"); //change IP address
			
			//initialize class variables
			balance = 0;
			portfolio = new LinkedList<Stock>();
			
			// Get current working directory and set up log
			String dir = Paths.get(".").toAbsolutePath().normalize().toString();
			String logDir = dir + "\\TraderLog.txt";
			File traderLog = new File(logDir);
			fw = new FileWriter(traderLog);
			bw = new BufferedWriter(fw);
			while (true)
			{
				byte[] receiveData = new byte[1024];
				
				// Recieve a packet
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try
                {
                    receiverSocket.receive(receivePacket);
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }
				Stock newStock = deserialize(receivePacket.getData());
				
				//decide to either buy or sell stock, or update portfolio
				if (ownedStock.contains(newStock.getName()))
				{
					sellStock(newStock);
				}
				else
					buyStock(newStock);
					
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