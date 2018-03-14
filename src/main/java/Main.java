import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		WebDriver driver = new ChromeDriver();

		// selenium is lame about relative paths
		File testFile = new File("testfiles/Cryptocurrency Screener - Yahoo Finance.html");
		String absolutePath = String.valueOf(testFile.getAbsoluteFile());

//		driver.get("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");
		driver.get("file:///" + absolutePath);

		List<WebElement> columnHeads = driver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[*]/td[2]/a"));
		List<WebElement> rowHeads = driver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/thead/tr/th[*]/span"));

		System.out.printf("Row Heads: %s\n", rowHeads.size());
		for (WebElement anElement : rowHeads) {
			System.out.print(anElement.getText() + " | ");
		}

		System.out.println("\n");

		System.out.printf("Column Heads: %s\n", columnHeads.size());
		for (WebElement anElement : columnHeads) {
			System.out.print(anElement.getText() + " | ");
		}
	}
}
