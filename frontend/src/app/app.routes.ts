import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { SignInPageComponent } from './sign-in-page/sign-in-page.component';
import { UserPageComponent } from './user-page/user-page.component';
import { CodeEditorComponent } from './code-editor/code-editor.component';

export const routes: Routes = [
    {
        path: '',
        component: HomePageComponent,
        title: 'Home Page',
    },
    {
        path: 'sign-in',
        component: SignInPageComponent,
        title: 'Sign In Page',
    },
    {
        path: 'user/:userId',
        component: UserPageComponent,
        title: 'User Page',
    },
    {
        path: 'user/:userId/files/:fileId',
        component: CodeEditorComponent,
        title: 'Code Editor Page',
    }
];
