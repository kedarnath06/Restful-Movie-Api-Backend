package com.MovieTicketApi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie's title!")
    private String title;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie's director!")
    private String director;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie's studio!")
    private String studio;

    @Column(nullable = false, length = 200)
    private Integer releaseYear;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie's poster!")
    private String poster;
}