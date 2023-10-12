package com.blackjack.jraoms.config;

import com.blackjack.jraoms.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class MyUserDetails implements UserDetails {

    private final User user;

    //To get user's id after authenticated from spring security context holder
    public int getUserId(){
        return this.user.getId();
    }

    //To get user's name after authenticated from spring security context holder
    public String getUserName(){
        return this.user.getName();
    }

    public String getUserImage() {
    	return this.user.getProfilePicture();
    }
    
    public String getUserRole() {
    	return this.user.getRole().toString();
    }
    
    public void setUserImage(String image) {
    	this.user.setProfilePicture(image);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnable();
    }
    
}
