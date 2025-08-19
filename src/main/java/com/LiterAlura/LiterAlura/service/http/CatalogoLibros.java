package com.LiterAlura.LiterAlura.service.http;

import com.LiterAlura.LiterAlura.api.dto.GAuthor;
import com.LiterAlura.LiterAlura.api.dto.GBook;
import com.LiterAlura.LiterAlura.domain.Author;
import com.LiterAlura.LiterAlura.domain.Book;
import com.LiterAlura.LiterAlura.repository.AuthorRepository;
import com.LiterAlura.LiterAlura.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogoLibros {

    private final BookRepository books;
    private final AuthorRepository authors;

    public CatalogoLibros(BookRepository books, AuthorRepository authors) {
        this.books = books;
        this.authors = authors;
    }

    /** Guarda un libro proveniente de la API evitando duplicados por externalId. */
    @Transactional
    public Book saveFromApi(GBook g) {
        // externalId en nuestra entidad es String -> convertir el id (long) del DTO
        String externalId = String.valueOf(g.id());

        Book book = books.findByExternalId(externalId)
                .orElseGet(() -> new Book(
                        externalId,
                        g.title(),
                        (g.languages() != null && !g.languages().isEmpty())
                                ? g.languages().get(0)
                                : "unknown",
                        g.downloadCount()
                ));

        // Autores: crea si no existe y vincula
        if (g.authors() != null) {
            for (GAuthor ga : g.authors()) {
                Author a = authors.findByNameIgnoreCase(ga.name())
                        .orElseGet(() -> {
                            Author na = new Author();
                            na.setName(ga.name());
                            na.setBirthYear(ga.birthYear());
                            na.setDeathYear(ga.deathYear());

                            return na;
                        });
                // asegurar que el autor tenga id y quede gestionado
                a = authors.save(a);

                // vincular en ambos sentidos
                book.addAuthor(a); // este método también añade el book al autor
            }
        }

        return books.save(book);
    }

    // ===== Consultas usadas por el menú =====
    public List<Book> listAllBooks() {
        // Si dejaste Book.authors como EAGER, con findAll alcanza;
        // si lo tienes LAZY, usa un fetch-join: books.findAllWithAuthors()
        return books.findAll();
    }

    public List<Author> listAllAuthors() {
        return authors.findAll();
    }

    public List<Author> authorsAliveIn(int year) {
        return authors.findAuthorsAliveIn(year);
    }

    public List<Book> listByLanguage(String lang) {
        return books.findByLanguageIgnoreCase(lang);
    }

    public List<Book> top10() {
        return books.findTop10ByOrderByDownloadCountDesc();
    }

    public List<String> listAllLanguages() {
        return books.findAll().stream().map(Book::getLanguage).distinct().toList();
    }
}
