import java.util.List;
import java.sql.*;

class Snapshot {

	private static final String CONNECTION_STRING = "jdbc:sqlite:src/main/resources/coinsnapshot.db";
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

	static boolean commitSnapshot(List<Coin> coins) {
		// create connection
		if (!createConnection()) return false;

		// attempt to create tables for all coins
		for (Coin coin: coins) {

			// conform to table title strictures
			String TABLE_TITLE = coin.getSymbol().toLowerCase().replaceAll("[^a-z]", "");

			// create table (if not exist)
			if (!createTable(TABLE_TITLE)) return false;

			// insert record
			if (!insertRecord(coin, TABLE_TITLE)) return false;
		}

		// close connection
		return closeConnection();
	}

	private static boolean insertRecord(Coin c, String TABLE_TITLE) {
		String INSERT_SNAPSHOT =
				"INSERT INTO " + TABLE_TITLE + " values(NULL,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement insertCoin = conn.prepareStatement(INSERT_SNAPSHOT);
			insertCoin.setString(1, c.getSymbol());
			insertCoin.setString(2, c.getName());
			insertCoin.setDouble(3, c.getPrice());
			insertCoin.setDouble(4, c.getChange());
			insertCoin.setDouble(5, c.getpChange());
			insertCoin.setDouble(6, c.getMarketCap());
			insertCoin.setDouble(7, c.getVolume());
			insertCoin.setDouble(8, c.getVolume24h());
			insertCoin.setDouble(9, c.getTotalVolume24h());
			insertCoin.setDouble(10, c.getCirculatingSupply());
			insertCoin.executeUpdate();
			insertCoin.close();
		} catch (SQLException e) {
			System.out.println("Couldn't insert data: " + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean createTable(String TABLE_TITLE) {
		String CREATE_COIN_TABLE =
				"CREATE TABLE IF NOT EXISTS " + TABLE_TITLE +
						"(" +
						"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
						COLUMN_DATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
						COLUMN_SYMBOL + " String, " +
						COLUMN_NAME + " String, " +
						COLUMN_PRICE + " Double," +
						COLUMN_CHANGE + " Double, " +
						COLUMN_PCHANGE + " Double, " +
						COLUMN_MARKETCAP + " Double, " +
						COLUMN_VOLUME + " Double, " +
						COLUMN_VOLUME24H + " Double, " +
						COLUMN_TOTALVOLUME24H + " Double, " +
						COLUMN_CIRCULATINGSUPPLY + " Double" +
						");"
				;

		try {
			PreparedStatement createTable = conn.prepareStatement(CREATE_COIN_TABLE);
			createTable.execute();
			createTable.close();
		} catch (SQLException e) {
			System.out.printf("Couldn't create table for coin {%s}\n%s\n", TABLE_TITLE, e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean createConnection() {
		if(conn == null) {
			try {
				conn = DriverManager.getConnection(CONNECTION_STRING);
			} catch (SQLException e) {
				System.out.println("Couldn't connect to database: " + e.getMessage());
				return false;
			}
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
