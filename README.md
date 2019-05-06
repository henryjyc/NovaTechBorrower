# NovaTechBorrower
Repo for Borrower

## setup
1. If you do not have maven installed, you need to install it
2. Import this project as a maven project
3. Set up MySQL schema using the schema in src/test/resources/schema.sql
4. Change root and password in src/main/resources/database-config.properties
        to access your MySQL server with the library schema

## Changes (may need to add)
- When you change the database-config.properties, git maybe tracking it,
so you may need to do this
`git update-index --assume-unchanged src/main/resources/database-config.properties`
which should prevent git from tracking your user and password change.
