package com.obaju.model;

import javax.validation.constraints.NotNull;
public class UserForm {
    @NotNull
    String name;
    @NotNull
    String email;
    @NotNull
    String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
