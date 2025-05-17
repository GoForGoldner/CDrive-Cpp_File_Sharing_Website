import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'user/:userId',
    renderMode: RenderMode.Client
  },
  {
    path: 'user/:userId/files/:fileId',
    renderMode: RenderMode.Client
  }
];
