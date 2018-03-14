import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.Arrays;
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

		// construct matrix of coins and their respective properties/metrics
		String[][] matrix = new String[columnHeads.size()][rowHeads.size()];
		for (int column = 0, numPairs = columnHeads.size(); column < numPairs; column++) {
			WebElement pair = columnHeads.get(column);

			for (int row = 0, numProperties = rowHeads.size(); row < numProperties; row++) {
				WebElement property = rowHeads.get(row);
				matrix[column][row] = property.getText();
			}

		}

		// print matrix
		System.out.println(Arrays.deepToString(matrix));
	}
}
