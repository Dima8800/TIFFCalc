package calc.square.imagecalc;

import calc.square.imagecalc.models.Photo;
import calc.square.imagecalc.models.Result;
import calc.square.imagecalc.request.RequestController;
import calc.square.imagecalc.utils.DPI;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataScene {
    private Stage stage;
    private CalcApplication calcApplication;
    private Button saveButton;
    private HBox buttonLayout;

    VBox resultLayout = new VBox(10);

    Result results = new Result();
    List<Photo> photoList = new ArrayList<>();

    public DataScene(Stage stage, CalcApplication calcApplication) {
        this.stage = stage;
        this.calcApplication = calcApplication;
    }

    public void showResults(Result result) {
        photoList = getInfoAboutPhotos(result.getId());
        results = result;

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-background-color: red;");
        backButton.setOnAction(e -> returnToMainScreen());

        saveButton = new Button("Добавить");
        saveButton.setStyle("-fx-background-color: green;");
        saveButton.setOnAction(e -> openFilles());

        buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(backButton, saveButton);

        updateResults(); // Вызов для обновления интерфейса

        stage.getScene().setRoot(resultLayout);
    }

    private List<Photo> getInfoAboutPhotos(long id) {
        RequestController requestController = new RequestController();
        return requestController.getPhotos(id);
    }

    private void updateResults() {
        results.setFilles(getInfoAboutPhotos(results.getId()));

        resultLayout.getChildren().clear();

        resultLayout.getChildren().addAll(
                buttonLayout,
                new Label("Выбран заказ: " + results.getNumber()) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }},
                new Label("Всего файлов: " + results.getTotalFiles()) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }},
                new Label("Общий периметр: " + results.getTotalPerimetr()) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }},
                new Label("Общая площадь: " + results.getTotalArea()) {{ setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); }}
        );

        for (Photo photo : photoList) {
            System.out.println(photo.toString());
            resultLayout.getChildren().add(new Label("Файл: " + photo.getNameFile() +
                    ", Периметр: " + photo.getPerimetr() +
                    ", Площадь: " + photo.getArea()));
        }
    }

    public void openFilles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл TIFF");

        // Установка фильтра для файлов TIFF
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TIFF файлы (*.tif, *.tiff)", "*.tif", "*.tiff");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            String selectedFolderPath = selectedFile.getAbsolutePath();

            int lastSlashIndex = selectedFolderPath.lastIndexOf('\\');
            selectedFolderPath = selectedFolderPath.substring(0, lastSlashIndex);
            try {
                BufferedImage bufferedImage = readTiffImage(selectedFile);
                if (bufferedImage != null) {
                    DPI dpi = new DPI();
                    Photo photo = new Photo();

                    photo.setPerimetr(
                            dpi.calculatePerimeterInMm(bufferedImage, fileName, selectedFolderPath)
                    );
                    photo.setArea(
                            dpi.calculateAreaInMm(bufferedImage, fileName, selectedFolderPath)
                    );
                    photo.setNameFile(fileName);

                    photoList.add(photo);

                    results.setFilles(photoList);
                    results.setTotalPerimetr(photo.getPerimetr() + results.getTotalPerimetr());
                    results.setTotalArea(photo.getArea() + results.getTotalArea());
                    results.setTotalFiles(photoList.size());

                    UpdateDataBase(results);
                    updateResults();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка при загрузке изображения.");
            }
        } else {
            System.out.println("Файл не выбран.");
        }
    }

    // Метод для чтения TIFF изображения
    private BufferedImage readTiffImage(File file) throws Exception {
        try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(input);
                return reader.read(0); // Чтение первого изображения из TIFF
            } else {
                throw new IllegalArgumentException("Нет доступных читателей для данного файла.");
            }
        }
    }

    private void UpdateDataBase(Result result){
        RequestController requestController = new RequestController();

        requestController.updateResult(result);
    }

    private void returnToMainScreen() {
        calcApplication.start(stage);
    }
}