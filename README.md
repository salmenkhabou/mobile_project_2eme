# Health Tracker - Application Android de Suivi de Santé

## 📱 Description

Health Tracker est une application Android native développée en Java qui permet aux utilisateurs de suivre leurs habitudes de santé et de recevoir des recommandations personnalisées simples.

## 🚀 Fonctionnalités Principales

### 📊 Suivi des Données de Santé

- **Comptage de pas** : Intégration avec Google Fit API pour suivre l'activité physique
- **Suivi des calories** : Monitoring des calories brûlées et consommées
- **Suivi du sommeil** : Analyse des habitudes de sommeil
- **Scanner nutritionnel** : Scan des codes-barres avec Open Food Facts API

### 📈 Visualisation et Analyses

- **Tableau de bord** : Vue d'ensemble quotidienne des données
- **Graphiques** : Évolution sur 7 jours avec MPAndroidChart
- **Calendrier d'activités** : Historique des activités par date
- **Détails par métrique** : Analyse approfondie de chaque type de données

### 🎯 Recommandations Personnalisées

- **Conseils nutritionnels** : Basés sur les données alimentaires scannées
- **Objectifs personnalisés** : Calculs selon l'âge, poids et taille
- **Recommandations d'activité** : Suggestions d'exercice adaptées
- **Conseils de sommeil** : Amélioration des habitudes de repos

### 🔔 Notifications et Rappels

- **Rappels d'hydratation** : Toutes les 2 heures (8h-20h)
- **Rappels d'activité** : Encouragement à atteindre les objectifs
- **Notifications configurables** : Activation/désactivation dans les paramètres

## 🏗️ Architecture Technique

### Technologies Utilisées

- **Android Native** : Java
- **SDK minimum** : Android 8.0 (API 26)
- **Bibliothèques principales** :
  - Google Fit API (données fitness)
  - Open Food Facts API (données nutritionnelles)
  - Retrofit (requêtes HTTP)
  - MPAndroidChart (graphiques)
  - ZXing (scanner code-barres)
  - Firebase (backend optionnel)

### Structure du Projet

```
com.example.projet_android/
├── activities/
│   ├── MainActivity.java           # Écran d'accueil
│   ├── MainActivity2.java          # Dashboard principal
│   ├── DetailActivity.java         # Détails des métriques
│   ├── CalendarActivity.java       # Calendrier d'activités
│   ├── NutritionActivity.java      # Scanner nutritionnel
│   └── SettingsActivity.java       # Paramètres
├── models/
│   ├── HealthData.java            # Modèle des données de santé
│   └── FoodItem.java              # Modèle des aliments
├── services/
│   ├── GoogleFitManager.java       # Gestion Google Fit API
│   ├── NutritionManager.java       # Gestion Open Food Facts API
│   ├── NotificationHelper.java     # Gestion des notifications
│   └── *ReminderReceiver.java     # Récepteurs de rappels
├── utils/
│   └── PreferencesManager.java     # Gestion des préférences utilisateur
└── adapters/
    └── ActivitiesAdapter.java      # Adaptateur RecyclerView
```

## 📋 Prérequis

### Développement

- **Android Studio** : 4.0+
- **JDK** : 17+
- **Android SDK** : API 26+
- **Gradle** : 8.0+

### Permissions Requises

- Internet (APIs)
- Localisation (Google Fit)
- Capteurs corporels (Google Fit)
- Reconnaissance d'activité (Google Fit)
- Caméra (scanner code-barres)
- Notifications
- Alarmes (rappels programmés)

## 🛠️ Installation et Configuration

### 1. Cloner et Ouvrir le Projet

```bash
git clone <repository-url>
cd projet_android
```

### 2. Configuration Google Fit

1. Créer un projet dans [Google Cloud Console](https://console.cloud.google.com/)
2. Activer l'API Google Fit
3. Configurer OAuth 2.0 pour Android
4. Ajouter la clé API dans le projet

### 3. Compilation

```bash
./gradlew assembleDebug
```

### 4. Installation sur Appareil

```bash
./gradlew installDebug
```

## 🎮 Utilisation

### Premier Lancement

1. **Écran d'accueil** : Introduction à l'application
2. **Configuration initiale** : Aller dans Paramètres pour configurer le profil
3. **Permissions** : Autoriser l'accès aux données de santé

### Navigation Principale

- **Dashboard** : Vue d'ensemble des données du jour
- **Scanner** : Scanner les aliments pour le suivi nutritionnel
- **Calendrier** : Consulter l'historique des activités
- **Détails** : Analyse approfondie de chaque métrique
- **Paramètres** : Configuration du profil et des objectifs

### Configuration des Objectifs

1. Aller dans **Paramètres**
2. Renseigner **Profil utilisateur** (âge, poids, taille)
3. Définir les **Objectifs quotidiens**
4. Configurer les **Notifications**
5. Consulter les **Recommandations personnalisées**

## 🔧 Personnalisation

### Ajout de Nouvelles Métriques

1. Étendre le modèle `HealthData`
2. Mettre à jour `GoogleFitManager`
3. Modifier les layouts du dashboard
4. Ajouter la logique dans `MainActivity2`

### Intégration d'Autres APIs

1. Créer une nouvelle classe Service
2. Ajouter les permissions nécessaires
3. Intégrer dans les activités concernées

### Personnalisation de l'Interface

- Modifier les layouts XML dans `res/layout/`
- Personnaliser les couleurs dans `res/values/colors.xml`
- Adapter les strings dans `res/values/strings.xml`

## 📊 APIs Intégrées

### Google Fit API

- Récupération des pas quotidiens
- Calories brûlées
- Données de sommeil (si disponibles)
- Fréquence cardiaque

### Open Food Facts API

```
Base URL: https://world.openfoodfacts.org/
Endpoint: /api/v0/product/{barcode}.json
```

## 🔔 Système de Notifications

### Types de Rappels

- **Hydratation** : Toutes les 2h (8h-20h)
- **Activité** : 18h quotidien
- **Repas** : Configurable
- **Sommeil** : Configurable

### Configuration

Les notifications sont gérées via `NotificationHelper` et peuvent être activées/désactivées dans les paramètres.

## 📈 Recommandations Implémentées

### Nutritionnelles

- Analyse des macronutriments
- Suggestions d'amélioration
- Alertes sur les excès

### Activité Physique

- Objectifs personnalisés selon l'âge
- Encouragement progressif
- Suivi de progression

### Calculs Personnalisés

- **IMC** : Poids / Taille²
- **Calories recommandées** : Formule de Harris-Benedict
- **Objectif de pas** : Adapté selon l'âge

## 🚀 Évolutions Futures

### Fonctionnalités Avancées

- [ ] Intégration Apple Health (version iOS)
- [ ] Synchronisation multi-appareils
- [ ] Rapports hebdomadaires/mensuels
- [ ] Partage social des progrès
- [ ] Défis et gamification

### Améliorations Techniques

- [ ] Migration vers Kotlin
- [ ] Architecture MVVM/MVP
- [ ] Tests unitaires et d'intégration
- [ ] CI/CD pipeline
- [ ] Analyse de performance

## 📝 Notes de Développement

### Gestion des Erreurs

- Fallback sur des données simulées si Google Fit n'est pas disponible
- Gestion des erreurs réseau pour les APIs
- Validation des inputs utilisateur

### Performance

- Mise en cache des données
- Chargement asynchrone
- Optimisation des requêtes API

### Sécurité

- Stockage sécurisé des préférences
- Validation des données d'entrée
- Gestion des permissions runtime

## 🤝 Contribution

Pour contribuer au projet :

1. Fork le repository
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Support

Pour toute question ou problème :

- Ouvrir une issue sur GitHub
- Consulter la documentation des APIs utilisées
- Vérifier les prérequis système

---

**Health Tracker** - Votre compagnon quotidien pour une vie plus saine ! 💪🏃‍♀️🥗
