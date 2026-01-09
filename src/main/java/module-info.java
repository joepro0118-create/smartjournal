module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Add this if you are using a database

    opens org.example to javafx.graphics, javafx.fxml;
    exports org.example;
}