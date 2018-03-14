import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		WebDriver driver = new ChromeDriver();

		// selenium is lame about relative paths
		File testFile = new File("testfiles/Cryptocurrency Screener - Yahoo Finance.html");
		String absolutePath = String.valueOf(testFile.getAbsoluteFile());

//		driver.get("https://finance.yahoo.com/cryptocurrencies?offset=0&count=150");
		driver.get("file:///" + absolutePath);


		System.out.println(driver.getTitle());
//		List<WebElement> allFormChildElements = driver.findElements(By.xpath("//form[@name='something']/*"));

	}
}
