package com.soundarya.enterpriseexpensemanagementsystem.dto;

public class EmployeeRequestDTO {

    private String name;
    private String email;

    public EmployeeRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}