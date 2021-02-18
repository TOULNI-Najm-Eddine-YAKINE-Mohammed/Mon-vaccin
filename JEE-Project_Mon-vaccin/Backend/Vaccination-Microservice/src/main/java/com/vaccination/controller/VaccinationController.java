package com.vaccination.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.user.entity.User;
import com.vaccination.entity.Vaccination;
import com.vaccination.repository.VaccinationRepository;

@RestController
@RequestMapping("/api/vaccinations")
@CrossOrigin(origins = "http://localhost:4200")
public class VaccinationController {
	
	private String url = "http://localhost:8080/api/users/";
	private String url2 = "http://localhost:8081/api/rdvs/";
	
	@Autowired
	private VaccinationRepository repository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@PostMapping("")
	public ResponseEntity<?> saveVaccination(@RequestHeader(value="Authorization") String token, @RequestBody ObjectNode objectNode) {
		String cin = objectNode.get("cin").asText();
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User centre = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		User citoyen = restTemplate.exchange(url + "citoyen/"+cin,HttpMethod.GET, entity, User.class).getBody();
		if(centre.getType().equals("centre")) {
			if(repository.existsByCitoyenCin(cin))
				return ResponseEntity.badRequest().body("ce citoyen a déjà une vaccination");
			restTemplate.exchange(url2+"/"+cin,HttpMethod.DELETE, entity, Void.class);
			Vaccination vac = new Vaccination(null, dateNow(), citoyen, centre);
			repository.save(vac);
			return ResponseEntity.ok(vac);
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("")
	public ResponseEntity<?> getVaccinations(@RequestHeader(value="Authorization") String token) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(userApi.getType().equals("admin")) {
			return ResponseEntity.ok(repository.findAll());
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/citoyen/{cin}")
	public ResponseEntity<?> existsByCitoyenCin(@RequestHeader(value="Authorization") String token, @PathVariable String cin) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(!userApi.getType().equals("admin")) {
			HashMap<String, Boolean> hm = new HashMap<>();
			hm.put("vaccination", repository.existsByCitoyenCin(cin));
			return ResponseEntity.ok(hm);
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/villes")
	@SuppressWarnings("unchecked")
	public ResponseEntity<?> getVaccinationsVille(@RequestHeader(value="Authorization") String token) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(userApi.getType().equals("admin")) {
			ArrayList<String> villes = restTemplate.exchange(url + "villes",HttpMethod.GET, entity, ArrayList.class).getBody();
			HashMap<String, Integer> hm = new HashMap<>();
			for(String ville: villes) {
				User centre =  restTemplate.exchange(url + "centres/"+ ville,HttpMethod.GET, entity, User.class).getBody();
				List<Vaccination> rdvs = repository.findByCentreIdOrderByDateAsc(centre.getId());
				hm.put(ville, rdvs.size());
			}
			return ResponseEntity.ok(hm);
		} else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	private HttpHeaders headers(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer "+token.substring(7));
		return headers;
	}
	
	private String dateNow() {
		String pattern = "dd-MM-yyyy_HH:mm";
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, Locale.FRANCE);
		return simpleDateFormat.format(new Date());
	}
}
