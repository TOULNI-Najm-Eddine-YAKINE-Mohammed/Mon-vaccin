import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private url = 'http://127.0.0.1:8080/api/users';

  constructor(
    private http: HttpClient,
  ) { }

  register(user: any) {
    return this.http.post(`${this.url}/register`, user, {
      headers:{'Content-Type': 'application/json',
      'No-Auth':'True'},
      responseType: 'text'
    });
  }

  login(credentials: any) {
    return this.http.post<{ token: string }>(`${this.url}/login`, credentials).pipe(
      map(response => {
        this.setAuthUserInfoInStorage(response.token);
      })
    );
  }

  logout(){
    localStorage.clear();
  }

  authenticatedUser(token: string): Observable<any> {
    return this.http.get<any>(`${this.url}/authenticated`,{
      headers:{Authorization: 'Bearer ' + token}
    });
  }

  setAuthUserInfoInStorage(token: string) {
    if(token != null) {
      this.http.get(`${this.url}/authenticated`,{
        headers:{Authorization: 'Bearer ' + token}
      }).toPromise().then(data => {
        localStorage.setItem('type', data['type']);
        localStorage.setItem('token', token);
      });
    }
  }

}
