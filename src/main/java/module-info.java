module com.study.pipeloadcalculation {
    requires javafx.controls;
    requires javafx.fxml;
    requires jts;

    opens com.study.pipeloadcalculation to javafx.fxml;
    exports com.study.pipeloadcalculation;
	exports com.study.pipeloadcalculation.model;
	opens com.study.pipeloadcalculation.model to javafx.fxml;
	exports com.study.pipeloadcalculation.controller;
	opens com.study.pipeloadcalculation.controller to javafx.fxml;
	exports com.study.pipeloadcalculation.service;
	opens com.study.pipeloadcalculation.service to javafx.fxml;
}