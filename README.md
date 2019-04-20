# NovaTechBorrower
Repo for Borrower

## setup
1. If you do not have maven installed, you need to install it
2. Import this project as a maven project
3. Set up MySQL schema using the schema in src/test/resources/schema.sql
4. Create a file called `database-config.properties` in src/main/resources. Look at the code down below for an example. <br/>
```
spring.datasource.url = jdbc:mysql://hostname:port/data-base-name?useSSL=false&serverTimezone=UTC
spring.datasource.username = username
spring.datasource.password = password

spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto=update
```
