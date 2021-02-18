package com.user.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
	Boolean existsByEmail(String email);
	Boolean existsByVille(String ville);
	Boolean existsByCin(String cin);
	User findByEmail(String email);
	User findByVille(String ville);
	User findByCin(String cin);
	List<User> findByType(String type);
}
