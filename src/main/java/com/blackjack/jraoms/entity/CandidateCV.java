package com.blackjack.jraoms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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
@Table(name = "candidate_cv")
public class CandidateCV {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false)
    private String name;
	
	@Column(nullable = false)
    private String type;
    
    @Lob
    @Column(length = 1048576)
    private byte[] data;

	public CandidateCV(String name, String type, byte[] data) {
		super();
		this.name = name;
		this.type = type;
		this.data = data;
	}
	    
}
