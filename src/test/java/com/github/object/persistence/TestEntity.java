package com.github.object.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class TestEntity {
    @Id
    private Long id;

    private Date date;

    public TestEntity() {
    }
}
