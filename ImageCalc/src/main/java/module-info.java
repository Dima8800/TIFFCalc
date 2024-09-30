module calc.square.imagecalc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;
    requires jdk.xml.dom;

    opens calc.square.imagecalc to javafx.fxml;
    exports calc.square.imagecalc;
    opens calc.square.imagecalc.models to javafx.base;
}