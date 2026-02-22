package com.example.grimorium_api.models;

public class UsersDto {
    private int id;
    private String name;
    private String email;
    private int booksRead;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getBooksRead() {
        return booksRead;
    }
    public void setBooksRead(int booksRead) {
        this.booksRead = booksRead;
    }
}
