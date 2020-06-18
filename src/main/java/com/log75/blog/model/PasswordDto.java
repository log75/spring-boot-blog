package com.log75.blog.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Created by alireza on 5/13/20.
 */
public class PasswordDto {

    private String oldPassword;

    private  String token;

    @NotBlank
    @Size(min = 8, message = "password most be more than 8 char")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}