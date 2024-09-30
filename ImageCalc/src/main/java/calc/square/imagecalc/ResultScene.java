package calc.square.imagecalc;

import calc.square.imagecalc.models.Result;
import calc.square.imagecalc.request.RequestController;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import javafx.util.Duration;

// TODO: Сделать сортировку постранично

// TODO: Реализовать поиск

public class ResultScene {
    private Stage stage;
    private CalcApplication calcApplication;

    private int currentPage = 0;
    private static final int RESULTS_PER_PAGE = 13;

    private String Search;

    public ResultScene(Stage stage, CalcApplication calcApplication) {
        this.stage = stage;
        this.calcApplication = calcApplication;
    }

    public void showResults() {
        TableView<Result> tableView = new TableView<>();

        TextField searchField = new TextField();
        searchField.setPromptText("Введите текст для поиска...");

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            Search = searchField.getText();
            search(tableView);
        });
        pause.play();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.playFromStart();
        });

        TableColumn<Result, Long> numberCol = new TableColumn<>("Номер");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<Result, Double> perimeterCol = new TableColumn<>("Общий периметр");
        perimeterCol.setCellValueFactory(new PropertyValueFactory<>("totalPerimetr"));

        TableColumn<Result, Double> areaCol = new TableColumn<>("Общая площадь");
        areaCol.setCellValueFactory(new PropertyValueFactory<>("totalArea"));

        TableColumn<Result, Integer> filesCol = new TableColumn<>("Всего файлов");
        filesCol.setCellValueFactory(new PropertyValueFactory<>("totalFiles"));

        TableColumn<Result, LocalDateTime> createdCol = new TableColumn<>("Создано");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<Result, LocalDateTime> updateCol = new TableColumn<>("Изменено");
        updateCol.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        tableView.getColumns().addAll(numberCol, perimeterCol, areaCol, filesCol, createdCol, updateCol);

        ObservableList<Result> data = FXCollections.observableArrayList();
        List<Result> results = getListWithResults();
        data.setAll(results);
        tableView.setItems(data);

        tableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Result selectedResult = tableView.getSelectionModel().getSelectedItem();
                if (selectedResult != null) {
                    showResultDetails(selectedResult);
                }
            }
        });

        for (TableColumn<Result, ?> column : tableView.getColumns()) {
            column.setGraphic(new Label(column.getText()));
            column.getGraphic().setOnMouseClicked(event -> {
                System.out.println("Нажатие на столбец: " + column.getText());
            });
        }

        Button nextPageButton = new Button("Следующая страница");
        nextPageButton.setOnAction(event -> goToNextPage(tableView));

        Button prevPageButton = new Button("Предыдущая страница");
        prevPageButton.setOnAction(event -> goToPrevPage(tableView));

        Button backButton = new Button("Назад");
        backButton.getStyleClass().add("red-button");
        backButton.setOnAction(event -> returnToMainScreen());

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(prevPageButton, nextPageButton, backButton);

        VBox layout = new VBox(searchField, tableView, buttonLayout);

        Scene resultScene = new Scene(layout, 800, 600);

        URL cssUrl = getClass().getResource("/styles/mainWindow.css");
        if (cssUrl != null) {
            layout.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("CSS файл не найден!");
        }

        stage.setScene(resultScene);
    }

    public void search(TableView<Result> tableView){
        updateTableData(tableView);
    }

    private void updateTableData(TableView<Result> tableView) {
        ObservableList<Result> data = FXCollections.observableArrayList();
        List<Result> results = getListWithResults();
        data.setAll(results);
        tableView.setItems(data);
    }

    private void goToNextPage(TableView<Result> tableView) {
        currentPage++;
        updateTableData(tableView);
    }

    private void goToPrevPage(TableView<Result> tableView) {
        if (currentPage > 0) {
            currentPage--;
            updateTableData(tableView);
        }
    }

    private void showResultDetails(Result result) {
        DataScene dataScene = new DataScene(stage, calcApplication);
        dataScene.showResults(result);
    }

    private List<Result> getListWithResults(){
        RequestController requestController = new RequestController();

        System.out.println(Search);
        return requestController.getAllResults(Search, currentPage, RESULTS_PER_PAGE);
    }

    private void returnToMainScreen() {
        calcApplication.start(stage);
    }
}
