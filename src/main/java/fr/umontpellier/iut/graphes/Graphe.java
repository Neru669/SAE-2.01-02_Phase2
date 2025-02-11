package fr.umontpellier.iut.graphes;

import fr.umontpellier.iut.trains.Jeu;
import fr.umontpellier.iut.trains.Joueur;
import fr.umontpellier.iut.trains.plateau.Tuile;

import java.util.*;

/**
 * Graphe simple non-orienté pondéré représentant le plateau du jeu.
 * Pour simplifier, on supposera que le graphe sans sommets est le graphe vide.
 * Le poids de chaque sommet correspond au coût de pose d'un rail sur la tuile correspondante.
 * Les sommets sont indexés par des entiers (pas nécessairement consécutifs).
 */

public class Graphe {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graphe graphe = (Graphe) o;
        /*
        Graphe g = (Graphe) o;
        List<Sommet> sommetsDeG = new ArrayList<>(graphe.getSommets());
        List<Sommet> listDeVoisinDeG = new ArrayList<>(sommetsDeG.get(0).getVoisins());
        List<Sommet> sommetsDeThis = new ArrayList<>(this.getSommets());
        List<Sommet> listDeVoisindeThis = new ArrayList<>(sommetsDeThis.get(0).getVoisins());
        boolean bon = true;
        for (int i = 0; i < graphe.getNbSommets(); i++){
            if (!(listDeVoisindeThis.isEmpty()) && listDeVoisinDeG.isEmpty()){
                for (int y = 0; y < listDeVoisindeThis.size() ; y++){
                    if (!sommetsDeThis.get(i).estVoisin(listDeVoisindeThis.get(y))){
                        bon = false;
                    }
                }
                i++;
            } else {
                return false;
            }
        }
        */
        return Objects.equals(sommets, graphe.sommets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sommets);
    }

    private final Set<Sommet> sommets;

    public Graphe(Set<Sommet> sommets) {
        this.sommets = sommets;
    }

    public Graphe(Jeu jeu){
        this.sommets = new HashSet<>();
        for (Tuile t : jeu.getTuiles()){
            if (!t.estMer()){
                this.sommets.add(new Sommet(t, jeu));
            }
        }
        for (Tuile tuile : jeu.getTuiles()){
            if (!tuile.estMer()){
                for (Tuile voisin : tuile.getVoisines()){
                    if (!voisin.estMer()){
                        this.ajouterArete(this.getSommet(jeu.getTuiles().indexOf(tuile)), this.getSommet(jeu.getTuiles().indexOf(voisin)));
                    }
                }
            }

        }
    }
    public Graphe(Jeu jeu, Joueur joueur){
        this.sommets = new HashSet<>();
        for (Tuile t : jeu.getTuiles()){
            if (t.hasRail(joueur) && !t.estMer()){
                this.sommets.add(new Sommet(t, jeu));
            }
        }
        for (Tuile tuile : jeu.getTuiles()){
            if (tuile.hasRail(joueur) && !tuile.estMer()){
                for (Tuile voisin : tuile.getVoisines()){
                    if (voisin.hasRail(joueur) && !voisin.estMer()){
                        this.ajouterArete(this.getSommet(jeu.getTuiles().indexOf(tuile)), this.getSommet(jeu.getTuiles().indexOf(voisin)));
                    }
                }
            }

        }
    }

    /**
     * Construit un graphe à n sommets 0..n-1 sans arêtes
     */
    public Graphe(int n) {
        sommets = new HashSet<>();
        for (int i = 0; i < n; i++){
            Sommet a = new Sommet.SommetBuilder().setIndice(i).setSurcout(0).setNbPointsVictoire(0).createSommet();
            sommets.add(a);
        }
    }

    public String toStringArete(){
        String s = "";
        Set<Set<Sommet>> aretes = getAretes();
        for (Set<Sommet> sommets : aretes){
            for (Sommet sommet : sommets){
                s += sommet.getIndice();
                s += ",";
            }
            s += ";\n";

        }
        return s;
    }

    /**
     * Construit un graphe vide
     */
    public Graphe() {
        this.sommets = new HashSet<>();
    }

    public Graphe(Graphe graphe){
        this.sommets = new HashSet<>();
        for (Sommet a : graphe.sommets){
            Sommet s = new Sommet.SommetBuilder().setIndice(a.getIndice()).setJoueurs(a.getJoueurs()).setSurcout(a.getSurcout()).setNbPointsVictoire(a.getNbPointsVictoire()).createSommet();
            sommets.add(s);
        }
        for (Sommet a : graphe.sommets) {
            for (Sommet voisin : a.getVoisins()) {
                graphe.ajouterArete(this.getSommet(a.getIndice()), this.getSommet(voisin.getIndice()));
            }
        }
    }

    /**
     * Construit un sous-graphe induit par un ensemble de sommets
     * sans modifier le graphe donné
     *
     * @param g le graphe à partir duquel on construit le sous-graphe
     * @param X les sommets à considérer (on peut supposer que X est inclus dans l'ensemble des sommets de g,
     *          même si en principe ce n'est pas obligatoire)
     */
    public Graphe(Graphe g, Set<Sommet> X) {
        Set<Sommet> sommetAEnlever = new HashSet<>();
        for (Sommet s : g.sommets){
            if (!X.contains(s)){
                sommetAEnlever.add(s);
            }
        }
        Graphe gInduit = new Graphe(g);
        for (Sommet s : sommetAEnlever){
            gInduit.supprimerSommet(gInduit.getSommet(s.getIndice())); // gInduit.supprimerSommet(s);
        }
        this.sommets = gInduit.getSommets();
    }

    /**
     * @return true si et seulement si la séquence d'entiers passée en paramètre
     * correspond à un graphe simple valide dont les degrés correspondent aux éléments de la liste.
     * Pré-requis : on peut supposer que la séquence est triée dans l'ordre croissant.
     */
    public static boolean sequenceEstGraphe(List<Integer> sequence) {
        double sum = 0;
        int max = 0;
        List<Integer> sequenceCopie = new ArrayList<>(sequence);
        for (Integer x :sequence){
            if (x == 0){
                sequenceCopie.remove(x);
            }
        }
        for (Integer x : sequenceCopie){
            sum += x;
            if (x >= sequenceCopie.size()) return false;
            if (x > max) max = x;
        }
        sum = sum/2;
        if ((int) sum != sum) return false;


        return (int) sum == sum;
    }

    /**
     * @param g        le graphe source, qui ne doit pas être modifié
     * @param ensemble un ensemble de sommets
     *                 pré-requis : l'ensemble donné est inclus dans l'ensemble des sommets de {@code g}
     * @return un nouveau graph obtenu en fusionnant les sommets de l'ensemble donné.
     * On remplacera l'ensemble de sommets par un seul sommet qui aura comme indice
     * le minimum des indices des sommets de l'ensemble. Le surcout du nouveau sommet sera
     * la somme des surcouts des sommets fusionnés. Le nombre de points de victoire du nouveau sommet
     * sera la somme des nombres de points de victoire des sommets fusionnés.
     * L'ensemble de joueurs du nouveau sommet sera l'union des ensembles de joueurs des sommets fusionnés.
     */
    public static Graphe fusionnerEnsembleSommets(Graphe g, Set<Sommet> ensemble) {
        Graphe gFusion = new Graphe(g);
        for (Sommet t : ensemble){ // gFusion a mtn les sommets qui ne sont pas en ensemble MAIS avec les arêtes des sommets supprimés
            gFusion.sommets.remove(t);
        }

        fusionnerSommets(ensemble, g); // calcul pour le sommet de l'ensemble

        gFusion.sommets.add(ensemble.iterator().next()); // rajoute le sommet fusionné à gFusion + rattache

        return gFusion;
    }

    private static void fusionnerSommets(Set<Sommet> ensemble, Graphe g){
        List<Sommet> sommetEnsemble = new ArrayList<>(ensemble); // conversion de Set en List pour pouvoir trier
        int surcout = 0; // calcul du surcout total
        int nbPoints = 0; // calcul du nbPointVictoire total
        Set<Integer> joueurs = new HashSet<>();
        for (Sommet s : ensemble){
            surcout += s.getSurcout();
            nbPoints += s.getNbPointsVictoire();
            joueurs.addAll(s.getJoueurs()); //no repeat puisque HashSet<>
        }

        Sommet minimum = new Sommet.SommetBuilder().setIndice(sommetEnsemble.get(0).getIndice()).
                setJoueurs(joueurs).setSurcout(surcout).setNbPointsVictoire(nbPoints).createSommet();

        g.rebrancherSommets(ensemble, minimum);

        ensemble.clear();
        ensemble.add(minimum);
        sommetEnsemble.sort(new PlusPetitSommet());
    }

    private void rebrancherSommets(Set<Sommet> ensemble, Sommet minimum){
        for (Sommet t : this.sommets){
            if (t.getVoisins().containsAll(ensemble)){
                t.getVoisins().removeAll(ensemble);
                t.ajouterVoisin(minimum);
            }
        }
    }

    /**
     * @param i un entier
     * @return le sommet d'indice {@code i} dans le graphe ou null si le sommet d'indice {@code i} n'existe pas dans this
     */
    public Sommet getSommet(int i) {
        for (Sommet s : sommets) {
            if (s.getIndice() == i) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return l'ensemble des sommets du graphe
     */
    public Set<Sommet> getSommets() {
        return sommets;
    }

    /**
     * @return l'ordre du graphe, c'est-à-dire le nombre de sommets
     */
    public int getNbSommets() {
        return sommets.size();
    }

    /**
     * @return l'ensemble d'arêtes du graphe sous forme d'ensemble de paires de sommets
     */
    public Set<Set<Sommet>> getAretes() {
        Set<Set<Sommet>> aretes = new HashSet<>();
        for (Sommet s : sommets){
            Set<Sommet> voisins = s.getVoisins();
            for (Sommet voisin : voisins){
                Set<Sommet> arr = new HashSet<>();
                arr.add(s);
                arr.add(voisin);
                aretes.add(arr);
            }
        }

        return aretes;
    }

    /**
     * @return le nombre d'arêtes du graphe
     */
    public int getNbAretes() {
        Set<Set<Sommet>> aretes = getAretes();
        return aretes.size();
    }

    /**
     * Ajoute un sommet d'indice i au graphe s'il n'est pas déjà présent
     *
     * @param i l'entier correspondant à l'indice du sommet à ajouter dans le graphe
     */
    public boolean ajouterSommet(int i) {
        Sommet sommet = new Sommet.SommetBuilder().setIndice(i).setSurcout(0).setNbPointsVictoire(0).createSommet();
        if (!sommets.contains(sommet)){
            sommets.add(sommet);
            return true;
        }
        return false;
    }

    /**
     * Ajoute un sommet au graphe s'il n'est pas déjà présent
     *
     * @param s le sommet à ajouter
     * @return true si le sommet a été ajouté, false sinon
     */
    public boolean ajouterSommet(Sommet s) {
        if (s == null) return false;
        return sommets.add(s);
    }

    /**
     * @param s le sommet dont on veut connaître le degré
     *          pré-requis : {@code s} est un sommet de this
     * @return le degré du sommet {@code s}
     */
    public int degre(Sommet s) {
        return s.getVoisins().size();
    }

    /**
     * @return true si et seulement si this est complet.
     */
    public boolean estComplet() {
        int ordre = this.sommets.size();
        int tailleMax = ordre*(ordre-1)/2;
        return tailleMax == getNbAretes();
    }

    /**
     * @return true si et seulement si this est une chaîne. On considère que le graphe vide est une chaîne.
     */
    public boolean estChaine() {
        if (sommets.size() < 2) return true;
        Sommet a = null;
        int i = 0;

        for (Sommet s : sommets){
            if (s.getVoisins().size() > 2 || s.getVoisins().size() < 1){
                return false;
            }
            else if (s.getVoisins().size() == 1){
                if (a == null) {
                    a = s;
                    i++;
                } // 1er sommet de degré 1
                else if (i < 2) i++; // 2d sommet de degré 1
                else return false; // s'il y a 3 sommets de degré 1
            }
        }
        if (i < 2) return false;
        // le graphe a donc 2 sommets de degré 1 et que des sommets de degré 2
        Set<Sommet> sommetDejaVisite = new HashSet<>();
        Sommet sommetCourant = a;
        sommetDejaVisite.add(sommetCourant);
        boolean check = false;
        while (sommetDejaVisite.size() < sommets.size()){
            check = false;
            for (Sommet voisin : sommetCourant.getVoisins()){
                if (!sommetDejaVisite.contains(voisin)){
                    sommetDejaVisite.add(voisin);
                    sommetCourant = voisin;
                    check = true;
                }
            }
            if (!check) return false;
        }
        return true;
    }

    /**
     * @return true si et seulement si this est un cycle. On considère que le graphe vide n'est pas un cycle.
     */
    public boolean estCycle() {
        if (sommets.size() < 3) return false;
        if (getNbAretes() != getNbSommets()) return false;
        if (!estConnexe()) return false;
        Set<Sommet> sommet = new HashSet<>(sommets);
        for (Sommet res : sommet){
            if (res.getVoisins().size() != 2){
                return false;
            }
        }
        return true;
    }

    /**
     * @return true si et seulement si this est une forêt. On considère qu'un arbre est une forêt
     * et que le graphe vide est un arbre.
     */
    public boolean estForet() {
        if (this.sommets.isEmpty()) return true;
        return estConnexe() && !possedeUnCycle();
    }

    /**
     * @return true si et seulement si this a au moins un cycle. On considère que le graphe vide n'est pas un cycle.
     */
    public boolean possedeUnCycle() {
        if (sommets.size() < 3) return false;
        Graphe g = eplucherDegres(1);
        return !(g.sommets.isEmpty());
    }

    private Graphe eplucherDegres(int n){ //enlève tous les sommets de degré 1
        Graphe g = new Graphe(this);
        boolean check = false;
        while (!check){
            check = true;
            for (Sommet s : sommets){
                if (g.sommets.contains(s) && degre(s) <= n){
                    g.supprimerSommet(s);
                    check = false;
                }
            }
        }
        return g;
    }

    /**
     * @return true si et seulement si this a un isthme
     */
    public boolean possedeUnIsthme() {
        Set<Graphe> graphes = this.separerEnPlusieursGrapheSelonComposantesConnexes();
        for (Graphe g : graphes){
            for (Sommet s : g.sommets){ // méthode non optimisée à voir si assez de temps
                if (this.testerSommetTousSesVoisins(s)) return true;
            }
        }
        return false;
    }

    private Set<Graphe> separerEnPlusieursGrapheSelonComposantesConnexes(){
        Set<Set<Sommet>> ss = this.getEnsembleClassesConnexite();
        Set<Graphe> graphes = new HashSet<>();
        for (Set<Sommet> sommetSet : ss){
            Graphe g = new Graphe(sommetSet);
            graphes.add(g);
        }
        return graphes;
    }

    private boolean testerSommetTousSesVoisins(Sommet s){
        Graphe g = new Graphe(this);
        Set<Sommet> voisins = new HashSet<>(g.getSommet(s.getIndice()).getVoisins());
        for (Sommet voisin : voisins){
            g.supprimerArete(s, voisin);
            if (!g.estConnexe()){
                return true;
            }
            g.ajouterArete(s, voisin);
        }
        return false;
    }

    public void ajouterArete(Sommet s, Sommet t) {
        if (s != null && t != null && (t != s) && (sommets.contains(s) && sommets.contains(t))) {
            s.ajouterVoisin(t);
            t.ajouterVoisin(s);
        }
    }

    public void supprimerArete(Sommet s, Sommet t) {
        s.getVoisins().remove(t);
        t.getVoisins().remove(s);
    }
    public void supprimerSommet(Sommet s){
        Set<Sommet> voisins = s.getVoisins();
        for (Sommet v : voisins){
            v.getVoisins().remove(s);
        }
        sommets.remove(s);
    }

    /**
     * @return une coloration gloutonne du graphe sous forme d'une Map d'ensemble indépendants de sommets.
     * L'ordre de coloration des sommets est suivant l'ordre décroissant des degrés des sommets
     * (si deux sommets ont le même degré, alors on les ordonne par indice croissant).
     */
    public Map<Integer, Set<Sommet>> getColorationGloutonne() {
        List<Integer> couleurs = this.creerCouleur();
        List<Sommet> ordre = new ArrayList<>(this.getSommets());
        ordre.sort(new ClasserSelonDegre());

        Map<Integer, Set<Sommet>> colorationGloutonne = new HashMap<>();
        Map<Sommet, Integer> coloration = new HashMap<>();
        Set<Integer> couleursMises = new HashSet<>();

        for (Sommet s : ordre){
            List<Integer> couleursPossibles = new ArrayList<>(couleurs);
            Set<Sommet> voisins = s.getVoisins();
            for (Sommet voisin : voisins){
                if (coloration.containsKey(voisin)){
                    couleursPossibles.remove(coloration.get(voisin));
                }
            }
            coloration.put(s, couleursPossibles.get(0));
            couleursMises.add(couleursPossibles.get(0));
        }

        this.ajouterColorationDansMap(couleursMises, coloration, colorationGloutonne);

        return colorationGloutonne;
    }

    public void ajouterColorationDansMap(Set<Integer> couleursMises, Map<Sommet, Integer> coloration, Map<Integer, Set<Sommet>> colorationGloutonne){
        for (Integer i : couleursMises){
            Set<Sommet> sommetSet = new HashSet<>();
            for (Map.Entry<Sommet, Integer> entry : coloration.entrySet()){
                if (Objects.equals(entry.getValue(), i)){
                    sommetSet.add(entry.getKey());
                }
            }
            colorationGloutonne.put(i, sommetSet);
        }
    }

    public List<Integer> creerCouleur(){
        List<Integer> couleurs = new ArrayList<>();
        for (int i = 0; i < this.degreMax() + 1; i++){ // création des couleurs
            couleurs.add(i);
        }
        return couleurs;
    }

    /**
     * @param depart  - ensemble non-vide de sommets
     * @param arrivee
     * @return le surcout total minimal du parcours entre l'ensemble de depart et le sommet d'arrivée
     * pré-requis : l'ensemble de départ et le sommet d'arrivée sont inclus dans l'ensemble des sommets de this
     */
    public int getDistance(Set<Sommet> depart, Sommet arrivee) {
        int distance = -1;
        for (Sommet s : depart){
            int distance2 = getDistance(s, arrivee);
            if (distance == -1 || distance2 < distance){
                distance = distance2;
            }
        }
        return distance;
    }

    /**
     * @return le surcout total minimal du parcours entre le sommet de depart et le sommet d'arrivée
     */
    public int getDistance(Sommet depart, Sommet arrivee) {
        boolean check = false;
        List<List<Sommet>> possiblites = new ArrayList<>();
        List<Sommet> deb = new ArrayList<>();
        List<Sommet> sommetsEnleves = new ArrayList<>();
        deb.add(depart);
        possiblites.add(deb);
        List<Sommet> derniereList = new ArrayList<>();
        Sommet sommetCourant = depart;
        while (!sommetCourant.equals(arrivee)){
            sommetsEnleves.add(sommetCourant);
            Set<Sommet> voisins = sommetCourant.getVoisins();
            List<Sommet> lastList = findTheLastOne(sommetCourant, possiblites);
            if (lastList != null){
                for (Sommet s : voisins){
                    if (!sommetsEnleves.contains(s)) {
                        List<Sommet> liste = new ArrayList<>(lastList);
                        liste.add(s);
                        possiblites.add(liste);
                    }
                }
                possiblites.remove(lastList);
                List<Sommet> listLeastCostly = returnTheLeastCostly(possiblites);
                if (listLeastCostly == null){
                    return Integer.MAX_VALUE;
                }
                derniereList = listLeastCostly;
                sommetCourant = listLeastCostly.get(listLeastCostly.size()-1);
            }
            else {
                break;
            }
            if (sommetCourant.equals(arrivee)){
                check = true;
            }

        }

        if (check){
            int surcout = 0;

            for (Sommet s : derniereList){
                surcout += s.getSurcout();
            }
            surcout -= derniereList.get(0).getSurcout();

            return surcout;
        }
        else {
            return Integer.MAX_VALUE;
        }

    }

    private static List<Sommet> findTheLastOne(Sommet sommet, List<List<Sommet>> possiblites){
        for (List<Sommet> sommetList : possiblites){
            if (sommetList.get(sommetList.size() - 1).equals(sommet)){
                return sommetList;
            }
        }
        return null;
    }

    private static List<Sommet> returnTheLeastCostly(List<List<Sommet>> possibilites){
        List<Sommet> leastCostly = null;
        int surcout = -1;
        for (List<Sommet> ls : possibilites){
            int surcoutlist = 0;
            for (Sommet s : ls){
                surcoutlist += s.getSurcout();
            }
            if (surcout == -1 || surcout > surcoutlist){
                surcout = surcoutlist;
                leastCostly = ls;
            }
        }
        return leastCostly;
    }

    /**
     * @return l'ensemble des classes de connexité du graphe sous forme d'un ensemble d'ensembles de sommets.
     */
    public Set<Set<Sommet>> getEnsembleClassesConnexite() {
        Set<Set<Sommet>> ensembleClassesConnexite = new HashSet<>();
        if (sommets.isEmpty())
            return ensembleClassesConnexite;
        Set<Sommet> sommets = new HashSet<>(this.sommets);
        while (!sommets.isEmpty()) {
            Sommet v = sommets.iterator().next();
            Set<Sommet> classe = getClasseConnexite(v);
            sommets.removeAll(classe);
            ensembleClassesConnexite.add(classe);
        }
        return ensembleClassesConnexite;
    }

    /**
     * @param v un sommet du graphe this
     * @return la classe de connexité du sommet {@code v} sous forme d'un ensemble de sommets.
     */
    public Set<Sommet> getClasseConnexite(Sommet v) {
        if (!sommets.contains(v))
            return new HashSet<>();
        Set<Sommet> classe = new HashSet<>();
        calculerClasseConnexite(v, classe);
        return classe;
    }

    private void calculerClasseConnexite(Sommet v, Set<Sommet> dejaVus) {
        dejaVus.add(v);
        Set<Sommet> voisins = v.getVoisins();

        for (Sommet voisin : voisins) {
            if (dejaVus.add(voisin))
                calculerClasseConnexite(voisin, dejaVus);
        }
    }

    /**
     * @return true si et seulement si this est connexe.
     */
    public boolean estConnexe() {
        return getClasseConnexite(getSommet(0)).equals(sommets);
    }

    /**
     * @return le degré maximum des sommets du graphe
     */
    public int degreMax() {
        int degreMax = 0;
        for (Sommet s : sommets){
            if (s.getVoisins().size() > degreMax){
                degreMax = s.getVoisins().size();
            }
        }
        return degreMax;
    }

    public int degreMin() {
        int degreMin = degre(getSommet(0));
        for (Sommet s : sommets){
            if (degre(s) < degreMin){
                degreMin = s.getVoisins().size();
            }
        }
        return degreMin;
    }

    /**
     * @return une coloration propre optimale du graphe sous forme d'une Map d'ensemble indépendants de sommets.
     * Chaque classe de couleur est représentée par un entier (la clé de la Map).
     * Pré-requis : le graphe est issu du plateau du jeu Train (entre autres, il est planaire).
     */
    public Map<Integer, Set<Sommet>> getColorationPropreOptimale() {
        Map<Sommet, Integer> coloration = new HashMap<>();
        List<Integer> couleurs = creerCouleur();
        coloration.put(sommetContenuDansUnTriangle(), 0);
        while (coloration.size() < this.getNbSommets()){
            List<Sommet> sommetsARajouter = sommetsARajouter(coloration);
            if (!sommetsARajouter.isEmpty()){
                for (Sommet s : sommetsARajouter){
                    colorierSommet(s, coloration, couleurs);
                }
            }
            else {
                for (Sommet s : this.getSommets()){
                    if (!coloration.containsKey(s)){
                        colorierSommet(s, coloration, couleurs);
                    }
                }
            }
        }

        Map<Integer, Set<Sommet>> colorationFinale = new HashMap<>();
        Set<Integer> couleursMises = new HashSet<>();
        for (Map.Entry<Sommet, Integer> entry : coloration.entrySet()){
            couleursMises.add(entry.getValue());
        }
        this.ajouterColorationDansMap(couleursMises, coloration, colorationFinale);

        return colorationFinale;
    }

    public Sommet sommetContenuDansUnTriangle(){
        for (Sommet sommet : sommets){
            if (sommet.getVoisins().size() >= 6) {
                return sommet;
            }
        }
        return sommets.iterator().next();
    }

    public List<Sommet> sommetsARajouter(Map<Sommet, Integer> coloration){
        List<Sommet> sommetsARajouter = new ArrayList<>();
        for (Map.Entry<Sommet, Integer> entry : coloration.entrySet()){
            for (Sommet s : entry.getKey().getVoisins()){
                if (!coloration.containsKey(s) && verifierSommetPlusOuEgalDeXVoisinsColores(s, coloration, 2)){
                    sommetsARajouter.add(s);
                }
            }
        }

        if (sommetsARajouter.isEmpty()){
            for (Map.Entry<Sommet, Integer> entry : coloration.entrySet()){
                for (Sommet s : entry.getKey().getVoisins()){
                    if (!coloration.containsKey(s)){
                        sommetsARajouter.add(s);
                        return sommetsARajouter;
                    }
                }
            }
        }

        return  sommetsARajouter;
    }

    public boolean verifierSommetPlusOuEgalDeXVoisinsColores(Sommet sommet, Map<Sommet, Integer> coloration, int X){
        int i = 0;
        for (Sommet voisin : sommet.getVoisins()){
            if (coloration.containsKey(voisin)){
                i++;
            }
        }
        return i >= 2;
    }

    public void enleverSommetsDontTousLesVoisinsSontColores(Map<Sommet, Integer> sommetsColores, List<Sommet> sommetsVoisinsNonColores){
        for (Map.Entry<Sommet, Integer> entry : sommetsColores.entrySet()){
            int size = entry.getKey().getVoisins().size();
            int i = 0;
            for (Sommet v : entry.getKey().getVoisins()){
                if (sommetsColores.containsKey(v)) {
                    i++;
                }
            }
            if (i < size){
                sommetsVoisinsNonColores.add(entry.getKey());
            }
        }
    }


    public void colorierSommet(Sommet sommet, Map<Sommet, Integer> sommetsColores, List<Integer> couleurs){
        List<Integer> couleursPrises = new ArrayList<>();
        for (Sommet voisin : sommet.getVoisins()){
            if (sommetsColores.containsKey(voisin)){
                couleursPrises.add(sommetsColores.get(voisin));
            }
        }
        for (Integer couleur : couleurs){
            if (!couleursPrises.contains(couleur)){
                sommetsColores.put(sommet, couleur);
                break;
            }
        }
    }

    public List<Sommet> sommetsPossibleColorier(Map<Sommet, Integer> sommetsColores, List<Sommet> sommetsVoisinsNonColores, int degre){
        List<Sommet> sommetsPossibles = new ArrayList<>();
        if (sommetsVoisinsNonColores.size() == 0){
            sommetsPossibles = new ArrayList<>(this.getSommets());
            return sommetsPossibles;
        }
        else {
            for (Sommet s : sommetsVoisinsNonColores){
                for (Sommet voisin : s.getVoisins()){
                    Set<Sommet> voisins = s.getVoisins();
                    if (degre == 1){
                        if (!sommetsColores.containsKey(voisin)) sommetsPossibles.add(voisin);
                    }
                    else {
                        for (Sommet v : voisin.getVoisins()){
                            if (this.verifierSiSommetAAuMoinsXSommetsColores(v, sommetsColores, degre)){
                                if (!sommetsColores.containsKey(voisin)) sommetsPossibles.add(voisin);
                            }
                        }
                    }
                }
            }
        }

        return sommetsPossibles;
    }

    public boolean verifierSiSommetAAuMoinsXSommetsColores(Sommet sommet, Map<Sommet, Integer> sommetsColores, int X){
        int i = 0;
        for (Sommet s : sommet.getVoisins()){
            if (sommetsColores.containsKey(s)){
                i++;
            }
        }
        return i >= X;
    }

    /**
     * @return true si et seulement si this possède un sous-graphe complet d'ordre {@code k}
     */
    public boolean possedeSousGrapheComplet(int k) {
        if (getNbSommets() == k && this.estComplet()) {
            return true;
        }
        for (Sommet s : sommets) {
            Set<Sommet> sousGraphePotentiel = new HashSet<>();
            sousGraphePotentiel.add(s);
            for (Sommet voisin : s.getVoisins()) {
                Set<Sommet> voisinsSimilaires = listeDeVoisinsVoisins(voisin, k-2, false, null, new HashSet<>());
                if (voisinsSimilaires.contains(s)) {
                    sousGraphePotentiel.add(voisin);
                    if (sousGraphePotentiel.size() == k) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public Set<Sommet> listeDeVoisinsVoisins(Sommet SommetTester, int nbVoisinSimilaireAttendu, boolean commencer, Sommet premierVoisinBon, Set<Sommet> visited) {
    int conteurVoisins = 0;
    Set<Sommet> sousGrapheEventuel = new HashSet<>();
    Set<Sommet> voisinsDuSommetTester = SommetTester.getVoisins();
    if (commencer){
        voisinsDuSommetTester.remove(premierVoisinBon);
    }
    for (Sommet voisinDuSommetTester : voisinsDuSommetTester) {
        if (!visited.contains(voisinDuSommetTester)) {
            visited.add(voisinDuSommetTester);
            for (Sommet voisinDuVoisin : voisinsDuSommetTester) {
                if (voisinDuSommetTester.estVoisin(voisinDuVoisin)) {
                    conteurVoisins++;
                    if (nbVoisinSimilaireAttendu == conteurVoisins) {
                        sousGrapheEventuel.add(voisinDuSommetTester);
                        listeDeVoisinsVoisins(voisinDuSommetTester, nbVoisinSimilaireAttendu, true, voisinDuSommetTester, visited);
                    }
                }
            }
            conteurVoisins = 0;
        }
    }
    return sousGrapheEventuel;
}

    /**
     * @param g un graphe
     * @return true si et seulement si this possède un sous-graphe isomorphe à {@code g}
     */
    public boolean possedeSousGrapheIsomorphe(Graphe g) {
        int nbSommetDeG = g.getNbSommets();
        int nbSommetATester = this.getNbSommets() - nbSommetDeG;

        //Set<Sommet> sommetsDeThis = this.sommets;
        //Set<Sommet> sommetsDeG = g.getSommets();
        int[][] matriceDeG = g.genererMatrice();
        Set<Set<Sommet>> sequenceDejaVue = new HashSet<>();
        boolean sequenceValide = false;
        Graphe test = null;
        do {
            test = new Graphe(this);
            sequenceValide = test.genererSequence(nbSommetATester, sequenceDejaVue, g);
            if (sequenceValide) {
                sequenceValide = comparerMatrices(matriceDeG, test.genererMatrice());
            }
            if (!sequenceValide) {
                sequenceDejaVue.add(test.getSommets());
            }
        } while (!sequenceValide);

        return sequenceValide;
    }


    public boolean genererSequence(int nombreASupprimer, Set<Set<Sommet>> sequenceDejaVue, Graphe graphe){
        Random random = new Random();
        List<Sommet> listeSommets = new ArrayList<>(this.sommets);

        for (int i = 0; i < nombreASupprimer; i++) {
            if (!listeSommets.isEmpty()) {
                int indexAleatoire = random.nextInt(listeSommets.size());
                Sommet sommetASupprimer = listeSommets.get(indexAleatoire);
                this.supprimerSommet(sommetASupprimer);
                listeSommets.remove(indexAleatoire);
            } else {
                break;
            }
        }
        if (degreMin() < graphe.degreMin() || degreMax() < graphe.degreMax()){
            return false;
        }
        if (!sequenceDejaVue.contains(this.sommets)) {
            return true;
        }
        return false;
    }

    public int[][] genererMatrice() {
        int taille = this.getNbSommets();
        int[][] matrice = new int[taille][taille];

        for (Sommet s : this.sommets) {
            for (Sommet voisin : s.getVoisins()) {
                matrice[s.getIndice()][voisin.getIndice()] = 1;
            }
        }

        return matrice;
    }

        public boolean comparerMatrices(int[][] matriceG ,int[][] matriceTest) {
            if (matriceG.length != matriceTest.length || matriceG[0].length != matriceTest[0].length) {
                return false;
            }
            for (int i = 0; i < matriceG.length; i++) {
                for (int j = 0; j < matriceG[i].length; j++) {
                    if (matriceG[i][j] > matriceTest[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        }



    /**
     * @param s
     * @param t
     * @return un ensemble de sommets qui forme un ensemble critique de plus petite taille entre {@code s} et {@code t}
     */
    public Set<Sommet> getEnsembleCritique(Sommet s, Sommet t){
        Set<Sommet> ensembleCritique = new HashSet<>();
        if (!getClasseConnexite(s).equals(getClasseConnexite(t))) return ensembleCritique;

        Graphe g = new Graphe(getClasseConnexite(s));
        if (g.estCycle() || g.estChaine()){
            ensembleCritique.add(this.getSommets().iterator().next());
            return ensembleCritique;
        }
        else if (t.getVoisins().size() == 1){
            ensembleCritique.add(t.getVoisins().iterator().next());
            return ensembleCritique;
        }
        else if (s.getVoisins().size() == 1){
            ensembleCritique.add(s.getVoisins().iterator().next());
            return ensembleCritique;
        }
        else if (g.estComplet()){
            for (Sommet sommet : g.getSommets()) {
                if (!sommet.equals(s) || !sommet.equals(t)) ensembleCritique.add(sommet);
            }
            return ensembleCritique;
        }
        List<List<Sommet>> possibilites = new ArrayList<>();
        List<Sommet> sommetsUtilises = new ArrayList<>();
        List<Sommet> sommetsRestants = new ArrayList<>(this.sommets);

        // si 1 seul degré
        Graphe gClone = new Graphe(g);
        Sommet sommet = gClone.eplucherDegresV2(s, t, 1);
        if (sommet != null){
            gClone.getSommets().remove(sommet);
            if (!gClone.getClasseConnexite(s).equals(gClone.getClasseConnexite(t))){
                ensembleCritique.add(sommet);
                return ensembleCritique;
            }
        }


        //si pas trouvé le code à temps :
        int nbVoisinsS = s.getVoisins().size();
        int nbVoisinsT = t.getVoisins().size();
        if (nbVoisinsS >= nbVoisinsT){
            ensembleCritique.addAll(s.getVoisins());
        }
        else {
            ensembleCritique.addAll(s.getVoisins());
        }

        return ensembleCritique;
    }

    private Sommet eplucherDegresV2(Sommet s, Sommet t, int n){ //enlève tous les sommets de degré 1
        Graphe g = new Graphe(this);
        Sommet courant = null;
        boolean check = false;
        while (!check){
            check = true;
            for (Sommet sommet : sommets){
                courant = sommet;
                if (g.sommets.contains(sommet) && degre(sommet) <= n){
                    g.supprimerSommet(sommet);
                    if (!g.getClasseConnexite(s).equals(g.getClasseConnexite(t))){
                        return courant;
                    }
                    check = false;
                }
            }
        }
        return null;
    }
}
