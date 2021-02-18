import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VaccinationsService {

  private url = 'http://127.0.0.1:8082/api/vaccinations';
  private url2 = 'http://127.0.0.1:8081/api/rdvs';

  constructor(
    private http: HttpClient,
  ) { }

  addVaccination(cin: any) {
    return this.http.post(`${this.url}`, cin, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token'),
      'Content-Type': 'application/json',
      'No-Auth':'True'}
    });
  }

  getByCin(cin: string) : Observable<any> {
    return this.http.get<any>(`${this.url2}/cin/${cin}`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token'), responseType: 'text'}
    });
  }

  getByDate(date: string): Observable<[]> {
    return this.http.get<[]>(`${this.url2}/date/${date}`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }
  
}
