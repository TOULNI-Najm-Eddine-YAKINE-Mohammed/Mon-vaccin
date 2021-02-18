import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RendezVousService {

  private url = 'http://127.0.0.1:8081/api/rdvs';
  private url2 = 'http://127.0.0.1:8082/api/vaccinations';

  constructor(
    private http: HttpClient,
  ) { }

  hasRdv(cin: string): Observable<any> {
    return this.http.get<any>(`${this.url}/citoyen/${cin}`,{
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  hasVaccination(cin: string): Observable<any> {
    return this.http.get<any>(`${this.url2}/citoyen/${cin}`,{
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  addRdv(rdv: string) {
    return this.http.post(`${this.url}`, rdv, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  updateRdv(date: string) {
    return this.http.put(`${this.url}`, date, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  deleteRdv(cin: string) {
    return this.http.delete(`${this.url}/${cin}`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  getDisabledDates(ville:string): Observable<[]> {
    return this.http.get<[]>(`${this.url}/dates/`+ville, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  getDisponiblesHeures(ville:string, date:string): Observable<[]> {
    return this.http.get<[]>(`${this.url}/heures/`+ville+'/'+date, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }
  
}
