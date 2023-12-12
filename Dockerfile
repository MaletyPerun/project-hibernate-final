FROM openjdk:18

COPY target/project-hibernate-final-1.0.jar /hibernate-final-1.0.jar

CMD ["java", "-jar", "/hibernate-final-1.0.jar"]