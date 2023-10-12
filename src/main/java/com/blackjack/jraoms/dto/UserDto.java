package com.blackjack.jraoms.dto;

import java.time.LocalDate;
import com.blackjack.jraoms.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty("ID")
    private int id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("Enable")
    private boolean enable;
    @JsonProperty("Registration Date")
    private LocalDate registerDate;
    @JsonProperty("Profile Picture")
    private String profilePicture;
    @JsonProperty("Role")
    private Role role;
    @JsonProperty("Department ID")
    private int departmentId;
    @JsonProperty("Department")
    private String departmentName;
    @JsonProperty("Company ID")
    private int companyId;
    @JsonProperty("Company")
    private String CompanyName;


    @JsonProperty("Status") // Add the Status field
    private String status; // Add the Status field

    // Add the getter and setter methods for the Status field
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
