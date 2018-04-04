import java.lang.reflect.Field;
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
	private static List<String> properties;

	public Coin(List<String> properties) {
		Coin.properties = properties;
	}

	void parseRow(List<String> values) {
		Iterator propertiesItr = properties.iterator();
		Iterator valuesItr = values.iterator();
		Map<String, String> coinsProperties = new TreeMap<>();

		// collate key-value pairs
		while (propertiesItr.hasNext() && valuesItr.hasNext())
			coinsProperties.put(propertiesItr.next().toString(), valuesItr.next().toString());

		// attempt to evaluate each coin property from the associative array
		try {
			this.symbol = coinsProperties.get("Symbol");
			this.name = coinsProperties.get("Name");
			this.price = Double.valueOf(coinsProperties.get("Price (Intraday)").replaceAll(",", ""));
			this.change = Double.valueOf(coinsProperties.get("Change").replaceAll("[%,]", ""));
			this.pChange = Double.valueOf(coinsProperties.get("% Change").replaceAll("[%,]", ""));
			this.marketCap = parseMagnitude(coinsProperties.get("Market Cap"));
			this.volume = parseMagnitude(coinsProperties.get("Volume in Currency (Since 0:00 UTC)"));
			this.volume24h = parseMagnitude(coinsProperties.get("Volume in Currency (24Hr)"));
			this.totalVolume24h = parseMagnitude(coinsProperties.get("Total Volume All Currencies (24Hr)"));
			this.circulatingSupply = parseMagnitude(coinsProperties.get("Circulating Supply"));
		} catch (NullPointerException e) {
			System.out.println("Values Collation Error: Could not find Value");
			throw new NullPointerException(e.getMessage());
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
		StringBuilder remit = new StringBuilder("Coin{ ");

		for (Field field : this.getClass().getDeclaredFields())
			try {
				if (field.getType().toString().contains("class java.lang."))
					remit.append(field.getName()).append("=").append(field.get(this)).append(" ");
			} catch (IllegalAccessException e) {
				System.out.println("IllegalAccessException - " + field.getName());
			}

		return remit.toString() + '}';
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
