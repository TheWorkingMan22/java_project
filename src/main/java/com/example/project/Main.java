package com.example.project;

import com.example.project.managers.SchoolManager;
import com.example.project.ui.CourseTab;
import com.example.project.ui.StudentTab;
import com.example.project.ui.TeacherTab;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {
    private SchoolManager schoolManager = new SchoolManager();
    private ObservableList<String> teacherNames = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();

        // Initialize Tabs from separate classes
        TeacherTab teacherTab = new TeacherTab(schoolManager, teacherNames);
        CourseTab courseTab = new CourseTab(schoolManager, teacherNames);
        StudentTab studentTab = new StudentTab(schoolManager); // Pass manager here

        tabPane.getTabs().addAll(teacherTab, courseTab, studentTab);

        Scene scene = new Scene(tabPane, 800, 600);
        stage.setTitle("School Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}