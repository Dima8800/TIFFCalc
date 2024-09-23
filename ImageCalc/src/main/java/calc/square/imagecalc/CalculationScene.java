package calc.square.imagecalc;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CalculationScene {
    private Stage stage;
    private CalcApplication calcApplication;
    private String selectedFolderPath;

    private double totalArea = 0;
    private double totalPerimeter = 0;

    public CalculationScene(Stage stage, CalcApplication calcApplication, String selectedFolderPath) {
        this.stage = stage;
        this.calcApplication = calcApplication;
        this.selectedFolderPath = selectedFolderPath;
    }

    public void showResults() {
        File selectedFolder = new File(selectedFolderPath);
        File[] files = selectedFolder.listFiles();
        int totalFiles = (files != null) ? files.length : 0;

        VBox resultLayout = new VBox(10);

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-background-color: red;");
        backButton.setOnAction(e -> returnToMainScreen());

        Button saveButton = new Button("Сохранить");
        saveButton.setStyle("-fx-background-color: green;");
        saveButton.setOnAction(e -> saveResults());

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(backButton, saveButton);

        String folderName = selectedFolder.getName();
        resultLayout.getChildren().addAll(
                buttonLayout,
                new Label("Выбран заказ: " + folderName) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }},
                new Label("Всего файлов: " + totalFiles) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");}}
        );

        for (File file : files) {
            if (file.getName().endsWith(".tiff") || file.getName().endsWith(".tif")) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    String fileName = file.getName();
                    double area = calculateAreaInMm(image, fileName); // Площадь в пикселях
                    double perimeter = calculatePerimeterInMm(image, fileName); // Периметр в пикселях

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

    public double calculateAreaInMm(BufferedImage image, String fileName) {
        double dpiX = getDPI(image, fileName); // Получаем DPI по оси X
        double dpiY = getDPI(image, fileName); // Получаем DPI по оси Y

        // Преобразуем ширину и высоту из пикселей в миллиметры
        double widthInMm = (image.getWidth() / dpiX) * 25.4;
        double heightInMm = (image.getHeight() / dpiY) * 25.4;

        return widthInMm * heightInMm; // Площадь в мм²
    }

    public double getDPI(BufferedImage image, String fileName) {
        try {
            File file = new File(selectedFolderPath + "/" + fileName);
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            //IIOMetadata metadata = ImageIO.getImageReadersByFormatName("TIFF").next().getImageMetadata(0);
            // Здесь нужно извлечь DPI из метаданных
            // Это зависит от формата метаданных, поэтому может потребоваться дополнительная обработка
            // Например, для TIFF это может быть в секции "Dimension" или "TIFFField"
            // Для упрощения, используем фиксированное значение DPI, если не удалось его получить
            return 300; // Вернуть стандартное значение DPI (например, 300)
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("err");
            return 300; // По умолчанию
        }
    }

    public double calculatePerimeterInMm(BufferedImage image, String fileName) {
        double dpiX = getDPI(image, fileName);
        double dpiY = getDPI(image, fileName);

        double widthInMm = (image.getWidth() / dpiX) * 25.4;
        double heightInMm = (image.getHeight() / dpiY) * 25.4;

        return 2 * (widthInMm + heightInMm); // Периметр в мм
    }

    private void returnToMainScreen() {
        calcApplication.start(stage);
    }

    private void saveResults() {
        System.out.println("Результаты сохранены!");
    }
}
