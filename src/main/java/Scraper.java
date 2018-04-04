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

	Scraper(String url) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-javascript");
		this.chromeDriver = new ChromeDriver(options);
		this.chromeDriver.get(url);

		if (this.chromeDriver.getTitle().isEmpty())
			throw new ExceptionInInitializerError("\n[empty page title] - Scraper Cannot Access Resource:\n- " + url + " -\n");
	}

	List<Coin> compileSnapshot() {
		List<String> symbols = getSymbols();
		if (symbols.size() > 2)
			System.out.printf("ROWS (symbols):\t%s\n", symbols.toString());
		else
			System.out.println("Unable to Acquire Coin Symbols From Row Header");

		List<String> properties = getProperties();
		if (properties.size() == 12)
			System.out.printf("COLUMNS (properties):\t%s\n\n", properties.toString());
		else
			System.out.println("Expected 12 Properties in Column Header, but found " + properties.size());

		// derive a coin and its respective properties from each table row
		String xpath = "//*[@id=\"scr-res-table\"]/table/tbody/tr";
		List<WebElement> tableRows = chromeDriver.findElements(By.xpath(xpath));
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
