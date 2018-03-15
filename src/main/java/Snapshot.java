import java.util.List;
import java.sql.*;

class Snapshot {

	private static final String CONNECTION_STRING = "jdbc:sqlite:src/main/resources/coinsnapshot.db";
	private static Connection conn;
	private static PreparedStatement createTable;

	// SQL vocabulary
	private static final String TABLE_SNAPSHOT = "snapshot";
	private static final String COLUMN_SNAPSHOT_SYMBOL = "symbol";
	private static final String COLUMN_SNAPSHOT_PRICE = "price";

	// table schema
	private static String CREATE_SNAPSHOT_TABLE =
			"CREATE TABLE " + TABLE_SNAPSHOT + '(' +
					COLUMN_SNAPSHOT_SYMBOL + " String, " +
					COLUMN_SNAPSHOT_PRICE + " Double" +
					");";

	private static String INSERT_SNAPSHOT =
			"INSERT INTO " + TABLE_SNAPSHOT + " values(?,?);";

	static boolean commitSnapshot(List<Coin> coins) {

		if(conn == null) {
			try {
				conn = DriverManager.getConnection(CONNECTION_STRING);
				createTable = conn.prepareStatement(CREATE_SNAPSHOT_TABLE);
				createTable.execute();
			} catch(SQLException e) {
				System.out.println("Couldn't connect to database: " + e.getMessage());
				return false;
			}
		}

		Coin c = coins.get(0);

		if(conn != null) {
			try {
				PreparedStatement insertCoin = conn.prepareStatement(INSERT_SNAPSHOT);
				insertCoin.setString(1, c.getSymbol());
				insertCoin.setDouble(2, c.getPrice());
				insertCoin.execute();
				insertCoin.close();
			} catch (SQLException e) {
				System.out.println("Couldn't insert data: " + e.getMessage());
				return false;
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
