package fr.boutique.modele;

import java.util.ArrayList;
import java.util.List;

public class Panier { 
    private List<LigneCommande> lignes = new ArrayList<>(); 
    private String codeReduction = null; 
 
    public void ajouterArticle(Article article, int quantite) { 
        if (article == null) 
            throw new IllegalArgumentException("L'article ne peut pas être null"); 
        if (quantite <= 0) 
            throw new IllegalArgumentException("La quantité doit être positive"); 
        lignes.add(new LigneCommande(article, quantite)); 
    } 
 
    public void appliquerCodeReduction(String code) { 
        if (code == null || code.isBlank()) 
            throw new IllegalArgumentException("Le code de réduction est invalide"); 
        this.codeReduction = code; 
    } 
 
    public double calculerTotal() { 
        double sousTotal = lignes.stream() 
            .mapToDouble(LigneCommande::sousTotal) 
            .sum(); 
        if ("REDUC10".equals(codeReduction)) return sousTotal * 0.90; 
        if ("REDUC20".equals(codeReduction)) return sousTotal * 0.80; 
        return sousTotal; 
    } 
 
    public int nombreArticles()  { return lignes.size(); } 
    public boolean estVide()     { return lignes.isEmpty(); } 
    public List<LigneCommande> getLignes() { return lignes; } 
} 