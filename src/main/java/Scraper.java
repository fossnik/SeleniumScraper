import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
		List<String> symbols = getSymbols();
		System.out.printf("ROWS (symbols):\t%s\n", symbols.toString());

		List<String> properties = getProperties();
		System.out.printf("COLUMNS (properties):\t%s\n\n", properties.toString());

		// derive a list of coins with their respective properties from table body
		List<WebElement> tableRows = chromeDriver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr"));

		return parseTableData(tableRows, properties);
	}

	private List<Coin> parseTableData(List<WebElement> tableRows, List<String> properties) {
		List<Coin> coins = new ArrayList<>();

		for (WebElement row : tableRows) {
			Coin coin = new Coin(properties);
			List<String> rowElements = row.findElements(By.tagName("td")).stream()
					.map(WebElement::getText)
					.filter(s -> (!s.isEmpty()))
					.collect(Collectors.toList());

			coin.parseRow(rowElements);

			coins.add(coin);
		}

		return coins;
	}

	private List<String> getSymbols() {
		String xpath = "//*[@id=\"scr-res-table\"]/table/tbody/tr[*]/td[2]/a";
		List<WebElement> headRows = chromeDriver.findElements(By.xpath(xpath));
		return headRows.stream().map(WebElement::getText).collect(Collectors.toList());
	}

	private List<String> getProperties() {
		String xpath = "//*[@id=\"scr-res-table\"]/table/thead/tr/th[*]/span";
		List<WebElement> headCols = chromeDriver.findElements(By.xpath(xpath));
		return headCols.stream().map(WebElement::getText).collect(Collectors.toList());
	}
}
