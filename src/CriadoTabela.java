import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CriadoTabela {
    public void criaTabelaDB(Connection conexao) throws SQLException {
        try (Statement criatable = conexao.createStatement()) {
            System.out.println("Criando tabela produto...");
            String ct = "CREATE TABLE IF NOT EXISTS produto (" +
                    "id_produto INTEGER PRIMARY KEY," +
                    "nome_produto TEXT NOT NULL," +
                    "quantidade INTEGER," +
                    "preco DOUBLE," +
                    "status TEXT" +
                    ");";
            criatable.execute(ct);
            System.out.println("Tabela produto criada (ou já existia).");
        } catch (SQLException e) {
            System.out.println("Erro criando tabela: " + e.getMessage());
        }
    }
}


