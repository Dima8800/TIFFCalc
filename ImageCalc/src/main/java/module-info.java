module calc.square.imagecalc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens calc.square.imagecalc to javafx.fxml;
    exports calc.square.imagecalc;
}