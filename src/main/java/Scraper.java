import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Scraper {
	WebDriver chromeDriver;

	Scraper() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-javascript");
		this.chromeDriver = new ChromeDriver(options);

		// TEST FILE
//		File testFile = new File("src/main/resources/Cryptocurrency Screener - Yahoo Finance.html");
//		String absolutePath = String.valueOf(testFile.getAbsoluteFile());
//		driver.get("file:///" + absolutePath);

		this.chromeDriver.get("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");
	}

	List<Coin> CompileSnapshot() {
		List<WebElement> headRows = chromeDriver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[*]/td[2]/a"));
		List<String> symbols = headRows.stream().map(WebElement::getText).collect(Collectors.toList());
		System.out.printf("ROWS (symbols):\t%s\n", symbols.toString());

		List<WebElement> headCols = chromeDriver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/thead/tr/th[*]/span"));
		List<String> properties = headCols.stream().map(WebElement::getText).collect(Collectors.toList());
		System.out.printf("COLUMNS (properties):\t%s\n\n", properties.toString());

		// build a list of coins with their respective properties
		List<Coin> coins = new ArrayList<Coin>();

		Coin coin = new Coin(properties);
		for (int row = 1; row < symbols.size() + 1; row++) {
			String xpath = "//*[@id=\"scr-res-table\"]/table/tbody/tr[" +
					 row + "]/td[position() > 1 and position() < 12]";

			List<WebElement> tableData = chromeDriver.findElements(By.xpath(xpath));
			
			coin.parseScrapedData(tableData);

			coins.add(coin);
		}

		return coins;
	}
}
