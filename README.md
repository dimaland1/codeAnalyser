# Analyseur Statique de Code orienté objet

Cet outil permet d'effectuer une analyse statique de projets Java, fournissant des métriques sur la structure du code et générant un graphe d'appel interactif.

## Prérequis

- Java JDK
- Maven (pour la gestion des dépendances)
- JavaFX (pour l'interface graphique)

## Installation

1. Clonez le dépôt :
   ```
   git clone https://github.com/dimaland1/codeAnalyzer.git
   ```
2. Naviguez dans le répertoire du projet :
   ```
   cd codeAnalyzer
   ```
3. Compilez le projet avec Maven :
   ```
   mvn clean install
   ```

## Utilisation

### Mode CLI (Interface en Ligne de Commande)

1. Exécutez le programme :
   ```
   Main.java
   ```
2. Choisissez l'option 1 pour continuer en CLI.
3. Entrez le chemin absolu du projet Java que vous souhaitez analyser lorsque vous y êtes invité.
4. Les résultats de l'analyse seront affichés dans la console.
5. Après l'analyse initiale, vous aurez accès à un sous-menu avec les options suivantes :
   - Analyser les classes avec beaucoup de méthodes
   - Visualiser le graphe d'appel
   - Retourner au menu principal

### Mode GUI (Interface Graphique Utilisateur)

1. Exécutez le programme :
   ```
   Main.java
   ```
2. Choisissez l'option 2 pour lancer l'interface graphique.
3. Dans l'interface graphique :
   - Cliquez sur "Choisir un projet" pour sélectionner le répertoire du projet à analyser.
   - Les résultats de l'analyse s'afficheront dans l'interface.
   - Utilisez le champ "Nombre de méthodes" et le bouton "Rechercher" pour effectuer une analyse supplémentaire.
   - Cliquez sur "Afficher le graphe d'appel" pour visualiser le graphe interactif.

## Fonctionnalités

- Calcul de métriques de code (nombre de classes, méthodes, lignes de code, etc.)
- Génération d'un graphe d'appel interactif
- Identification des classes et méthodes complexes
- Visualisation des dépendances entre les méthodes
- Affichage des classes avec un nombre n de méthodes (seuil configurable)
- Interface en ligne de commande avec options d'analyse multiples
- Interface graphique intuitive avec affichage des résultats en temps réel

## Dépannage

- Si vous rencontrez des problèmes avec JavaFX, assurez-vous qu'il est correctement installé et configuré dans votre PATH.
- Pour les erreurs liées à Maven, vérifiez que toutes les dépendances sont correctement téléchargées.
- Si l'analyse des classes avec beaucoup de méthodes ne fonctionne pas, vérifiez que vous avez entré un nombre valide dans le champ correspondant.
