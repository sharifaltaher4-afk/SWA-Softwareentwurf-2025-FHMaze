package main.java.de.fhms.eti.players.creation;

import main.java.de.fhms.eti.players.domain.IFormManager;
import main.java.de.fhms.eti.players.domain.Maze;
import main.java.de.fhms.eti.players.strategy.INavigationStrategy;

/**
 * Factory-Interface für die Erstellung von Navigationsstrategien
 * <p>
 * Implementiert das Factory Method Pattern (Erzeugungsmuster)
 * <p>
 * Vorteile:
 * - Entkopplung: Client kennt nur Interface, nicht konkrete Implementierung
 * - OCP: Neue Strategien ohne Änderung bestehenden Codes
 * - SRP: Erzeugungslogik separiert von Nutzungslogik
 */
public interface INavigationStrategyFactory {
    /**
     * Erstellt eine Navigationsstrategie basierend auf dem Level
     *
     * @param level       Das aktuelle Spiellevel (1 oder 2)
     * @param maze        Das Labyrinth-Gedächtnis
     * @param formManager Der FormManager (nur für Level 2)
     * @return Die passende Navigationsstrategie
     */
    INavigationStrategy createStrategy(int level, Maze maze, IFormManager formManager);

    /**
     * Erstellt eine Navigationsstrategie basierend auf dem Typ
     *
     * @param strategyType Der gewünschte Strategietyp
     * @param maze         Das Labyrinth-Gedächtnis
     * @param formManager  Der FormManager (optional)
     * @return Die gewünschte Navigationsstrategie
     */
    INavigationStrategy createStrategyByType(StrategyType strategyType, Maze maze, IFormManager formManager);
}
