package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import server.Stock;

public class TestTrader extends Trader {

	public TestTrader() throws UnknownHostException, IOException {
		super(10000.0);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws UnknownHostException, IOException{
		TestTrader trader = new TestTrader();
	}
	

	public void buyStock(LinkedList<Stock> stockList) {
		// TODO Auto-generated method stub

	}


	public void sellStock(LinkedList<Stock> stockList) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performTrading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Stock> getStocksToBuy(ArrayList<Stock> stockList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Stock> getStocksToSell(ArrayList<Stock> stockList) {
		// TODO Auto-generated method stub
		return null;
	}
}
