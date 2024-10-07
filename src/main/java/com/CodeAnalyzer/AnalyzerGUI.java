package com.CodeAnalyzer;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Interface graphique principale pour l'analyseur de code Java.
 * Utilise JavaFX pour créer une fenêtre interactive permettant à l'utilisateur
 * de sélectionner un projet à analyser et d'afficher les résultats.
 */
public class AnalyzerGUI extends Application {
    private TextArea resultArea;
    private CodeAnalyzer analyzer;
    private Stage primaryStage;
    private TextField methodThresholdField;

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Java Code Analyzer");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Button chooseButton = new Button("Choisir un projet");
        Button showCallGraphButton = new Button("Afficher le graphe d'appel");
        showCallGraphButton.setDisable(true);  // Désactivé jusqu'à ce que l'analyse soit effectuée
        
        methodThresholdField = new TextField();
        methodThresholdField.setPromptText("Nombre de méthodes");
        Button analyzeManyMethodsButton = new Button("Rechercher");
        analyzeManyMethodsButton.setDisable(true);  // Désactivé jusqu'à ce que l'analyse soit effectuée
        
        HBox analysisBox = new HBox(10, methodThresholdField, analyzeManyMethodsButton);
        
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(20);

        chooseButton.setOnAction(e -> {
            chooseAndAnalyzeProject(primaryStage);
            showCallGraphButton.setDisable(false);
            analyzeManyMethodsButton.setDisable(false);
        });

        showCallGraphButton.setOnAction(e -> launchCallGraphGUI());

        analyzeManyMethodsButton.setOnAction(e -> analyzeManyMethods());

        root.getChildren().addAll(chooseButton, showCallGraphButton, analysisBox, resultArea);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Ouvre un sélecteur de dossier pour choisir le projet à analyser,
     * puis lance l'analyse et affiche les résultats.
     */
    private void chooseAndAnalyzeProject(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Sélectionner un projet Java");
        File selectedDirectory = directoryChooser.showDialog(stage);
        
        if (selectedDirectory != null) {
            analyzer = new CodeAnalyzer();
            analyzer.analyze(selectedDirectory.getPath());
            
            String results = captureOutput(() -> {
                analyzer.printStatistics();
                analyzer.printCallGraphStatistics();
            });
            
            resultArea.setText(results);
        }
    }

    /**
     * Lance l'interface graphique du graphe d'appel.
     */
    private void launchCallGraphGUI() {
        if (analyzer != null && analyzer.getCallGraph() != null) {
            System.out.println("lancement du call graph");
            CallGraphGUI.displayGraph(analyzer.getCallGraph(), primaryStage);
        } else {
            showAlert("Erreur", "Veuillez d'abord analyser un projet avant d'afficher le graphe d'appel.");
        }
    }

    /**
     * Analyse les classes avec beaucoup de méthodes et ajoute les résultats au texte existant.
     */
    private void analyzeManyMethods() {
        if (analyzer == null) {
            showAlert("Erreur", "Veuillez d'abord analyser un projet.");
            return;
        }

        try {
            int threshold = Integer.parseInt(methodThresholdField.getText());
            
            String results = captureOutput(() -> analyzer.printClassesWithManyMethods(threshold));
            
            resultArea.appendText("\n\n" + results);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un nombre valide dans le champ 'Nombre de méthodes'.");
        }
    }
    
    /**
     * Capture la sortie console d'une opération et la retourne sous forme de chaîne.
     */
    private String captureOutput(Runnable operation) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        
        operation.run();
        
        System.out.flush();
        System.setOut(old);
        
        return baos.toString();
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte avec le titre et le contenu spécifiés.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}