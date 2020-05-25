package com.edu.avas.Model;

public class User {
    String userName, userEmail, userPhone, userPic, uid, userFullName, userFathersName, userAge, userDOB, userSchool,userCity,userReference,
            userClass, userSpouse, userFormStatus;

    public User() {

    }

    public User(String userName, String userEmail, String userPhone, String userPic, String uid,
                String userFullName, String userFathersName, String userAge, String userDOB, String userSchool,
                String userClass, String userSpouse, String userFormStatus,String userCity,String userReference) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPic = userPic;
        this.uid = uid;
        this.userFullName = userFullName;
        this.userFathersName = userFathersName;
        this.userAge = userAge;
        this.userDOB = userDOB;
        this.userSchool = userSchool;
        this.userClass = userClass;
        this.userSpouse = userSpouse;
        this.userFormStatus = userFormStatus;
        this.userCity = userCity;
        this.userReference = userReference;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserFathersName() {
        return userFathersName;
    }

    public void setUserFathersName(String userFathersName) {
        this.userFathersName = userFathersName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(String userDOB) {
        this.userDOB = userDOB;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public String getUserSpouse() {
        return userSpouse;
    }

    public void setUserSpouse(String userSpouse) {
        this.userSpouse = userSpouse;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserReference() {
        return userReference;
    }

    public void setUserReference(String userReference) {
        this.userReference = userReference;
    }

    public String getUserFormStatus() {
        return userFormStatus;
    }

    public void setUserFormStatus(String userFormStatus) {
        this.userFormStatus = userFormStatus;
    }
}