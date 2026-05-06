FROM maven:3.9-eclipse-temurin-17

RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    gnupg

# Install Chrome
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt install -y ./google-chrome-stable_current_amd64.deb

WORKDIR /tests

COPY . .

CMD ["mvn", "clean", "test"]
