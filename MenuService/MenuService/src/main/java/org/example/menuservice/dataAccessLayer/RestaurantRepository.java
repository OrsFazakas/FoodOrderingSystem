package org.example.menuservice.dataAccessLayer;

import org.example.menuservice.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndCity(String name, String city);

    List<Restaurant> findAllByNameContaining(String name);

    List<Restaurant> findAllByCityContaining(String city);
}
