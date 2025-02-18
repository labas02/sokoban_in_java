module com.example.sokoban_in_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.sokoban_in_java to javafx.fxml;
    exports com.example.sokoban_in_java;
}