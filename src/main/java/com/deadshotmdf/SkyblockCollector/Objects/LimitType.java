package com.deadshotmdf.SkyblockCollector.Objects;

public enum LimitType {

    //Clashing is used in placeCollector() method to send to corresponding message
    CLASHING(-1),
    PLAYER_LIMIT(1),
    ISLAND_LIMIT(2),
    BOTH(3),
    DISABLED(4);

    private final int value;

    private LimitType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static LimitType fromValue(int value){
        switch (value){
            case 1:
                return PLAYER_LIMIT;
            case 2:
                return ISLAND_LIMIT;
            case 3:
                return BOTH;
            default:
                return DISABLED;
        }
    }
}
