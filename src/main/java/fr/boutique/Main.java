package fr.boutique;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import fr.boutique.exception.StockInsuffisantException;
import fr.boutique.modele.Article;
import fr.boutique.modele.Commande;
import fr.boutique.modele.Panier;
import fr.boutique.service.DepotStock;
import fr.boutique.service.ServiceCommande;

public class Main {

    static final Map<String, Article> catalogue = new LinkedHashMap<>();
    static final Map<String, Integer> stocks    = new LinkedHashMap<>();
    static final Panier panier                  = new Panier();
    static String codeReductionActif            = null;
    static Commande derniereCommande            = null;
    static String erreurCommande                = null;

    public static void main(String[] args) throws Exception {

        catalogue.put("REF-001", new Article("REF-001", "Stylo bleu",         1.50));
        catalogue.put("REF-002", new Article("REF-002", "Stylo rouge",        1.50));
        catalogue.put("REF-003", new Article("REF-003", "Cahier A4",          3.99));
        catalogue.put("REF-004", new Article("REF-004", "Crayons de couleur", 5.49));

        stocks.put("REF-001", 10);
        stocks.put("REF-002", 5);
        stocks.put("REF-003", 8);
        stocks.put("REF-004", 2);

        DepotStock depotStock = reference -> stocks.getOrDefault(reference, 0);
        ServiceCommande serviceCommande = new ServiceCommande(depotStock);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // ajouter un article
        server.createContext("/ajouter", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> p = parseBody(exchange);
                Article article = catalogue.get(p.get("ref"));
                int qte = Integer.parseInt(p.getOrDefault("quantite", "1"));
                if (article != null && qte > 0) panier.ajouterArticle(article, qte);
            }
            sendHtml(exchange, panierFragment());
        });

        // code de reduction
        server.createContext("/reduction", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> p = parseBody(exchange);
                String code = p.getOrDefault("code", "").trim();
                if (!code.isBlank()) {
                    try { panier.appliquerCodeReduction(code); codeReductionActif = code; }
                    catch (IllegalArgumentException e) { codeReductionActif = null; }
                }
            }
            sendHtml(exchange, panierFragment());
        });

        // lancer une commande
        server.createContext("/commander", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> p = parseBody(exchange);
                try {
                    derniereCommande = serviceCommande.passerCommande(panier, p.getOrDefault("client", ""));
                    erreurCommande   = null;
                } catch (StockInsuffisantException | IllegalStateException e) {
                    derniereCommande = null;
                    erreurCommande   = e.getMessage();
                }
            }
            sendHtml(exchange, commandeFragment());
        });

        // GET page principale
        server.createContext("/", exchange -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang='fr'><head>")
                .append("<meta charset='UTF-8'>")
                .append("<title>Boutique en ligne</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; background: #f5f5f5; }")
                .append("h1 { color: #2c3e50; } h2 { color: #34495e; border-bottom: 2px solid #3498db; padding-bottom: 6px; margin: 24px 0 12px; }")
                .append("table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; }")
                .append("th { background: #3498db; color: white; padding: 10px; text-align: left; }")
                .append("td { padding: 10px; border-bottom: 1px solid #eee; }")
                .append("tr:last-child td { border-bottom: none; }")
                .append(".total { font-size: 1.2em; font-weight: bold; color: #27ae60; }")
                .append(".success { background: #d4edda; border: 1px solid #c3e6cb; padding: 12px; border-radius: 6px; color: #155724; }")
                .append(".error   { background: #f8d7da; border: 1px solid #f5c6cb; padding: 12px; border-radius: 6px; color: #721c24; }")
                .append(".tag-reduc { background: #f39c12; color: #fff; padding: 2px 8px; border-radius: 4px; font-size: .85em; }")
                .append("input[type=number] { width: 58px; padding: 4px 6px; border: 1px solid #ccc; border-radius: 4px; }")
                .append("input[type=text]   { padding: 6px 10px; border: 1px solid #ccc; border-radius: 4px; width: 180px; }")
                .append("button { padding: 6px 14px; border: none; border-radius: 4px; cursor: pointer; color: #fff; background: #3498db; }")
                .append("button:hover { background: #2980b9; }")
                .append(".btn-success { background: #27ae60; } .btn-success:hover { background: #219150; }")
                .append("</style></head><body>")
                .append("<h1>🛍️ Boutique en ligne</h1>")

                
                .append("<h2>📋 Catalogue</h2>")
                .append("<table><tr><th>Article</th><th>Prix</th><th>Stock</th><th>Ajouter</th></tr>");

            catalogue.forEach((ref, article) -> {
                int stock = stocks.getOrDefault(ref, 0);
                html.append("<tr><td>").append(article.getNom()).append("</td>")
                    .append("<td>").append(String.format("%.2f €", article.getPrix())).append("</td>")
                    .append("<td>").append(stock).append("</td><td>");
                if (stock > 0)
                    html.append("<input type='number' id='qte-").append(ref)
                        .append("' value='1' min='1' max='").append(stock).append("'>")
                        .append(" <button onclick=\"ajouterArticle('").append(ref).append("')\">➕ Ajouter</button>");
                else
                    html.append("<em style='color:#aaa'>Rupture</em>");
                html.append("</td></tr>");
            });
            html.append("</table>")

                
                .append("<div id='panier-zone'>").append(panierFragment()).append("</div>")

                
                .append("<div id='commande-zone'></div>")

                // --- JS ---
                .append("<script>")
                .append("function post(url, data, target) {")
                .append("  fetch(url, {method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'},")
                .append("    body: new URLSearchParams(data).toString()})")
                .append("  .then(r => r.text()).then(html => { document.getElementById(target).innerHTML = html; });}")
                .append("function ajouterArticle(ref) {")
                .append("  post('/ajouter', {ref, quantite: document.getElementById('qte-'+ref).value}, 'panier-zone');}")
                .append("function appliquerReduction() {")
                .append("  post('/reduction', {code: document.getElementById('code-input').value}, 'panier-zone');}")
                .append("function passerCommande() {")
                .append("  const c = document.getElementById('client-input').value;")
                .append("  if (!c.trim()) { alert('Veuillez entrer un identifiant client.'); return; }")
                .append("  post('/commander', {client: c}, 'commande-zone');}")
                .append("</script>")
                .append("</body></html>");

            sendHtml(exchange, html.toString());
        });

        server.start();
        System.out.println("Serveur démarré sur le port 8080...");
    }

    // Panier fragment
    static String panierFragment() {
        StringBuilder h = new StringBuilder();
        h.append("<h2>🛒 Panier</h2>");
        if (panier.estVide()) {
            h.append("<p style='color:#999'>Votre panier est vide.</p>");
        } else {
            h.append("<table><tr><th>Article</th><th>Quantité</th><th>Sous-total</th></tr>");
            panier.getLignes().forEach(l ->
                h.append("<tr><td>").append(l.article().getNom())
                 .append("</td><td>").append(l.quantite())
                 .append("</td><td>").append(String.format("%.2f €", l.sousTotal()))
                 .append("</td></tr>")
            );
            h.append("<tr><td colspan='2' style='text-align:right'>");
            if (codeReductionActif != null)
                h.append("Code : <span class='tag-reduc'>").append(codeReductionActif).append("</span>");
            else
                h.append("<strong>Total</strong>");
            h.append("</td><td class='total'>").append(String.format("%.2f €", panier.calculerTotal())).append("</td></tr>")
             .append("</table>");
        }
        h.append("<h2 style='margin-top:20px'>🏷️ Code de réduction</h2>")
         .append("<input type='text' id='code-input' placeholder='REDUC10 ou REDUC20'>")
         .append(" <button onclick='appliquerReduction()'>Appliquer</button>")
         .append("<p style='font-size:.82em;color:#888;margin-top:6px'>REDUC10 → -10% &nbsp; REDUC20 → -20%</p>")
         .append("<h2 style='margin-top:20px'>✅ Commander</h2>")
         .append("<input type='text' id='client-input' placeholder='Identifiant client'>")
         .append(" <button class='btn-success' onclick='passerCommande()'>Passer la commande</button>");
        return h.toString();
    }

    // --- Resultat de Commande fragment ---
    static String commandeFragment() {
        if (derniereCommande != null)
            return "<div class='success' style='margin-top:20px'><strong>✅ Commande passée avec succès !</strong><br>" +
                "Client : " + derniereCommande.identifiantClient() + "<br>" +
                "Total  : " + String.format("%.2f €", derniereCommande.total()) + "<br>" +
                "Date   : " + derniereCommande.dateCreation() + "</div>";
        if (erreurCommande != null)
            return "<div class='error' style='margin-top:20px'>❌ " + erreurCommande + "</div>";
        return "";
    }

    // --- Helpers ---
    static void sendHtml(HttpExchange exchange, String html) throws java.io.IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    static Map<String, String> parseBody(HttpExchange exchange) throws java.io.IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2)
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
        }
        return params;
    }
}