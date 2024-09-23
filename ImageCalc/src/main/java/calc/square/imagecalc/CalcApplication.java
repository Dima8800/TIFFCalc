package calc.square.imagecalc;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;

public class CalcApplication extends Application {
    private Label folderLabel;
    private Button calculateButton;
    private Button selectFolderButton;
    private boolean folderSelected = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Калькулятор");

        folderLabel = new Label("Выберите папку...");
        folderLabel.setFont(new Font("Arial", 16));

        selectFolderButton = new Button("Выбрать папку");
        selectFolderButton.setOnAction(e -> chooseFolder(primaryStage));

        calculateButton = new Button("Рассчитать");
        calculateButton.setDisable(true);
        calculateButton.setOnAction(e -> calculate(primaryStage));

        Button viewCalculationsButton = new Button("Посмотреть расчеты");
        viewCalculationsButton.setOnAction(e -> viewCalculations());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(folderLabel, selectFolderButton, calculateButton, viewCalculationsButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 800);

        URL cssUrl = getClass().getResource("/styles/mainWindow.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("CSS файл не найден!");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseFolder(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выбрать папку");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFolder = directoryChooser.showDialog(primaryStage);
        if (selectedFolder != null) {
            folderLabel.setText("Выбрана папка: " + selectedFolder.getAbsolutePath());
            folderSelected = true;
            updateButtonStates();
        }
    }

    private void updateButtonStates() {
        calculateButton.setDisable(!folderSelected);
        selectFolderButton.setText(folderSelected ? "Поменять папку" : "Выбрать папку");
    }

    private void calculate(Stage primaryStage) {
        String selectedFolderPath = folderLabel.getText().replace("Выбрана папка: ", "");

        CalculationScene calculationScene = new CalculationScene(primaryStage, this, selectedFolderPath);
        calculationScene.showResults();
    }

    private void viewCalculations() {
        System.out.println("Здесь будут ваши расчеты.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}