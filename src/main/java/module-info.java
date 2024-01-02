module at.ac.fhcampuswien.teamproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires java.sql;

    opens at.ac.fhcampuswien.teamproject to javafx.fxml;
    exports at.ac.fhcampuswien.teamproject;
}