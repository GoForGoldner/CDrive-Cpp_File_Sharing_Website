import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { NGX_MONACO_EDITOR_CONFIG } from 'ngx-monaco-editor-v2';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { UserService } from './services/user.service';
import { CppFileService } from './services/cpp-file.service';
import { AuthInterceptor } from './services/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
  provideRouter(routes),
  provideClientHydration(withEventReplay()),
  provideHttpClient(withFetch(), withInterceptorsFromDi()),
    UserService,
    CppFileService,
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  {
    provide: NGX_MONACO_EDITOR_CONFIG,
    useValue: {
      baseUrl: 'assets/monaco',
      defaultOptions: { scrollBeyondLastLine: false }
    }
  }]
};
