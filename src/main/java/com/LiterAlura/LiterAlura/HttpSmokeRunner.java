package com.LiterAlura.LiterAlura;

import com.LiterAlura.LiterAlura.api.dto.GBook;
import com.LiterAlura.LiterAlura.service.http.HttpBookClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("http-smoke") // solo corre si activas el perfil 'http-smoke'
public class HttpSmokeRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        HttpBookClient client = new HttpBookClient();
        List<GBook> res = client.search("don quijote");
        System.out.println("Resultados: " + res.size());

        res.stream()
                .limit(5)
                .forEach(b -> System.out.printf(
                        "- [%d] %s | langs=%s | downloads=%d%n",
                        b.id(), b.title(), b.languages(), b.downloadCount()
                ));
    }
}
