FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
RUN npx ngcc --properties es2023 browser module main --first-only --create-ivy-entry-points
COPY . .
RUN npm run build -- --configuration=development

FROM nginx:stable
COPY default.conf /etc/nginx/conf.d
COPY --from=build /app/dist/cdrive/browser /usr/share/nginx/html/
EXPOSE 80

#RUN npm install -g @angular/cli@latest
