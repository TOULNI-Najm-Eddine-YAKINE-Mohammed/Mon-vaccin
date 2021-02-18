import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private url = 'http://127.0.0.1:8080/api/users';
  private url2 = 'http://127.0.0.1:8081/api/rdvs';
  private url3 = 'http://127.0.0.1:8082/api/vaccinations';

  constructor(
    private http: HttpClient,
  ) { }

  addCentre(user: any) {
    return this.http.post(`${this.url}/centre`, user, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  getVaccinations(): Observable<[]> {
    return this.http.get<[]>(`${this.url3}`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  getCentres(): Observable<[]> {
    return this.http.get<[]>(`${this.url}/centres`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  getRdvNbrs(): Observable<[]> {
    return this.http.get<[]>(`${this.url2}/villes`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  getVaccinationNbrs(): Observable<[]> {
    return this.http.get<[]>(`${this.url3}/villes`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  getRdvSemaine(): Observable<[]> {
    return this.http.get<[]>(`${this.url2}/semaine`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }
}
