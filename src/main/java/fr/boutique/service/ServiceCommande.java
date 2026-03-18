package fr.boutique.service;

import java.time.LocalDateTime;

import fr.boutique.exception.StockInsuffisantException;
import fr.boutique.modele.Commande;
import fr.boutique.modele.LigneCommande;
import fr.boutique.modele.Panier;

public class ServiceCommande { 
    private final DepotStock depotStock; 
 
    public ServiceCommande(DepotStock depotStock) { 
        this.depotStock = depotStock; 
    } 
 
    public Commande passerCommande(Panier panier, String identifiantClient) { 
        if (panier.estVide()) 
            throw new IllegalStateException("Impossible de commander : le panier est vide"); 
        if (identifiantClient == null || identifiantClient.isBlank()) 
            throw new IllegalArgumentException("L'identifiant client est invalide"); 
        for (LigneCommande ligne : panier.getLignes()) { 
            int stockDisponible = depotStock.getStock(ligne.article().getReference()); 
            if (stockDisponible < ligne.quantite()) 
                throw new StockInsuffisantException( 
                    "Stock insuffisant pour : " + ligne.article().getNom()); 
        } 
        return new Commande(identifiantClient, panier.calculerTotal(), LocalDateTime.now()); 
    } 
} 