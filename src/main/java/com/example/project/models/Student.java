package com.example.project.models;

import java.util.*;

public class Student {
    private String name;
    private String studentId;
    // Map to store Course object and the grade received (0.0 - 4.0)
    private Map<Course, Double> enrolledCourses;

    public Student(String name) {
        this.name = name;
        this.studentId = "STU" + (new Random().nextInt(9000) + 1000);
        this.enrolledCourses = new HashMap<>();
    }

    // Method to enroll in a course (initially no grade)
    public void enroll(Course course) {
        if (!enrolledCourses.containsKey(course)) {
            enrolledCourses.put(course, 0.0);
            course.enrollStudent(this); // Tell the course it has a new student
            System.out.println("LOG: " + name + " enrolled in " + course.getCourseName());
        }
    }

    // Method to modify/set the grade for a specific course
    public void setGrade(Course course, double grade) {
        if (enrolledCourses.containsKey(course)) {
            enrolledCourses.put(course, grade);
        } else {
            System.out.println("Error: Student is not enrolled in this course.");
        }
    }

    // Calculates GPA based on the average of all course grades
    public double calculateGPA() {
        if (enrolledCourses.isEmpty()) return 0.0;

        double sum = 0;
        for (double grade : enrolledCourses.values()) {
            sum += grade;
        }
        return sum / enrolledCourses.size();
    }

    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public Map<Course, Double> getEnrolledCourses() { return enrolledCourses; }
}