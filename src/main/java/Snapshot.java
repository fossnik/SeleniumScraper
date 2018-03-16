import java.util.List;
import java.sql.*;

class Snapshot {

	private static final String CONNECTION_STRING = "jdbc:sqlite:src/main/resources/coinsnapshot.db";
	private static Connection conn;
	private static PreparedStatement createTable;

	// SQL vocabulary
	private static String TABLE_TITLE = null;
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

	private static String INSERT_SNAPSHOT =
			"INSERT INTO " + TABLE_TITLE + " values(?,?,?,?,?,?,?,?,?);";

	static boolean commitSnapshot(List<Coin> coins) {

		// create connection
		if(conn == null) {
			try {
				conn = DriverManager.getConnection(CONNECTION_STRING);
			} catch (SQLException e) {
				System.out.println("Couldn't connect to database: " + e.getMessage());
				return false;
			}
		}

		// attempt to create tables for all coins
		for (Coin c: coins) {
			// table schema
			String TABLE_TITLE = c.getSymbol();
			String CREATE_TABLE =
					"CREATE TABLE " + TABLE_TITLE +
						'(' +
							COLUMN_SYMBOL + " String, " +
							COLUMN_NAME + " String, " +
							COLUMN_PRICE + " Double," +
							COLUMN_CHANGE + " Double, " +
							COLUMN_PCHANGE + " Double, " +
							COLUMN_MARKETCAP + " Double, " +
							COLUMN_VOLUME + " Double, " +
							COLUMN_VOLUME24H + " Double, " +
							COLUMN_TOTALVOLUME24H + " Double, " +
							COLUMN_CIRCULATINGSUPPLY + " Double, " +
						");"
					;

			try {
				// TODO: If table doesn't exist for this coin, create it
				// if( somethign something somethign)
				createTable = conn.prepareStatement(CREATE_TABLE);
				createTable.execute();
			} catch (SQLException e) {
				System.out.printf("Couldn't create table for coin {%s}\n%s", TABLE_TITLE, e.getMessage());
				return false;
			}

			if (conn != null) {
				try {
					// TODO: Insert time record
					PreparedStatement insertCoin = conn.prepareStatement(INSERT_SNAPSHOT);
					insertCoin.setString(1, c.getSymbol());
					insertCoin.setString(2, c.getName());
					insertCoin.setDouble(3, c.getChange());
					insertCoin.setDouble(4, c.getpChange());
					insertCoin.setDouble(5, c.getMarketCap());
					insertCoin.setDouble(6, c.getVolume());
					insertCoin.setDouble(7, c.getVolume24h());
					insertCoin.setDouble(8, c.getTotalVolume24h());
					insertCoin.setDouble(9, c.getCirculatingSupply());
					insertCoin.execute();
					insertCoin.close();
				} catch (SQLException e) {
					System.out.println("Couldn't insert data: " + e.getMessage());
					return false;
				}
			}
		}

		closeDB();

		return true;
	}

	private static void closeDB() {
		try {
			if(createTable !=  null)
				createTable.close();
			if(conn != null)
				conn.close();
		} catch(SQLException e) {
			System.out.println("Couldn't close connection: " + e.getMessage());
		}
	}

}
