import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class Front extends Application {
    private ProdutoDAO produtoDAO;
    private ObservableList<Produto> produtos;
    private TableView<Produto> tableView;
    private TextField nomeInput, quantidadeInput, precoInput;
    private ComboBox<String> statusComboBox;
    private Connection conexaoDB;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Conectando ao banco em: " + new java.io.File("banco_projeto.db").getAbsolutePath());

        conexaoDB = ConexaoDB.conectarDB();
        produtoDAO = new ProdutoDAO(conexaoDB);
        produtos = FXCollections.observableArrayList(produtoDAO.listarTodos());

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        // Campos de entrada
        HBox nomeBox = new HBox(10);
        Label nomeLabel = new Label("Nome:");
        nomeInput = new TextField();
        nomeBox.getChildren().addAll(nomeLabel, nomeInput);

        HBox quantidadeBox = new HBox(10);
        Label quantidadeLabel = new Label("Quantidade:");
        quantidadeInput = new TextField();
        quantidadeBox.getChildren().addAll(quantidadeLabel, quantidadeInput);

        HBox precoBox = new HBox(10);
        Label precoLabel = new Label("Preço:");
        precoInput = new TextField();
        precoBox.getChildren().addAll(precoLabel, precoInput);

        HBox statusBox = new HBox(10);
        Label statusLabel = new Label("Status:");
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Estoque Normal", "Estoque Baixo");
        statusBox.getChildren().addAll(statusLabel, statusComboBox);

        // Botões
        Button addButton = new Button("Adicionar");
        addButton.setOnAction(e -> {
            try {
                String preco = precoInput.getText().replace(",", ".");
                Produto produto = new Produto(
                        nomeInput.getText(),
                        Integer.parseInt(quantidadeInput.getText()),
                        Double.parseDouble(preco),
                        statusComboBox.getValue()
                );
                produtoDAO.inserir(produto);
                produtos.setAll(produtoDAO.listarTodos());
                limparCampos();
            } catch (Exception ex) {
                System.out.println("Erro ao adicionar: " + ex.getMessage());
            }
        });

        Button updateButton = new Button("Atualizar");
        updateButton.setOnAction(e -> {
            Produto selecionado = tableView.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                try {
                    selecionado.setNome(nomeInput.getText());
                    selecionado.setQuantidade(Integer.parseInt(quantidadeInput.getText()));
                    String preco = precoInput.getText().replace(",", ".");
                    selecionado.setPreco(Double.parseDouble(preco));
                    selecionado.setStatus(statusComboBox.getValue());
                    produtoDAO.atualizar(selecionado);
                    produtos.setAll(produtoDAO.listarTodos());
                    limparCampos();
                } catch (Exception ex) {
                    System.out.println("Erro ao atualizar: " + ex.getMessage());
                }
            }
        });

        Button deleteButton = new Button("Excluir");
        deleteButton.setOnAction(e -> {
            Produto selecionado = tableView.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                produtoDAO.excluir(selecionado.getId());
                produtos.setAll(produtoDAO.listarTodos());
                limparCampos();
            }
        });

        Button clearButton = new Button("Limpar");
        clearButton.setOnAction(e -> limparCampos());

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, clearButton);

        // TableView
        tableView = new TableView<>();
        tableView.setItems(produtos);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        TableColumn<Produto, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Produto, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Produto, Integer> quantidadeCol = new TableColumn<>("Quantidade");
        quantidadeCol.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<Produto, Double> precoCol = new TableColumn<>("Preço");
        precoCol.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Produto, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(idCol, nomeCol, quantidadeCol, precoCol, statusCol);

        // Preenche os campos ao selecionar um produto na tabela
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nomeInput.setText(newSel.getNome());
                quantidadeInput.setText(String.valueOf(newSel.getQuantidade()));
                precoInput.setText(String.valueOf(newSel.getPreco()));
                statusComboBox.setValue(newSel.getStatus());
            }
        });

        // Adiciona tudo no VBox
        vBox.getChildren().addAll(nomeBox, quantidadeBox, precoBox, statusBox, buttonBox, tableView);

        Scene scene = new Scene(vBox, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Cadastro de Produtos");
        stage.show();
    }

    private void limparCampos() {
        nomeInput.clear();
        quantidadeInput.clear();
        precoInput.clear();
        statusComboBox.setValue(null);
    }

    @Override
    public void stop() {
        try {
            if (conexaoDB != null && !conexaoDB.isClosed()) {
                conexaoDB.close();
                System.out.println("Conexão com banco fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
