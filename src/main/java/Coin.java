import org.openqa.selenium.WebElement;

import java.security.InvalidParameterException;
import java.util.*;

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

	public Coin(List<String> properties, List<WebElement> values) {
		Iterator p = properties.iterator();
		Iterator v = values.iterator();
		Map<String, String> coinsProperties = new HashMap<String, String>();

		// collate key-value pairs
		while (p.hasNext() && v.hasNext())
			coinsProperties.put(p.next().toString(), ((WebElement)v.next()).getText());

		try {
			this.symbol = coinsProperties.get("Symbol");
			this.name = coinsProperties.get("Name");
			this.price = Double.valueOf(coinsProperties.get("Price (Intraday)").replaceAll(",", ""));
			this.change = Double.valueOf(coinsProperties.get("Change").replaceAll(",", "").replaceAll("%", ""));
			this.pChange = Double.valueOf(coinsProperties.get("% Change").replaceAll(",", "").replaceAll("%", ""));
			this.marketCap = parseMagnitude(coinsProperties.get("Market Cap"));
			this.volume = parseMagnitude(coinsProperties.get("Volume in Currency (Since 0:00 UTC)"));
			this.volume24h = parseMagnitude(coinsProperties.get("Volume in Currency (24Hr)"));
			this.totalVolume24h = parseMagnitude(coinsProperties.get("Total Volume All Currencies (24Hr)"));
			this.circulatingSupply = parseMagnitude(coinsProperties.get("Circulating Supply"));
		} catch (NullPointerException e) {
			throw new NullPointerException("Values Collation Error: Could not find Value");
		}
	}

	private Double parseMagnitude(String s) {

		String string = s.replaceAll("[^0-9.MBT]", "");

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

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public Double getPrice() {
		return price;
	}

	public Double getChange() {
		return change;
	}

	public Double getpChange() {
		return pChange;
	}

	public Double getMarketCap() {
		return marketCap;
	}

	public Double getVolume() {
		return volume;
	}

	public Double getVolume24h() {
		return volume24h;
	}

	public Double getTotalVolume24h() {
		return totalVolume24h;
	}

	public Double getCirculatingSupply() {
		return circulatingSupply;
	}
}
