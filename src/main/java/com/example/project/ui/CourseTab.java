package com.example.project.ui;

import com.example.project.managers.SchoolManager;
import com.example.project.models.Course;
import com.example.project.models.Student;
import com.example.project.models.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CourseTab extends Tab {
    private SchoolManager manager;
    private ObservableList<String> teacherNames;

    public CourseTab(SchoolManager manager, ObservableList<String> teacherNames) {
        super("Courses");
        this.manager = manager;
        this.teacherNames = teacherNames;
        setClosable(false);
        init();
    }

    private void init() {
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20));

        VBox left = new VBox(10);
        TextField nameIn = new TextField();
        ComboBox<String> tDrop = new ComboBox<>(teacherNames);
        Button btn = new Button("Create Course");
        left.getChildren().addAll(new Label("Course Name:"), nameIn, new Label("Teacher:"), tDrop, btn);

        VBox right = new VBox(10);
        ObservableList<Course> courses = FXCollections.observableArrayList();
        ListView<Course> listView = new ListView<>(courses);
        TextArea details = new TextArea();
        right.getChildren().addAll(new Label("Courses:"), listView, details);

        btn.setOnAction(e -> {
            if (!nameIn.getText().isEmpty() && tDrop.getValue() != null) {
                Course c = manager.createCourse(nameIn.getText());
                Teacher t = manager.getTeacherByName(tDrop.getValue());
                if (t != null) c.assignTeacher(t);
                courses.setAll(manager.getAllCourses());
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                StringBuilder sb = new StringBuilder("Teacher: " + newV.getTeacherName() + "\nRoster:\n");
                for (Student s : newV.getEnrolledStudents()) sb.append("- ").append(s.getName()).append("\n");
                details.setText(sb.toString());
            }
        });

        setOnSelectionChanged(e -> { if (isSelected()) courses.setAll(manager.getAllCourses()); });

        mainLayout.getChildren().addAll(left, right);
        setContent(mainLayout);
    }
}