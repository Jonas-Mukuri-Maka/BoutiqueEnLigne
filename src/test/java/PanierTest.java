import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import boutique.modele.Article;
import boutique.modele.Panier; 
 
class PanierTest { 
 
    @Test 
    void ajouterArticleDeitAugmenterLeNombreDeArticles() { 
        // Arranger 
        Panier panier = new Panier(); 
        Article article = new Article("REF-001", "Stylo bleu", 1.50); 
 
        // Agir 
        panier.ajouterArticle(article, 2); 
 
        // Affirmer 
        assertEquals(1, panier.nombreArticles()); 
    }
    
    @Test
    void calculerTotalDoitRetournerLaSommeDessousTotaux(){

        Panier panier = new Panier(); 
        Article article1 = new Article("REF-001", "Stylo bleu", 1.50);
        Article article2 = new Article("REF-002", "Stylo rouge", 1.50);

        panier.ajouterArticle(article1, 3);
        panier.ajouterArticle(article2, 3);

        assertEquals(9.00, panier.calculerTotal());
    }

    @Test
    void panierVideDoitRetournerEstVideEgalTrue(){
        Panier panier = new Panier();

        assertTrue(panier.estVide());
    }

    @Test
    void panierAvecArticlesNeDoitPasEtreVide(){
        Panier panier = new Panier();
        Article article = new Article("REF-003", "Stylo jaune", 1.50);

        panier.ajouterArticle(article, 5);

        assertFalse(panier.estVide());
    }
}