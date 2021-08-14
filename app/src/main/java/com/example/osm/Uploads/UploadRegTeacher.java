package com.example.osm.Uploads;

public class UploadRegTeacher {
    String firstName, lastName, email, college, qualification, password, mobileNo, loginId, profileImg, dob, city,
            emailVisible, phoneVisible;

    public UploadRegTeacher() {
    }

    public UploadRegTeacher(String firstName, String lastName, String email,
                            String college, String qualification, String password, String mobileNo,
                            String loginId, String profileImg, String dob, String city, String emailVisible,
                            String phoneVisible) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.college = college;
        this.qualification = qualification;
        this.password = password;
        this.mobileNo = mobileNo;
        this.loginId = loginId;
        this.profileImg = profileImg;
        this.dob = dob;
        this.city = city;
        this.emailVisible = emailVisible;
        this.phoneVisible = phoneVisible;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmailVisible() {
        return emailVisible;
    }

    public void setEmailVisible(String emailVisible) {
        this.emailVisible = emailVisible;
    }

    public String getPhoneVisible() {
        return phoneVisible;
    }

    public void setPhoneVisible(String phoneVisible) {
        this.phoneVisible = phoneVisible;
    }
}
