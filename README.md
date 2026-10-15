# TP1 - Injection des dépendances (IoC) par réflexion

Ce projet est un **travail pratique Java** qui illustre la mise en œuvre de
l'**injection des dépendances** (couplage faible) sans utiliser de framework
comme Spring.

Les classes à instancier sont **lues depuis un fichier de configuration**
(`config.txt`) et les objets sont créés dynamiquement grâce à la
**réflexion Java** (`Class.forName`, `newInstance`, `Method.invoke`).

---

## Objectifs pédagogiques

- Comprendre le principe d'**Inversion de Contrôle (IoC)** et d'**injection des dépendances**.
- Séparer les couches **DAO**, **Métier** et **Présentation** derrière des **interfaces**.
- Éviter le `new` direct dans le code métier (couplage faible).
- Utiliser la **réflexion** pour instancier des classes à partir de leur nom.
- Externaliser la configuration dans un fichier texte.

---

## Architecture du projet

```
Tp1_Pd/
├── config.txt                         # Configuration : noms des classes à charger
├── pom.xml                            # Configuration Maven
└── src/
    ├── main/java/org/example/
    │   ├── dao/
    │   │   ├── IDao.java              # Interface de persistance
    │   │   └── DaoImpl.java           # Implémentation : retourne 100.0
    │   ├── metier/
    │   │   ├── IMetier.java           # Interface métier
    │   │   └── MetierImpl.java        # Implémentation : calcul = valeur * 2
    │   └── presentation/
    │       └── Presentation2.java     # Point d'entrée : lit config + injection
    └── test/java/org/example/
        └── AppTest.java               # Test JUnit de base
```

### Rôle des couches

| Couche       | Interface | Implémentation | Rôle |
|--------------|-----------|----------------|------|
| DAO          | `IDao`    | `DaoImpl`      | Récupère une valeur depuis une source de données (`getValue()` → `100.0`) |
| Métier       | `IMetier` | `MetierImpl`   | Effectue le calcul métier (`calcul()` → `valeur * 2`) |
| Présentation | –         | `Presentation2`| Assemble les objets et lance le traitement |

### Le fichier de configuration `config.txt`

```text
org.example.dao.DaoImpl
org.example.metier.MetierImpl
```

- **Ligne 1** : la classe DAO à instancier.
- **Ligne 2** : la classe Métier à instancier.

Il suffit de modifier ces noms pour changer d'implémentation **sans recompiler** le code.

---

## Prérequis

- **JDK 8** ou supérieur
- **Maven** (ou simplement un IDE comme IntelliJ IDEA / Eclipse)
- Testé avec IntelliJ IDEA

---

## Exécution

### Option 1 — En ligne de commande (Maven)

Depuis la racine du projet (le dossier qui contient `config.txt`) :

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.presentation.Presentation2"
```

> Remarque : `config.txt` doit se trouver dans le **répertoire de travail**
> courant, sinon le programme lève l'erreur
> `Nom de classe manquant dans le fichier config` (ou `FileNotFoundException`).

### Option 2 — Avec IntelliJ IDEA

1. Ouvrir le projet (`File > Open` → sélectionner le dossier `Tp1_Pd`).
2. Vérifier que le **working directory** de la run configuration est bien la racine du projet.
3. Exécuter la classe `org.example.presentation.Presentation2` (clic droit → `Run`).

---

## Fonctionnement détaillé

La méthode `main()` de `Presentation2` exécute les étapes suivantes :

1. **Lecture de la configuration** — `loadClassNames("config.txt")` lit les deux
   premières lignes et récupère les noms complets des classes.
2. **Création du DAO** — `createInstance()` instancie la classe DAO via
   `Class.forName(...).getDeclaredConstructor().newInstance()`.
3. **Création du Métier** — même mécanisme pour la classe Métier.
4. **Injection de la dépendance** — `injectDependency()` recherche par réflexion
   la méthode `setDao(IDao)` et y injecte l'objet DAO.
5. **Lancement du traitement** — `metier.calcul()` appelle `dao.getValue()`,
   multiplie la valeur par `2` et affiche le résultat.

Ainsi, `MetierImpl` **ne connaît pas** `DaoImpl` : il ne dépend que de
l'interface `IDao`, ce qui rend le code faiblement couplé et facilement testable.

---

## Résultats

### Sortie console

```
DAO charge : DaoImpl
Metier charge : MetierImpl
[DAO] Récupére la valeur depuis la source de données
[METIER] Résultat : 200.0
Valeur du resultat : 200.0
```

**Explication :**

- `DaoImpl.getValue()` retourne la valeur de base `100.0`.
- `MetierImpl.calcul()` calcule `100.0 * 2` = `200.0`.
- Résultat final : **200.0**.

### Capture d'écran de l'exécution

<!--
  Pour ajouter une capture d'écran :
  1. Créer un dossier `docs/` à la racine du projet.
  2. Y placer l'image (ex. `docs/resultat.png`).
  3. Décommenter la ligne ci-dessous.
-->
<!-- ![Résultat de l'exécution](docs/resultat.png) -->

---

## Concepts clés mis en œuvre

- **Couplage faible** via des interfaces (`IDao`, `IMetier`).
- **Injection par setter** (`setDao`) plutôt que par champ.
- **Réflexion Java** : instanciation dynamique et appel de méthode dynamique.
- **Configuration externalisée** (fichier `config.txt`).

---

## Auteur

**Lahlali Mouad**
Dépôt GitHub : [lahlalimouad6-ux/TP1_Pd](https://github.com/lahlalimouad6-ux/TP1_Pd)
