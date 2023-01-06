package com.github.object.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ManyManyEntity {
    @Id
    private Long id;

    @ManyToOne
    private TestEntity someField;

    public void setId(Long id) {
        this.id = id;
    }

    public void setSomeField(TestEntity someField) {
        this.someField = someField;
    }
}
