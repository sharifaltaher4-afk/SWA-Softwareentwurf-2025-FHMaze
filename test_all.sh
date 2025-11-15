cd ~/fhmaze_pg2_2_prak_2025

echo "=== VOLLSTÄNDIGER TEST ALLER LABYRINTHE ==="
for i in 01 02 03 04 05 06 07 08 09 10; do
    echo ""
    echo "=== Labyrinth $i ==="
    ./linux-runner.sh Mazes/${i}_*.json Players/MyBot.jar 2>/dev/null
    result=$(grep "Spiel beendet" bot.log | tail -1)
    success=$(grep "FINISH gesendet" bot.log | tail -1)
    
    if [ -n "$success" ]; then
        echo "[SUCCESS] $result"
    else
        echo "[FAILED] $result"
    fi
done

echo ""
echo "=== ZUSAMMENFASSUNG ==="

