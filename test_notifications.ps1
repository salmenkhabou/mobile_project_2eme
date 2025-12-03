# 🧪 Script de Test Automatisé - Notifications Android Fitness App
# Exécuter avec: .\test_notifications.ps1

param(
    [switch]$QuickTest,
    [switch]$FullTest,
    [switch]$DebugLogs
)

Write-Host "🏥 ANDROID FITNESS APP - TEST NOTIFICATIONS" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green

# Configuration
$AppPackage = "com.example.projet_android"
$MainActivity = "$AppPackage/.MainActivity2"
$ProjectPath = "C:\Android_Native\projet_android"

function Test-AdbConnection {
    Write-Host "`n📱 Vérification connexion ADB..." -ForegroundColor Yellow
    $devices = adb devices
    if ($devices -match "device$") {
        Write-Host "✅ Appareil Android connecté" -ForegroundColor Green
        return $true
    } else {
        Write-Host "❌ Aucun appareil Android détecté!" -ForegroundColor Red
        Write-Host "   Connectez votre appareil et activez le débogage USB" -ForegroundColor Yellow
        return $false
    }
}

function Build-And-Install {
    Write-Host "`n🔨 Build et installation de l'app..." -ForegroundColor Yellow
    
    Set-Location $ProjectPath
    
    # Clean build
    Write-Host "   Nettoyage..." -NoNewline
    $result = .\gradlew clean 2>$null
    if ($LASTEXITCODE -eq 0) { Write-Host " ✅" -ForegroundColor Green } else { Write-Host " ❌" -ForegroundColor Red }
    
    # Build debug
    Write-Host "   Compilation..." -NoNewline  
    $result = .\gradlew assembleDebug 2>$null
    if ($LASTEXITCODE -eq 0) { Write-Host " ✅" -ForegroundColor Green } else { Write-Host " ❌" -ForegroundColor Red }
    
    # Install
    Write-Host "   Installation..." -NoNewline
    $result = .\gradlew installDebug 2>$null  
    if ($LASTEXITCODE -eq 0) { Write-Host " ✅" -ForegroundColor Green } else { Write-Host " ❌" -ForegroundColor Red }
}

function Start-App {
    Write-Host "`n🚀 Démarrage de l'application..." -ForegroundColor Yellow
    adb shell am start -n $MainActivity
    Start-Sleep -Seconds 3
    Write-Host "✅ Application démarrée" -ForegroundColor Green
}

function Show-NotificationChannels {
    Write-Host "`n📢 Vérification des canaux de notification..." -ForegroundColor Yellow
    
    $channels = adb shell dumpsys notification | Select-String "projet_android" -A 2 -B 2
    
    if ($channels) {
        Write-Host "✅ Canaux détectés:" -ForegroundColor Green
        $channels | ForEach-Object { Write-Host "   $_" -ForegroundColor White }
    } else {
        Write-Host "❌ Aucun canal détecté" -ForegroundColor Red
    }
}

function Show-ScheduledAlarms {
    Write-Host "`n⏰ Vérification des alarmes programmées..." -ForegroundColor Yellow
    
    $alarms = adb shell dumpsys alarm | Select-String "projet_android"
    
    if ($alarms) {
        Write-Host "✅ Alarmes programmées:" -ForegroundColor Green
        $alarms | ForEach-Object { Write-Host "   $_" -ForegroundColor White }
    } else {
        Write-Host "❌ Aucune alarme détectée" -ForegroundColor Red
        Write-Host "   Activez les notifications dans les Settings de l'app" -ForegroundColor Yellow
    }
}

function Start-LogMonitoring {
    Write-Host "`n📋 Surveillance des logs (Appuyez sur Ctrl+C pour arrêter)..." -ForegroundColor Yellow
    Write-Host "   Recherche: MainActivity2, HealthNotification, NotificationHelper" -ForegroundColor Cyan
    
    adb logcat | Select-String "MainActivity2|HealthNotification|NotificationHelper|notifications quotidiennes"
}

function Show-UserInstructions {
    Write-Host "`n📋 INSTRUCTIONS UTILISATEUR:" -ForegroundColor Cyan
    Write-Host "=============================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1️⃣  Ouvrez l'app Health Tracker sur votre appareil" -ForegroundColor White
    Write-Host "2️⃣  Appuyez sur le bouton ⚙️ SETTINGS" -ForegroundColor White  
    Write-Host "3️⃣  Activez 'Notifications' si désactivé" -ForegroundColor White
    Write-Host "4️⃣  Activez 'Water Reminders' si désactivé" -ForegroundColor White
    Write-Host "5️⃣  Retournez à l'écran principal" -ForegroundColor White
    Write-Host ""
    Write-Host "🔔 POUR VOIR LES NOTIFICATIONS:" -ForegroundColor Yellow
    Write-Host "   • Balayez vers le bas depuis le haut de l'écran Android" -ForegroundColor White
    Write-Host "   • Les notifications apparaîtront dans le panel" -ForegroundColor White
    Write-Host ""
    Write-Host "⏰ HORAIRES DES NOTIFICATIONS AUTOMATIQUES:" -ForegroundColor Yellow
    Write-Host "   • 08:00 - 🌅 Rappel matinal motivationnel" -ForegroundColor White
    Write-Host "   • 09:00-19:00 - 💧 Rappels eau (toutes les 2h)" -ForegroundColor White  
    Write-Host "   • 12:30 - 🚶 Rappel activité midi" -ForegroundColor White
    Write-Host "   • 17:30 - 🏃 Rappel activité soir" -ForegroundColor White
    Write-Host "   • 20:00 - 🌙 Bilan de la journée" -ForegroundColor White
    Write-Host ""
    Write-Host "🎯 NOTIFICATIONS D'OBJECTIFS (déclenchées automatiquement):" -ForegroundColor Yellow
    Write-Host "   • ≥10,000 pas → 🎉 Félicitations objectif pas!" -ForegroundColor White
    Write-Host "   • ≥2,000 cal → 🔥 Excellent objectif calories!" -ForegroundColor White  
    Write-Host "   • ≥7h sommeil → 😴 Parfait objectif sommeil!" -ForegroundColor White
}

function Test-ManualNotification {
    Write-Host "`n🧪 Test de notification manuelle..." -ForegroundColor Yellow
    
    # Essayer de déclencher une notification test
    Write-Host "   Tentative déclenchement notification test..." -ForegroundColor Cyan
    adb shell am broadcast -a android.intent.action.TIME_SET
    
    Start-Sleep -Seconds 2
    Write-Host "   Vérifiez le panel notifications Android!" -ForegroundColor Green
}

# MENU PRINCIPAL
Write-Host "`nChoisissez une option:" -ForegroundColor Cyan
Write-Host "1️⃣  Test Rapide (Quick Test)" -ForegroundColor White  
Write-Host "2️⃣  Test Complet (Full Test)" -ForegroundColor White
Write-Host "3️⃣  Surveillance Logs (Debug)" -ForegroundColor White
Write-Host "4️⃣  Instructions Utilisateur seulement" -ForegroundColor White

if (-not $QuickTest -and -not $FullTest -and -not $DebugLogs) {
    $choice = Read-Host "`nEntrez votre choix (1-4)"
} else {
    if ($QuickTest) { $choice = "1" }
    elseif ($FullTest) { $choice = "2" }  
    elseif ($DebugLogs) { $choice = "3" }
}

switch ($choice) {
    "1" {
        Write-Host "`n🚀 DÉMARRAGE TEST RAPIDE" -ForegroundColor Green
        if (Test-AdbConnection) {
            Start-App
            Show-NotificationChannels
            Show-UserInstructions
        }
    }
    
    "2" {
        Write-Host "`n🚀 DÉMARRAGE TEST COMPLET" -ForegroundColor Green
        if (Test-AdbConnection) {
            Build-And-Install
            Start-App
            Show-NotificationChannels
            Show-ScheduledAlarms
            Test-ManualNotification
            Show-UserInstructions
        }
    }
    
    "3" {
        Write-Host "`n🚀 SURVEILLANCE LOGS" -ForegroundColor Green
        if (Test-AdbConnection) {
            Start-LogMonitoring
        }
    }
    
    "4" {
        Show-UserInstructions
    }
    
    default {
        Write-Host "❌ Option invalide" -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n🎉 SCRIPT TERMINÉ!" -ForegroundColor Green
Write-Host "💡 Pour relancer: .\test_notifications.ps1" -ForegroundColor Yellow
