import java.util.List;
import java.sql.*;

class Snapshot {

	private static final String CONNECTION_STRING = "jdbc:sqlite:src/main/resources/RelationalSnapshot.db";
	private static Connection conn;

	// SQL Vocabulary - Individual Coin Tables
	private static final String COLUMN_DATETIME = "dateCreated";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_PRICE = "price";
	private static final String COLUMN_CHANGE = "change";
	private static final String COLUMN_PCHANGE = "pChange";
	private static final String COLUMN_MARKETCAP = "marketCap";
	private static final String COLUMN_VOLUME = "volume";
	private static final String COLUMN_VOLUME24H = "volume24h";
	private static final String COLUMN_TOTALVOLUME24H = "totalVolume24h";
	private static final String COLUMN_CIRCULATINGSUPPLY = "circulatingSupply";

	// SQL Vocabulary - Table of all Names & Symbols
	private static final String COIN_NAMES_AND_SYMBOLS_TABLE = "_all_your_coin";
	private static final String COLUMN_SYMBOL_SAFE = "symbol_safe";
	private static final String COLUMN_SYMBOL_FULL = "symbol_full";

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
						COLUMN_SYMBOL_SAFE + " TEXT PRIMARY KEY, " +
						COLUMN_SYMBOL_FULL + " TEXT, " +
						COLUMN_NAME + " TEXT" +
						");"
				;

		try {
			PreparedStatement createTable = conn.prepareStatement(CREATE_COIN_NAMES_AND_SYMBOLS_TABLE);
			createTable.execute();
			createTable.close();
		} catch (SQLException e) {
			System.out.println("-FAIL db- initial creation of names & symbols table\n" +
					"QUERY:\t" + CREATE_COIN_NAMES_AND_SYMBOLS_TABLE + '\n' + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean createIndividualCoinTable(Coin coin) {
		// add this coin to the table of all coin names and symbols (if not exist)
		String INSERT_COINNAME = "INSERT OR IGNORE INTO " + COIN_NAMES_AND_SYMBOLS_TABLE + " values(?,?,?);";

		try {
			PreparedStatement insertCoin = conn.prepareStatement(INSERT_COINNAME);
			insertCoin.setString(1, getSafeSymbol(coin));
			insertCoin.setString(2, coin.getSymbol());
			insertCoin.setString(3, coin.getName());
			insertCoin.executeUpdate();
			insertCoin.close();
		} catch (SQLException e) {
			System.out.println("-FAIL db- add new coin to names & symbols table\n" +
					"QUERY:\t" + INSERT_COINNAME + '\n' + e.getMessage());
			return false;
		}

		String TABLE_TITLE = getSafeSymbol(coin);
		String CREATE_SNAPSHOT_RECORDS_TABLE =
				"CREATE TABLE IF NOT EXISTS " + TABLE_TITLE +
						"(" +
						"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
						COLUMN_DATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
						COLUMN_SYMBOL_SAFE + " TEXT, " +
						COLUMN_PRICE + " Double, " +
						COLUMN_CHANGE + " Double, " +
						COLUMN_PCHANGE + " Double, " +
						COLUMN_MARKETCAP + " Double, " +
						COLUMN_VOLUME + " Double, " +
						COLUMN_VOLUME24H + " Double, " +
						COLUMN_TOTALVOLUME24H + " Double, " +
						COLUMN_CIRCULATINGSUPPLY + " Double, " +
						"FOREIGN KEY(" + COLUMN_SYMBOL_SAFE + ") REFERENCES " +
						COIN_NAMES_AND_SYMBOLS_TABLE + "(" + COLUMN_SYMBOL_SAFE + ")" +
						");"
				;

		try {
			PreparedStatement createTable = conn.prepareStatement(CREATE_SNAPSHOT_RECORDS_TABLE);
			createTable.execute();
			createTable.close();
		} catch (SQLException e) {
			System.out.println("-FAIL db- create individual coin table\n" +
					"QUERY:\t" + CREATE_SNAPSHOT_RECORDS_TABLE + '\n' + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean insertRecord(Coin c) {
		String TABLE_TITLE = getSafeSymbol(c);
		String INSERT_SNAPSHOT =
				"INSERT INTO " + TABLE_TITLE + " values(NULL,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?);";

		try {
			PreparedStatement insertSnap = conn.prepareStatement(INSERT_SNAPSHOT);
			insertSnap.setString(1, getSafeSymbol(c));
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
			System.out.println("-FAIL db- insert snapshot record\n" +
					"QUERY:\t" + INSERT_SNAPSHOT + '\n' + e.getMessage());
			return false;
		}
		return true;
	}

	private static String getSafeSymbol(Coin c) {
		// normalize input and conform to SQL strictures for table titles
		return c.getSymbol().toLowerCase().replaceAll("[^a-z]", "");
	}

	private static boolean createConnection() {
		try { conn = DriverManager.getConnection(CONNECTION_STRING); }
		catch (SQLException e) {
			System.out.println("-FAIL db- getConnection: " + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean closeConnection() {
		try { conn.close(); }
		catch(SQLException e) {
			System.out.println("-FAIL db- close: " + e.getMessage());
			return false;
		}
		return true;
	}
}
