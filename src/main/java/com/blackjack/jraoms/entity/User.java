package com.blackjack.jraoms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(length = 45, nullable = false)
	private String name;
	
	@Column(length = 45, nullable = false)
	private String email;
	
	@Column(length = 200, nullable = false)
	private String password;
	
	@Column(nullable = false)
	private boolean enable;

	@Column(nullable = false)
	private LocalDate registerDate;
	
	@Column(length = 45, nullable = false)
	private String profilePicture;

	@Enumerated(EnumType.STRING)
	private Role role;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="department_id")
	private Department department;

}
