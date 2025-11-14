#!/bin/bash
cd /home/shaif/fhmaze_pg2_2_prak_2025/

echo "Kompiliere alle Java-Dateien..."
javac src/de/fhms/eti/players/*.java

if [ $? -eq 0 ]; then
    echo "Kompilierung erfolgreich!"

    echo "Erstelle JAR-Dateien..."
    cd src

    # MyBot.jar ohne Ausführen des Bots erstellen
    echo "Main-Class: de.fhms.eti.players.MyBot" > MyBot.mf
    jar cfm MyBot.jar MyBot.mf -C . de/fhms/eti/players

    # MinimalBot.jar ohne Ausführen des Bots erstellen
    echo "Main-Class: de.fhms.eti.players.MinimalBot" > MinimalBot.mf
    jar cfm MinimalBot.jar MinimalBot.mf -C . de/fhms/eti/players

    echo "JAR-Dateien erstellt"

    cd ..

    # JARs in Players kopieren
    mkdir -p Players
    cp src/MyBot.jar Players/
    cp src/MinimalBot.jar Players/
    echo "JARs in Players/ kopiert"

    # Aufräumen
    rm src/*.mf
    echo "Temporäre Dateien entfernt"

else
    echo "Kompilierungsfehler!"
fi
