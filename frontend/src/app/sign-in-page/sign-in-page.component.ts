import { Component, ElementRef, ViewChild } from '@angular/core';
import {LoginBody, UserService} from '../services/user.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-sign-in-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './sign-in-page.component.html',
  styleUrl: './sign-in-page.component.scss',
  providers: [UserService, AuthService]
})
export class SignInPageComponent {
  @ViewChild("usernameTakenError") usernameTakenErrorModal!: ElementRef<HTMLDialogElement>;
  @ViewChild("usernameShortError") usernameShortErrorModal!: ElementRef<HTMLDialogElement>;
  @ViewChild("passwordShortError") passwordShortErrorModel!: ElementRef<HTMLDialogElement>;
  @ViewChild("loginError") loginErrorModel!: ElementRef<HTMLDialogElement>;

  public loginForm: FormGroup;

  public constructor(public userService: UserService,
    private formBuilder: FormBuilder) {

    // Creates the Form for the sign-in page
    this.loginForm = this.formBuilder.group({
      username: ['', [
        Validators.required,
        Validators.minLength(8)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8)
      ]]
    });
  }

  private addDialogListeners(): void {
    this.usernameTakenErrorModal.nativeElement.addEventListener("click", () => this.usernameTakenErrorModal.nativeElement.close());
    this.usernameShortErrorModal.nativeElement.addEventListener("click", () => this.usernameShortErrorModal.nativeElement.close());
    this.passwordShortErrorModel.nativeElement.addEventListener("click", () => this.passwordShortErrorModel.nativeElement.close());
    this.loginErrorModel.nativeElement.addEventListener("click", () => this.loginErrorModel.nativeElement.close());
  }

  public logIn() {
    const { username, password } = this.loginForm.value;

    console.log("Logging " + username + " with password " + password + " in...");

    this.addDialogListeners();

    this.userService.logInUser({username: username, password: password} as LoginBody);
  }

  public signIn() {
    const { username, password } = this.loginForm.value;

    console.log("Signing " + username + " in...");

    this.addDialogListeners();

    // If the username or password is invalid
    if (this.loginForm.get('username')?.invalid) {
      console.log("Username invalid. Showing username error page.");
      this.usernameShortErrorModal.nativeElement.showModal();
      return;
    } else if (this.loginForm.get('password')?.invalid) {
      console.log("Password invalid. Showing password error page.");
      this.passwordShortErrorModel.nativeElement.showModal();
      return;
    }

    this.userService.addUser({username: username, password: password} as LoginBody);
  }
}
