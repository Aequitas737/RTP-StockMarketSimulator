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

	public static void main(String[] args) throws Exception{
		System.out.println("asdf");
		TestTrader trader = new TestTrader();
		System.out.println("test trader launched");
	}
	
	@Override
	public void buyStock(LinkedList<Stock> stockList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sellStock(LinkedList<Stock> stockList) {
		// TODO Auto-generated method stub

	}

}
