# Quick Setup Guide - Supabase Configuration

## Step 1: Create Supabase Account & Project

1. Go to https://supabase.com and sign up/login
2. Click "New Project"
3. Fill in:
   - **Name**: airline-booking-legacy
   - **Database Password**: Choose a strong password (save it!)
   - **Region**: Choose closest to you
   - **Plan**: Free tier is sufficient
4. Click "Create new project" (wait 2-3 minutes for setup)

## Step 2: Get Your Database Connection Details

### Method A: Using Connection String (Recommended)

1. In Supabase Dashboard → **Settings** → **Database**
2. Scroll to "Connection string" section
3. Select **URI** tab
4. Copy the connection string (looks like):
   ```
   postgresql://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres
   ```

### Method B: Manual Configuration

1. In Supabase Dashboard → **Settings** → **Database**
2. Copy these values:
   - **Host**: `db.xxxxx.supabase.co` (where xxxxx is your project ref)
   - **Database name**: `postgres`
   - **Port**: `5432`
   - **User**: `postgres`
   - **Password**: The password you set when creating the project

## Step 3: Update application.properties

Open: `src/main/resources/application.properties`

### Option A: Using Connection String (Simpler)

Replace the placeholder URL:

```properties
spring.datasource.url=jdbc:postgresql://db.YOUR-PROJECT-REF.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=YOUR-DATABASE-PASSWORD
```

**Example (with fake credentials):**

```properties
spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=MyStr0ngP@ssw0rd!
```

## Step 4: Enable Direct Database Access (Important!)

By default, Supabase may block direct connections. Enable it:

1. Go to **Settings** → **Database**
2. Scroll to **Connection Pooling**
3. Under "Direct Connection", ensure it's enabled
4. Note: You may need to add your IP to allowlist (if using IPv4 restrictions)

### Add IP Address (if needed):

1. Go to **Settings** → **Database** → **Connection Pooling**
2. Click "Add new IP address"
3. Add `0.0.0.0/0` (allow all - for development only!)
4. Or add your specific public IP address

## Step 5: Verify Configuration

### Check your values match this format:

```properties
spring.datasource.url=jdbc:postgresql://[HOST]:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=[YOUR-PASSWORD]
```

### Common Mistakes:

- ❌ Missing `jdbc:` prefix → ✅ `jdbc:postgresql://...`
- ❌ Wrong port (6543 vs 5432) → ✅ Use port `5432`
- ❌ Password with special characters not escaped → ✅ Use plain password in properties
- ❌ Using "Connection String" port 6543 → ✅ Use "Direct Connection" port 5432

## Step 6: Build and Run

### Using Maven:

```bash
cd springboot
mvn clean install
mvn spring-boot:run
```

### Using VS Code:

1. Open `SpringbootApplication.java`
2. Click "Run" above the main method
3. Or press F5

## Step 7: Verify Success

You should see in console:

```
========================================
🚀 Airline Booking Legacy System Starting...
========================================
📝 Seeding Customer data...
✅ Created 4 customers
...
✨ Legacy Profile Module Ready!
```

## Troubleshooting

### Error: "Connection refused"

**Solution**:

- Check if Supabase project is active (not paused)
- Verify host URL is correct
- Ensure port is 5432 (not 6543)

### Error: "password authentication failed"

**Solution**:

- Double-check password in Supabase Dashboard → Settings → Database
- Reset database password if needed
- Ensure no extra spaces in password

### Error: "SSL connection required"

**Solution**: Add to `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
```

### Error: "Connection timeout"

**Solution**:

- Check your internet connection
- Try adding your IP to Supabase allowlist
- Temporarily allow all IPs: 0.0.0.0/0

### Error: "Database does not exist"

**Solution**:

- Use `postgres` as database name (not your project name)
- Correct format: `.../postgres` at the end of URL

## Alternative: Use Local PostgreSQL (for testing)

If Supabase connection fails, use local PostgreSQL:

1. Install PostgreSQL locally
2. Create database: `airline_booking`
3. Update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/airline_booking
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Test API After Startup

### Test with cURL:

```bash
# Login with sample customer
curl -X POST http://localhost:8081/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"icNumber":"040225-14-1143","password":"12345678"}'
```

### Test with Browser:

- http://localhost:8081/api/customers
- http://localhost:8081/api/staff
- http://localhost:8081/api/passengers

## Need Help?

1. Check Supabase status: https://status.supabase.com
2. Review Supabase logs: Dashboard → Logs
3. Check Spring Boot logs in console
4. Verify Java version: `java -version` (need Java 17+)

---

## Quick Reference Card

**Your Configuration Checklist:**

```
□ Supabase account created
□ Project created and active
□ Database password saved
□ Host URL copied from Settings → Database
□ application.properties updated with correct values
□ Port is 5432 (not 6543)
□ IP address added to allowlist (if needed)
□ Maven dependencies downloaded
□ Application runs without errors
□ Sample data loaded successfully
□ API endpoints responding
```

**Sample Login Credentials (pre-seeded):**

- Customer: IC `040225-14-1143`, Password `12345678`
- Staff: ID `S001`, Password `11111`

Happy coding! 🚀
