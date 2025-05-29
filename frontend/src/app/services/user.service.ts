import { Injectable, Optional } from '@angular/core';
import { CppFile } from './cpp-file.service';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { environment } from '../../environments/environment';


export class User {
  public constructor(public userId: number, public username: string, public password: string, public cppFiles: CppFile[]) { }
}

export class LoginRequest {
  public constructor(public user: User, public token: string) { }
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(public http: HttpClient) { }
  public urlStart = environment.API_BASE_URL + "/api/user";
  public urlLogin: string = environment.API_BASE_URL + "/api/login";

  logInUser(userInfo: User): Observable<LoginRequest> {
    let requestBody = {
      username: userInfo.username,
      password: userInfo.password
    };

    return this.http.post<LoginRequest>(this.urlLogin, requestBody);
  }

  addUser(userInfo: User): Observable<LoginRequest> {
    let apiUrl: string = this.urlStart;

    return this.http.post<LoginRequest>(apiUrl, userInfo).pipe(
      map((response: LoginRequest) => {
        return response;
      }),
      catchError((error: any) => {
        return throwError(() => error);
      })
    );
  }

  getUser(userId: number): Observable<User> {
    let apiURl: string = this.urlStart + "/" + userId;
    console.log("Sending fetch at " + apiURl);

    return this.http.get<any>(apiURl).pipe(
      map((response: any) => {
        return new User(response.id, response.username, response.password, response.cpp_files);
      }),
      catchError((error) => {
        return new Observable<any>();
      })
    );
  }

  addCppFileToUser(userId: number, cppFile: CppFile): Observable<User> {
    let apiUrl: string = this.urlStart + "/" + userId + "/files";

    let requestBody = {
      filename: cppFile.filename,
      source_code: cppFile.source_code
    };

    return this.http.put<any>(apiUrl, requestBody).pipe(
      map((response: any) => {
        return new User(response.id, response.username, response.password, response.cpp_files);
      }),
      catchError((error: any) => {
        return new Observable<any>();
      })
    );
  }

  removeCppFileFromUser(userId: number, cppFileId: number): Observable<User> {
    let apiUrl: string = this.urlStart + "/" + userId + "/files/" + cppFileId;

    return this.http.delete<any>(apiUrl).pipe(
      map((response: any) => {
        return new User(response.id, response.username, response.password, response.cpp_files);
      }),
      catchError((error: any) => {
        return new Observable<any>();
      })
    );
  }
}
