FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Copy the package.json and package-lock.json
COPY ./web-app/package*.json ./

# Install the dependencies
RUN npm install

# Copy the rest of the files
COPY ./web-app/ .

# Build the application
RUN npm run build

# Expose the port
# EXPOSE 3000

# Start the application
# CMD ["npm", "start"]
