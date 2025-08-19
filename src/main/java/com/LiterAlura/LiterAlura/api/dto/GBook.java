package com.LiterAlura.LiterAlura.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GBook(
        long id,
        String title,
        List<GAuthor> authors,
        List<String> languages,
        @JsonAlias("download_count") int downloadCount
) {}
