package dk.lyngby.model;

import dk.lyngby.dto.UserRatingDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_rating")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String movieId;
    private Double rating;

    public UserRating(UserRatingDTO dto) {
        this.username = dto.getUsername();
        this.rating = dto.getRating();
        this.movieId = dto.getMovieId();
    }
}
