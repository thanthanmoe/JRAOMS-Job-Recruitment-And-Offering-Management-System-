package com.blackjack.jraoms.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false, columnDefinition = "TEXT") // Using TEXT data type for 'about'
	private String about;

	@Column(nullable = false)
	private String link;

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Department> departments;

}