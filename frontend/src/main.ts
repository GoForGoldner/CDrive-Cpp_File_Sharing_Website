import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { NgxMonacoEditorConfig } from 'ngx-monaco-editor-v2';

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));

export const monacoConfig: NgxMonacoEditorConfig = {
  baseUrl: 'assets/monaco', // This should point to where you copied the assets
  defaultOptions: { scrollBeyondLastLine: false },
  onMonacoLoad: () => {
    console.log('Monaco Editor has been loaded');
  }
};
