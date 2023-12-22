FROM openjdk:18

#ENV HIBERNATE_CONNECTION_URL=jdbc:mysql://localhost:3306/world
#ENV HIBERNATE_CONNECTION_USERNAME=root
#ENV HIBERNATE_CONNECTION_PASSWORD=username


#COPY target/project-hibernate-final-1.0.jar /project-hibernate-final-1.0.jar
#COPY target/dependency /dependency
#COPY ./src/main/resources/hibernate.properties /app/hibernate.properties



#CMD ["java", "-jar", "/test-1.0.jar"]
#CMD ["java", "-cp", "/dependency/*:/project-hibernate-final-1.0.jar", "ru.teplyakov.Main"]


#FROM openjdk:11
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY src /Users/teplo/IdeaProjects/project-hibernate-final/src
COPY pom.xml /Users/teplo/IdeaProjects/project-hibernate-final
RUN mvn -f /Users/teplo/IdeaProjects/project-hibernate-final/pom.xml clean package -DskipTests

FROM openjdk:18


#COPY --from=build /Users/teplo/.m2/repository/com/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar /app/mysql-connector-java-8.0.33.jar
COPY --from=build /Users/teplo/IdeaProjects/project-hibernate-final/target/*.jar /app/project-hibernate-final-1.0.jar
#CMD ["java", "-jar", "/app/project-hibernate-final-1.0.jar"]

CMD ["java", "-cp", "/app/project-hibernate-final-1.0.jar:/app/mysql-connector-java-8.0.33.jar", "ru.teplyakov.Main"]


# Копирование hibernate.properties в контейнер
#COPY ./src/main/resources/hibernate.properties /app/hibernate.properties
#COPY target/dependency /dependency
#COPY ./target/project-hibernate-final-1.0.jar /app/project-hibernate-final-1.0.jar

# Запуск приложения
#CMD ["java", "-jar", "/app/project-hibernate-final-1.0.jar"]




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