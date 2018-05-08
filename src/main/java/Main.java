import java.util.List;

public class Main {
	public static void main(String[] args) {

		System.out.println("\nLoading Selenium...\n");
		Scraper scraper = new Scraper("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");

		try {
			System.out.println("\nScraping...\n");
			List<Coin> coins = scraper.compileSnapshot();
			for (Coin coin: coins)
				System.out.println(coin.toString());

			System.out.println("\nScraping Completed - Persisting to DB...");
			if (Snapshot.commitSnapshot(coins))
				System.out.println("\n\tSnapshot Successfully Committed to DB");
			else
				System.out.println("\n\tFailed to Push to database");

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
