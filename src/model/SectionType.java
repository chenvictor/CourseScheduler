package model;

import java.util.HashMap;
import java.util.Map;

public class SectionType {

    private static Map<String, SectionType> types = new HashMap<>();

    public static SectionType getType(String typeString) {
        if (types.containsKey(typeString)) {
            return types.get(typeString);
        }
        SectionType newType = new SectionType(typeString);
        types.put(typeString, newType);
        return newType;
    }

    private final String typeString;

    private SectionType(String typeString) {
        this.typeString = typeString;
    }

    @Override
    public String toString() {
        return typeString;
    }

}
