package sh.yannick.dhbw.interactive.speech;

import lombok.Getter;

public class Pattern {
    public static final int DCT_CLASSIFIERS = 13;

    @Getter
    private final String name;
    private final float[] values;

    public Pattern(float[] DCTVector, String name) {
        values = new float[DCT_CLASSIFIERS];
        this.name = name;
        System.arraycopy(DCTVector, 1, values, 0, DCT_CLASSIFIERS);
    }

    public float distance(Pattern p) {
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += Math.pow(values[i] - p.values[i], 2);
        }
        return (float) Math.sqrt(sum);
    }
}
