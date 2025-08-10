import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB{
    private static final String URL_BANCO = "jdbc:sqlite:banco_projeto.db";

    public static Connection conectarDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Conectando ao banco em: " + new java.io.File("banco_projeto.db").getAbsolutePath());
            return DriverManager.getConnection(URL_BANCO);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver não encontrado: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }


    public static Connection connectarGenerico(String url, String user, String password){
        try{
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Error"+e.getMessage());
            return null;
        }
    }


}