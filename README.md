## Local setup

### Database

Start the `MySQL80` Windows service, then run the schema and application-user scripts with a MySQL administrator account:

```powershell
$mysql = 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
Get-Content .\database\schema.sql | & $mysql -u root -p
Get-Content .\database\create-app-user.sql | & $mysql -u root -p
```

The application uses the restricted `gls_user` account rather than `root`.

### Start the backend

From `backend`, set the database credentials for the current PowerShell session and start Spring Boot:

```powershell
$env:DB_USERNAME = 'gls_user'
$env:DB_PASSWORD = 'gls_dev_password'
mvn spring-boot:run
```

The API is available at `http://localhost:8080`.

The Maven build copies the sibling `frontend` directory into the Spring Boot
JAR. This allows a deployed backend service to serve the website and `/api`
endpoints from the same origin.

### Start the frontend

From `frontend`, serve the static pages over HTTP:

```powershell
python -m http.server 8000
```

Open `http://localhost:8000/index.html` for the main site. Use the `Admin`
button in the top navigation to open the integrated admin login and dashboard.
The calculator and consultation form are available directly on the main page.

## Production deployment

The recommended deployment is one HTTPS Spring Boot service serving both the
frontend pages and the `/api` endpoints. This keeps the public site, admin
panel, session cookie, database API, and uploaded media on the same origin.

Set these environment variables in the hosting provider before starting the
application:

```text
DB_URL=jdbc:mysql://<database-host>:3306/gls_construction?useSSL=true&serverTimezone=UTC
DB_USERNAME=<restricted-database-user>
DB_PASSWORD=<database-password>
ADMIN_USERNAME=<admin-username>
ADMIN_PASSWORD=<strong-admin-password>
COOKIE_SECURE=true
UPLOAD_DIR=/persistent/uploads
```

`UPLOAD_DIR` must point to persistent storage. Without a persistent volume,
uploaded project images and videos can disappear when the hosting service
restarts or redeploys. If the frontend is hosted on a separate HTTPS origin,
set `FRONTEND_ORIGINS` to that exact origin and set the `gls-api-base` meta tag
in `index.html` to the backend `/api` URL; same-origin hosting is preferred.
