package com.CodeAnalyzer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;

/**
 * Interface graphique pour visualiser le graphe d'appel.
 * Utilise JavaFX pour créer une représentation visuelle du graphe.
 */
public class CallGraphGUI extends Application {
    private static CallGraph callGraph;
    private static final double RADIUS = 5;

    public static void displayGraph(CallGraph graph, Stage owner) {
        callGraph = graph;
        Stage stage = new Stage();
        stage.initOwner(owner);
        createAndShowGUI(stage);
        stage.show();
    }

    private static void createAndShowGUI(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        Map<String, Circle> nodeMap = new HashMap<>();
        Map<String, List<Line>> edgesMap = new HashMap<>();
        List<String> methods = new ArrayList<>(callGraph.getAllMethods());

        // Create nodes
        for (int i = 0; i < methods.size(); i++) {
            String method = methods.get(i);
            double angle = 2 * Math.PI * i / methods.size();
            double x = 400 + 300 * Math.cos(angle);
            double y = 300 + 300 * Math.sin(angle);

            Circle circle = new Circle(x, y, RADIUS);
            Text text = new Text(x + 10, y, method);
            nodeMap.put(method, circle);
            edgesMap.put(method, new ArrayList<>());

            root.getChildren().addAll(circle, text);

            // Add interaction to highlight the node and its edges
            circle.setOnMouseClicked(event -> highlightNodeAndEdges(method, nodeMap, edgesMap));
        }

        // Create edges
        for (String caller : methods) {
            Circle callerNode = nodeMap.get(caller);
            for (String callee : callGraph.getCallees(caller)) {
                Circle calleeNode = nodeMap.get(callee);
                if (calleeNode != null) {
                    Line line = new Line(callerNode.getCenterX(), callerNode.getCenterY(),
                                         calleeNode.getCenterX(), calleeNode.getCenterY());
                    line.setStroke(Color.GRAY);
                    root.getChildren().add(line);
                    edgesMap.get(caller).add(line);
                    edgesMap.get(callee).add(line); // Store edges for both caller and callee
                }
            }
        }

        stage.setTitle("Call Graph Visualization");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Met en surbrillance le nœud sélectionné et ses arêtes associées.
     */
    private static void highlightNodeAndEdges(String method, Map<String, Circle> nodeMap, 
                                              Map<String, List<Line>> edgesMap) {
        // Reset all nodes and edges
        nodeMap.values().forEach(node -> node.setFill(Color.BLACK));
        edgesMap.values().forEach(lines -> lines.forEach(line -> line.setStroke(Color.GRAY)));

        // Highlight the selected node
        Circle selectedNode = nodeMap.get(method);
        if (selectedNode != null) {
            selectedNode.setFill(Color.RED);

            // Highlight all edges connected to the selected node
            List<Line> connectedEdges = edgesMap.get(method);
            if (connectedEdges != null) {
                connectedEdges.forEach(line -> line.setStroke(Color.RED));
            }
        }
    }


    /**
     * Lance l'interface graphique du graphe d'appel.
     */
    public static void launchGUI(CallGraph graph) {
        callGraph = graph;
        launch();
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        Map<String, Circle> nodeMap = new HashMap<>();
        Map<String, List<Line>> edgesMap = new HashMap<>();
        List<String> methods = new ArrayList<>(callGraph.getAllMethods());

        // Create nodes
        for (int i = 0; i < methods.size(); i++) {
            String method = methods.get(i);
            double angle = 2 * Math.PI * i / methods.size();
            double x = 400 + 300 * Math.cos(angle);
            double y = 300 + 300 * Math.sin(angle);

            Circle circle = new Circle(x, y, RADIUS);
            Text text = new Text(x + 10, y, method);
            nodeMap.put(method, circle);
            edgesMap.put(method, new ArrayList<>());

            root.getChildren().addAll(circle, text);

            // Add interaction to highlight the node and its edges
            circle.setOnMouseClicked(event -> highlightNodeAndEdges(method, nodeMap, edgesMap));
        }

        // Create edges
        for (String caller : methods) {
            Circle callerNode = nodeMap.get(caller);
            for (String callee : callGraph.getCallees(caller)) {
                Circle calleeNode = nodeMap.get(callee);
                if (calleeNode != null) {
                    Line line = new Line(callerNode.getCenterX(), callerNode.getCenterY(),
                                         calleeNode.getCenterX(), calleeNode.getCenterY());
                    line.setStroke(Color.GRAY);
                    root.getChildren().add(line);
                    edgesMap.get(caller).add(line);
                    edgesMap.get(callee).add(line); // Store edges for both caller and callee
                }
            }
        }

        primaryStage.setTitle("Call Graph Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
