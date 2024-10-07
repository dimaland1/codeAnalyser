package com.CodeAnalyzer;

import java.util.*;

/**
 * Représente le graphe d'appel des méthodes dans le code analysé.
 * Stocke les relations entre les méthodes appelantes et appelées.
 */
public class CallGraph {
    private Map<String, Set<String>> graph = new HashMap<>();
    
    public Set<String> getAllMethods() {
        Set<String> allMethods = new HashSet<>(graph.keySet());
        graph.values().forEach(allMethods::addAll);
        return allMethods;
    }

    public void addCall(String caller, String callee) {
        graph.computeIfAbsent(caller, k -> new HashSet<>()).add(callee);
    }

    public void printGraph() {
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            System.out.println(entry.getKey() + " appelle : " + entry.getValue());
        }
    }

    /**
     * Retourne l'ensemble des méthodes appelées par la méthode spécifiée.
     */
    public Set<String> getCallees(String method) {
        return graph.getOrDefault(method, Collections.emptySet());
    }


    /**
     * Retourne l'ensemble des méthodes qui appellent la méthode spécifiée.
     */
    public Set<String> getCallers(String method) {
        Set<String> callers = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            if (entry.getValue().contains(method)) {
                callers.add(entry.getKey());
            }
        }
        return callers;
    }
    
    
    /**
     * Calcule la profondeur maximale du graphe d'appel.
     */
    public int getMaxCallDepth() {
        Map<String, Integer> memo = new HashMap<>();
        int maxDepth = 0;
        for (String node : graph.keySet()) {
            maxDepth = Math.max(maxDepth, getDepth(node, memo));
        }
        return maxDepth;
    }
    

    /**
     * Méthode récursive pour calculer la profondeur d'un nœud dans le graphe.
     * Utilise la mémoïzation pour optimiser les calculs répétés.
     */
    private int getDepth(String node, Map<String, Integer> memo) {
        if (memo.containsKey(node)) return memo.get(node);
        
        int maxChildDepth = 0;
        for (String child : graph.getOrDefault(node, Collections.emptySet())) {
            maxChildDepth = Math.max(maxChildDepth, getDepth(child, memo));
        }
        
        int depth = 1 + maxChildDepth;
        memo.put(node, depth);
        return depth;
    }
}