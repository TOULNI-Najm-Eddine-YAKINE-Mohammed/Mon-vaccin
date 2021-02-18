package com.user.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.entity.User;
import com.user.repository.UserRepository;
import com.user.security.JwtTokenUtil;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		if(user.getEmail()!=null && user.getPassword()!=null && user.getEmail().trim()!="" && user.getPassword().trim()!="") {
			if(repository.existsByEmail(user.getEmail()))
				return ResponseEntity.badRequest().body("Erreur: email existe déjà");
			else {
				user.setPassword(bcryptEncoder.encode(user.getPassword()));
				repository.save(user);
				return ResponseEntity.ok("L'utilisateur enregistré avec succès");
			}
		}
		return ResponseEntity.badRequest().body("Email ou mot de passe est vide");
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user){
		if(user.getEmail()!=null && user.getPassword()!=null) {
			if(repository.existsByEmail(user.getEmail())) {
				try {
					authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
				} catch (BadCredentialsException e) {
					return ResponseEntity.badRequest().body("Erreur: email ou mot de passe est incorrect");
				}
				final String token = jwtTokenUtil.generateToken(user.getEmail());
				HashMap<String, String> hm = new HashMap<>();
				hm.put("token", token);
				return ResponseEntity.ok(hm);
			}
			else
				return ResponseEntity.badRequest().body("Erreur: cet email n'existe pas");
		}
		return ResponseEntity.badRequest().body("Email ou mot de passe est vide");
	}
	
	@GetMapping("/authenticated")
	public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
		User user = repository.findByEmail(authentication.getName());
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/citoyen/{cin}")
	public ResponseEntity<?> getCitoyen(Authentication authentication, @PathVariable String cin) {
		User user = repository.findByEmail(authentication.getName());
		if(!user.getType().equals("admin")) {
			if(repository.existsByCin(cin)) {
				return ResponseEntity.ok(repository.findByCin(cin));
			}
			return ResponseEntity.badRequest().body("Erreur: pas d'utilisateur avec ce CIN");
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@PutMapping("/citoyen")
	public ResponseEntity<?> updateCitoyen(Authentication authentication,@RequestBody User newUser) {
		User user = repository.findByEmail(authentication.getName());
		if(user.getType().equals("citoyen")) {
			String nom = newUser.getNom();
			String prenom = newUser.getPrenom();
			String cin = newUser.getCin();
			String age = newUser.getAge();
			String sexe = newUser.getSexe();
			String tel = newUser.getTel();
			if (nom!=null && prenom!=null && cin!=null && sexe!=null && age!=null && tel!=null && nom.trim()!="" 
					&& prenom.trim()!="" && cin.trim()!="" && sexe.trim()!="" && age.trim()!="" && tel.trim()!="") {
				if(repository.existsByCin(cin))
					return ResponseEntity.badRequest().body("Erreur: CIN existe déjà");
				user.setNom(nom);
				user.setPrenom(prenom);
				user.setCin(cin);
				user.setAge(age);
				user.setSexe(sexe);
				user.setTel(tel);
				repository.save(user);
				return ResponseEntity.ok(user);
			}
			return ResponseEntity.badRequest().body("Erreur: il faut remplir touts les champs");
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@PutMapping("/citoyen/reset")
	public ResponseEntity<?> resetCitoyen(Authentication authentication) {
		User user = repository.findByEmail(authentication.getName());
		if(user.getType().equals("citoyen")) {
			user.setNom(null);
			user.setPrenom(null);
			user.setCin(null);
			user.setAge(null);
			user.setSexe(null);
			user.setTel(null);
			repository.save(user);
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@PostMapping("/centre")
	public ResponseEntity<?> addCentre(Authentication authentication,@RequestBody User centre) {
		User user = repository.findByEmail(authentication.getName());
		if(user.getType().equals("admin")) {
			String email = centre.getEmail();
			String password = centre.getPassword();
			String adresse = centre.getAdresse();
			String ville = centre.getVille();
			if (email!=null && password!=null && adresse!=null && ville!=null && email.trim()!="" && password.trim()!="" && adresse.trim()!="" && ville.trim()!="") {
				if(repository.existsByEmail(email))
					return ResponseEntity.badRequest().body("Erreur: email existe déjà");
				else if(repository.existsByVille(ville))
					return ResponseEntity.badRequest().body("Erreur: cette ville est déjà contient un centre");
				else {
					User addCentre = new User();
					addCentre.setEmail(email);
					addCentre.setPassword(bcryptEncoder.encode(password));
					addCentre.setAdresse(adresse);
					addCentre.setVille(ville);
					addCentre.setType("centre");
					repository.save(addCentre);
					return ResponseEntity.ok(addCentre);
				}
			}
			return ResponseEntity.badRequest().body("Erreur: il faut remplir touts les champs");
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'êtes pas un admin");
	}
	
	@GetMapping("/centres")
	public ResponseEntity<?> getCentres(Authentication authentication) {
		User user = repository.findByEmail(authentication.getName());
		if(!user.getType().equals("centre"))
			return ResponseEntity.ok(repository.findByType("centre"));
		else
			return ResponseEntity.badRequest().body("Erreur: vous n'êtes pas un admin");
	}
	
	@GetMapping("/centres/{ville}")
	public ResponseEntity<?> getCentreByVille(Authentication authentication,@PathVariable String ville) {
		User user = repository.findByEmail(authentication.getName());
		if(!user.getType().equals("centre")) {
			if(repository.existsByVille(ville))
				return ResponseEntity.ok(repository.findByVille(ville));
			else
				return ResponseEntity.badRequest().body("Erreur: pas de centre dans cette ville");
		}
		else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/villes")	
	public ResponseEntity<?> getCitoyen(Authentication authentication) {
		User user = repository.findByEmail(authentication.getName());
		if(!user.getType().equals("centre")) {
			ArrayList<String> villes = new ArrayList<>();
			for(User u: repository.findByType("centre")) {
				villes.add(u.getVille());
			}
			return ResponseEntity.ok(villes);
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
}
