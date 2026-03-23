package com.example.project;

import com.example.project.models.*;
import com.example.project.managers.SchoolManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    // One manager to rule them all
    private SchoolManager schoolManager = new SchoolManager();

    // Observable lists to keep the UI dropdowns in sync
    private ObservableList<String> teacherNames = FXCollections.observableArrayList();
    private ObservableList<String> courseNames = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();

        // --- TAB 1: TEACHER MANAGEMENT ---
        Tab teacherTab = new Tab("Teachers");
        VBox teacherLayout = new VBox(10);
        teacherLayout.setPadding(new Insets(20));

        TextField teacherInput = new TextField();
        teacherInput.setPromptText("Enter Teacher Name");
        Button btnAddTeacher = new Button("Register Teacher");
        ListView<String> teacherListView = new ListView<>(teacherNames);

        btnAddTeacher.setOnAction(e -> {
            String name = teacherInput.getText();
            if (!name.isEmpty()) {
                schoolManager.createTeacher(name);
                // Update UI list (get the last added teacher's name)
                teacherNames.clear();
                schoolManager.getAllTeachers().forEach(t -> teacherNames.add(t.getName()));
                teacherInput.clear();
            }
        });

        teacherLayout.getChildren().addAll(new Label("Teacher Name:"), teacherInput, btnAddTeacher, new Label("Staff List:"), teacherListView);
        teacherTab.setContent(teacherLayout);

        // --- TAB 2: COURSE MANAGEMENT ---
        Tab courseTab = new Tab("Courses");
        VBox courseLayout = new VBox(10);
        courseLayout.setPadding(new Insets(20));

        TextField courseInput = new TextField();
        courseInput.setPromptText("Course Name");
        ComboBox<String> teacherDropdown = new ComboBox<>(teacherNames);
        teacherDropdown.setPromptText("Select Teacher");
        teacherDropdown.setMaxWidth(Double.MAX_VALUE);

        Button btnAddCourse = new Button("Create Course");
        TextArea courseDisplay = new TextArea();
        courseDisplay.setEditable(false);

        btnAddCourse.setOnAction(e -> {
            String cName = courseInput.getText();
            String tName = teacherDropdown.getValue();

            if (!cName.isEmpty() && tName != null) {
                // 1. Create the course through the manager
                Course newCourse = schoolManager.createCourse(cName);

                // 2. Find the actual Teacher object using the name from the dropdown
                Teacher selectedTeacher = schoolManager.getTeacherByName(tName);

                // 3. Assign the teacher object to the course
                if (selectedTeacher != null) {
                    newCourse.assignTeacher(selectedTeacher);
                }

                courseNames.add(newCourse.getCourseName() + " (" + newCourse.getCourseId() + ")");
                courseInput.clear();
                updateCourseArea(courseDisplay);
            } else {
                new Alert(Alert.AlertType.WARNING, "Select a teacher first!").show();
            }
        });

        courseLayout.getChildren().addAll(new Label("Course Name:"), courseInput, new Label("Assign Teacher:"), teacherDropdown, btnAddCourse, courseDisplay);
        courseTab.setContent(courseLayout);

// --- TAB 3: STUDENT MANAGEMENT & DIRECTORY ---
        Tab studentTab = new Tab("Students");
        HBox studentMainLayout = new HBox(20); // Horizontal split
        studentMainLayout.setPadding(new Insets(20));

// --- LEFT SIDE: CONTROLS (250px wide) ---
        VBox studentInputLayout = new VBox(10);
        studentInputLayout.setPrefWidth(250);

// 1. Registration Section
        Label regLabel = new Label("Register New Student");
        regLabel.setStyle("-ソフトweight: bold;");
        TextField studentInput = new TextField();
        studentInput.setPromptText("Enter Full Name");
        Button btnCreateStudent = new Button("Add to Registry");
        btnCreateStudent.setMaxWidth(Double.MAX_VALUE);

// 2. Enrollment Section
        Label enrollLabel = new Label("Enrollment & Grading");
        enrollLabel.setStyle("-fx-font-weight: bold;");
        Separator sep = new Separator();

// ObservableLists link the UI to the SchoolManager data
        ObservableList<Student> studentObservableList = FXCollections.observableArrayList();
        ObservableList<Course> courseObservableList = FXCollections.observableArrayList();

        ComboBox<Student> studentDropdown = new ComboBox<>(studentObservableList);
        ComboBox<Course> courseDropdown = new ComboBox<>(courseObservableList);
        studentDropdown.setPromptText("Select Student");
        courseDropdown.setPromptText("Select Course");
        studentDropdown.setMaxWidth(Double.MAX_VALUE);
        courseDropdown.setMaxWidth(Double.MAX_VALUE);

// Cell Factories to show NAMES instead of Object Addresses
        studentDropdown.setCellFactory(lv -> new ListCell<Student>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getName());
            }
        });
        studentDropdown.setButtonCell(new ListCell<Student>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getName());
            }
        });

        courseDropdown.setCellFactory(lv -> new ListCell<Course>() {
            @Override protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getCourseName() + " (" + c.getCourseId() + ")");
            }
        });
        courseDropdown.setButtonCell(new ListCell<Course>() {
            @Override protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getCourseName());
            }
        });

        Button btnEnroll = new Button("Confirm Enrollment");
        btnEnroll.setMaxWidth(Double.MAX_VALUE);

// 3. Grading Section
        TextField gradeInput = new TextField();
        gradeInput.setPromptText("Score (0.0 - 4.0)");
        Button btnSetGrade = new Button("Update Grade & GPA");
        btnSetGrade.setMaxWidth(Double.MAX_VALUE);

        studentInputLayout.getChildren().addAll(
                regLabel, studentInput, btnCreateStudent,
                sep,
                enrollLabel, studentDropdown, courseDropdown, btnEnroll,
                new Label("Assign Grade:"), gradeInput, btnSetGrade
        );

// --- RIGHT SIDE: STUDENT DIRECTORY (350px wide) ---
        VBox studentListLayout = new VBox(10);
        studentListLayout.setPrefWidth(350);
        HBox.setHgrow(studentListLayout, javafx.scene.layout.Priority.ALWAYS);

        ListView<Student> allStudentsView = new ListView<>(studentObservableList);
// Custom Cell to show Name + GPA in the list
        allStudentsView.setCellFactory(lv -> new ListCell<Student>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setText(null);
                } else {
                    setText(s.getName() + " [GPA: " + String.format("%.2f", s.calculateGPA()) + "]");
                }
            }
        });

        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Select a student to view their report card...");
        detailsArea.setEditable(false);
        detailsArea.setPrefHeight(200);

        studentListLayout.getChildren().addAll(new Label("Student Directory"), allStudentsView, new Label("Student Details / Grades:"), detailsArea);

// --- LOGIC & EVENT HANDLERS ---

// Create Student Action
        btnCreateStudent.setOnAction(e -> {
            String name = studentInput.getText();
            if (!name.isEmpty()) {
                schoolManager.createStudent(name);
                studentObservableList.setAll(schoolManager.getAllStudents()); // Refresh List
                studentInput.clear();
            }
        });

// Sync List Selection with Dropdown
        allStudentsView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                studentDropdown.setValue(newVal);
                // Display full course list for the selected student
                StringBuilder sb = new StringBuilder("Report Card for " + newVal.getName() + ":\n");
                if (newVal.getEnrolledCourses().isEmpty()) {
                    sb.append("No courses enrolled.");
                } else {
                    newVal.getEnrolledCourses().forEach((course, grade) ->
                            sb.append("• ").append(course.getCourseName()).append(": ").append(grade).append("\n")
                    );
                }
                detailsArea.setText(sb.toString());
            }
        });

// Enroll Action
        btnEnroll.setOnAction(e -> {
            Student s = studentDropdown.getValue();
            Course c = courseDropdown.getValue();
            if (s != null && c != null) {
                schoolManager.enrollStudentInCourse(s.getName(), c.getCourseId());
                allStudentsView.refresh(); // Update the display strings in the list
                detailsArea.setText(s.getName() + " enrolled in " + c.getCourseName());
            }
        });

// Grade Action
        btnSetGrade.setOnAction(e -> {
            Student s = studentDropdown.getValue();
            Course c = courseDropdown.getValue();
            try {
                double g = Double.parseDouble(gradeInput.getText());
                if (s != null && c != null) {
                    s.setGrade(c, g);
                    allStudentsView.refresh(); // Update GPA in list immediately
                    gradeInput.clear();
                    // Trigger the details update manually
                    allStudentsView.getSelectionModel().select(null); // Deselect
                    allStudentsView.getSelectionModel().select(s);    // Re-select to refresh text area
                }
            } catch (NumberFormatException ex) {
                detailsArea.setText("Error: Enter a numeric grade (0.0 to 4.0)");
            }
        });

// Tab Refresh Logic
        studentTab.setOnSelectionChanged(event -> {
            if (studentTab.isSelected()) {
                courseObservableList.setAll(schoolManager.getAllCourses());
                studentObservableList.setAll(schoolManager.getAllStudents());
            }
        });

        studentMainLayout.getChildren().addAll(studentInputLayout, studentListLayout);
        studentTab.setContent(studentMainLayout);

        tabPane.getTabs().addAll(teacherTab, courseTab, studentTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabPane, 500, 600);
        stage.setTitle("School Management System");
        stage.setScene(scene);
        stage.show();
    }

    private void updateCourseArea(TextArea area) {
        StringBuilder sb = new StringBuilder();
        schoolManager.getAllCourses().forEach(c ->
                sb.append(c.getCourseId()).append(" - ").append(c.getCourseName()).append("\n")
        );
        area.setText(sb.toString());
    }

    public static void main(String[] args) {
        launch();
    }
}