package com.LiterAlura.LiterAlura.service.http;

import com.LiterAlura.LiterAlura.api.dto.GBook;
import com.LiterAlura.LiterAlura.api.dto.GutendexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class HttpBookClient {

    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpBookClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public List<GBook> search(String query) {
        try {
            // URL-encode SIEMPRE
            String q = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://gutendex.com/books/?search=" + q;

            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .GET()
                    .header("User-Agent", "LiterAlura/1.0 (Java 17)")
                    .timeout(Duration.ofSeconds(15)) // más tiempo para respuesta
                    .build();

            // Pequeño retry por si hay timeout transitorio
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        GutendexResponse r = mapper.readValue(resp.body(), GutendexResponse.class);
                        return r.results();
                    } else {
                        System.err.println("Gutendex status=" + resp.statusCode() + " body=" + resp.body());
                        return List.of();
                    }
                } catch (HttpTimeoutException te) {
                    if (attempt == 2) throw te; // en el segundo intento, ya dejamos el error
                    System.err.println("Timeout, reintentando...");
                }
            }

            return List.of();
        } catch (Exception e) {
            System.err.println("Error consultando Gutendex: " + e.getMessage());
            return List.of();
        }
    }
}
