import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		WebDriver driver = new ChromeDriver(options);

		// TEST FILE
//		File testFile = new File("src/main/resources/Cryptocurrency Screener - Yahoo Finance.html");
//		String absolutePath = String.valueOf(testFile.getAbsoluteFile());
//		driver.get("file:///" + absolutePath);

		driver.get("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");
		WebElement table = driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table"));

		List<WebElement> headRows = table.findElements(By.xpath("//tbody/tr[*]/td[2]/a"));
		List<String> symbols = new ArrayList<String>();
		for (WebElement r : headRows)
			symbols.add(r.getText());
		System.out.printf("ROWS:\t%s\n", symbols.toString());

		List<WebElement> headCols = table.findElements(By.xpath("//thead/tr/th[*]/span"));
		List<String> properties = new ArrayList<String>();
		for (WebElement c : headCols)
			properties.add(c.getText());
		System.out.printf("COLUMNS:\t%s\n\n", properties.toString());

		// build a list of coins with their respective properties
		List<Coin> coins = new ArrayList<Coin>();
		for (int i = 1; i < symbols.size() + 1; i++) {
			String xpath = "//tbody/tr[" +
					i + "]/td[position() >= 2 and not(position() > 11)]";
			coins.add(new Coin(properties,
					table.findElements(By.xpath(xpath))));
		}

		for (Coin coin: coins)
			System.out.println(coin.toString());

		if (Snapshot.commitSnapshot(coins))
			System.out.println("Snapshot Successfully Committed to DB");
		else
			System.out.println("Failed to Push to database");
	}
}
