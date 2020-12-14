package com.edu.avas.Model;

public class Courses {
    String courseName,courseDesc,coursePic,courseVideo,videoTitle,orderId,status,courseAvailability,coursePrice;

    public Courses() {
    }

    public Courses(String courseName, String courseDesc,String coursePic,String status,
                   String courseVideo,String videoTitle,String courseAvailability,String coursePrice) {
        this.courseName = courseName;
        this.courseDesc = courseDesc;
        this.courseAvailability = courseAvailability;
        this.coursePic = coursePic;
        this.status = status;
        this.courseVideo = courseVideo;
        this.videoTitle = videoTitle;
        this.coursePrice = coursePrice;
    }

    public Courses(String courseName,String orderId,String status) {
        this.courseName = courseName;
        this.orderId = orderId;
        this.status = status;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoursePic() {
        return coursePic;
    }

    public void setCoursePic(String coursePic) {
        this.coursePic = coursePic;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    public String getCourseAvailability() {
        return courseAvailability;
    }

    public void setCourseAvailability(String courseAvailability) {
        this.courseAvailability = courseAvailability;
    }

    public String getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(String coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseVideo() {
        return courseVideo;
    }

    public void setCourseVideo(String courseVideo) {
        this.courseVideo = courseVideo;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

}