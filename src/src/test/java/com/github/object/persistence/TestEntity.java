package com.github.object.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class TestEntity {
    @Id
    private Long id;

    private LocalDate date;

    public TestEntity() {}

    public TestEntity(Long id, LocalDate date) {
        this.id = id;
        this.date = date;
    }
}
