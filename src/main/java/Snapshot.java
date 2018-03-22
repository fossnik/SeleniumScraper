import java.util.List;
import java.sql.*;

class Snapshot {

	private static final String CONNECTION_STRING = "jdbc:sqlite:src/main/resources/coinsnapshot.db";
	private static Connection conn;

	static boolean commitSnapshot(List<Coin> coins) {
		// create connection
		if (!createConnection()) return false;

		// attempt to create tables for all coins
		for (Coin coin: coins) {

			// conform to table title strictures
			String TABLE_TITLE = coin.getSymbol().toLowerCase().replaceAll("[^a-z]", "");

			if (createTable(TABLE_TITLE)) return false;

			if (insertRecord(coin, TABLE_TITLE)) return false;
		}

		// close connection
		return closeConnection();
	}

	private static boolean insertRecord(Coin c, String TABLE_TITLE) {
		String INSERT_SNAPSHOT =
				"INSERT INTO " + TABLE_TITLE + " values(CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?,?);";
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
			return true;
		}
		return false;
	}

	private static boolean createTable(String TABLE_TITLE) {
		String CREATE_COIN_TABLE =
				"CREATE TABLE IF NOT EXISTS " + TABLE_TITLE +
						"(" +
						"dateCreated" + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
						"symbol" + " String, " +
						"name" + " String, " +
						"price" + " Double," +
						"change" + " Double, " +
						"pChange" + " Double, " +
						"marketCap" + " Double, " +
						"volume" + " Double, " +
						"volume24h" + " Double, " +
						"totalVolume24h" + " Double, " +
						"circulatingSupply" + " Double" +
						");"
				;

		try {
			PreparedStatement createTable = conn.prepareStatement(CREATE_COIN_TABLE);
			createTable.execute();
			createTable.close();
		} catch (SQLException e) {
			System.out.printf("Couldn't create table for coin {%s}\n%s\n", TABLE_TITLE, e.getMessage());
			return true;
		}
		return false;
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
