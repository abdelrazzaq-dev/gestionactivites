# Quick Start Guide - Gestion des Activités Pédagogiques

## 🚀 Running the Application

### Option 1: Using Maven
```bash
cd /home/abderrazzak/NetBeansProjects/gestionactivites
mvn clean compile exec:java
```

### Option 2: Using NetBeans
1. Open the project in NetBeans
2. Right-click on the project
3. Select "Run"

### Option 3: Package and Run JAR
```bash
mvn clean package
java -jar target/gestionactivites-1.0-SNAPSHOT.jar
```

## 🔑 Default Login

When the application starts, a login dialog will appear:

- **Username:** `admin`
- **Password:** `admin123`
- **Role:** Administrateur

## 📖 Using the Application

### 1. Login Screen
- Enter your credentials
- Click "Se connecter"
- Use "Mot de passe oublié?" to reset password if needed

### 2. Main Window (3 Tabs)

#### Tab 1: Activités
- **View all activities** in a table
- **Add new activity:** Click "Ajouter" button
- **Edit activity:** Select row and click "Modifier"
- **Delete activity:** Select row and click "Supprimer"
- **Filter:** Use dropdowns for Status and Type
- **Search:** Enter text and click "Rechercher"

#### Tab 2: Participations
- **Select an activity** from dropdown
- **View all students** and their participation status
- **Edit participation:** Select a student and click "Modifier Participation"
- **Update:**
  - Mark as participated (checkbox)
  - Enter score (0-100)
  - Add feedback/comments
- **Statistics** shown at top (participation rate & average score)

#### Tab 3: Statistiques
- **Summary statistics** showing:
  - Total number of activities
  - Overall participation rate
  - Overall average score
  - Breakdown by status and type
- **Pie Chart:** Activities by type
- **Bar Chart:** Participation rate per activity
- Click "Actualiser" to refresh

## 📝 Sample Data

The system creates sample data automatically:
- 15 students (Ahmed, Fatima, Mohamed, etc.)
- 4 activities:
  - Introduction aux Bases de Données (IN_PROGRESS)
  - Projet de Gestion d'Entreprise (IN_PROGRESS)
  - Quiz sur les Algorithmes (COMPLETED)
  - Travaux Pratiques - Programmation Python (PLANNED)

## 🔧 Common Tasks

### Creating a New Activity
1. Go to "Activités" tab
2. Click "Ajouter"
3. Fill in:
   - Titre (required)
   - Description
   - Type (Cours, Devoir, Projet, etc.)
   - Statut (Planifiée, En cours, etc.)
   - Professeur
   - Échéance (format: JJ/MM/AAAA, e.g., 31/01/2026)
4. Click "Enregistrer"

### Managing Participation
1. Go to "Participations" tab
2. Select an activity from dropdown
3. Select a student from the table
4. Click "Modifier Participation"
5. Update:
   - Check "L'étudiant a participé"
   - Enter score (e.g., 85.5)
   - Add comments
6. Click "Enregistrer"

### Viewing Statistics
1. Go to "Statistiques" tab
2. View summary and charts
3. Click "Actualiser" to refresh

## 💾 Database

- **Location:** `~/gestionactivites.mv.db`
- **Type:** H2 Embedded Database
- **Persistence:** All data is saved automatically
- **Backup:** Simply copy the `.mv.db` file

## 🐛 Troubleshooting

### "Database may be already in use" error
```bash
# Stop all running instances
pkill -f gestionactivites

# Remove lock file
rm -f ~/gestionactivites.mv.db
```

### Application won't start
```bash
# Rebuild from scratch
cd /home/abderrazzak/NetBeansProjects/gestionactivites
mvn clean compile
mvn exec:java
```

### Can't login with admin/admin123
The default admin is created on first startup. If it doesn't work:
1. Delete the database file: `rm ~/gestionactivites.mv.db`
2. Restart the application

## 📊 Features Overview

✅ **Complete CRUD operations** for activities
✅ **Full participation tracking** with scores and feedback
✅ **Advanced filtering and search**
✅ **Real-time statistics and charts**
✅ **Secure authentication** with BCrypt
✅ **Password reset** functionality
✅ **Modern UI** with FlatLaf
✅ **Database persistence** with Hibernate/JPA

## 🔒 Security Notes

- Passwords are hashed using BCrypt (12 rounds)
- Sessions are managed securely
- User roles control access
- Password reset tokens expire after 24 hours

## 📧 Email Configuration

Email functionality is in **development mode** (logs to console).

To enable real email sending:
1. Edit `src/main/java/.../service/EmailService.java`
2. Configure SMTP settings (lines 19-22)
3. Uncomment the actual sending code (lines 78-93)

## 🎯 Next Steps

1. Create additional users (students, professors)
2. Add real activities for your courses
3. Track student participation
4. Generate reports from statistics
5. Configure email for password reset

## 📚 Documentation

- Full README: `/home/abderrazzak/NetBeansProjects/gestionactivites/README.md`
- Logs: `logs/gestionactivites.log`
- Database config: `src/main/resources/META-INF/persistence.xml`

Enjoy using the Gestion des Activités Pédagogiques system! 🎓
