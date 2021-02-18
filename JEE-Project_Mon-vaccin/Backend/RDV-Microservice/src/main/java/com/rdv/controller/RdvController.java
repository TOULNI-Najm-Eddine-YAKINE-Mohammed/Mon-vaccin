package com.rdv.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rdv.entity.Rdv;
import com.rdv.repository.RdvRepository;
import com.user.entity.User;

@RestController
@RequestMapping("/api/rdvs")
@CrossOrigin(origins = "http://localhost:4200")
public class RdvController {
	
	private int nbrParJour = 120;
	private int nbrPar2Heures = 40;
	private String url = "http://localhost:8080/api/users/";
	private String url2 = "http://localhost:8082/api/vaccinations/";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private RdvRepository repository;
	
	@PostMapping("")
	public ResponseEntity<?> saveRdv(@RequestHeader(value="Authorization") String token, @RequestBody ObjectNode objectNode) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		Boolean isVac = (restTemplate.exchange(url2 + "citoyen/"+userApi.getCin(),HttpMethod.GET, entity, ObjectNode.class).getBody()).asBoolean();
		if(userApi.getType().equals("citoyen") && !isVac) {
			if (repository.existsByCitoyenCin(userApi.getCin()))
				return ResponseEntity.badRequest().body("Erreur: vous avez déjà un rendez-vous");
			
			String date = objectNode.get("date").asText();
			String jour = date.substring(0,10);
			User centre =  restTemplate.exchange(url + "centres/"+ objectNode.get("ville").asText(),HttpMethod.GET, entity, User.class).getBody();
			String centreId = centre.getId();
			
			if (repository.findByDateContainingAndCentreId(jour, centre.getId()).size() > nbrParJour)
				return ResponseEntity.badRequest().body("Erreur: Maximum de citoyens par jour : "+nbrParJour);
			
			String heure1 = jour+"_10:00-12:00";
			String heure2 = jour+"_12:00-14:00";
			String heure3 = jour+"_14:00-16:00";
			
			if ((date.equals(heure1) && repository.findByDateContainingAndCentreId(heure1, centreId).size() > nbrPar2Heures)||
				(date.equals(heure2) && repository.findByDateContainingAndCentreId(heure2, centreId).size() > nbrPar2Heures)||
				(date.equals(heure3) && repository.findByDateContainingAndCentreId(heure3, centreId).size() > nbrPar2Heures))
						return ResponseEntity.badRequest().body("Erreur: Maximum de citoyens par 2h : "+nbrPar2Heures);
			
			User citoyen = new User();
			citoyen.setNom(objectNode.get("nom").asText());
			citoyen.setPrenom(objectNode.get("prenom").asText());
			citoyen.setCin(objectNode.get("cin").asText());
			citoyen.setSexe(objectNode.get("sexe").asText());
			citoyen.setAge(objectNode.get("age").asText());
			citoyen.setTel(objectNode.get("tel").asText());
			
			HttpEntity<User> requestUpdate = new HttpEntity<>(citoyen, headers);
			restTemplate.exchange(url + "citoyen",HttpMethod.PUT, requestUpdate, Void.class);
			userApi = restTemplate.exchange(url + "authenticated", HttpMethod.GET, entity, User.class).getBody();
			Rdv rdv = new Rdv(null, objectNode.get("date").asText() ,userApi,centre);
			repository.save(rdv);
			return ResponseEntity.ok(rdv);
		} else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@PutMapping("")
	public ResponseEntity<?> updateRdv(@RequestHeader(value="Authorization") String token, @RequestBody ObjectNode objectNode) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		Boolean isVac = (restTemplate.exchange(url2 + "citoyen/"+userApi.getCin(),HttpMethod.GET, entity, ObjectNode.class).getBody()).asBoolean();
		if(userApi.getType().equals("citoyen") && !isVac) {
			if (repository.existsByCitoyenCin(userApi.getCin())) {
				
				String date = objectNode.get("date").asText();
				Rdv rdv = repository.findByCitoyenCin(userApi.getCin());
				String centreId = rdv.getCentre().getId();
				String jour = date.substring(0,10);
				
				if (repository.findByDateContainingAndCentreId(jour, centreId).size() > nbrParJour)
					return ResponseEntity.badRequest().body("Erreur: Maximum de citoyens par jour : "+nbrParJour);
				
				String heure1 = jour+"_10:00-12:00";
				String heure2 = jour+"_12:00-14:00";
				String heure3 = jour+"_14:00-16:00";
				
				if ((date.equals(heure1) && repository.findByDateContainingAndCentreId(heure1, centreId).size() > nbrPar2Heures)||
					(date.equals(heure2) && repository.findByDateContainingAndCentreId(heure2, centreId).size() > nbrPar2Heures)||
					(date.equals(heure3) && repository.findByDateContainingAndCentreId(heure3, centreId).size() > nbrPar2Heures))
						return ResponseEntity.badRequest().body("Erreur: Maximum de citoyens par 2h : "+nbrPar2Heures);
				rdv.setDate(date);
				repository.save(rdv);
				return ResponseEntity.ok(rdv);
			}
			else
				return ResponseEntity.badRequest().body("Erreur: vous n'avez pas un rendez-vous");
		} else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("")
	public ResponseEntity<?> getRdvs(@RequestHeader(value="Authorization") String token) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(userApi.getType().equals("centre"))
			return ResponseEntity.ok(repository.findByCentreIdOrderByDateAsc(userApi.getId()));
		if (userApi.getType().equals("admin"))
			return ResponseEntity.ok(repository.findAll());
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/date/{date}")
	public ResponseEntity<?> getRdvsByDate(@RequestHeader(value="Authorization") String token, @PathVariable String date) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(userApi.getType().equals("centre"))
			return ResponseEntity.ok(repository.findByDateContainingAndCentreId(date, userApi.getId()));
		if(userApi.getType().equals("admin"))
			return ResponseEntity.ok(repository.findByDateContaining(date));
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/cin/{cin}")
	public ResponseEntity<?> getRdvByCitoyenCin(@RequestHeader(value="Authorization") String token, @PathVariable String cin) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(!userApi.getType().equals("admin"))
			return ResponseEntity.ok(repository.findByCitoyenCin(cin));
		else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@DeleteMapping("/{cin}")
	public ResponseEntity<?> deleteRdvByCitoyenCin(@RequestHeader(value="Authorization") String token, @PathVariable String cin) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(!userApi.getType().equals("admin")) {
			if(repository.existsByCitoyenCin(cin)) {
				repository.deleteByCitoyenCin(cin);
				if(userApi.getType().equals("citoyen"))
					return(ResponseEntity.ok(restTemplate.exchange(url + "citoyen/reset",HttpMethod.PUT, entity, User.class).getBody()));
				return ResponseEntity.ok("rendez-vous supprimé");
			}
			return ResponseEntity.badRequest().body("Erreur: ce citoyen n'a pas de rendez-vous");
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
			hm.put("hasRdv", repository.existsByCitoyenCin(cin));
			return ResponseEntity.ok(hm);
		}
		return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/dates/{ville}")
	public ResponseEntity<?> getDatesUnvalablesOfCentre(@RequestHeader(value="Authorization") String token, @PathVariable String ville) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		Boolean isVac = (restTemplate.exchange(url2 + "citoyen/"+userApi.getCin(),HttpMethod.GET, entity, ObjectNode.class).getBody()).asBoolean();
		if(userApi.getType().equals("citoyen") && !isVac) {
			User centre =  restTemplate.exchange(url + "centres/"+ ville,HttpMethod.GET, entity, User.class).getBody();
			List<Rdv> rdvs = repository.findByCentreIdOrderByDateAsc(centre.getId());
			ArrayList<String> dates = new ArrayList<>();
			for(Rdv rdv:rdvs) {
				String date = rdv.getDate().substring(0,10);
				if(repository.findByDateContainingAndCentreId(date, centre.getId()).size() > nbrParJour) {
					if(!dates.contains(date))
						dates.add(date);
				}
			}
			return ResponseEntity.ok(dates);
		} else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/heures/{ville}/{date}")
	public ResponseEntity<?> getHeuresOfCentreDate(@RequestHeader(value="Authorization") String token, @PathVariable String ville, @PathVariable String date) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		Boolean isVac = (restTemplate.exchange(url2 + "citoyen/"+userApi.getCin(),HttpMethod.GET, entity, ObjectNode.class).getBody()).asBoolean();
		if(userApi.getType().equals("citoyen") && !isVac) {
			String centreId = "";
			centreId =  (restTemplate.exchange(url + "centres/"+ ville,HttpMethod.GET, entity, User.class).getBody()).getId();
			ArrayList<String> heures = new ArrayList<>();
			String heure1 = date+"_10:00-12:00";
			String heure2 = date+"_12:00-14:00";
			String heure3 = date+"_14:00-16:00";
			if (repository.findByDateContainingAndCentreId(heure1, centreId).size() <= nbrPar2Heures)
				heures.add("10:00-12:00");
			if (repository.findByDateContainingAndCentreId(heure2, centreId).size() <= nbrPar2Heures)
				heures.add("12:00-14:00");
			if (repository.findByDateContainingAndCentreId(heure3, centreId).size() <= nbrPar2Heures)
				heures.add("14:00-16:00");
			return ResponseEntity.ok(heures);
		} else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/villes")
	@SuppressWarnings("unchecked")
	public ResponseEntity<?> getRdvsVille(@RequestHeader(value="Authorization") String token) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(userApi.getType().equals("admin")) {
			ArrayList<String> villes = restTemplate.exchange(url + "villes",HttpMethod.GET, entity, ArrayList.class).getBody();
			HashMap<String, Integer> hm = new HashMap<>();
			for(String ville: villes) {
				User centre =  restTemplate.exchange(url + "centres/"+ ville,HttpMethod.GET, entity, User.class).getBody();
				List<Rdv> rdvs = repository.findByCentreIdOrderByDateAsc(centre.getId());
				hm.put(ville, rdvs.size());
			}
			return ResponseEntity.ok(hm);
		} else
			return ResponseEntity.badRequest().body("Erreur: vous n'avez pas le droit");
	}
	
	@GetMapping("/semaine")
	public ResponseEntity<?> getRdvsSemaine(@RequestHeader(value="Authorization") String token) {
		HttpHeaders headers = headers(token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		User userApi = restTemplate.exchange(url + "authenticated",HttpMethod.GET, entity, User.class).getBody();
		if(userApi.getType().equals("admin")) {
			HashMap<String, Integer> hm = new HashMap<>();
			for(int i=0; i<7; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, +i);
				String pattern = "dd-MM";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.FRANCE);
				String d = simpleDateFormat.format(cal.getTime());
				List<Rdv> rdvs = repository.findByDateContaining(d);
				d = d.replace('-', '/');
				hm.put(d, rdvs.size());
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
}