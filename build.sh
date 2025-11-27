#!/bin/bash

set -xe

echo "Kompiliere alle Java-Dateien..."
javac src/de/fhms/eti/players/*/*.java

if [ $? -eq 0 ]; then
    echo "Kompilierung erfolgreich!"

    echo "Erstelle JAR-Dateien..."
    cd src

    # MyBot.jar ohne Ausführen des Bots erstellen
    echo "Main-Class: de.fhms.eti.players.Erzeugungsmuster_Klassen.MyBot" > MyBot.mf
    jar cfm MyBot.jar MyBot.mf -C . de/fhms/eti/players

    # MinimalBot.jar ohne Ausführen des Bots erstellen
    echo "Main-Class: de.fhms.eti.players.Domain_Klassen.MinimalBot" > MinimalBot.mf
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
    exit 1
fi
