import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		WebDriver driver = new ChromeDriver();

		// selenium is lame about relative paths
		File testFile = new File("testfiles/Cryptocurrency Screener - Yahoo Finance.html");
		String absolutePath = String.valueOf(testFile.getAbsoluteFile());
		driver.get("file:///" + absolutePath);

//		driver.get("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");

		List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[*]/td[2]"));
		List<WebElement> columns = driver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/thead/tr/th[*]/span"));

		List<String> symbols = new ArrayList<String>();
		for (WebElement r : rows)
			symbols.add(r.getText());
		System.out.printf("ROWS:\t%s\n", symbols.toString());

		List<String> properties = new ArrayList<String>();
		for (WebElement c : columns)
			properties.add(c.getText());
		System.out.printf("COLUMNS:\t%s\n\n", properties.toString());

		// build a list of coins with their respective properties
		List<Coin> coins = new ArrayList<Coin>();
//		for (int i = 1; i < symbols.size() + 1; i++) {
		for (int i = 1; i < 3; i++) {
			String xpath = "//*[@id=\"scr-res-table\"]/table/tbody/tr[" +
					i + "]/td[position() >= 2 and not(position() > 11)]";
			coins.add(new Coin(properties,
					driver.findElements(By.xpath(xpath))));
		}

		for (Coin coin: coins) {
			System.out.println(coin.toString());
		}

		if (Snapshot.commitSnapshot(coins))
			System.out.println("Snapshot Successfully Committed to DB");
		else
			System.out.println("Failed to Push to database");
	}
}
