 package web.bot.helper.data;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        Dotenv dotenv = Dotenv.load();
        String dbName = dotenv.get("DATABASE_NAME");
        String dbClient = dotenv.get("DATABASE_CLIENT");
        String dbUrl = dotenv.get("DATABASE_URL");
        String dbPort = dotenv.get("DATABASE_PORT");
        String url = "jdbc:"+dbClient+dbUrl+dbPort+dbName;
        String user = dotenv.get("DATABASE_USER");
        String password = dotenv.get("DATABASE_PASSWORD");
  // Cadena vacía ya que no se requiere contraseña

        return DriverManager.getConnection(url,
	 user, password);
    }
}
