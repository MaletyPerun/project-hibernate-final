## Study project with Hibernate and Docker.

### Overview
#### Simple application to setup relation with JPA/HQL and DB on select-query without problem N+1.
Also testing time on queries between MySQL and Redis.

###
#### MySQL and Redis start via Docker container.
In DB using tables Country, City and Languages.

### 
#### Technology stack:

- Java 18
- Maven 4.0.0
- Hibernate 5.6.15.Final
- MySQL-connector 8.0.33
- p6spy 3.9.1
- Junit 5.9.2
- Log4j 2.20.0


### Quick start
#### To start the application need:

1. download project
```shell
git clone https://github.com/MaletyPerun/project-hibernate-final.git
```
2. start docker container via console in package of project ".../project-hibernate-final"
```shell
docker-compose up
```
— download and start containers on images of MySQL and Redis.

3. make dump DB from package:
`
..src/main/resourses/dump-hibernate-final.sql
`

4. build application via console in package of project "../project-hibernate-final"

```shell
mvn clean install
```

#### After that Maven builds the application with test a time between queries of MySQL and Redis.

Using:
SessionFactory, JPA-annotation, HQL, Logging, docker-compose