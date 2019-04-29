package com.security.demo.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum BusinessCategory {
    MANUFACTURING("Manufacturing"),
    ENTERTAINMENT("Entertainment"),
    ICT("Ict"),
    CONSTRUCTION("Construction");

    private String value;

    BusinessCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }


    public static synchronized BusinessCategory find(String feeBearer) {
        try {
            return BusinessCategory.valueOf(feeBearer);
        } catch (Exception e) {
            return findByValue(feeBearer);
        }
    }

    private static BusinessCategory findByValue(String value) {
        BusinessCategory type = null;

        for (BusinessCategory category : BusinessCategory.values()) {
            if( category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        return  null;

    }
}
