import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable } from '@angular/core';
import { Observable, Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UtilService {

  invokeNavbarInit = new EventEmitter();
  subsVar: Subscription;
  private url = 'http://127.0.0.1:8080/api/users';
  private url2 = 'http://127.0.0.1:8081/api/rdvs';

  constructor(
    private http: HttpClient
  ) { }

  getVilles(token: string): Observable<[]> {
    return this.http.get<[]>(`${this.url}/villes`, {
      headers:{Authorization: 'Bearer ' + token}
    });
  }

  getRdvs(): Observable<[]> {
    return this.http.get<[]>(`${this.url2}`, {
      headers:{Authorization: 'Bearer ' + localStorage.getItem('token')}
    });
  }

  renderNavbar() {
    this.invokeNavbarInit.emit();
  }
}
