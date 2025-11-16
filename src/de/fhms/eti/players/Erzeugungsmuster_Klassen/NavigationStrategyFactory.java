package de.fhms.eti.players.Erzeugungsmuster_Klassen;

import de.fhms.eti.players.Domain_Klassen.IFormManager;
import de.fhms.eti.players.Domain_Klassen.Maze;
import de.fhms.eti.players.Strategie_Klassen.BFSPathFinderStrategy;
import de.fhms.eti.players.Strategie_Klassen.FormCollectionStrategy;
import de.fhms.eti.players.Strategie_Klassen.INavigationStrategy;
import de.fhms.eti.players.Strategie_Klassen.IntelligentExplorationStrategy;
import de.fhms.eti.players.Strategie_Klassen.RightHandRuleStrategy;
import de.fhms.eti.players.Strategie_Klassen.SimpleNavigationStrategy;

/**
 * Konkrete Factory für Navigationsstrategien
 * 
 * Implementiert das Factory Method Pattern
 * 
 * Verantwortung (SRP):
 * - Kapselung der Objekt-Erzeugung
 * - Auswahl der passenden Strategie basierend auf Level/Typ
 * - Konfiguration der erstellten Strategien
 * 
 * Erfüllt SOLID:
 * - SRP: Nur für Erzeugung zuständig
 * - OCP: Erweiterbar ohne Änderung (neue Strategien hinzufügen)
 * - DIP: Abhängigkeit von Interfaces, nicht Implementierungen
 */
public class NavigationStrategyFactory implements INavigationStrategyFactory {

    /**
     * Erstellt Strategie basierend auf Level
     * 
     * Level 1: SimpleNavigationStrategy (einfache Erkundung)
     * Level 2: FormCollectionStrategy (Formular-Sammlung)
     * 
     * @param level Das Spiellevel
     * @param maze Das Labyrinth-Gedächtnis
     * @param formManager Der FormManager
     * @return Passende Navigationsstrategie
     */
    @Override
    public INavigationStrategy createStrategy(int level, Maze maze, IFormManager formManager) {
        INavigationStrategy strategy;

        if (level == 1) {
            System.err.println("[FACTORY] Factory: Erstelle SimpleNavigationStrategy fuer Level 1");
            strategy = new SimpleNavigationStrategy(maze);
        } else if (level == 2) {
            System.err.println("[FACTORY] Factory: Erstelle FormCollectionStrategy fuer Level 2");
            if (formManager == null) {
                throw new IllegalArgumentException("FormManager erforderlich fuer Level 2");
            }
            strategy = new FormCollectionStrategy(maze, formManager, new BFSPathFinderStrategy());
        } else {
            System.err.println("  Unbekanntes Level " + level + " - nutze SimpleNavigationStrategy");
            strategy = new SimpleNavigationStrategy(maze);
        }

        return strategy;
    }
    
    /**
     * Erstellt Strategie basierend auf explizitem Typ
     * 
     * Ermöglicht manuelle Strategie-Auswahl
     * 
     * @param strategyType Der gewünschte Strategietyp
     * @param maze Das Labyrinth-Gedächtnis
     * @param formManager Der FormManager (optional)
     * @return Gewünschte Navigationsstrategie
     */
    @Override
    public INavigationStrategy createStrategyByType(StrategyType strategyType, Maze maze, IFormManager formManager) {
        INavigationStrategy strategy;

        switch (strategyType) {
            case SIMPLE:
                System.err.println("Factory: Erstelle SimpleNavigationStrategy");
                strategy = new SimpleNavigationStrategy(maze);
                break;

            case RIGHT_HAND_RULE:
                System.err.println("[FACTORY] Factory: Erstelle RightHandRuleStrategy");
                strategy = new RightHandRuleStrategy();
                break;

            case FORM_COLLECTION:
                System.err.println("[FACTORY] Factory: Erstelle FormCollectionStrategy");
                if (formManager == null) {
                    throw new IllegalArgumentException("FormManager erforderlich fuer FormCollectionStrategy");
                }
                strategy = new FormCollectionStrategy(maze, formManager, new BFSPathFinderStrategy());
                break;

            case INTELLIGENT_EXPLORATION:
                System.err.println("[FACTORY] Factory: Erstelle IntelligentExplorationStrategy");
                strategy = new IntelligentExplorationStrategy(maze);
                break;

            default:
                System.err.println("  Unbekannter Strategietyp - nutze SimpleNavigationStrategy");
                strategy = new SimpleNavigationStrategy(maze);
                break;
        }

        return strategy;
    }
}

