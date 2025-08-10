import javafx.application.Application;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conexao = ConexaoDB.conectarDB();

            new CriadoTabela().criaTabelaDB(conexao);
            Application.launch(Front.class, args);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
