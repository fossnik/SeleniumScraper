import java.util.List;
import java.sql.*;

class Snapshot {

	private static final String CONNECTION_STRING = "jdbc:sqlite:src/main/resources/RelationalSnapshot.db";
	private static Connection conn;

	// SQL vocabulary
	private static final String COLUMN_DATETIME = "dateCreated";
	private static final String COLUMN_SYMBOL = "symbol";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_PRICE = "price";
	private static final String COLUMN_CHANGE = "change";
	private static final String COLUMN_PCHANGE = "pChange";
	private static final String COLUMN_MARKETCAP = "marketCap";
	private static final String COLUMN_VOLUME = "volume";
	private static final String COLUMN_VOLUME24H = "volume24h";
	private static final String COLUMN_TOTALVOLUME24H = "totalVolume24h";
	private static final String COLUMN_CIRCULATINGSUPPLY = "circulatingSupply";

	private static final String COIN_NAMES_AND_SYMBOLS_TABLE = "all_your_coin";

	static boolean commitSnapshot(List<Coin> coins) {
		if (!createConnection()) return false;

		if (!createTableOfAllCoinsNamesAndSymbols()) return false;

		// attempt to create tables for all coins
		for (Coin coin: coins) {
			if (!createIndividualCoinTable(coin)) return false;
			if (!insertRecord(coin)) return false;
		}

		return closeConnection();
	}

	private static boolean createTableOfAllCoinsNamesAndSymbols() {
		String CREATE_COIN_NAMES_AND_SYMBOLS_TABLE =
				"CREATE TABLE IF NOT EXISTS " + COIN_NAMES_AND_SYMBOLS_TABLE +
						"(" +
						COLUMN_SYMBOL + " TEXT PRIMARY KEY, " +
						COLUMN_NAME + " TEXT" +
						");"
				;

		try {
			PreparedStatement createTable = conn.prepareStatement(CREATE_COIN_NAMES_AND_SYMBOLS_TABLE);
			createTable.execute();
			createTable.close();
		} catch (SQLException e) {
			System.out.println("Failed constructing table of all coin names/symbols\n" +
					"QUERY:\t" + CREATE_COIN_NAMES_AND_SYMBOLS_TABLE + '\n' + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean createIndividualCoinTable(Coin coin) {
		String TABLE_TITLE = getTable_title(coin);

		String CREATE_SNAPSHOT_RECORDS_TABLE =
				"CREATE TABLE IF NOT EXISTS " + TABLE_TITLE +
						"(" +
						"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
						COLUMN_DATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
						COLUMN_SYMBOL + " TEXT, " +
						COLUMN_PRICE + " Double, " +
						COLUMN_CHANGE + " Double, " +
						COLUMN_PCHANGE + " Double, " +
						COLUMN_MARKETCAP + " Double, " +
						COLUMN_VOLUME + " Double, " +
						COLUMN_VOLUME24H + " Double, " +
						COLUMN_TOTALVOLUME24H + " Double, " +
						COLUMN_CIRCULATINGSUPPLY + " Double, " +
						"FOREIGN KEY(" + COLUMN_SYMBOL + ") REFERENCES " +
						COIN_NAMES_AND_SYMBOLS_TABLE + "(" + COLUMN_SYMBOL + ")" +
						");"
				;

		try {
			PreparedStatement createTable = conn.prepareStatement(CREATE_SNAPSHOT_RECORDS_TABLE);
			createTable.execute();
			createTable.close();
		} catch (SQLException e) {
			System.out.println("Failed creating of individual coin table\n" +
					"QUERY:\t" + CREATE_SNAPSHOT_RECORDS_TABLE + '\n' + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean insertRecord(Coin c) {
		// add this coin to the table of all coin names and symbols (if not exist)
		String INSERT_COINNAME = String.format("INSERT OR IGNORE INTO %s values(?,?);", COIN_NAMES_AND_SYMBOLS_TABLE);
		try {
			PreparedStatement insertCoin = conn.prepareStatement(INSERT_COINNAME);
			insertCoin.setString(1, c.getSymbol());
			insertCoin.setString(2, c.getName());
			insertCoin.executeUpdate();
			insertCoin.close();
		} catch (SQLException e) {
			System.out.println("Failed appending coin to Table of all coins\n" +
					"QUERY:\t" + INSERT_COINNAME + '\n' + e.getMessage());
			return false;
		}

		String TABLE_TITLE = getTable_title(c);

		String INSERT_SNAPSHOT =
				"INSERT INTO " + TABLE_TITLE + " values(NULL,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement insertSnap = conn.prepareStatement(INSERT_SNAPSHOT);
			insertSnap.setString(1, c.getSymbol());
			insertSnap.setDouble(2, c.getPrice());
			insertSnap.setDouble(3, c.getChange());
			insertSnap.setDouble(4, c.getpChange());
			insertSnap.setDouble(5, c.getMarketCap());
			insertSnap.setDouble(6, c.getVolume());
			insertSnap.setDouble(7, c.getVolume24h());
			insertSnap.setDouble(8, c.getTotalVolume24h());
			insertSnap.setDouble(9, c.getCirculatingSupply());
			insertSnap.executeUpdate();
			insertSnap.close();
		} catch (SQLException e) {
			System.out.println("Failed inserting snapshot record\n" +
					"QUERY:\t" + INSERT_SNAPSHOT + '\n' + e.getMessage());
			return false;
		}
		return true;
	}

	private static String getTable_title(Coin c) {
		// normalize input and conform to SQL strictures for table titles
		return c.getSymbol().toLowerCase().replaceAll("[^a-z]", "");
	}

	private static boolean createConnection() {
		try {
			conn = DriverManager.getConnection(CONNECTION_STRING);
		} catch (SQLException e) {
			System.out.println("Couldn't connect to database: " + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean closeConnection() {
		try {
			conn.close();
		} catch(SQLException e) {
			System.out.println("Couldn't close connection: " + e.getMessage());
			return false;
		}
		return true;
	}

}
