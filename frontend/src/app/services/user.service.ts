import {computed, effect, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {finalize} from 'rxjs';
import {environment} from '../../environments/environment';
import {UserResponse} from '../json-responses/user-response';
import {LoginResponse} from '../json-responses/login-response';
import {CppFileResponse} from '../json-responses/cpp-file-response';
import {CodeEntryResponse} from '../json-responses/code-entry-response';
import {Router} from '@angular/router';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private urlStart = environment.API_BASE_URL + "/api/user";
  private urlLogin: string = environment.API_BASE_URL + "/api/login";

  // User properties
  public readonly id = signal(-1);
  public username = signal("");
  public password = signal("");
  public cppFiles = signal<CppFileResponse[]>([]);

  // Service properties
  public readonly loading = signal(false);
  public readonly loadingMessage = signal("");
  public readonly loggedIn = signal(false);

  public readonly user = computed(() => {
    return {
      id: this.id(),
      username: this.username(),
      password: this.password(),
      cpp_files: this.cppFiles()
    } as UserResponse;
  });

  private cppFilesToImport = signal<CppFileBody[]>([]);

  constructor(private http: HttpClient, private router: Router, private authService: AuthService) {
    effect(() => {
      if (this.cppFilesToImport().length > 0) {
        this.addFileFromQueue();
      }
    });
  }

  /**
   * Logs the user into the service.
   * @param requestBody - The username and password to verify
   */
  logInUser(requestBody: LoginBody): void {
    this.loading.set(true);
    this.loadingMessage.set("Logging in...");

    this.http.post<LoginResponse>(this.urlLogin, requestBody).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (response: LoginResponse) => {
        this.setUserData(response.user);
        this.authService.setToken(response.token);
        this.loggedIn.set(true);

        console.log("Response:");
        console.log(response);
        console.log("User:");
        console.log(this.user());
        console.log("CppFiles:");
        console.log(this.cppFiles());
        console.log(this.cppFiles().length + " -> cpp length");

        this.router.navigate(['/user/' + this.id()]);
      }
    });
  }

  /**
   * Adds a user into the service.
   */
  addUser(newUser: LoginBody): void {
    let apiUrl: string = this.urlStart;

    this.loading.set(true);
    this.loadingMessage.set("Creating user...");
    console.log("Creating user...");

    this.http.post<LoginResponse>(apiUrl, newUser).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: response => {
        this.setUserData(response.user);
        this.authService.setToken(response.token);

        console.log("Response:");
        console.log(response);
        console.log("User:");
        console.log(this.user);

        this.loggedIn.set(true);
        console.log("Attempting to navigate to user page...");
        console.log(this.user());
        this.router.navigate(['/user/' + this.id()]);
      }
    });
  }

  /**
   * Gets a user from the service.
   * @param userId - The id of the user in the database
   */
  getUser(userId: number): void {
    let apiURl: string = this.urlStart + "/" + userId;

    this.loading.set(true);
    this.loadingMessage.set("Getting user...");

    this.http.get<UserResponse>(apiURl).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: response => {
        this.setUserData(response);
        console.log("User received:");
        console.log(response);
      }
    });
  }

  /**
   * Adds a C++ file to the current user.
   * @param cppFile - A body containing the filename and the source_code of the file
   */
  addCppFilesToUser(cppFiles: CppFileBody[]): void {
    this.cppFilesToImport.update((currentFiles) => [...currentFiles, ...cppFiles]);
  }

  private addFileFromQueue() {
    let apiUrl: string = this.urlStart + "/" + this.id() + "/files";

    this.loading.set(true);

    const currentFile = this.cppFilesToImport().at(-1);
    if (!currentFile) return;

    console.log("Current file");
    console.log(currentFile);

    const cppFileJson = {
      filename: currentFile.filename,
      source_code: [{code: currentFile.code, date: new Date().toISOString().split('.')[0]} as CodeEntryResponse]
    };

    this.http.put<UserResponse>(apiUrl, cppFileJson).pipe(
      finalize(() => {
        this.loading.set(false);
        this.cppFilesToImport().pop();
        this.addFileFromQueue()
      })
    ).subscribe({
      next: response => this.setUserData(response)
    });
  }

  /**
   * Removes a C++ file from the current user.
   * @param cppFileId - The id of the C++ to remove
   */
  removeCppFileFromUser(cppFileId: number): void {
    let apiUrl: string = this.urlStart + "/" + this.id() + "/files/" + cppFileId;

    this.loading.set(true);
    this.loadingMessage.set("Removing file...");

    this.http.delete<UserResponse>(apiUrl).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: response => this.setUserData(response)
    });
  }

  private setUserData(userResponse: UserResponse) {
    this.id.set(userResponse.id);
    this.username.set(userResponse.username);
    this.password.set(userResponse.password);
    this.cppFiles.set(userResponse.cpp_files);
  }
}

export interface LoginBody {
  username: string;
  password: string;
}

export interface CppFileBody {
  filename: string;
  code: string;
}
