package sh.yannick.dhbw.interactive.speech;

public class SignalProcessing {
    private final int frameSize, myCosSize2Pi;
    private final float[] mySin, myCos;

    public SignalProcessing(int size) {
        frameSize = size;
        myCosSize2Pi = size;
        myCos = new float[myCosSize2Pi];
        mySin = new float[myCosSize2Pi];
        for (int i = 0; i < size; i++) {
            myCos[i] = (float) Math.cos(2 * Math.PI * i / size);
            mySin[i] = (float) Math.sin(2 * Math.PI * i / size);
        }
    }

    public void calculateFFT(float[] re, float[] im, int size) {
        for (int span = size / 2; span > 0; span >>= 1) {
            for (int j = 0; j < span; j++) {
                int a = j * (myCosSize2Pi / 2) / span;
                float s = -mySin[a], c = myCos[a];
                for (int i = j; i < size; i += (span << 1)) {
                    float tmpR = re[i] + re[i + span], tmpI = im[i] + im[i + span];
                    float tmR = re[i] - re[i + span], tmI = im[i] - im[i + span];
                    re[i + span] = tmR * c - tmI * s;
                    re[i] = tmpR;
                    im[i + span] = tmI * c + tmR * s;
                    im[i] = tmpI;
                }
            }
        }

        // Permutation(bit reversal)
        for (int i = 1, j = 0; i < size - 1; i++) {
            int k;
            for (k = size >> 1; j >= k; k >>= 1) j -= k;
            j += k;
            if (i < j) { // swap result[i] and result[j]
                float tmpR = re[i], tmpI = im[i];
                re[i] = re[j];
                im[i] = im[j];
                re[j] = tmpR;
                im[j] = tmpI;
            }
        }
    }

    public void calculateDCT(float[] x, float[] y) {
        int N = x.length;
        for (int k = 0; k < N; k++) {
            float sum = 0;
            for (int n = 0; n < N; n++)
                sum += x[n] * (float) Math.cos(Math.PI * (n + 0.5) * k / N);
            y[k] = sum;
        }
    }

    public void calculateSpectrum(float[] signal, float[] spectrum) {
        float[] re = new float[frameSize], im = new float[frameSize];

        for (int i = 0; i < frameSize; i++) {
            re[i] = signal[i] * (0.54f - 0.46f * myCos[i * myCosSize2Pi / (frameSize - 1) % myCosSize2Pi]);
            im[i] = 0.0f;
        }

        calculateFFT(re, im, frameSize);

        for (int i = 0; i < frameSize / 2; i++) {
            spectrum[i] = (float) Math.sqrt(re[i] * re[i] + im[i] * im[i]);
        }
    }

    public void calculateCepstrum(float[] spectrum, float[] cepstrum) {
        int size = spectrum.length;
        float[] re = new float[size], im = new float[size];

        for (int i = 0; i < size; i++) {
            re[i] = (float) Math.log(10000 * spectrum[i] + 1) / 500;
            im[i] = 0.0f;
        }
        calculateFFT(re, im, size);

        for (int i = 0; i < size / 2; i++)
            cepstrum[i] = (re[i] * re[i] + im[i] * im[i]);
    }

    public void liftering(float[] spectrum, int lo, int hi, float[] excitation, float[] vocaltract) {
        int size = spectrum.length;
        float[] re = new float[size], im = new float[size];
        float[] cre = new float[size], cim = new float[size];

        for (int i = 0; i < size; i++) {
            re[i] = spectrum[i];
            im[i] = 0.0f;
        }
        calculateFFT(re, im, size);

        for (int i = 0; i < size; i++) {
            im[i] = -im[i];
            float f;
            if (i >= hi && i <= size - 1 - hi) f = 0;
            else if (i <= lo || i >= size - 1 - lo) f = 1;
            else f = i < size / 2 ? (float) (hi - i) / (hi - lo) : (float) (i - (size - 1 - hi)) / (hi - lo);
            cre[i] = (1 - f) * re[i];
            cim[i] = (1 - f) * im[i];
            re[i] *= f;
            im[i] *= f;
        }

        calculateFFT(re, im, size);
        calculateFFT(cre, cim, size);

        for (int i = 0; i < size; i++) {
            vocaltract[i] = (float) Math.sqrt(re[i] * re[i] + im[i] * im[i]) / size;
            excitation[i] = (float) Math.sqrt(cre[i] * cre[i] + cim[i] * cim[i]) / size;
        }
    }

    public void calculateMFCC(float[] spectrum, int[] melFilter, float[] mfcc) {
        int iPrev = 1;
        for (int i = 0; i < melFilter.length - 1; i++) {
            float sum = 0, weight = 0, w;
            int iAct = melFilter[i], iNext = melFilter[i + 1];
            for (int j = iPrev; j < iNext; j++) {
                w = j < iAct ? (float) (j - iPrev) / (iAct - iPrev) : 1 - (float) (j - iAct) / (iNext - iAct);
                weight += w;
                sum += w * spectrum[j] * spectrum[j];
            }
            mfcc[i] = (float) Math.log(10000 * sum / weight + 1) / 10;
        }
    }
}
