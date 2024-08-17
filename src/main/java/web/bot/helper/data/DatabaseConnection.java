 package web.bot.helper.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mariadb://localhost:3306/bot_telegram_db";
        String user = "root";
        String password = "";
  // Cadena vacía ya que no se requiere contraseña

        return DriverManager.getConnection(url,
	 user, password);
    }
}
