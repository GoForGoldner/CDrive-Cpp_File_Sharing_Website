# Build stage
FROM node:18-alpine AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# Production stage
FROM nginx:alpine

# Remove default nginx static resources
RUN rm -rf /usr/share/nginx/html/*
# Copy Angular app files
COPY dist/cdrive/browser /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
