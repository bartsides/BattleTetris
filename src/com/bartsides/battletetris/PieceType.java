package com.bartsides.battletetris;

import java.util.HashMap;
import java.util.Map;

public enum PieceType {
    I(0), O(1), T(2), S(3), Z(4), J(5), L(6);

    private final int id;
    private static Map<Integer, PieceType> map = new HashMap<Integer, PieceType>();

    static {
        for (PieceType type : PieceType.values()) {
            map.put(type.id, type);
        }
    }

    PieceType(int id) { this.id = id; }

    public int getValue() { return id; }

    public static PieceType valueOf(int id) {
        return map.get(id);
    }
}
