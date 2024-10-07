package com.CodeAnalyzer;

import java.util.Scanner;
import javafx.application.Application;

/**
 * Classe principale qui sert de point d'entrée pour l'application.
 * Permet à l'utilisateur de choisir entre l'interface en ligne de commande (CLI) et l'interface graphique (GUI).
 */
public class Main {
    private static CodeAnalyzer analyzer;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n===== Analyseur Statique de Code =====");
            System.out.println("1. Continuer en CLI");
            System.out.println("2. Lancer le GUI");
            System.out.println("3. Quitter");
            System.out.print("Choisissez une option : ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    runCLI(scanner);
                    break;
                case 2:
                    launchGUI();
                    return; // Exit main method after launching GUI
                case 3:
                    System.out.println("Au revoir !");
                    scanner.close();
                    return;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    /**
     * Exécute l'analyse en mode ligne de commande (CLI).
     */
    private static void runCLI(Scanner scanner) {
        System.out.println("Veuillez entrer le chemin du projet à analyser:");
        String projectPath = scanner.nextLine();
        
        if (projectPath == null || projectPath.trim().isEmpty()) {
            System.out.println("Erreur: Le chemin du projet est vide ou invalide.");
            return;
        }

        analyzer = new CodeAnalyzer();
        analyzer.analyze(projectPath);
        analyzer.printStatistics();
        analyzer.printCallGraphStatistics();
    
        while (true) {
            System.out.println("\nQue voulez-vous faire maintenant ?");
            System.out.println("1. Afficher les classes avec n de méthodes");
            System.out.println("2. Visualiser le graphe d'appel");
            System.out.println("3. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    analyzeManyMethods(scanner);
                    break;
                case 2:
                    visualizeCallGraph();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    /**
     * Analyse les classes avec beaucoup de méthodes.
     */
    private static void analyzeManyMethods(Scanner scanner) {
        System.out.print("Entrez le nombre minimum de méthodes : ");
        int threshold = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        analyzer.printClassesWithManyMethods(threshold);
    }

    /**
     * Visualise le graphe d'appel.
     */
    private static void visualizeCallGraph() {
        if (analyzer != null && analyzer.getCallGraph() != null) {
            CallGraphGUI.launchGUI(analyzer.getCallGraph());
        } else {
            System.out.println("Erreur: Aucun graphe d'appel n'est disponible.");
        }
    }
    
    /**
     * Lance l'interface graphique (GUI) de l'application.
     */
    private static void launchGUI() {
        Application.launch(AnalyzerGUI.class);
    }
}