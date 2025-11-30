#!/bin/bash

set -e  # Exit on error (kein -x für saubere Ausgabe)

echo "============================================================"
echo "FHMaze Bot - Build Script"
echo "============================================================"

# ============================================================
# SCHRITT 1: KOMPILIERE HAUPT-CODE
# ============================================================

echo ""
echo "Kompiliere Haupt-Code..."
javac -d target/classes \
      -sourcepath src/main/java \
      src/main/java/de/fhms/eti/players/*/*.java

if [ $? -eq 0 ]; then
    echo "Haupt-Code erfolgreich kompiliert!"
else
    echo "Kompilierungsfehler im Haupt-Code!"
    exit 1
fi

# ============================================================
# SCHRITT 2: ERSTELLE JAR-DATEIEN
# ============================================================

echo ""
echo "Erstelle JAR-Dateien..."

# MyBot.jar erstellen
echo "Main-Class: de.fhms.eti.players.Erzeugungsmuster_Klassen.MyBot" > target/MyBot.mf
jar cfm target/MyBot.jar target/MyBot.mf -C target/classes de/fhms/eti/players

if [ $? -eq 0 ]; then
    echo "MyBot.jar erstellt"
else
    echo "Fehler beim Erstellen von MyBot.jar"
    exit 1
fi

# MinimalBot.jar erstellen
echo "Main-Class: de.fhms.eti.players.Domain_Klassen.MinimalBot" > target/MinimalBot.mf
jar cfm target/MinimalBot.jar target/MinimalBot.mf -C target/classes de/fhms/eti/players

if [ $? -eq 0 ]; then
    echo "MinimalBot.jar erstellt"
else
    echo "Fehler beim Erstellen von MinimalBot.jar"
    exit 1
fi

# ============================================================
# SCHRITT 3: KOPIERE JARs IN PLAYERS VERZEICHNIS
# ============================================================

echo ""
echo "Kopiere JARs nach Players/..."
mkdir -p Players
cp target/MyBot.jar Players/
cp target/MinimalBot.jar Players/

echo "JARs in Players/ kopiert"

# ============================================================
# SCHRITT 4: AUFRÄUMEN
# ============================================================

echo ""
echo "Räume auf..."
rm -f target/*.mf

echo "Temporäre Dateien entfernt"

# ============================================================
# FERTIG
# ============================================================

echo ""
echo "============================================================"
echo "BUILD ERFOLGREICH!"
echo "============================================================"
echo ""
echo "JAR-Dateien verfügbar in:"
echo "  Players/MyBot.jar"
echo "  Players/MinimalBot.jar"
echo ""
echo "Ausführen mit:"
echo "  ./linux-runner.sh Mazes/01_Geradeaus.json"
echo ""