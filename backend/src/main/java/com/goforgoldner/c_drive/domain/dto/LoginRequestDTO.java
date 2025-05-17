package com.goforgoldner.c_drive.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequestDTO {
    @JsonProperty("user")
    private UserDTO userDTO;
    @JsonProperty("token")
    private String jwtToken;

    public LoginRequestDTO(UserDTO userDTO, String jwtToken) {
        this.jwtToken = jwtToken;
        this.userDTO = userDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
