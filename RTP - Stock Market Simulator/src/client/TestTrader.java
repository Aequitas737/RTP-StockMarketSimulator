package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;

import server.Stock;

public class TestTrader extends Trader {

	public TestTrader() throws UnknownHostException, IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws UnknownHostException, IOException{
		TestTrader trader = new TestTrader();
	}
	
	@Override
	public void buyStock(LinkedList<Stock> stockList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sellStock(LinkedList<Stock> stockList) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void analyzeMarketForDecision() {
		// TODO Auto-generated method stub
		
	}
}
