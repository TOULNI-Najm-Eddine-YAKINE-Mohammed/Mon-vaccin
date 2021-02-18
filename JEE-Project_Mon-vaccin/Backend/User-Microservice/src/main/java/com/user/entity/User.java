package com.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
	
	@Id private String id;
	private String nom;
	private String prenom;
	private String cin;
	private String sexe;
	private String age;
	private String tel;
	private String adresse;
	private String ville;
	private String email;
	private String password;
	private String type = "citoyen";
	
}
