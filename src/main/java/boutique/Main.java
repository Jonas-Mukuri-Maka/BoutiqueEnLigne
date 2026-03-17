package boutique;

import boutique.modele.Article;
import boutique.modele.Panier;


public class Main {
    public static void main(String[] args) {
        Panier panier = new Panier(); 
        Article article1 = new Article("REF-001", "Stylo bleu", 1.50);
        Article article2 = new Article("REF-002", "Stylo rouge", 1.50);

        panier.ajouterArticle(article1, 3);
        panier.ajouterArticle(article2, 3);

        System.out.println("Application started");
        try {
            Thread.sleep(1000_000); // sleep for ~17 minutes
        } catch (InterruptedException e) {
        }
    }
}
