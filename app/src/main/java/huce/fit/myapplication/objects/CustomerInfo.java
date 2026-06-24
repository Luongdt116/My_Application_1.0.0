package huce.fit.myapplication.objects;

import java.io.Serializable;

public class CustomerInfo implements Serializable {
    private int infoId;
    private int accountId; 
    private String fullName;
    private String phoneNumber;
    private String birthYear;
    private String gender;

    public CustomerInfo() {
    }

    public CustomerInfo(int accountId, String fullName, String phoneNumber, String birthYear, String gender) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    // Getter and Setter
    public int getInfoId() { return infoId; }
    public void setInfoId(int infoId) { this.infoId = infoId; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getBirthYear() { return birthYear; }
    public void setBirthYear(String birthYear) { this.birthYear = birthYear; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
