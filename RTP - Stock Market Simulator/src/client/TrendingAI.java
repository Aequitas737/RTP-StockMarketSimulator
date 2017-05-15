public class TrendingAI implements Trader
{
	public void buyStock(Stock stockToBuy) throws Exception
    {
        if (buyAlgorithm(stockToBuy))
		{
			//send request to buy from stock marketIPAdress
			
			portfolio.add(stockToBuy);
			ownedStock.add(stockToBuy.getName());
			//do something with balance here
			
		}
    }
	
	public void sellStock(Stock stockToBuy) throws Exception
    {
        if (sellAlgorithm(stockToBuy))
		{
			
		}
		else
		{
			//update stocks
		}
    
}