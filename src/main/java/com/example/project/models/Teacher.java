package com.example.project.models;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class Teacher {
    private String name;
    private String teacherId;
    private List<Course> teachingLoad;

    public Teacher(String name) {
        this.name = name;
        this.teacherId = generateTeacherId(name);
        this.teachingLoad = new ArrayList<>();
    }

    // Generates: TCH + 4 random digits (e.g., TCH8821)
    private String generateTeacherId(String name) {
        int randomNum = new Random().nextInt(9000) + 1000;
        return "TCH" + randomNum;
    }

    // Assigns a course to this teacher's schedule
    public void addCourse(Course course) {
        if (!teachingLoad.contains(course)) {
            teachingLoad.add(course);
            System.out.println("LOG: " + name + " is now teaching " + course.getCourseName());
        }
    }

    // Removes a course if the teacher is reassigned
    public void removeCourse(Course course) {
        teachingLoad.remove(course);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public List<Course> getTeachingLoad() {
        return teachingLoad;
    }

    // Formatted info for the UI
    public void displayTeacherInfo() {
        System.out.println("Teacher: " + name + " [" + teacherId + "]");
        System.out.println("Courses: " + teachingLoad.size());
    }

}