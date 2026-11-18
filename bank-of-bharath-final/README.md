Bank of Bharath

Features: register, login, check balance, transfer amount, update profile, change password, delete account.

Run locally
mvn spring-boot:run
Open http://localhost:8080

Environment variables for deployment
DB_URL   jdbc:mysql://HOST:PORT/DBNAME?useSSL=true&serverTimezone=UTC
DB_USER  database username
DB_PASS  database password
JWT_SECRET  random string of 32 or more characters

Deploy on Render
1. Push this folder to a GitHub repository.
2. Render: New, Web Service, connect the repository, Runtime Docker.
3. Add the four environment variables above and deploy.
