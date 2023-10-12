package com.blackjack.jraoms.service;

import java.util.List;
import java.util.stream.Collectors;

import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.dto.UserDto;
import com.blackjack.jraoms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

@Service
public class UserViewService {

    private final UserRepository userRepository;

    @Autowired
    public UserViewService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getAllUsers(ModelMap model) {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream().map(this::convertToDto).collect(Collectors.toList());
        model.addAttribute("users", userDtos);
        return "/admin/manage-users";
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .enable(user.isEnable())
                .registerDate(user.getRegisterDate())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : 0)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : "")
                .companyId(user.getDepartment() != null ? user.getDepartment().getCompany().getId() : 0)
                .CompanyName(user.getDepartment() != null ? user.getDepartment().getCompany().getName() : "")
                .build();
    }


}
