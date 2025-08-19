package com.LiterAlura.LiterAlura.repository;

import com.LiterAlura.LiterAlura.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByExternalId(String externalId);
    List<Book> findByLanguageIgnoreCase(String lang);
    List<Book> findTop10ByOrderByDownloadCountDesc();
}
