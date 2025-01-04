module com.study.pipeloadcalculation {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.study.pipeloadcalculation to javafx.fxml;
    exports com.study.pipeloadcalculation;
}