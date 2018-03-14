import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		WebDriver driver = new ChromeDriver();

		// selenium is lame about relative paths
		File testFile = new File("testfiles/Cryptocurrency Screener - Yahoo Finance.html");
		String absolutePath = String.valueOf(testFile.getAbsoluteFile());

//		driver.get("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");
		driver.get("file:///" + absolutePath);

		List<WebElement> elementsList = driver.findElements(By.xpath("//*[@id=\"scr-res-table\"]/table/tbody/tr[*]/td[2]/a"));

		System.out.printf("Total Pairs: %s\n\n", elementsList.size());
		for (WebElement anElement : elementsList) {
			System.out.println(anElement.getText());
		}
	}
}
