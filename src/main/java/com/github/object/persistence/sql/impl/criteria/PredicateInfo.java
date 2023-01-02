package com.github.object.persistence.sql.impl.criteria;

import java.util.ArrayList;
import java.util.List;

class PredicateInfo {

    private final List<String> fields;

    PredicateInfo() {
        this.fields = new ArrayList<>();
    }

    List<String> usedFields() {
        return fields;
    }

    void addUsedField(String field) {
        fields.add(field);
    }

}
