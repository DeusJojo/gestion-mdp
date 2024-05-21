import { Component, inject } from '@angular/core';
import { Login } from '../../model/Login.model';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginObj: Login;
  errorLog: boolean = false;
  private configUrlLogin: string =
  'http://localhost:8080/login';

  constructor(private http: HttpClient, private router: Router) {
    this.loginObj = new Login();
  }

  onLogin() {
    return this.http
      .post(this.configUrlLogin, this.loginObj)
      .pipe(
        catchError((e) => {
          console.error(e);
          this.errorLog = true;
          return of(null);
        })
      )
      .subscribe((res: any) => {
        if (res) {
          this.errorLog = false;
          localStorage.setItem('access-token', res['access-token']);
          localStorage.setItem('refresh-token', res['refresh-token']);
          this.router.navigateByUrl('/dashboard');
        } else {
          this.errorLog = true;
        }
      });
  }
}
