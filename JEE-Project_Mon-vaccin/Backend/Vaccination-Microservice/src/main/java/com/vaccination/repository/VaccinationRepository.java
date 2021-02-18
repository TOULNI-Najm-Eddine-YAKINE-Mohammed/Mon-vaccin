package com.vaccination.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vaccination.entity.Vaccination;

public interface VaccinationRepository extends MongoRepository<Vaccination, String> {
	Boolean existsByCitoyenCin(String cin);
	List<Vaccination> findByCentreIdOrderByDateAsc(String centreId);
}
