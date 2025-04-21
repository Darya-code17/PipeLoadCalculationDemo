module com.study.pipesloadcalculation {
    requires javafx.controls;
    requires javafx.fxml;
    requires jts;

    opens com.study.pipesloadcalculation to javafx.fxml;
    exports com.study.pipesloadcalculation;
	exports com.study.pipesloadcalculation.model;
	opens com.study.pipesloadcalculation.model to javafx.fxml;
	exports com.study.pipesloadcalculation.controller;
	opens com.study.pipesloadcalculation.controller to javafx.fxml;
	exports com.study.pipesloadcalculation.service;
	opens com.study.pipesloadcalculation.service to javafx.fxml;
}