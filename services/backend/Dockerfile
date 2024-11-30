FROM python:3.9-slim

# Set the working directory
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Install any needed packages specified in requirements.txt
RUN pip install -r ./src/requirements.txt

# Run app.py when the container launches
CMD ["python", "-m", "src.app"]