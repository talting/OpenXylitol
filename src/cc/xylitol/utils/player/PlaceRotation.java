
package cc.xylitol.utils.player;

public final class PlaceRotation {
    private final PlaceInfo placeInfo;
    public final Rotation rotation;

    public PlaceInfo getPlaceInfo() {
        return this.placeInfo;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public PlaceRotation(PlaceInfo placeInfo, Rotation rotation) {
        this.placeInfo = placeInfo;
        this.rotation = rotation;
    }

    public PlaceInfo component1() {
        return this.placeInfo;
    }

    public Rotation component2() {
        return this.rotation;
    }

    public PlaceRotation copy(PlaceInfo placeInfo, Rotation rotation) {
        return new PlaceRotation(placeInfo, rotation);
    }

    public static PlaceRotation copy$default(PlaceRotation var0, PlaceInfo var1, Rotation var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.placeInfo;
        }
        if ((var3 & 2) != 0) {
            var2 = var0.rotation;
        }
        return var0.copy(var1, var2);
    }

    public String toString() {
        return "PlaceRotation(placeInfo=" + this.placeInfo + ", rotation=" + this.rotation + ")";
    }

    public int hashCode() {
        return (this.placeInfo != null ? this.placeInfo.hashCode() : 0) * 31 + (this.rotation != null ? this.rotation.hashCode() : 0);
    }

    public boolean equals(Object var1) {
        if (this != var1) {
            if (var1 instanceof PlaceRotation) {
                PlaceRotation var2 = (PlaceRotation)var1;
                return this.placeInfo.equals(var2.placeInfo) && this.rotation.equals(var2.rotation);
            }
            return false;
        }
        return true;
    }
}

