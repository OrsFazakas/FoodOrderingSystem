package org.example.menuservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name="restaurant")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private String city;

    @OneToMany
    private Set<MenuItem> menuItems;
}
