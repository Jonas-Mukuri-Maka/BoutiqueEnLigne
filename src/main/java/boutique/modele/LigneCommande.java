package boutique.modele;

public record LigneCommande(Article article, int quantite) { 
    public LigneCommande { 
        if (article == null) 
            throw new IllegalArgumentException("L'article ne peut pas être null"); 
        if (quantite <= 0) 
            throw new IllegalArgumentException("La quantité doit être positive"); 
    } 
 
    public double sousTotal() { 
        return article.getPrix() * quantite; 
    } 
}