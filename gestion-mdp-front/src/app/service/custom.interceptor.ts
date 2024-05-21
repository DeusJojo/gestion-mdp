import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { switchMap } from 'rxjs/operators';

export const customInterceptor: HttpInterceptorFn = (req, next) => {
  let httpClient = inject(HttpClient);
  let router = inject(Router);
  const accessToken = localStorage.getItem('access-token');
  const refreshToken = localStorage.getItem('refresh-token');

  if (accessToken && req.url !== 'http://localhost:8080/api/refreshToken') {
    let decodedToken = jwtDecode(accessToken);
    const isExpired =
      decodedToken && decodedToken.exp
        ? decodedToken.exp < Date.now() / 1000
        : false;
    if (isExpired && refreshToken) {
      return httpClient
        .get('http://localhost:8080/api/refreshToken', {
          headers: { Authorization: `Bearer ${refreshToken}` },
        })
        .pipe(
          switchMap((newToken: any) => {
            localStorage.setItem('access-token', newToken['access-token']);
            const cloneRequest = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken['access-token']}`,
              },
            });
            return next(cloneRequest);
          })
        );
    } else {
      const cloneRequest = req.clone({
        setHeaders: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      return next(cloneRequest);
    }
  }
  return next(req);
};
