FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Tailwind CSS via le CLI standalone (pas de npm - cf. ADR-0006). Téléchargé avant
# COPY frontend ./frontend pour que ce layer reste en cache Docker tant que le
# binaire ne change pas, même si les sources CSS sont modifiées.
RUN curl -fsSL -o /usr/local/bin/tailwindcss \
        https://github.com/tailwindlabs/tailwindcss/releases/latest/download/tailwindcss-linux-x64 \
    && chmod +x /usr/local/bin/tailwindcss

COPY src ./src
COPY frontend ./frontend

# Compile les deux CSS avant le packaging Maven pour qu'ils soient inclus comme
# ressources statiques normales dans le jar.
RUN tailwindcss -i frontend/public.css -o src/main/resources/static/css/public.css --minify \
    && tailwindcss -i frontend/backoffice.css -o src/main/resources/static/css/backoffice.css --minify

RUN mvn -B package -DskipTests

FROM eclipse-temurin:25-jre

# "cwebp" (paquet Debian "webp") : conversion des images en WebP à l'upload et à la
# volée pour les visuels du seed (cf. CwebpConversionAdapter, ADR-0024) — binaire de
# référence plutôt qu'une dépendance Java native, même logique que le CLI Tailwind
# standalone (ADR-0006).
RUN apt-get update \
    && apt-get install -y --no-install-recommends webp \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/site-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
