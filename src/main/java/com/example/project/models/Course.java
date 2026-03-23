package com.example.project.models;

import java.util.*;

public class Course {
    private String courseName;
    private String courseId;
    private Teacher assignedTeacher; // Now uses the Teacher class
    private List<Student> enrolledStudents = new ArrayList<>();

    public Course(String name) {
        this.courseName = name;
        this.courseId = generateId(name);
    }

    private String generateId(String name) {
        String prefix = (name.length() >= 3) ? name.substring(0, 3).toUpperCase() : name.toUpperCase();
        int randomNum = new Random().nextInt(900) + 100;
        return prefix + randomNum;
    }

    // This method now uses your Teacher class
    public void assignTeacher(Teacher teacher) {
        this.assignedTeacher = teacher;
        // This calls the method in your Teacher class to update their list!
        if (teacher != null) {
            teacher.addCourse(this);
        }
    }
    // --- Enroll Student Logic ---
    public void enrollStudent(Student student) {
        if (student != null && !enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            System.out.println("LOG: " + student.getName() + " has been added to the roster for " + this.courseName);
        } else if (enrolledStudents.contains(student)) {
            System.out.println("LOG: Student " + student.getName() + " is already enrolled in " + this.courseName);
        }
    }

    // Returns the list of all students currently in this class
    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    // Helpful for displaying the roster in your JavaFX UI
    public int getStudentCount() {
        return enrolledStudents.size();
    }
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }

    public String getTeacherName() {
        return (assignedTeacher != null) ? assignedTeacher.getName() : "None";
    }
}