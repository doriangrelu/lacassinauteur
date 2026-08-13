FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
COPY frontend ./frontend

# Tailwind CSS via le CLI standalone (pas de npm - cf. ADR-0006) : compile les deux
# CSS avant le packaging Maven pour qu'ils soient inclus comme ressources statiques
# normales dans le jar.
RUN curl -fsSL -o /usr/local/bin/tailwindcss \
        https://github.com/tailwindlabs/tailwindcss/releases/latest/download/tailwindcss-linux-x64 \
    && chmod +x /usr/local/bin/tailwindcss \
    && tailwindcss -i frontend/public.css -o src/main/resources/static/css/public.css --minify \
    && tailwindcss -i frontend/backoffice.css -o src/main/resources/static/css/backoffice.css --minify

RUN mvn -B package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/site-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
