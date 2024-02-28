package sh.yannick.dhbw.interactive.speech;

import javax.sound.sampled.*;
import java.io.IOException;

public class AudioInput {
    final float sampleFreq = 16000F;
    final int frameSize = 2048;
    final int mfccSize = 26;

    final int sampleSizeInBits = 16;
    final int rawBufLen = 2 * frameSize;

    private final byte[] audioRawData = new byte[rawBufLen];
    final int channels = 1;
    final boolean signed = true, bigEndian = false;
    private final float[] samples = new float[frameSize];

    private final float[] spectrum = new float[frameSize / 2];
    private final float[] cepstrum = new float[frameSize / 4];
    private final float[] excitation = new float[frameSize / 2];
    private final float[] vocalTract = new float[frameSize / 2];
    private final float[] mfcc = new float[mfccSize];
    private final float[] dct = new float[mfccSize];
    private final int[] melFreqBands = new int[mfccSize + 1];
    private final SignalProcessing signalProc;
    private AudioInputStream inStream;

    AudioInput() {
        AudioFormat format = new AudioFormat(sampleFreq, sampleSizeInBits, channels, signed, bigEndian);
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            line.open(format);
            line.start();
            inStream = new AudioInputStream(line);
        } catch (LineUnavailableException e) {
            System.out.printf("Cannot open audio line: %s%n", e.getMessage());
        }

        signalProc = new SignalProcessing(frameSize);
        for (int i = 0; i <= mfccSize; i++) {
            double Mel = (double) (2697 * i) / (mfccSize - 1);
            double Hz = (Math.exp(Mel / 1125) - 1) * 700;
            melFreqBands[i] = (int) ((frameSize / 2) * Hz / (sampleFreq / 2));
        }
    }

    float getFrameDuration() {
        return frameSize / sampleFreq * 1000;
    }

    float[] getSampleData() {
        return samples;
    }

    float[] getSpectrum() {
        return spectrum;
    }

    float[] getExcitation() {
        return excitation;
    }

    float[] getVocalTract() {
        return vocalTract;
    }

    float[] getCepstrum() {
        return cepstrum;
    }

    float[] getMFCC() {
        return mfcc;
    }

    int[] getMelFreqBands() {
        return melFreqBands;
    }

    float[] getDCT() {
        return dct;
    }

    void audioUpdate() {
        if (inStream == null) return;
        try {
            inStream.read(audioRawData, 0, rawBufLen);
        } catch (IOException ex) {
            System.out.println("Cannot read audio data from inStream");
            return;
        }

        int j = 0;
        for (int i = 0; i < frameSize; i++) {
            int val = ((int) audioRawData[j++]) & 255;
            val |= ((int) audioRawData[j++]) << 8;
            samples[i] = (float) val / 32768.f;
        }


        signalProc.calculateSpectrum(samples, spectrum);
        signalProc.calculateCepstrum(spectrum, cepstrum);
        signalProc.liftering(spectrum, 15, 25, excitation, vocalTract);
        signalProc.calculateMFCC(spectrum, melFreqBands, mfcc);
        signalProc.calculateDCT(mfcc, dct);
    }
}

