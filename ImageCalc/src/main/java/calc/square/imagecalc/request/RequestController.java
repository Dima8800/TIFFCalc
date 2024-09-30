package calc.square.imagecalc.request;

import calc.square.imagecalc.models.Photo;
import calc.square.imagecalc.models.Result;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ## Запросы к Базе Данных для сохранение\получение результатов расчета
 *
 * @author Горелов Дмитрий
 * */

public class RequestController {

    private static final String url = "jdbc:postgresql://localhost:5432/TiFF";
    private static final String user = "postgres";
    private static final String password = "Dd25022006dD";

    public void CreateDataBase(){
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            // Создание таблицы Result
            String createResultTable = "CREATE TABLE IF NOT EXISTS Result (" +
                    "id SERIAL PRIMARY KEY, " +
                    "number VARCHAR(255) NOT NULL, " +
                    "total_perimetr DOUBLE PRECISION, " +
                    "total_area DOUBLE PRECISION, " +
                    "total_files INTEGER, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";

            // Создание таблицы Photo
            String createPhotoTable = "CREATE TABLE IF NOT EXISTS Photo (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name_file VARCHAR(255) NOT NULL, " +
                    "perimetr DOUBLE PRECISION, " +
                    "area DOUBLE PRECISION, " +
                    "result_id BIGINT, " +
                    "FOREIGN KEY (result_id) REFERENCES Result(id) ON DELETE CASCADE" +
                    ");";

            statement.executeUpdate(createResultTable);
            statement.executeUpdate(createPhotoTable);

            System.out.println("Таблицы успешно созданы.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveResult(Result result) {
        String insertResultSQL = "INSERT INTO Result (number, total_perimetr, total_area, total_files, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";

        long idResult = 0;

        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(null);

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertResultSQL)) {

            preparedStatement.setString(1, result.getNumber());
            preparedStatement.setDouble(2, result.getTotalPerimetr());
            preparedStatement.setDouble(3, result.getTotalArea());
            preparedStatement.setInt(4, result.getTotalFiles());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(result.getCreatedAt()));

            if (result.getUpdatedAt() == null) {
                preparedStatement.setNull(6, Types.TIMESTAMP);
            } else {
                preparedStatement.setTimestamp(6, Timestamp.valueOf(result.getUpdatedAt()));
            }

            ResultSet generatedKeys = preparedStatement.executeQuery();
            if (generatedKeys.next()) {
                result.setId(generatedKeys.getLong(1));
            }

            System.out.println("Результат успешно сохранен с ID: " + result.getId());

            idResult = result.getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        savePhoto(result.getFilles(), idResult);
    }

    private void savePhoto(List<Photo> filles, long idResult) {
        if (idResult == 0) {
            System.err.println("doesn't save Result");
            return;
        }

        String insertPhotoSQL = "INSERT INTO Photo (name_file, perimetr, area, result_id) VALUES (?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertPhotoSQL)) {

            for (Photo photo : filles) { // Итерируемся по списку photos
                preparedStatement.setString(1, photo.getNameFile());
                preparedStatement.setDouble(2, photo.getPerimetr());
                preparedStatement.setDouble(3, photo.getArea());
                preparedStatement.setLong(4, idResult);

                preparedStatement.executeUpdate();
            }

            System.out.println("Фотографии успешно сохранены.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Result> getAllResults(String search, int page, int pageSize) {
        List<Result> results = new ArrayList<>();
        String selectSQL;

        // Если search пустой, выбираем все результаты
        if (search == null || search.trim().isEmpty()) {
            selectSQL = "SELECT * FROM Result LIMIT ? OFFSET ?;";
        } else {
            selectSQL = "SELECT * FROM Result WHERE LOWER(number) LIKE LOWER(?) " +
                    "OR CAST(total_perimetr AS TEXT) LIKE ? " +
                    "OR CAST(total_area AS TEXT) LIKE ? " +
                    "OR total_files = ? " +
                    "LIMIT ? OFFSET ?;";
        }

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            // Устанавливаем параметры для поиска и пагинации
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";

                preparedStatement.setString(1, searchPattern);
                preparedStatement.setString(2, searchPattern);
                preparedStatement.setString(3, searchPattern);

                try {
                    int totalFilesSearch = Integer.parseInt(search.trim());
                    preparedStatement.setInt(4, totalFilesSearch);
                } catch (NumberFormatException e) {
                    preparedStatement.setInt(4, -1);
                }

                preparedStatement.setInt(5, pageSize);
                preparedStatement.setInt(6, page * pageSize);
            } else {
                preparedStatement.setInt(1, pageSize);
                preparedStatement.setInt(2, page * pageSize);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Result result = new Result();

                    result.setId(resultSet.getLong("id"));
                    result.setNumber(resultSet.getString("number"));
                    result.setTotalPerimetr(resultSet.getDouble("total_perimetr"));
                    result.setTotalArea(resultSet.getDouble("total_area"));
                    result.setTotalFiles(resultSet.getInt("total_files"));
                    result.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    Timestamp updatedAtTimestamp = resultSet.getTimestamp("updated_at");
                    if (updatedAtTimestamp != null) {
                        result.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
                    } else {
                        result.setUpdatedAt(null);
                    }

                    results.add(result);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Photo> getPhotos(long id){
        String query = "SELECT * FROM photo WHERE result_id = ?";

        List<Photo> photos = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Photo photo = new Photo();

                photo.setNameFile(resultSet.getString("name_file"));
                photo.setPerimetr(resultSet.getDouble("perimetr"));
                photo.setArea(resultSet.getDouble("area"));
                photo.setId(resultSet.getLong("id"));

                photos.add(photo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return photos;
    }


    public void updateResult(Result result) {
        result.toString();
        String updateResultSQL = "UPDATE Result SET number = ?, total_perimetr = ?, total_area = ?, total_files = ?, updated_at = ? WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(updateResultSQL)) {

            preparedStatement.setString(1, result.getNumber());
            preparedStatement.setDouble(2, result.getTotalPerimetr());
            preparedStatement.setDouble(3, result.getTotalArea());
            preparedStatement.setInt(4, result.getTotalFiles());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setLong(6, result.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                addNewPhotos(result.getFilles(), result.getId());
            } else {
                System.err.println("Не удалось обновить результат с ID: " + result.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePhotos(List<Photo> photos, long idResult) {
        String updatePhotoSQL = "UPDATE Photo SET name_file = ?, perimetr = ?, area = ? WHERE result_id = ? AND id = ?;";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(updatePhotoSQL)) {

            for (Photo photo : photos) {
                if (photo.getId() != null) {
                    preparedStatement.setString(1, photo.getNameFile());
                    preparedStatement.setDouble(2, photo.getPerimetr());
                    preparedStatement.setDouble(3, photo.getArea());
                    preparedStatement.setLong(4, idResult);
                    preparedStatement.setLong(5, photo.getId());

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Фотография успешно обновлена: " + photo.getNameFile());
                    } else {
                        System.err.println("Не удалось обновить фотографию: " + photo.getNameFile());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewPhotos(List<Photo> photos, long idResult) {
        String insertPhotoSQL = "INSERT INTO Photo (name_file, perimetr, area, result_id) VALUES (?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertPhotoSQL)) {

            for (Photo photo : photos) {
                if (photo.getId() == null) {
                    System.out.println("ss");// Проверяем, что id отсутствует
                    preparedStatement.setString(1, photo.getNameFile());
                    preparedStatement.setDouble(2, photo.getPerimetr());
                    preparedStatement.setDouble(3, photo.getArea());
                    preparedStatement.setLong(4, idResult);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Новая фотография успешно добавлена: " + photo.getNameFile());
                    } else {
                        System.err.println("Не удалось добавить новую фотографию: " + photo.getNameFile());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
