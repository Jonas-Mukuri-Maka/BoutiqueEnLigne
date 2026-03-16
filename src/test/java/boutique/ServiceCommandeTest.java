package boutique;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import boutique.exception.StockInsuffisantException;
import boutique.modele.Article;
import boutique.modele.Commande;
import boutique.modele.Panier;
import boutique.service.DepotStock;
import boutique.service.ServiceCommande;

class ServiceCommandeTest { 
    private DepotStock stockDisponible = reference -> 100; 
    private ServiceCommande service; 
    private Panier panier; 
    private Article articleTest; 
 
    @BeforeEach 
    void initialiser() { 
        service     = new ServiceCommande(stockDisponible); 
        panier      = new Panier(); 
        articleTest = new Article("REF-001", "Cahier", 3.50); 
    } 
 
    @Test 
    void commandeValideDoitRetournerUneCommande() { 
        panier.ajouterArticle(articleTest, 2); 
        Commande commande = service.passerCommande(panier, "CLIENT-42"); 
        assertNotNull(commande); 
        assertEquals(7.0, commande.total(), 0.001); 
    } 

    @Test
    void panierVideDoitLeverIllegalStateException(){
        assertThrows(IllegalStateException.class,
        () -> service.passerCommande(panier, "CLIENT-42"));
    }

    @Test
    void identifiantClientNulDoitLeverException() {
        panier.ajouterArticle(articleTest, 1);
        assertThrows(IllegalArgumentException.class,
            () -> service.passerCommande(panier, null));
    }

    @Test
    void identifiantClientVideDoitLeverException() {
        panier.ajouterArticle(articleTest, 1);
        assertThrows(IllegalArgumentException.class,
            () -> service.passerCommande(panier, "   "));
    }

    @Test
    void stockInsuffisantDoitLeverStockInsuffisantException() {
        DepotStock stockLimite = reference -> 1;
        ServiceCommande serviceStockLimite = new ServiceCommande(stockLimite);
        panier.ajouterArticle(articleTest, 5);
        assertThrows(StockInsuffisantException.class,
            () -> serviceStockLimite.passerCommande(panier, "CLIENT-42"));
    }

    @Test
    void totalCommandeDoitCorrespondreAuTotalDuPanier() {
        panier.ajouterArticle(articleTest, 3);
        panier.ajouterArticle(new Article("REF-002", "Stylo", 1.50), 2);
        Commande commande = service.passerCommande(panier, "CLIENT-42");
        assertEquals(panier.calculerTotal(), commande.total(), 0.001);
    }
} 