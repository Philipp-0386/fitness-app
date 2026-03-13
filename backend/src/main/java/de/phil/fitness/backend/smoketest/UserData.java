package de.phil.fitness.backend.smoketest;

import jakarta.persistence.*;

@Entity
@Table(name = "USERDATA")
public class UserData {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    public UserData() { }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getAge() {
        return this.age;
    }
}
