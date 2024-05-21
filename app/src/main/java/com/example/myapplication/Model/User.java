package com.example.myapplication.Model;

public class User {
    private String userId;
    private String username;
    private String email;
    private String role;
    private String password;
    private String age;
    private String gender;

    public String getRole() {
        return role;
    }

    public User() {
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User(String userId, String username, String email, String role, String password, String age, String gender) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
