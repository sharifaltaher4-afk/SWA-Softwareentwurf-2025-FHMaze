package de.fhms.eti.players.Erzeugungsmuster_Klassen;

/**
 * Enum für verfügbare Strategietypen
 * 
 * Ermöglicht typsichere Auswahl von Navigationsstrategien
 */
public enum StrategyType {
    SIMPLE,                    // SimpleNavigationStrategy - bevorzugt unbesuchte Felder
    RIGHT_HAND_RULE,          // RightHandRuleStrategy - folgt rechter Wand
    FORM_COLLECTION,          // FormCollectionStrategy - sammelt Formulare
    INTELLIGENT_EXPLORATION   // IntelligentExplorationStrategy - A* Pathfinding
}