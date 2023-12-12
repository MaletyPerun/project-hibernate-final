FROM openjdk:18

COPY target/project-hibernate-final-1.0.jar /hibernate-final-1.0.jar

CMD ["java", "-jar", "/hibernate-final-1.0.jar"]

# Используем JDK для сборки
#FROM openjdk:18 as builder

# Копируем все исходные файлы из текущей директории в образ
#COPY . /usr/src/app

# Указываем рабочую директорию
#WORKDIR /usr/src/app

# Собираем JAR-файл
#RUN /bin/bash -c 'javac ru/company/*.java'  # компиляция
#RUN /bin/bash -c 'jar cfe app.jar ru.company.Main ru/company/*.class'  # создание JAR-файла с указанием главного класса

# Теперь создаем другой образ, где будем запускать наше приложение
#FROM openjdk:18
#COPY --from=builder /usr/src/app/app.jar /usr/app/app.jar
#WORKDIR /usr/app
#CMD ["java", "-jar", "app.jar"]