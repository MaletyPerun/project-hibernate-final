FROM eclipse-temurin:20

COPY target/test-1.0.jar /test-1.0.jar
#COPY target/dependency /dependency

#CMD ["java", "-jar", "/test-1.0.jar"]
#CMD ["java", "-cp", "/dependency/*:/test-1.0.jar", "ru.teplyakov.Main"]
ENTRYPOINT ["java", "-jar", "test-1.0.jar"]

#узнать
#Use 'docker scan' to run Snyk tests against images to find vulnerabilities and learn how to fix them

#v.7 (доделать)
#FROM maven:3.8.3-openjdk-11 AS build
#WORKDIR /app
#COPY pom.xml .
## Сначала копируем только файл pom.xml и выполняем загрузку зависимостей
#RUN mvn dependency:go-offline
#COPY src ./src
## Затем собираем проект
#RUN mvn package
#
## Используем образ OpenJDK для выполнения приложения
#FROM eclipse-temurin:20
#WORKDIR /app
## Копируем JAR файл из сборочного образа Maven
#COPY --from=build /target/test-1.0.jar ./test.jar
#CMD ["java", "-jar", "test.jar"]