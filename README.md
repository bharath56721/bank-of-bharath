# Bank of Bharath – Banking Management System

A web-based Banking Management System developed using Java and Spring Boot. The application provides essential banking operations with secure user authentication and account management features.

## Features

- **User Registration:** Create a new bank account.
- **User Login:** Secure login functionality.
- **Check Balance:** View your available account balance.
- **Transfer Amount:** Transfer money between accounts.
- **Update Profile:** Update personal account details.
- **Change Password:** Change your account password.
- **Delete Account:** Delete your banking account.

## Technologies Used

- Java
- Spring Boot
- Spring Security
- MySQL
- Maven
- HTML, CSS, JavaScript

## Run Locally

**1. Clone the repository**

```bash
git clone https://github.com/bharath56721/bank-of-bharath.git
```

**2. Navigate to the project folder**

```bash
cd bank-of-bharath
```

**3. Run the application**

```bash
mvn spring-boot:run
```

**4. Open in your browser**

```text
http://localhost:8080
```

## Environment Variables for Deployment

Configure the following environment variables in your hosting platform:

| Variable | Description |
|---|---|
| `DB_URL` | MySQL database connection URL |
| `DB_USER` | Database username |
| `DB_PASS` | Database password |
| `JWT_SECRET` | Random secret string of 32 or more characters |

**DB_URL example:**

```text
jdbc:mysql://HOST:PORT/DBNAME?useSSL=true&serverTimezone=UTC
```

Do not upload actual database passwords or JWT secrets to GitHub.

## Deploy on Render

1. Push the project folder to a GitHub repository.
2. Open [Render](https://render.com/).
3. Select **New → Web Service**.
4. Connect your GitHub repository.
5. Select **Docker** as the runtime.
6. Add the four environment variables listed above.
7. Click **Deploy**.
8. After successful deployment, open your Render application URL.

## Project Structure

```text
bank-of-bharath/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │
│   └── test/
│
├── pom.xml
├── Dockerfile
└── README.md
```

## Author

**Bharath G P**

Computer Science Engineering Graduate

**Technical Skills:** Java, SQL, Python, Spring Boot, React

**GitHub:** https://github.com/bharath56721

**Email:** bharathgpcareer@gmail.com

---

*This project was developed for learning and demonstrating banking application development using Java and Spring Boot.*
