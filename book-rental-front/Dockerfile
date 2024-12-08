# The Node.js image for runtime
FROM node:21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install the dependencies for the Angular app
RUN npm install

# Copy the rest of the application files into the working directory
COPY . .

# Build the Angular app for production
RUN npm run build -- --output-path=dist/book-front --base-href /book-front/

# Get nginx for web server
FROM nginx:alpine

# Copy the built Angular files
COPY --from=build /app/dist/book-front/browser /usr/share/nginx/html/book-front

# Copy the custom Nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 to allow HTTP traffic to the Nginx server
EXPOSE 80

# Command to start Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]