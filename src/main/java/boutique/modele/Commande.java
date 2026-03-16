package boutique.modele;

import java.time.LocalDateTime;

public record Commande(String identifiantClient, double total, LocalDateTime dateCreation) {
    
} 