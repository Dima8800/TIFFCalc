package calc.square.imagecalc;

import calc.square.imagecalc.models.Photo;
import calc.square.imagecalc.models.Result;
import calc.square.imagecalc.request.RequestController;
import calc.square.imagecalc.utils.DPI;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalculationScene {
    private Stage stage;
    private CalcApplication calcApplication;
    private String selectedFolderPath;

    private double totalArea = 0;
    private double totalPerimeter = 0;

    Result result = new Result();

    public CalculationScene(Stage stage, CalcApplication calcApplication, String selectedFolderPath) {
        this.stage = stage;
        this.calcApplication = calcApplication;
        this.selectedFolderPath = selectedFolderPath;
    }

    public void showResults() {
        File selectedFolder = new File(selectedFolderPath);
        File[] files = selectedFolder.listFiles();
        String folderName = selectedFolder.getName();

        result.setNumber(folderName);
        result.setTotalFiles((files != null) ? files.length : 0);

        VBox resultLayout = new VBox(10);

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-background-color: red;");
        backButton.setOnAction(e -> returnToMainScreen());

        Button saveButton = new Button("Сохранить");
        saveButton.setStyle("-fx-background-color: green;");
        saveButton.setOnAction(e -> saveResults());

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(backButton, saveButton);

        resultLayout.getChildren().addAll(
                buttonLayout,
                new Label("Выбран заказ: " + folderName) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }},
                new Label("Всего файлов: " + result.getTotalFiles()) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }},
                new Label("Общий периметр: 0") {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }},
                new Label("Общая площадь: 0") {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }}
        );

        Label perimeterLabel = (Label) resultLayout.getChildren().get(resultLayout.getChildren().size() - 2);
        Label areaLabel = (Label) resultLayout.getChildren().get(resultLayout.getChildren().size() - 1);

        for (File file : files) {
            if (file.getName().endsWith(".tiff") || file.getName().endsWith(".tif")) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    String fileName = file.getName();

                    DPI dpi = new DPI();

                    double area = dpi.calculateAreaInMm(image, fileName, selectedFolderPath); // Площадь в мм²
                    double perimeter = dpi.calculatePerimeterInMm(image, fileName, selectedFolderPath); // Периметр в мм

                    Photo photo = new Photo();

                    photo.setArea(area);
                    photo.setPerimetr(perimeter);
                    photo.setNameFile(fileName);

                    result.setTotalArea(result.getTotalArea() + area);
                    result.setTotalPerimetr(result.getTotalPerimetr() + perimeter);

                    List<Photo> currentFilles = result.getFilles();

                    List<Photo> newFilles = new ArrayList<>();

                    if (currentFilles != null) {
                        newFilles.addAll(currentFilles);
                    }

                    newFilles.add(photo);
                    result.setFilles(newFilles);

                    perimeterLabel.setText("Общий периметр: " + result.getTotalPerimetr());
                    areaLabel.setText("Общая площадь: " + result.getTotalArea());

                    resultLayout.getChildren().add(new Label("Файл: " + file.getName() +
                            ", Площадь: " + area + " мм², Периметр: " + perimeter + " мм"));
                } catch (IOException e) {
                    e.printStackTrace();
                    resultLayout.getChildren().add(new Label("Ошибка при чтении файла: " + file.getName()));
                }
            }
        }

        Scene resultScene = new Scene(resultLayout, 600, 800);
        stage.setScene(resultScene);
    }


    private void returnToMainScreen() {
        calcApplication.start(stage);
    }

    private void saveResults() {
        RequestController requestController = new RequestController();
//        requestController.CreateDataBase();  создание таблиц в бд
        requestController.saveResult(result);

        returnToMainScreen();
    }
}
