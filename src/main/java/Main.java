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

//		driver.get("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");
		driver.get("file:///" + absolutePath);

		List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[*]/td[2]"));
		List<WebElement> columns = driver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/thead/tr/th[*]/span"));

		List<String> symbols = new ArrayList<String>();
		for (WebElement r : rows)
			symbols.add(r.getText());

		List<String> properties = new ArrayList<String>();
		for (WebElement c : columns)
			properties.add(c.getText());

		// build a list of coins with their respective properties
		List<Coin> coins = new ArrayList<Coin>();
		for (int i = 2; i < symbols.size() + 1; i++) {
			coins.add(new Coin(
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[2]/a")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[3]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[4]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[5]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[6]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[7]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[8]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[9]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[10]")),
					driver.findElement(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[" + i + "]/td[11]"))
			));
		}

		for (Coin coin: coins) {
			System.out.println(coin.toString());
		}
	}
}
