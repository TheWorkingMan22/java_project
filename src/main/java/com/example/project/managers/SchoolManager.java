package com.example.project.managers;

import com.example.project.models.Course;
import com.example.project.models.Student;
import com.example.project.models.Teacher;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class SchoolManager {
    // These MUST be declared here at the top (Class Level)
    private Map<String, Course> courseMap = new HashMap<>();
    private Map<String, Teacher> teacherMap = new HashMap<>();
    private Map<String, Student> studentMap = new HashMap<>();

    // Create Teacher
    public void createTeacher(String name) {
        Teacher t = new Teacher(name);
        teacherMap.put(t.getTeacherId(), t); // Now teacherMap is "resolved"
        System.out.println("Registered: " + t.getName());
    }

    // This is the method we added to fix the UI dropdown selection
    public Teacher getTeacherByName(String name) {
        for (Teacher t : teacherMap.values()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }
    // Helper to get all teachers for the UI
    public Collection<Teacher> getAllTeachers() {
        return teacherMap.values();
    }

    // Existing Course methods...
    public Course createCourse(String name) {
        Course c = new Course(name);
        courseMap.put(c.getCourseId(), c);
        return c;
    }

    public Collection<Course> getAllCourses() {
        return courseMap.values();
    }
    public void enrollStudentInCourse(String studentName, String courseId) {
        // 1. Find the student and course objects
        Student student = getStudentByName(studentName);
        Course course = courseMap.get(courseId);

        if (student != null && course != null) {
            // 2. Tell the student to add the course
            student.enroll(course);

            // 3. Tell the course to add the student
            course.enrollStudent(student);

            System.out.println("SUCCESS: " + studentName + " is now in " + course.getCourseName());
        } else {
            System.out.println("ERROR: Student or Course not found.");
        }
    }
    // --- Added Student Creation ---
    public Student createStudent(String name) {
        Student s = new Student(name);
        studentMap.put(s.getStudentId(), s);
        System.out.println("Registered Student: " + s.getName() + " (" + s.getStudentId() + ")");
        return s;
    }

    // --- The Missing "Get All" Method ---
    public Collection<Student> getAllStudents() {
        return studentMap.values();
    }
    public Student getStudentByName(String name) {
        for (Student s : studentMap.values()) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

}