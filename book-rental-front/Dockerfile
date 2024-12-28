# The Node.js image for runtime
FROM node:18 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install the dependencies for the Angular app
RUN npm install -g npm@10

# Remove existing dependencies and reinstall fresh ones
RUN rm -rf node_modules package-lock.json && npm install

# Copy the rest of the application files into the working directory
COPY . .

# Build the Angular app for production
RUN npm rebuild && npm cache clean --force && npm run build -- --configuration production

# Get nginx for web server
FROM nginx:alpine

# Copy the built Angular files
COPY --from=build /app/dist/book-rental-front /usr/share/nginx/html

# Copy the custom Nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Command to start Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]