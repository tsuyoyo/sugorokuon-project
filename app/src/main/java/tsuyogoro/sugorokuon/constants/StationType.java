package tsuyogoro.sugorokuon.constants;

import tsuyogoro.sugorokuon.network.gtm.SugorokuonTagManagerWrapper;

public enum StationType {

    RADIKO("radiko"),

    NHK("nhk")

    ;

    StationType(String value) {
        this.value = value;
    }

    public String value;

    public int getAdFrequency() {
        if (value.equals(RADIKO.value)) {
            return SugorokuonTagManagerWrapper.getRadikoTimetableAdFrequency();
        }
        else if (value.equals(NHK.value)) {
            return SugorokuonTagManagerWrapper.getNhkTimetableAdFrequency();
        }
        else {
            return 0;
        }
    }

    public static StationType getType(String value) {

        for (StationType type : StationType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }

        return null;
    }

}
