# 📱 Guide Complet des Notifications - Android Fitness App

## 🔍 Comment Voir les Notifications

### 1. **Via l'Interface Android**

#### Méthode 1: Panel de Notifications

1. **Balayer vers le bas** depuis le haut de l'écran Android
2. Les notifications apparaîtront dans le **panel de notifications**
3. **Appuyer** sur une notification pour ouvrir l'app

#### Méthode 2: Paramètres Android

```
Paramètres > Applications > Health Tracker > Notifications
```

- Voir les **4 canaux de notifications** configurés
- Activer/Désactiver par type de notification
- Configurer les sons et vibrations

### 2. **Via ADB et Logcat (Développeur)**

#### Voir les logs en temps réel:

```powershell
# Connecter l'appareil Android et activer les logs
adb logcat | findstr "MainActivity2"
adb logcat | findstr "HealthNotification"
adb logcat | findstr "NotificationHelper"
```

#### Forcer l'affichage des notifications pour test:

```powershell
# Changer l'heure système pour déclencher les notifications
adb shell su -c "date 120108002025.00"  # 8:00 AM (rappel matinal)
adb shell su -c "date 120109002025.00"  # 9:00 AM (rappel eau)
adb shell su -c "date 120120002025.00"  # 8:00 PM (bilan soirée)
```

### 3. **Via les Paramètres de l'App**

1. Ouvrir l'app **Health Tracker**
2. Aller dans **Paramètres** (⚙️)
3. Activer les options:
   - ✅ **"Notifications"**
   - ✅ **"Water Reminders"**
4. Les notifications se programmeront automatiquement

## 🎯 Types de Notifications Implémentées

### 📅 **Notifications Quotidiennes Automatiques**

| Heure      | Type        | Titre                                            | Description                           |
| ---------- | ----------- | ------------------------------------------------ | ------------------------------------- |
| 8:00 AM    | 🌅 Matinal  | "Bonjour ! Nouvelle journée, nouveaux objectifs" | Message motivationnel + conseil santé |
| 9:00-19:00 | 💧 Eau      | "Temps de s'hydrater !"                          | Rappel toutes les 2h pour boire       |
| 12:30 PM   | 🚶 Activité | "Bougez un peu !"                                | Rappel d'activité midi                |
| 17:30 PM   | 🏃 Activité | "Temps de bouger !"                              | Rappel d'activité soir                |
| 20:00 PM   | 🌙 Bilan    | "Bilan de votre journée"                         | Résumé progrès + conseil détente      |

### 🏆 **Notifications d'Objectifs** (Déclenchées par atteinte)

| Objectif        | Seuil      | Notification                                   |
| --------------- | ---------- | ---------------------------------------------- |
| 👟 **Pas**      | 10,000 pas | "🎉 Félicitations ! Objectif de pas atteint !" |
| 🔥 **Calories** | 2,000 cal  | "🔥 Excellent ! Objectif calories atteint !"   |
| 😴 **Sommeil**  | 7-9 heures | "😴 Parfait ! Bonne nuit de sommeil !"         |

## 🔧 Architecture Technique

### **Components Principaux**

```
📁 services/
├── 🎯 NotificationHelper.java           # Notifications de base
├── 🏥 HealthNotificationManager.java    # Gestionnaire principal
├── 📢 HealthNotificationService.java    # Service objectifs
├── 💧 WaterReminderReceiver.java       # Récepteur eau
├── 🚶 StepsReminderReceiver.java       # Récepteur pas
└── 📅 DailyHealthReminderReceiver.java # Récepteur quotidien
```

### **Canaux de Notifications Android**

1. `daily_reminders` - Rappels quotidiens
2. `health_tips` - Conseils santé
3. `achievements` - Accomplissements
4. `water_reminder` - Rappels hydratation

## 🔥 Firebase Integration

### **Services Firebase Utilisés**

#### 📊 **Firebase Realtime Database**

```gradle
implementation("com.google.firebase:firebase-database:20.3.0")
```

- **Usage**: Stockage cloud des données de santé
- **Sync**: Synchronisation multi-appareils
- **Backup**: Sauvegarde automatique

#### 🔐 **Firebase Authentication**

```gradle
implementation("com.google.firebase:firebase-auth:22.3.0")
```

- **Usage**: Authentification utilisateurs
- **Sécurité**: Connexion sécurisée
- **Multi-plateforme**: Même compte sur tous appareils

#### 📱 **Firebase Cloud Messaging (FCM)**

```gradle
implementation("com.google.firebase:firebase-messaging:23.4.0")
```

- **Usage**: Notifications push depuis serveur
- **Remote**: Notifications à distance
- **Ciblage**: Notifications personnalisées

### **Comment Firebase Fonctionne**

#### 1. **Configuration Automatique**

- Les services Firebase s'initialisent au démarrage
- Pas de `google-services.json` visible = config par code
- Authentication et Database prêts à l'usage

#### 2. **Notifications Locales vs Distantes**

```java
// 🏠 LOCALES (AlarmManager)
HealthNotificationManager.scheduleMorningReminder()
→ Programmées sur l'appareil
→ Fonctionnent hors-ligne

// ☁️ DISTANTES (Firebase FCM)
Firebase Cloud Messaging
→ Envoyées depuis serveur
→ Nécessitent internet
→ Notifications marketing/urgentes
```

#### 3. **Synchronisation des Données**

```java
// Sauvegarde automatique vers Firebase
DatabaseManager → Firebase Database
→ Données disponibles sur tous appareils
→ Restauration après réinstallation
```

## 🧪 Comment Tester les Notifications

### **Méthode 1: Test Immédiat**

```powershell
# Déclencher une notification test via ADB
adb shell am broadcast -a com.example.projet_android.TEST_NOTIFICATION
```

### **Méthode 2: Simulation Temporelle**

```powershell
# Avancer l'heure pour déclencher les rappels programmés
adb shell su -c "date MMJJhhmmAAAA.ss"
# Exemple: 1er décembre 2025, 8:00 AM
adb shell su -c "date 120108002025.00"
```

### **Méthode 3: Atteindre les Objectifs**

1. **Marcher 10,000+ pas** → Notification pas
2. **Brûler 2,000+ calories** → Notification calories
3. **Dormir 7+ heures** → Notification sommeil
4. **Utiliser l'app normalement** → Toutes notifications

### **Méthode 4: Via Interface**

1. Ouvrir **Settings** dans l'app
2. Toggle **"Notifications"** OFF puis ON
3. Les rappels se reprogramment automatiquement

## 📋 Vérifications de Fonctionnement

### ✅ **Checklist Notifications**

- [ ] Panel Android affiche les notifications
- [ ] 4 canaux créés dans Paramètres Android
- [ ] Rappels programmés visible dans logs
- [ ] Notifications s'ouvrent dans l'app
- [ ] Objectifs déclenchent notifications
- [ ] Settings app contrôle les notifications

### 🔍 **Debug Commands**

```powershell
# Voir les logs de notifications
adb logcat | findstr "Notification"

# Voir les alarmes programmées
adb shell dumpsys alarm | findstr "projet_android"

# Voir les permissions notifications
adb shell dumpsys notification | findstr "projet_android"
```

## 🚨 Résolution de Problèmes

### **Pas de Notifications**

1. ✅ Vérifier permissions dans Paramètres Android
2. ✅ "Ne pas déranger" désactivé
3. ✅ Notifications activées dans l'app
4. ✅ Redémarrer l'app

### **Notifications en Double**

1. ✅ Vérifier logs pour alarmes multiples
2. ✅ Réinstaller l'app si nécessaire
3. ✅ Clear data de l'app

### **Firebase Non Connecté**

1. ✅ Internet disponible
2. ✅ Compte Google connecté
3. ✅ Services Google Play à jour

---

## 📱 Résumé Rapide

**VOIR NOTIFICATIONS**: Balayer vers le bas depuis haut de l'écran
**TESTER**: Settings app → Toggle notifications OFF/ON  
**DEBUG**: `adb logcat | findstr "MainActivity2"`
**FIREBASE**: Auth + Database + Messaging configurés
**TYPES**: 5 automatiques + 3 objectifs = 8 types total

L'app a un système de notifications **complet et professionnel** 🏆
