package server;

public class Stock {

	private String name;
	private double price;
	private int quantity;
	
	public Stock(String name, double price){
		this.name = name;
		this.price = price;
		this.quantity = 0;
	}
	public String getName() {
		return this.name;
	}

	public void updatePrice(double newPrice){
		this.price = newPrice;
	}
	
	public double getPrice(){
		return this.price;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
	
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}
}
