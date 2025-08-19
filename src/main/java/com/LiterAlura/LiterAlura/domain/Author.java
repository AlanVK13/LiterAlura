package com.LiterAlura.LiterAlura.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer birthYear;
    private Integer deathYear;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();

    public Author() { }

    public Author(String name, Integer birthYear, Integer deathYear) {
        this.name = name;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
    }

    // helpers
    public void addBook(Book b) {
        books.add(b);
        b.getAuthors().add(this);
    }

    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
    public Integer getDeathYear() { return deathYear; }
    public void setDeathYear(Integer deathYear) { this.deathYear = deathYear; }
    public Set<Book> getBooks() { return books; }
    public void setBooks(Set<Book> books) { this.books = books; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author that)) return false;
        return name != null && name.equalsIgnoreCase(that.name);
    }
    @Override public int hashCode() {
        return Objects.hash(name == null ? null : name.toLowerCase());
    }

    @Override
    public String toString() {
        String birth = (birthYear != null) ? birthYear.toString() : "?";
        String death = (deathYear != null) ? deathYear.toString() : "?";
        return name + " (" + birth + "–" + death + ")";
    }

}
