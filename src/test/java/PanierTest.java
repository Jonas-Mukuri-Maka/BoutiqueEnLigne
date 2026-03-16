import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    //  Cas Invalides
    @Test 
    void quantiteNulleDoitLeverUneException() { 
        Panier panier = new Panier(); 
        Article article = new Article("REF-001", "Stylo", 1.50); 
        assertThrows(IllegalArgumentException.class, 
                () -> panier.ajouterArticle(article, 0)); 
    }

    @Test
    void articleNulDoitLeverException() {
        Panier panier = new Panier();
        assertThrows(IllegalArgumentException.class, 
            () -> panier.ajouterArticle(null, 0));
    }

    @Test
    void quantiteNegativeDoitLeverException() {
        Panier panier = new Panier(); 
        Article article = new Article("REF-001", "Stylo", 1.50); 
        assertThrows(IllegalArgumentException.class, 
                () -> panier.ajouterArticle(article, -3)); 
    }

    @Test
    void codeReductionVideDoitLeverException(){
        Panier panier = new Panier();
        assertThrows(IllegalArgumentException.class, 
                () -> panier.appliquerCodeReduction("")); 
    }

    @Test
    void codeReductionNulDoitLeverException(){
        Panier panier = new Panier();
        assertThrows(IllegalArgumentException.class, 
                () -> panier.appliquerCodeReduction(null)); 
    }

    //  Cas Limites
    @Test 
    void articleOfferDoitEtreAccepteEtNePasCompterDansLeTotal() { 
        Panier panier = new Panier(); 
        Article articleGratuit = new Article("OFFERT-01", "Stylo offert", 0.0); 
        panier.ajouterArticle(articleGratuit, 1); 
        assertEquals(0.0, panier.calculerTotal(), 0.001); 
    }

    @Test
    void quantiteUneDoitEtreAcceptee(){
        Panier panier = new Panier();
        Article article = new Article("Test", "un article", 9.99);
        panier.ajouterArticle(article, 1);
        assertEquals(9.99, panier.calculerTotal(),0.001);
    }

    @Test
    void prixEleveDoitFonctionner(){
        Panier panier = new Panier();
        Article article = new Article("Test", "un article eleve", 999.99);
        panier.ajouterArticle(article, 1);
        assertEquals(999.99, panier.calculerTotal(),0.001);
    }

    @Test
    void panierAvecUnSeulArticleDoitFonctionner(){
        Panier panier = new Panier();
        Article article = new Article("Test", "un article", 9.99);
        panier.ajouterArticle(article, 1);
        assertEquals(1, panier.nombreArticles());
    }

    @Test
    void plusieursArticlesDifferentsDansPanier(){
        Panier panier = new Panier();
        Article article1 = new Article("Test", "un article", 3.99);
        Article article2 = new Article("Test", "un autre article", 9.99);
        Article article3 = new Article("Test", "et encore un autre article", 15.99);
        panier.ajouterArticle(article1, 1);
        panier.ajouterArticle(article2, 1);
        panier.ajouterArticle(article3, 1);
        assertEquals(3, panier.nombreArticles());
    }
}