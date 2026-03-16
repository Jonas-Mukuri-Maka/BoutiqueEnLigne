import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import boutique.modele.Article;
import boutique.modele.Panier; 
 
class PanierReductionTest { 
 
    @ParameterizedTest 
    @CsvSource({ 
        "       ,  100.0",   // pas de code de réduction 
        "REDUC10,   90.0",   // -10% 
        "REDUC20,   80.0"    // -20% 
    }) 
    void calculerTotalDoitAppliquerLaBonneReduction( 
            String code, double totalAttendu) { 
        // Arranger 
        Panier panier = new Panier(); 
        Article article = new Article("REF-001", "Classeur", 10.0); 
        panier.ajouterArticle(article, 10); // sous-total = 100.0 
 
        // Agir 
        if (code != null && !code.isBlank()) { 
            panier.appliquerCodeReduction(code.trim()); 
        } 
 
        // Affirmer 
        assertEquals(totalAttendu, panier.calculerTotal(), 0.001); 
    } 

    @ParameterizedTest
    @CsvSource({
        "       ,  50.0",    // pas de code
        "REDUC10,  45.0",    // -10%
        "REDUC20,  40.0"     // -20%
    })
    void calculerTotalPlusieursArticlesDoitAppliquerLaBonneReduction(
            String code, double totalAttendu) {
        // Arranger
        Panier panier = new Panier();
        panier.ajouterArticle(new Article("REF-001", "Classeur", 10.0), 3);
        panier.ajouterArticle(new Article("REF-002", "Stylo",     5.0), 4);
        

        // Agir
        if (code != null && !code.isBlank()) {
            panier.appliquerCodeReduction(code.trim());
        }

        // Affirmer
        assertEquals(totalAttendu, panier.calculerTotal(), 0.001);
    }

    @ParameterizedTest
    @CsvSource({
        "      , Cahier,  1.0, 1",    // référence est vide
        "REF-01,       ,  1.0, 1",    // nom est vide
        "REF-01, Cahier, -1.0, 1",    // le prix est négative
        "REF-01, Cahier,  1.0, 0",    // quantité = 0
        "REF-01, Cahier,  1.0, -3"    // quantité est négative
    })
    void ajouterArticleInvalidDoitLeverIllegalArgumentException(
            String reference, String nom, double prix, int quantite) {
        Panier panier = new Panier();
        assertThrows(IllegalArgumentException.class, () -> {
            Article article = new Article(reference, nom, prix);
            panier.ajouterArticle(article, quantite);
        });
    }
} 