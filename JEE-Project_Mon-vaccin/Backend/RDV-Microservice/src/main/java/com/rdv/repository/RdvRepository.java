package com.rdv.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rdv.entity.Rdv;

public interface RdvRepository extends MongoRepository<Rdv, String> {
	
	Boolean existsByCitoyenCin(String cin);
	Boolean existsByDateContaining(String date);
	List<Rdv> findByDateContaining(String date);
	List<Rdv> findByCentreIdOrderByDateAsc(String id);
	List<Rdv> findByDateContainingAndCentreId(String date, String id);
	Rdv findByCitoyenCin(String cin);
	void deleteByCitoyenCin(String cin);
}
