package com.LiterAlura.LiterAlura.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usado para evitar duplicados a partir del id de Gutendex
    @Column(name = "external_id", unique = true, nullable = false)
    private String externalId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String language;

    @Column(name = "download_count")
    private Integer downloadCount;

    // Para la demo: EAGER evita el error de lazy init al listar
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    /* =======================
       Constructores
       ======================= */

    public Book() { }

    public Book(String externalId, String title, String language, Integer downloadCount) {
        this.externalId = externalId;
        this.title = title;
        this.language = language;
        this.downloadCount = downloadCount;
    }

    /* =======================
       Getters / Setters
       ======================= */

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    /* =======================
       Helpers
       ======================= */

    public void addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
    }

    /* =======================
       equals / hashCode
       ======================= */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        // Preferimos externalId para igualdad (id puede ser null antes de persistir)
        return Objects.equals(externalId, book.externalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId);
    }

    /* =======================
       toString bonito
       ======================= */

    @Override
    public String toString() {
        String authorsStr = (authors == null || authors.isEmpty())
                ? "sin autores"
                : authors.stream()
                .map(Author::getName)
                .sorted()
                .reduce((a, b) -> a + ", " + b)
                .orElse("sin autores");

        return "%s | %s | downloads=%d | autores=%s".formatted(
                title,
                language,
                downloadCount != null ? downloadCount : 0,
                authorsStr
        );
    }
}
