package com.example.project.ui;

import com.example.project.managers.SchoolManager;
import com.example.project.models.Course;
import com.example.project.models.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StudentTab extends Tab {
    private SchoolManager schoolManager;
    private ObservableList<Student> studentObservableList = FXCollections.observableArrayList();
    private ObservableList<Course> courseObservableList = FXCollections.observableArrayList();

    public StudentTab(SchoolManager manager) {
        super("Students");
        this.schoolManager = manager;
        this.setClosable(false);
        init();
    }

    private void init() {
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20));

        // --- LEFT COLUMN: INPUTS ---
        VBox inputLayout = new VBox(10);
        inputLayout.setPrefWidth(280);

        // Register Student
        TextField studentInput = new TextField();
        studentInput.setPromptText("Enter Full Name");
        Button btnCreate = new Button("Register Student");
        btnCreate.setMaxWidth(Double.MAX_VALUE);

        // Enrollment & Grading
        ComboBox<Student> studentDropdown = new ComboBox<>(studentObservableList);
        ComboBox<Course> courseDropdown = new ComboBox<>(courseObservableList);
        studentDropdown.setPromptText("Select Student");
        courseDropdown.setPromptText("Select Course");
        studentDropdown.setMaxWidth(Double.MAX_VALUE);
        courseDropdown.setMaxWidth(Double.MAX_VALUE);

        setupComboBoxes(studentDropdown, courseDropdown);

        Button btnEnroll = new Button("Enroll in Course");
        btnEnroll.setMaxWidth(Double.MAX_VALUE);

        TextField gradeInput = new TextField();
        gradeInput.setPromptText("Grade (0.0 - 4.0)");
        Button btnSetGrade = new Button("Update Grade & GPA");
        btnSetGrade.setMaxWidth(Double.MAX_VALUE);

        inputLayout.getChildren().addAll(
                new Label("1. Register:"), studentInput, btnCreate,
                new Separator(),
                new Label("2. Enroll:"), studentDropdown, courseDropdown, btnEnroll,
                new Label("3. Grade:"), gradeInput, btnSetGrade
        );

        // --- RIGHT COLUMN: DIRECTORY ---
        VBox listLayout = new VBox(10);
        listLayout.setPrefWidth(350);
        ListView<Student> allStudentsView = new ListView<>(studentObservableList);
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setPrefHeight(250);

        setupListView(allStudentsView, detailsArea, studentDropdown);
        listLayout.getChildren().addAll(new Label("Student Directory:"), allStudentsView, detailsArea);

        // --- LOGIC ---
        btnCreate.setOnAction(e -> {
            if (!studentInput.getText().isEmpty()) {
                schoolManager.createStudent(studentInput.getText());
                refreshLists();
                studentInput.clear();
            }
        });

        btnEnroll.setOnAction(e -> {
            Student s = studentDropdown.getValue();
            Course c = courseDropdown.getValue();
            if (s != null && c != null) {
                schoolManager.enrollStudentInCourse(s.getName(), c.getCourseId());
                allStudentsView.refresh();
            }
        });

        btnSetGrade.setOnAction(e -> {
            Student s = studentDropdown.getValue();
            Course c = courseDropdown.getValue();
            try {
                double g = Double.parseDouble(gradeInput.getText());
                if (s != null && c != null) {
                    s.setGrade(c, g);
                    allStudentsView.refresh();
                    gradeInput.clear();
                }
            } catch (NumberFormatException ex) {
                detailsArea.setText("Error: Enter a valid number.");
            }
        });

        // Tab Refresh
        this.setOnSelectionChanged(e -> {
            if (this.isSelected()) refreshLists();
        });

        mainLayout.getChildren().addAll(inputLayout, listLayout);
        this.setContent(mainLayout);
    }

    private void refreshLists() {
        studentObservableList.setAll(schoolManager.getAllStudents());
        courseObservableList.setAll(schoolManager.getAllCourses());
    }

    private void setupComboBoxes(ComboBox<Student> sDrop, ComboBox<Course> cDrop) {
        sDrop.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getName());
            }
        });
        sDrop.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getName());
            }
        });
        cDrop.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getCourseName());
            }
        });
        cDrop.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getCourseName());
            }
        });
    }

    private void setupListView(ListView<Student> view, TextArea area, ComboBox<Student> drop) {
        view.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) setText(null);
                else setText(s.getName() + " [GPA: " + String.format("%.2f", s.calculateGPA()) + "]");
            }
        });

        view.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                drop.setValue(newV);
                StringBuilder sb = new StringBuilder("Report Card for " + newV.getName() + ":\n");
                newV.getEnrolledCourses().forEach((c, g) -> sb.append("- ").append(c.getCourseName()).append(": ").append(g).append("\n"));
                area.setText(sb.toString());
            }
        });
    }
}