package com.github.object.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
class TestEntity {
    @Id
    private Long id;

    private LocalDate date;
}
