package com.CodeAnalyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe principale pour l'analyse statique de code Java.
 * Utilise JavaParser pour analyser les fichiers Java et extraire des informations.
 */
public class CodeAnalyzer {
    protected final List<ClassInfo> classes = new ArrayList<>();
    private final Set<String> packages = new HashSet<>();
    private int totalLines = 0;
    private int totalMethods = 0;
    private int totalAttributes = 0;
    private CallGraph callGraph = new CallGraph();

    /**
     * Analyse un projet Java à partir du chemin spécifié.
     */
    public void analyze(String projectPath) {
        try {
            processDirectory(new File(projectPath));
        } catch (IOException e) {
            System.err.println("Error analyzing project: " + e.getMessage());
        }
    }


    /**
     * Traite récursivement un répertoire pour analyser tous les fichiers Java.
     */
    private void processDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file);
            } else if (file.getName().endsWith(".java")) {
                analyzeJavaFile(file);
            }
        }
    }
  
    /**
     * Analyse un fichier Java en utilisant JavaParser.
     * Extrait les informations sur les classes, méthodes et attributs.
     */
    private void analyzeJavaFile(File file) throws IOException {
    	JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(file)
            .getResult()
            .orElseThrow(() -> new IOException("Failed to parse " + file.getName()));
        
        // Extract package information
        cu.getPackageDeclaration().ifPresent(pkg -> 
            packages.add(pkg.getNameAsString())
        );

        // Analyze classes
        for (ClassOrInterfaceDeclaration classDecl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            ClassInfo classInfo = new ClassInfo(classDecl.getNameAsString());
            
            // Analyze methods
            for (MethodDeclaration methodDecl : classDecl.getMethods()) {
                MethodInfo methodInfo = new MethodInfo(
                    methodDecl.getNameAsString(),
                    countMethodLines(methodDecl),
                    methodDecl.getParameters().size()
                );
                classInfo.methods.add(methodInfo);
                totalMethods++;

                // Analyze method calls for the call graph
                String callerMethod = classInfo.name + "." + methodInfo.name;
                methodDecl.findAll(MethodCallExpr.class).forEach(call -> {
                    String calledMethod = call.getNameAsString();
                    getCallGraph().addCall(callerMethod, calledMethod);
                });
            }

            // Analyze attributes
            int attributeCount = classDecl.getFields().size();
            classInfo.attributes = attributeCount;
            totalAttributes += attributeCount;

            classes.add(classInfo);
            totalLines += countClassLines(classDecl);
        }
    }
    
    /**
     * Compte le nombre de lignes dans une méthode.
     */
    private int countMethodLines(MethodDeclaration method) {
        return method.toString().split("\n").length;
    }

    /**
     * Compte le nombre de lignes dans une classe.
     */
    private int countClassLines(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.toString().split("\n").length;
    }

    /**
     * Affiche les statistiques d'analyse du code.
     */
    public void printStatistics() {
        System.out.println("=== Statistiques d'analyse du code ===");
        System.out.println("1. Nombre de classes : " + classes.size());
        System.out.println("2. Nombre de lignes de code : " + totalLines);
        System.out.println("3. Nombre total de méthodes : " + totalMethods);
        System.out.println("4. Nombre total de packages : " + packages.size());
        
        double avgMethodsPerClass = classes.isEmpty() ? 0 : (double) totalMethods / classes.size();
        System.out.printf("5. Moyenne de méthodes par classe : %.2f%n", avgMethodsPerClass);
        
        double avgLinesPerMethod = totalMethods == 0 ? 0 : (double) totalLines / totalMethods;
        System.out.printf("6. Moyenne de lignes par méthode : %.2f%n", avgLinesPerMethod);
        
        double avgAttributesPerClass = classes.isEmpty() ? 0 : (double) totalAttributes / classes.size();
        System.out.printf("7. Moyenne d'attributs par classe : %.2f%n", avgAttributesPerClass);

        printTopClasses();  
        printTopMethods();
        printMaxParameters();
    }

    /**
     * Affiche Les 10% des classes qui possèdent le plus grand nombre de méthodes et d'attributs.
     */
    private void printTopClasses() {
        int topN = Math.max(1, classes.size() / 10);
        
        System.out.println("\n8. Top 10% des classes par nombre de méthodes :");
        List<ClassInfo> topByMethods = classes.stream()
            .sorted((c1, c2) -> Integer.compare(c2.methods.size(), c1.methods.size()))
            .limit(topN)
            .collect(Collectors.toList());
        
        topByMethods.forEach(c -> System.out.printf("   %s (%d méthodes)%n", 
            c.name, c.methods.size()));

        System.out.println("\n9. Top 10% des classes par nombre d'attributs :");
        List<ClassInfo> topByAttributes = classes.stream()
            .sorted((c1, c2) -> Integer.compare(c2.attributes, c1.attributes))
            .limit(topN)
            .collect(Collectors.toList());
        
        topByAttributes.forEach(c -> System.out.printf("   %s (%d attributs)%n", 
            c.name, c.attributes));

        System.out.println("\n10. Classes dans les deux catégories précédentes :");
        topByMethods.stream()
            .filter(topByAttributes::contains)
            .forEach(c -> System.out.println("   " + c.name));
    }

    
    /**
     * Classe avec plus de n méthodes
     */
    public void printClassesWithManyMethods(int threshold) {
        System.out.println("\n11. Classes avec plus de " + threshold + " méthodes :");
        classes.stream()
            .filter(c -> c.methods.size() >= threshold)
            .forEach(c -> System.out.printf("   %s (%d méthodes)%n", 
                c.name, c.methods.size()));
    }

    /**
     * Affiche les méthodes les plus longues.
     */
    private void printTopMethods() {
        System.out.println("\n12. Top 10% des méthodes par nombre de lignes :");
        classes.stream()
            .flatMap(c -> c.methods.stream()
                .map(m -> new MethodDetail(c.name, m)))
            .sorted((m1, m2) -> Integer.compare(m2.method.lines, m1.method.lines))
            .limit(Math.max(1, totalMethods / 10))
            .forEach(md -> System.out.printf("   %s.%s (%d lignes)%n", 
                md.className, md.method.name, md.method.lines));
    }

    /**
     * Affiche le nombre maximal de paramètres pour une méthode.
     */
    private void printMaxParameters() {
        OptionalInt maxParams = classes.stream()
            .flatMap(c -> c.methods.stream())
            .mapToInt(m -> m.parameters)
            .max();
        
        System.out.println("\n13. Nombre maximal de paramètres : " + 
            maxParams.orElse(0));
    }

    /**
     * Affiche les statistiques du graphe d'appel.
     */
    public void printCallGraphStatistics() {
        System.out.println("\n=== Statistiques du graphe d'appel ===");
        System.out.println("Graphe d'appel :");
        getCallGraph().printGraph();
        
        System.out.println("\nProfondeur maximale des appels : " + getCallGraph().getMaxCallDepth());
        
        String mostCalledMethod = findMostCalledMethod();
        System.out.println("Méthode la plus appelée : " + mostCalledMethod);
        System.out.println("Appelée par : " + getCallGraph().getCallers(mostCalledMethod));
    }

    /**
     * Trouve la méthode la plus appelée dans le graphe d'appel.
     */
    private String findMostCalledMethod() {
        return getCallGraph().getAllMethods().stream()
            .max(Comparator.comparingInt(method -> getCallGraph().getCallers(method).size()))
            .orElse("Aucune méthode trouvée");
    }

    private static class ClassInfo {
        String name;
        List<MethodInfo> methods = new ArrayList<>();
        int attributes;

        ClassInfo(String name) {
            this.name = name;
        }
    }

    private static class MethodInfo {
        String name;
        int lines;
        int parameters;

        MethodInfo(String name, int lines, int parameters) {
            this.name = name;
            this.lines = lines;
            this.parameters = parameters;
        }
    }

    private static class MethodDetail {
        String className;
        MethodInfo method;

        MethodDetail(String className, MethodInfo method) {
            this.className = className;
            this.method = method;
        }
    }

    /**
     * Point d'entrée de teste pour l'exécution en ligne de commande.
     */
    public static void main(String[] args) {
        String projectPath;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Veuillez entrer le chemin du projet à analyser:");
        projectPath = scanner.nextLine();
        scanner.close();
       
        if (projectPath == null || projectPath.trim().isEmpty()) {
            System.out.println("Erreur: Le chemin du projet est vide ou invalide.");
            return;
        }

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(projectPath);
        analyzer.printStatistics();
        analyzer.printCallGraphStatistics();
    
        CallGraphGUI.launchGUI(analyzer.getCallGraph());
    }

	public CallGraph getCallGraph() {
		return callGraph;
	}

}