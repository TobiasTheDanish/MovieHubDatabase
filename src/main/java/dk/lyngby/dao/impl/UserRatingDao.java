package dk.lyngby.dao.impl;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dto.UserRatingDTO;
import dk.lyngby.exception.ApiException;
import dk.lyngby.model.UserRating;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UserRatingDao {
    private static UserRatingDao instance = null;
    private final EntityManagerFactory emf;
    private UserRatingDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static UserRatingDao getInstance() {
        if (instance == null) {
            instance = new UserRatingDao(HibernateConfig.getEntityManagerFactory());
        }
        return instance;
    }
    public List<UserRating> readAll(String username) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            TypedQuery<UserRating> q = em.createQuery("SELECT ur FROM UserRating ur WHERE ur.username = :username", UserRating.class);
            q.setParameter("username", username);
            List<UserRating> ratings = q.getResultList();

            em.getTransaction().commit();

            return ratings;
        }
    }

    public UserRating read(String username, String movieId) throws ApiException {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            TypedQuery<UserRating> q = em.createQuery("SELECT ur FROM UserRating ur WHERE ur.username = :username AND ur.movieId = :movieId", UserRating.class);
            q.setParameter("username", username);
            q.setParameter("movieId", movieId);

            if (q.getResultList().isEmpty()) {
                return null;
            }
            UserRating ratings = q.getSingleResult();

            em.getTransaction().commit();

            return ratings;
        }
    }

    public UserRating create(UserRatingDTO dto) {
        UserRating ratingToCreate = new UserRating(dto);

        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(ratingToCreate);
            em.getTransaction().commit();
        }

        return ratingToCreate;
    }

    public UserRating update(UserRating rating) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(rating);
            em.getTransaction().commit();
            return rating;
        }
    }
}
