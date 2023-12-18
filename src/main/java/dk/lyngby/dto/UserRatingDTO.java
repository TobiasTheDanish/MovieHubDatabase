package dk.lyngby.dto;

import dk.lyngby.model.User;
import dk.lyngby.model.UserRating;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRatingDTO {
    private String username;
    private String movieId;
    private Double rating;

    public UserRatingDTO(UserRating userRating) {
        this.username = userRating.getUsername();
        this.rating = userRating.getRating();
        this.movieId = userRating.getMovieId();
    }
}
