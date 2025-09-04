# Build stage
FROM node:20 AS build
WORKDIR /app
# Copy package manifests and install dependencies
COPY frontend-web/package*.json ./
RUN npm install
# Copy application sources
COPY frontend-web/ .
RUN npm run build

# Nginx stage
FROM nginx:1.27-alpine
# Copy Nginx configuration from the frontend module
COPY frontend-web/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
