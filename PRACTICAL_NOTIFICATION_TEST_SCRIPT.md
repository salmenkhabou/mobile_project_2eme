# 🧪 Script de Test Pratique - Notifications Android

## 🚀 Tests Rapides à Exécuter

### 1. **Vérification de l'État Actuel**

```powershell
# Vérifier que l'app est compilée
cd "C:\Android_Native\projet_android"
.\gradlew assembleDebug

# Installer l'app sur l'appareil connecté
.\gradlew installDebug
```

### 2. **Test Immédiat des Notifications**

#### A. Via Interface App (MÉTHODE RECOMMANDÉE)

1. Ouvrir l'app **Health Tracker**
2. Aller dans **Paramètres** (bouton ⚙️)
3. **Activer** "Notifications" si désactivé
4. **Activer** "Water Reminders" si désactivé
5. **Retourner** à l'écran principal
6. Les notifications sont maintenant programmées!

#### B. Forcer une Notification Test

```powershell
# Se connecter à l'appareil Android
adb devices

# Voir les logs de notifications en temps réel
adb logcat | Select-String "MainActivity2|HealthNotification|NotificationHelper"
```

### 3. **Déclencher Notifications Manuellement**

#### Test 1: Notification Matinale (8:00 AM)

```powershell
# Changer l'heure système à 8:00 AM
adb shell "su -c 'date 120108002025.00'"

# OU sans root (méthode alternative)
adb shell am broadcast -a android.intent.action.TIME_SET
```

#### Test 2: Notification d'Eau (9:00 AM)

```powershell
# Changer l'heure à 9:00 AM pour rappel d'eau
adb shell "su -c 'date 120109002025.00'"
```

#### Test 3: Notification du Soir (8:00 PM)

```powershell
# Changer l'heure à 8:00 PM pour bilan
adb shell "su -c 'date 120120002025.00'"
```

### 4. **Déclencher Notifications d'Objectifs**

#### Simuler Atteinte d'Objectif Pas

```powershell
# Ouvrir l'app et naviguer dans les données
# Ou via ADB broadcast personnalisé
adb shell am start -n com.example.projet_android/.MainActivity2
```

#### Dans l'App:

1. Utiliser les **boutons "Voir Pas/Calories/Sommeil"**
2. Les données GoogleFit se synchronisent
3. Si objectif atteint → notification automatique

## 📱 Comment Voir les Notifications

### **Méthode 1: Panel Notifications Android**

1. **Balayer vers le bas** depuis le haut de l'écran
2. Les notifications apparaissent dans la liste
3. **Appuyer sur une notification** → ouvre l'app
4. **Balayer la notification** → la supprimer

### **Méthode 2: Historique Notifications**

```
Paramètres Android > Apps > Health Tracker > Notifications > Historique des notifications
```

### **Méthode 3: Via ADB (Debug)**

```powershell
# Voir toutes les notifications actives
adb shell dumpsys notification | Select-String "projet_android"

# Voir les alarmes programmées
adb shell dumpsys alarm | Select-String "projet_android"

# Logs en temps réel
adb logcat -s "MainActivity2:*" "HealthNotificationManager:*"
```

## 🎯 Scénarios de Test Complets

### **Scénario 1: Premier Démarrage**

```powershell
# 1. Installer l'app
.\gradlew installDebug

# 2. Ouvrir l'app
adb shell am start -n com.example.projet_android/.MainActivity2

# 3. Vérifier les logs
adb logcat | Select-String "Système de notifications quotidiennes activé"

# 4. Vérifier les canaux créés
adb shell dumpsys notification | Select-String "daily_reminders|health_tips|achievements|water_reminder"
```

### **Scénario 2: Test Cycle Complet 24h**

```powershell
# Matin (8:00)
adb shell "su -c 'date 120108002025.00'"
Start-Sleep -Seconds 5

# Milieu de journée (12:30)
adb shell "su -c 'date 120112302025.00'"
Start-Sleep -Seconds 5

# Rappel eau (15:00)
adb shell "su -c 'date 120115002025.00'"
Start-Sleep -Seconds 5

# Soir (20:00)
adb shell "su -c 'date 120120002025.00'"

# Voir le résultat
Write-Host "Vérifiez le panel notifications Android!"
```

### **Scénario 3: Test Objectifs**

```powershell
# Démarrer l'app et simuler activité
adb shell am start -n com.example.projet_android/.MainActivity2

Write-Host "1. Dans l'app, appuyez sur 'Voir Pas'"
Write-Host "2. Synchronisez avec Google Fit"
Write-Host "3. Si >10,000 pas → notification automatique!"
Write-Host "4. Même processus pour Calories (>2000) et Sommeil (>7h)"
```

## 🔍 Vérifications de Succès

### **✅ Checklist Après Tests**

- [ ] **4 canaux** visibles dans Paramètres Android
- [ ] **Notifications programmées** visible dans logs
- [ ] **Panel Android** affiche les notifications test
- [ ] **Appuyer notification** → ouvre l'app
- [ ] **Objectifs déclenchent** notifications
- [ ] **Settings app** contrôle le système

### **📊 Commandes de Vérification**

```powershell
# Vérifier canaux créés
adb shell dumpsys notification | Select-String "CHANNEL"

# Vérifier alarmes actives
adb shell dumpsys alarm | Select-String "projet_android" | Measure-Object

# Vérifier permissions
adb shell dumpsys package com.example.projet_android | Select-String "NOTIFICATION"

# Statut complet
adb shell "dumpsys notification | grep -A5 -B5 'projet_android'"
```

## 🐛 Résolution Problèmes Courants

### **Problème: Pas de Notifications**

```powershell
# Solution 1: Vérifier permissions
adb shell dumpsys notification | Select-String "projet_android"

# Solution 2: Réactiver dans l'app
# Ouvrir app → Settings → Toggle notifications OFF puis ON

# Solution 3: Clear data et redémarrer
adb shell pm clear com.example.projet_android
.\gradlew installDebug
```

### **Problème: Root Non Disponible pour 'date'**

```powershell
# Alternative sans root: Utiliser l'émulateur Android Studio
# Ou changer l'heure manuellement dans Paramètres Android

# Méthode alternative via broadcast
adb shell am broadcast -a com.example.projet_android.FORCE_NOTIFICATION_TEST
```

### **Problème: ADB Non Reconnu**

```powershell
# Vérifier ADB
adb version

# Redémarrer ADB si nécessaire
adb kill-server
adb start-server

# Vérifier appareil connecté
adb devices
```

## 📋 Résultats Attendus

### **Après Activation Réussie:**

1. **8:00 AM**: 🌅 "Bonjour ! Nouvelle journée, nouveaux objectifs"
2. **9:00-19:00**: 💧 "Temps de s'hydrater !" (toutes les 2h)
3. **12:30 PM**: 🚶 "Bougez un peu !"
4. **17:30 PM**: 🏃 "Temps de bouger !"
5. **20:00 PM**: 🌙 "Bilan de votre journée"

### **Objectifs Atteints:**

- **≥10,000 pas**: 🎉 "Félicitations ! Objectif de pas atteint !"
- **≥2,000 cal**: 🔥 "Excellent ! Objectif calories atteint !"
- **≥7h sommeil**: 😴 "Parfait ! Bonne nuit de sommeil !"

---

## 🏆 Script de Test Automatisé

```powershell
# Copier-coller ce script dans PowerShell pour test complet

Write-Host "🧪 DÉMARRAGE TEST NOTIFICATIONS ANDROID FITNESS APP" -ForegroundColor Green

# Étape 1: Build et Install
Write-Host "`n1️⃣ Building et installation..." -ForegroundColor Yellow
Set-Location "C:\Android_Native\projet_android"
.\gradlew clean assembleDebug installDebug

# Étape 2: Démarrage App
Write-Host "`n2️⃣ Démarrage de l'application..." -ForegroundColor Yellow
adb shell am start -n com.example.projet_android/.MainActivity2
Start-Sleep -Seconds 3

# Étape 3: Vérification Logs
Write-Host "`n3️⃣ Vérification logs (10 secondes)..." -ForegroundColor Yellow
$job = Start-Job -ScriptBlock { adb logcat | Select-String "notifications quotidiennes activé" }
Start-Sleep -Seconds 10
Stop-Job $job; Remove-Job $job

# Étape 4: Instructions utilisateur
Write-Host "`n4️⃣ ACTIONS MANUELLES REQUISES:" -ForegroundColor Cyan
Write-Host "   a) Dans l'app → Settings → Activer 'Notifications'"
Write-Host "   b) Activer 'Water Reminders'"
Write-Host "   c) Revenir à l'écran principal"
Write-Host "`n   Appuyez sur ENTRÉE quand terminé..."
Read-Host

# Étape 5: Vérification finale
Write-Host "`n5️⃣ Vérification des canaux créés..." -ForegroundColor Yellow
$channels = adb shell dumpsys notification | Select-String "daily_reminders|health_tips|achievements|water_reminder"
if ($channels) {
    Write-Host "✅ SUCCÈS: Canaux de notification détectés!" -ForegroundColor Green
} else {
    Write-Host "❌ ERREUR: Canaux non détectés" -ForegroundColor Red
}

Write-Host "`n🎉 TEST TERMINÉ! Balayez vers le bas sur Android pour voir les futures notifications!" -ForegroundColor Green
```

**Utilisez ce script pour tester rapidement le système complet!** 🚀
