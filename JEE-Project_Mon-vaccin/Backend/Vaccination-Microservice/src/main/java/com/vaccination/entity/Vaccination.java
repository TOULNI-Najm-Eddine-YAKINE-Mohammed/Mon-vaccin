package com.vaccination.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vaccinations")
public class Vaccination {
	
	@Id private String id;
	private String date;
	private User citoyen;
	private User centre;
}