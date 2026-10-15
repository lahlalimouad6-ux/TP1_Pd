package org.example.presentation;

import org.example.dao.IDao;
import org.example.metier.IMetier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Presentation2 {

    public static void main(String[] args) {
        try {
            // 1. Lecture des classes definies dans le fichier config
            String[] classes = loadClassNames("config.txt");

            // 2. Creation de l'objet DAO
            IDao dao = (IDao) createInstance(classes[0]);
            System.out.println("DAO charge : "
                    + dao.getClass().getSimpleName());

            // 3. Creation de l'objet Metier
            IMetier metier = (IMetier) createInstance(classes[1]);
            System.out.println("Metier charge : "
                    + metier.getClass().getSimpleName());

            // 4. Injection du DAO dans le metier
            injectDependency(metier, "setDao", dao);

            // 5. Lancement du traitement
            double resultat = metier.calcul();
            System.out.println("Valeur du resultat : " + resultat);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'execution : "
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    // Recupere les noms des deux classes depuis le fichier
    private static String[] loadClassNames(String fileName)
            throws IOException {

        String[] nomsClasses = new String[2];

        try (BufferedReader br =
                     new BufferedReader(new FileReader(fileName))) {

            for (int i = 0; i < nomsClasses.length; i++) {
                String ligne = br.readLine();

                if (ligne == null || ligne.trim().isEmpty()) {
                    throw new IOException(
                            "Nom de classe manquant dans le fichier config");
                }

                nomsClasses[i] = ligne.trim();
            }
        }

        return nomsClasses;
    }

    // Cree un objet a partir du nom complet de sa classe
    private static Object createInstance(String nomClasse)
            throws Exception {

        Class<?> classe = Class.forName(nomClasse);
        Constructor<?> constructeur = classe.getDeclaredConstructor();

        return constructeur.newInstance();
    }

    // Affecte le DAO au metier en utilisant la reflexion
    private static void injectDependency(
            Object objet, String nomSetter, Object dao) throws Exception {

        Class<?> classe = objet.getClass();
        Class<?> typeDao = dao.getClass().getInterfaces()[0];

        Method methode = classe.getMethod(nomSetter, typeDao);
        methode.invoke(objet, dao);
    }
}