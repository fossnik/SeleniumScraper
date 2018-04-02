import java.util.List;

public class Main {
	public static void main(String[] args) {

		Scraper scraper = new Scraper();

		try {
			List<Coin> coins = scraper.CompileSnapshot();
		
			for (Coin coin: coins)
				System.out.println(coin.toString());

			if (Snapshot.commitSnapshot(coins))
				System.out.println("Snapshot Successfully Committed to DB");
			else
				System.out.println("Failed to Push to database");

			scraper.chromeDriver.close();
			scraper.chromeDriver.quit();
			System.exit(0);
		}
		catch (Exception e) {
			scraper.chromeDriver.close();
			scraper.chromeDriver.quit();
			System.exit(1);
		}


	}
}
