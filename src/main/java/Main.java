import java.util.List;

public class Main {
	public static void main(String[] args) {

		Scraper scraper = new Scraper("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");

		try {
			List<Coin> coins = scraper.compileSnapshot();

			for (Coin coin: coins)
				System.out.println(coin.toString());

			if (Snapshot.commitSnapshot(coins))
				System.out.println("\n Snapshot Successfully Committed to DB");
			else
				System.out.println("\n Failed to Push to database");

			scraper.chromeDriver.quit();
			System.exit(0);
		}
		catch (Exception e) {
			e.printStackTrace();
			scraper.chromeDriver.quit();
			System.exit(1);
		}

	}
}
