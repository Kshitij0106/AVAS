package com.edu.avas.Model;

import java.util.ArrayList;

public class CoursesCategories {
    String title;
    ArrayList<Courses> CoursesList;

    public CoursesCategories() {
    }

    public CoursesCategories(String title, ArrayList<Courses> CoursesList) {
        this.title = title;
        this.CoursesList = CoursesList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Courses> getCoursesList() {
        return CoursesList;
    }

    public void setCoursesList(ArrayList<Courses> coursesList) {
        CoursesList = coursesList;
    }
}