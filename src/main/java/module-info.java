module com.example.masterprojectcsa {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.masterprojectcsa to javafx.fxml;
    exports com.example.masterprojectcsa;
}