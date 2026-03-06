package com.cambio_earth.symbiosis.dto;

public class VerifyUserDto {
    private String email;
    private String first;
    private String second;
    private String third;
    private String fourth;
    private String fifth;
    private String sixth;

    public String getFullCode() {
        return first + second + third + fourth + fifth + sixth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst() {
        return first;
    }
    
    public void setFirst(String first) {
        this.first = first;
    }
    
    public String getSecond() {
        return second;
    }
    
    public void setSecond(String second) {
        this.second = second;
    }
    
    public String getThird() {
        return third;
    }
    
    public void setThird(String third) {
        this.third = third;
    }
    
    public String getFourth() {
        return fourth;
    }
    
    public void setFourth(String fourth) {
        this.fourth = fourth;
    }
    
    public String getFifth() {
        return fifth;
    }
    
    public void setFifth(String fifth) {
        this.fifth = fifth;
    }
    
    public String getSixth() {
        return sixth;
    }
    
    public void setSixth(String sixth) {
        this.sixth = sixth;
    }
}