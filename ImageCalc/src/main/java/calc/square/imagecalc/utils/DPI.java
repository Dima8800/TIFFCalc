package calc.square.imagecalc.utils;

import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class DPI {

    public double calculateAreaInMm(BufferedImage image, String fileName, String selectedFolderPath) {
        double dpiX = getDPI(fileName, selectedFolderPath); // Получаем DPI по оси X
        double dpiY = getDPI(fileName, selectedFolderPath); // Получаем DPI по оси Y

        // Преобразуем ширину и высоту из пикселей в миллиметры
        double widthInMm = (image.getWidth() / dpiX) * 25.4;
        double heightInMm = (image.getHeight() / dpiY) * 25.4;

        return widthInMm * heightInMm; // Площадь в мм²
    }

    private double getDPI(String fileName, String selectedFolderPath) {
        try {
            File file = new File(selectedFolderPath + "/" + fileName);
            ImageInputStream stream = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);

            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(stream);

                IIOMetadata metadata = reader.getImageMetadata(0);
                IIOMetadataNode standardTree = (IIOMetadataNode) metadata.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
                IIOMetadataNode dimension = (IIOMetadataNode) standardTree.getElementsByTagName("Dimension").item(0);

                float horizontalPixelSizeMM = getPixelSizeMM(dimension, "HorizontalPixelSize");
                float verticalPixelSizeMM = getPixelSizeMM(dimension, "VerticalPixelSize");

                double dpiX = horizontalPixelSizeMM > 0 ? 25.4 / horizontalPixelSizeMM : 300; // 25.4 мм в дюймах
                double dpiY = verticalPixelSizeMM > 0 ? 25.4 / verticalPixelSizeMM : 300;

                return (dpiX + dpiY) / 2; // Возвращаем среднее значение DPI
            } else {
                System.err.printf("Could not read %s\n", file);
                return 300; // Значение по умолчанию
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading DPI");
            return 300;
        }
    }

    private static float getPixelSizeMM(final IIOMetadataNode dimension, final String elementName) {
        NodeList pixelSizes = dimension.getElementsByTagName(elementName);
        IIOMetadataNode pixelSize = pixelSizes.getLength() > 0 ? (IIOMetadataNode) pixelSizes.item(0) : null;
        return pixelSize != null ? Float.parseFloat(pixelSize.getAttribute("value")) : -1;
    }

    public double calculatePerimeterInMm(BufferedImage image, String fileName, String selectedFolderPath) {
        double dpiX = getDPI(fileName, selectedFolderPath);
        double dpiY = getDPI(fileName, selectedFolderPath);

        double widthInMm = (image.getWidth() / dpiX) * 25.4;
        double heightInMm = (image.getHeight() / dpiY) * 25.4;

        return 2 * (widthInMm + heightInMm); // Периметр в мм
    }
}
