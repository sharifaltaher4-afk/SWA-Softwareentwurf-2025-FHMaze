#!/bin/bash
cd MinimalBot/src
javac de/fhms/eti/players/MyBot.java
jar cvfm MyBot.jar META-INF/MANIFEST.MF de/fhms/eti/players/*.class
cp MyBot.jar ../../Players/
echo "✅ MyBot.jar wurde aktualisiert!"
