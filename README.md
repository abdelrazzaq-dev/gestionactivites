# Gestion des Activités Pédagogiques

Un système complet pour organiser, suivre et analyser les activités pédagogiques et la participation des étudiants.

## 📋 Fonctionnalités Implémentées

### ✅ Authentification et Sécurité
- [x] Connexion sécurisée avec BCrypt
- [x] Gestion des sessions utilisateur
- [x] Réinitialisation de mot de passe par email
- [x] Trois rôles d'utilisateur (Admin, Professeur, Étudiant)

### ✅ Gestion des Activités
- [x] Création, modification et suppression d'activités
- [x] Filtrage par statut et type
- [x] Recherche avancée par titre
- [x] Multiple types d'activités (Cours, Devoir, Projet, Quiz, etc.)
- [x] Suivi des échéances

### ✅ Gestion des Participations
- [x] Inscription des étudiants aux activités
- [x] Saisie des notes et feedbacks
- [x] Filtrage des participants par activité
- [x] Calcul automatique des taux de participation
- [x] Calcul des moyennes

### ✅ Statistiques et Rapports
- [x] Taux de participation global
- [x] Moyennes générales des notes
- [x] Graphiques (camembert et barres) avec JFreeChart
- [x] Répartition des activités par type et statut
- [x] Statistiques par activité

## 🗃️ Base de Données

### Tables Implémentées
- **users**: Gestion des utilisateurs (login, password hash, email, role, status)
- **students**: Informations des étudiants (matricule, nom, email, département, etc.)
- **activities**: Activités pédagogiques (titre, description, type, statut, professeur, échéance)
- **student_participations**: Participation des étudiants (étudiant, activité, note, feedback)

### Technologies
- **H2 Database**: Base de données embarquée pour le développement
- **Hibernate/JPA**: ORM pour la persistance des données
- **MySQL Support**: Configuration disponible pour la production

## 🚀 Installation et Exécution

### Prérequis
- Java 21 ou supérieur
- Maven 3.6+

### Compilation
```bash
mvn clean compile
```

### Exécution
```bash
mvn exec:java
```

Ou avec NetBeans:
- Clic droit sur le projet → Run

### Compte par défaut
- **Login**: admin
- **Password**: admin123
- **Role**: Administrateur

## 📁 Structure du Projet

```
src/main/java/com/fst/gestionactivites/
├── GestionActivites.java          # Point d'entrée
├── model/                          # Entités JPA
│   ├── User.java
│   ├── Student.java
│   ├── Activity.java
│   └── StudentParticipation.java
├── repository/                     # Repositories (DAO)
│   ├── UserRepository.java
│   ├── StudentRepository.java
│   ├── ActivityRepository.java
│   └── ParticipationRepository.java
├── service/                        # Services métier
│   ├── AuthenticationService.java
│   └── EmailService.java
├── data/                           # Gestion des données
│   ├── DatabaseManager.java
│   └── DataManager.java
└── gui/                            # Interface utilisateur
    ├── LoginDialog.java
    ├── MainFrame.java
    └── pannels/
        ├── ActivitiesPanel.java
        ├── ActivityDialog.java
        ├── ParticipationPanel.java
        ├── ParticipationDialog.java
        └── StatisticsPanel.java
```

## 🔧 Configuration

### Base de Données H2
La base de données H2 est stockée dans votre répertoire home:
```
~/gestionactivites.mv.db
```

### Configuration MySQL (Production)
Pour utiliser MySQL en production, modifiez `persistence.xml`:
```xml
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:mysql://localhost:3306/gestionactivites"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="password"/>
```

### Logs
Les logs sont sauvegardés dans:
```
logs/gestionactivites.log
```

## 📊 Utilisation

### 1. Connexion
Au lancement, une fenêtre de connexion s'affiche. Utilisez le compte admin par défaut.

### 2. Gestion des Activités
- Onglet "Activités" pour créer, modifier, supprimer des activités
- Utilisez les filtres pour rechercher des activités spécifiques
- Double-cliquez pour voir les détails

### 3. Gestion des Participations
- Onglet "Participations" pour gérer la participation des étudiants
- Sélectionnez une activité pour voir tous les participants
- Modifiez les notes et feedbacks

### 4. Statistiques
- Onglet "Statistiques" pour visualiser les graphiques et rapports
- Taux de participation par activité
- Répartition des activités par type

## 🔐 Sécurité

- Mots de passe hashés avec BCrypt (12 rounds)
- Validation des entrées utilisateur
- Gestion des sessions
- Support de la réinitialisation de mot de passe

## 📧 Email (Configuration requise)

Pour activer l'envoi d'emails pour la réinitialisation de mot de passe:
1. Éditez `EmailService.java`
2. Configurez les paramètres SMTP
3. Décommentez le code d'envoi réel

## 🎨 Interface

- **Look and Feel**: FlatLaf (moderne et professionnel)
- **Couleurs**: Interface épurée avec thème clair
- **Graphiques**: JFreeChart pour les visualisations
- **Responsive**: Interface adaptable

## 📝 Données de Test

Le système génère automatiquement:
- 15 étudiants fictifs
- 4 activités exemples
- Données de participation pour démonstration

## 🛠️ Technologies Utilisées

- **Java 21**: Langage de programmation
- **Swing**: Interface graphique
- **Hibernate 6.4**: ORM
- **H2 Database**: Base de données embarquée
- **JFreeChart**: Graphiques et statistiques
- **BCrypt**: Hashage de mots de passe
- **SLF4J + Logback**: Logging
- **Maven**: Gestion de dépendances
- **FlatLaf**: Look and Feel moderne

## 📄 Licence

Projet académique - FST

## 👥 Auteur

Développé pour la gestion des activités pédagogiques à la FST
