import org.openqa.selenium.WebElement;

import java.security.InvalidParameterException;

public class Coin {
	private String symbol;
	private String name;
	private Double price;
	private Double change;
	private Double pChange;
	private Double marketCap;
	private Double volume;
	private Double volume24h;
	private Double totalVolume24h;
	private Double circulatingSupply;

	public Coin(WebElement symbol, WebElement name, WebElement price, WebElement change, WebElement pChange, WebElement marketCap, WebElement volume, WebElement volume24h, WebElement totalVolume24h, WebElement circulatingSupply) {
		this.symbol = symbol.getText();
		this.name = name.getText();
		this.price = Double.valueOf(price.getText().replaceAll(",", ""));
		this.change = Double.valueOf(change.getText().replaceAll(",", "").replaceAll("%", ""));
		this.pChange = Double.valueOf(pChange.getText().replaceAll(",", "").replaceAll("%", ""));
		this.marketCap = parseMagnitude(marketCap);
		this.volume = parseMagnitude(volume);
		this.volume24h = parseMagnitude(volume24h);
		this.totalVolume24h = parseMagnitude(totalVolume24h);
		this.circulatingSupply = parseMagnitude(circulatingSupply);
	}
	
	private Double parseMagnitude(WebElement we) {

		String string = we.getText().replaceAll("[^0-9.MBT]", "");

		// M B and T for Millions, Billions, and Trillions. (eg 142.43B	=== 142,000,000,000)
		switch (string.charAt(string.length() - 1)) {
			case 'M':
				return Double.valueOf(string.replaceAll("M", "")) * 1000000D;
			case 'B':
				return Double.valueOf(string.replaceAll("B", "")) * 1000000000D;
			case 'T':
				return Double.valueOf(string.replaceAll("T", "")) * 1000000000000D;
			default:
				if (string.matches("[\\D.]+"))
					throw new InvalidParameterException("Magnitude Conversion Failure - Invalid Non-digit Characters");
				else
					return Double.valueOf(string);
		}
	}

	@Override
	public String toString() {
		return "Coin{" +
				"symbol='" + symbol + '\'' +
				", name='" + name + '\'' +
				", price=" + price +
				", change=" + change +
				", pChange=" + pChange +
				", marketCap=" + marketCap +
				", volume=" + volume +
				", volume24h=" + volume24h +
				", totalVolume24h=" + totalVolume24h +
				", circulatingSupply=" + circulatingSupply +
				'}';
	}
}
