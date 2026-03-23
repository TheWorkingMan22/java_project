package com.example.project.ui;

import com.example.project.managers.SchoolManager;
import com.example.project.models.Course;
import com.example.project.models.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TeacherTab extends Tab {
    private SchoolManager schoolManager;
    private ObservableList<String> teacherNames; // Shared with CourseTab

    public TeacherTab(SchoolManager manager, ObservableList<String> sharedTeacherNames) {
        super("Teachers");
        this.schoolManager = manager;
        this.teacherNames = sharedTeacherNames;
        setClosable(false);
        init();
    }

    private void init() {
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20));

        // LEFT: Input
        VBox inputLayout = new VBox(10);
        inputLayout.setPrefWidth(250);
        TextField teacherInput = new TextField();
        teacherInput.setPromptText("Teacher Name");
        Button btnAdd = new Button("Register Teacher");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        inputLayout.getChildren().addAll(new Label("Register Faculty:"), teacherInput, btnAdd);

        // RIGHT: List
        VBox listLayout = new VBox(10);
        ObservableList<Teacher> teachers = FXCollections.observableArrayList();
        ListView<Teacher> listView = new ListView<>(teachers);
        TextArea details = new TextArea();
        details.setEditable(false);
        listLayout.getChildren().addAll(new Label("Directory:"), listView, details);

        // LOGIC
        btnAdd.setOnAction(e -> {
            if (!teacherInput.getText().isEmpty()) {
                schoolManager.createTeacher(teacherInput.getText());
                refresh(teachers);
                teacherInput.clear();
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                StringBuilder sb = new StringBuilder("ID: " + newV.getTeacherId() + "\nCourses:\n");
                for (Course c : newV.getTeachingLoad()) sb.append("- ").append(c.getCourseName()).append("\n");
                details.setText(sb.toString());
            }
        });

        setOnSelectionChanged(e -> { if (isSelected()) refresh(teachers); });

        mainLayout.getChildren().addAll(inputLayout, listLayout);
        setContent(mainLayout);
    }

    private void refresh(ObservableList<Teacher> list) {
        list.setAll(schoolManager.getAllTeachers());
        teacherNames.clear();
        schoolManager.getAllTeachers().forEach(t -> teacherNames.add(t.getName()));
    }
}