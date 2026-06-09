package org.example.menuservice.dataAccessLayer;

import org.example.menuservice.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByNameContaining(String name);

    boolean existsByNameAndPrice(String name, Float price);
}
