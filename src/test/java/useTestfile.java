import java.io.File;
import java.util.List;

public class useTestfile {
	//	Essential copy of Main.java
	public static void main(String[] args) {
		// Hard Copy Test .html (static content)
		String relativePathOfHardCopy = "src/main/resources/Cryptocurrency Screener - Yahoo Finance.html";
		String absolutePath = new File(relativePathOfHardCopy).getAbsoluteFile().toString();

		Scraper scraper = new Scraper("file:///" + absolutePath);

		runScraper(scraper);
	}

	private static void runScraper(Scraper scraper) {
		try {
			List<Coin> coins = scraper.compileSnapshot();

			for (Coin coin : coins)
				System.out.println(coin.toString());

			if (Snapshot.commitSnapshot(coins))
				System.out.println("Snapshot Successfully Committed to DB");
			else
				System.out.println("Failed to Push to database");

			scraper.chromeDriver.quit();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			scraper.chromeDriver.quit();
			System.exit(1);
		}
	}
}
