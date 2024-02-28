package sh.yannick.dhbw.interactive.speech;

import lombok.Getter;

public class PatternMatching {
    @Getter
    private final int patternCnt = 10;
    private final Pattern[] patterns = new Pattern[patternCnt];

    public void storePattern(int i, Pattern p) {
        patterns[i] = p;
    }

    public Pattern getPattern(int i) {
        return patterns[i];
    }

    public Pattern classify(Pattern p) {
        Pattern best = null;
        float bestDist = Float.MAX_VALUE;
        for (Pattern pattern : patterns) {
            float dist = pattern.distance(p);
            if (dist < bestDist) {
                bestDist = dist;
                best = pattern;
            }
        }
        return best;
    }
}
