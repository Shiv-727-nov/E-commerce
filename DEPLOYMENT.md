# Deployment Guide

This guide provides step-by-step instructions for deploying the ShoeStore e-commerce platform.

## Prerequisites

- Docker and Docker Compose installed
- MySQL database (if not using Docker)
- Java 17+ (for local backend deployment)
- Node.js 18+ (for local frontend deployment)
- Razorpay account and API keys

## Quick Start with Docker

### 1. Clone the Repository
```bash
git clone <repository-url>
cd shoestore
```

### 2. Configure Environment Variables

Create a `.env` file in the root directory:
```bash
# Database Configuration
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_DATABASE=shoestore
MYSQL_USER=shoestore
MYSQL_PASSWORD=your_secure_password

# Razorpay Configuration
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret

# JWT Configuration
JWT_SECRET=your_very_long_and_secure_jwt_secret_key
```

### 3. Deploy with Docker Compose
```bash
docker-compose up -d
```

This will start:
- MySQL database on port 3306
- Spring Boot backend on port 8080
- React frontend on port 3000

### 4. Access the Application
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Admin Login: username: `admin`, password: `admin123`

## Production Deployment

### Option 1: Cloud Platform Deployment

#### AWS Deployment

1. **Set up RDS MySQL Database**:
   ```bash
   # Create RDS instance
   aws rds create-db-instance \
     --db-instance-identifier shoestore-db \
     --db-instance-class db.t3.micro \
     --engine mysql \
     --master-username admin \
     --master-user-password your_secure_password \
     --allocated-storage 20
   ```

2. **Deploy Backend to Elastic Beanstalk**:
   ```bash
   # Build the JAR file
   cd backend
   mvn clean package
   
   # Deploy to Elastic Beanstalk
   eb init
   eb create shoestore-backend
   eb deploy
   ```

3. **Deploy Frontend to S3 + CloudFront**:
   ```bash
   # Build the frontend
   cd frontend
   npm run build
   
   # Upload to S3
   aws s3 sync build/ s3://your-bucket-name
   
   # Create CloudFront distribution
   aws cloudfront create-distribution --distribution-config file://cloudfront-config.json
   ```

#### Google Cloud Platform Deployment

1. **Set up Cloud SQL**:
   ```bash
   gcloud sql instances create shoestore-db \
     --database-version=MYSQL_8_0 \
     --tier=db-f1-micro \
     --region=us-central1
   ```

2. **Deploy Backend to Cloud Run**:
   ```bash
   # Build and push Docker image
   docker build -t gcr.io/your-project/shoestore-backend ./backend
   docker push gcr.io/your-project/shoestore-backend
   
   # Deploy to Cloud Run
   gcloud run deploy shoestore-backend \
     --image gcr.io/your-project/shoestore-backend \
     --platform managed \
     --region us-central1
   ```

3. **Deploy Frontend to Firebase Hosting**:
   ```bash
   # Install Firebase CLI
   npm install -g firebase-tools
   
   # Build and deploy
   cd frontend
   npm run build
   firebase deploy
   ```

### Option 2: VPS Deployment

#### Ubuntu Server Setup

1. **Install Dependencies**:
   ```bash
   # Update system
   sudo apt update && sudo apt upgrade -y
   
   # Install Java 17
   sudo apt install openjdk-17-jdk -y
   
   # Install Node.js 18
   curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
   sudo apt-get install -y nodejs
   
   # Install MySQL
   sudo apt install mysql-server -y
   
   # Install Nginx
   sudo apt install nginx -y
   ```

2. **Set up MySQL Database**:
   ```bash
   sudo mysql_secure_installation
   
   # Create database and user
   sudo mysql -u root -p
   ```
   ```sql
   CREATE DATABASE shoestore;
   CREATE USER 'shoestore'@'localhost' IDENTIFIED BY 'your_secure_password';
   GRANT ALL PRIVILEGES ON shoestore.* TO 'shoestore'@'localhost';
   FLUSH PRIVILEGES;
   EXIT;
   ```

3. **Deploy Backend**:
   ```bash
   # Clone repository
   git clone <repository-url> /var/www/shoestore
   cd /var/www/shoestore/backend
   
   # Build application
   mvn clean package
   
   # Create systemd service
   sudo nano /etc/systemd/system/shoestore-backend.service
   ```
   
   Add the following content:
   ```ini
   [Unit]
   Description=ShoeStore Backend
   After=network.target
   
   [Service]
   Type=simple
   User=www-data
   WorkingDirectory=/var/www/shoestore/backend
   ExecStart=/usr/bin/java -jar target/shoestore-backend-0.0.1-SNAPSHOT.jar
   Restart=always
   
   [Install]
   WantedBy=multi-user.target
   ```
   
   ```bash
   # Start service
   sudo systemctl daemon-reload
   sudo systemctl enable shoestore-backend
   sudo systemctl start shoestore-backend
   ```

4. **Deploy Frontend**:
   ```bash
   cd /var/www/shoestore/frontend
   
   # Install dependencies and build
   npm install
   npm run build
   
   # Copy build files to Nginx directory
   sudo cp -r build/* /var/www/html/
   ```

5. **Configure Nginx**:
   ```bash
   sudo nano /etc/nginx/sites-available/shoestore
   ```
   
   Add the following configuration:
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       # Frontend
       location / {
           root /var/www/html;
           index index.html;
           try_files $uri $uri/ /index.html;
       }
       
       # Backend API
       location /api/ {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```
   
   ```bash
   # Enable site
   sudo ln -s /etc/nginx/sites-available/shoestore /etc/nginx/sites-enabled/
   sudo nginx -t
   sudo systemctl restart nginx
   ```

## Environment Configuration

### Backend Environment Variables

Create `application-prod.yml` in `backend/src/main/resources/`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-db-host:3306/shoestore?useSSL=true&serverTimezone=UTC
    username: ${DB_USERNAME:shoestore}
    password: ${DB_PASSWORD:your_secure_password}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

jwt:
  secret: ${JWT_SECRET:your_very_long_and_secure_jwt_secret_key}
  expiration: 86400000

razorpay:
  key-id: ${RAZORPAY_KEY_ID:your_razorpay_key_id}
  key-secret: ${RAZORPAY_KEY_SECRET:your_razorpay_key_secret}

cors:
  allowed-origins: ${FRONTEND_URL:https://your-domain.com}
```

### Frontend Environment Variables

Create `.env.production` in the frontend directory:

```bash
REACT_APP_API_URL=https://your-api-domain.com/api
REACT_APP_RAZORPAY_KEY_ID=your_razorpay_key_id
```

## SSL Certificate Setup

### Using Let's Encrypt (Certbot)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

## Monitoring and Logging

### Application Monitoring

1. **Set up log rotation**:
   ```bash
   sudo nano /etc/logrotate.d/shoestore
   ```
   
   Add:
   ```
   /var/www/shoestore/backend/logs/*.log {
       daily
       missingok
       rotate 52
       compress
       delaycompress
       notifempty
       create 644 www-data www-data
   }
   ```

2. **Monitor application health**:
   ```bash
   # Check backend status
   sudo systemctl status shoestore-backend
   
   # Check logs
   sudo journalctl -u shoestore-backend -f
   ```

### Database Monitoring

```bash
# Monitor MySQL performance
sudo mysql -u root -p
```

```sql
-- Check database size
SELECT 
    table_schema AS 'Database',
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables 
WHERE table_schema = 'shoestore'
GROUP BY table_schema;

-- Check slow queries
SHOW VARIABLES LIKE 'slow_query_log';
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

## Backup Strategy

### Database Backup

```bash
# Create backup script
sudo nano /usr/local/bin/backup-shoestore.sh
```

Add:
```bash
#!/bin/bash
BACKUP_DIR="/var/backups/shoestore"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="shoestore"
DB_USER="shoestore"
DB_PASS="your_secure_password"

mkdir -p $BACKUP_DIR
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/shoestore_$DATE.sql
gzip $BACKUP_DIR/shoestore_$DATE.sql

# Keep only last 7 days of backups
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
```

```bash
# Make executable and add to cron
sudo chmod +x /usr/local/bin/backup-shoestore.sh
sudo crontab -e
# Add: 0 2 * * * /usr/local/bin/backup-shoestore.sh
```

## Troubleshooting

### Common Issues

1. **Backend won't start**:
   ```bash
   # Check Java version
   java -version
   
   # Check port availability
   sudo netstat -tlnp | grep :8080
   
   # Check logs
   sudo journalctl -u shoestore-backend -n 50
   ```

2. **Database connection issues**:
   ```bash
   # Test MySQL connection
   mysql -u shoestore -p -h localhost shoestore
   
   # Check MySQL status
   sudo systemctl status mysql
   ```

3. **Frontend build issues**:
   ```bash
   # Clear npm cache
   npm cache clean --force
   
   # Delete node_modules and reinstall
   rm -rf node_modules package-lock.json
   npm install
   ```

### Performance Optimization

1. **Enable MySQL query cache**:
   ```sql
   SET GLOBAL query_cache_size = 268435456;
   SET GLOBAL query_cache_type = ON;
   ```

2. **Optimize Nginx**:
   ```nginx
   # Add to nginx.conf
   gzip on;
   gzip_vary on;
   gzip_min_length 1024;
   gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
   ```

3. **Enable Redis for session storage** (optional):
   ```bash
   sudo apt install redis-server -y
   ```

## Security Checklist

- [ ] Change default passwords
- [ ] Enable SSL/TLS
- [ ] Configure firewall (UFW)
- [ ] Set up fail2ban
- [ ] Regular security updates
- [ ] Database access restrictions
- [ ] API rate limiting
- [ ] Input validation
- [ ] SQL injection prevention
- [ ] XSS protection

## Maintenance

### Regular Tasks

1. **Weekly**:
   - Check application logs
   - Monitor disk space
   - Review security updates

2. **Monthly**:
   - Update dependencies
   - Review performance metrics
   - Test backup restoration

3. **Quarterly**:
   - Security audit
   - Performance optimization
   - Disaster recovery testing

For additional support, refer to the main README.md file or create an issue in the repository.

