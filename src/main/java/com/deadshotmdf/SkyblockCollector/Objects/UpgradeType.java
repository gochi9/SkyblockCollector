package com.deadshotmdf.SkyblockCollector.Objects;

public enum UpgradeType {

    RADIUS,
    SELL_MULTIPLIER,
    CHUNK_LOADER;

    public static UpgradeType getUpgradeType(String upgradeType){
        try{
            return UpgradeType.valueOf(upgradeType.toUpperCase());
        }
        catch(Throwable e){
            return null;
        }
    }

}
