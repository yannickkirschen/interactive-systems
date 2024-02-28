package sh.yannick.dhbw.interactive.speech;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class AudioDisplay {
    private final AudioInput ai;
    private final PatternMatching pm;
    private final GraphicsContext gc;

    private final double[] buttonX;
    private final double[] buttonY;
    private double buttonSize;

    private int buttonClicked;
    private int clickFade = 0;

    private double mouseX = 0;
    private double mouseY = 0;

    public AudioDisplay(AudioInput a, PatternMatching p, GraphicsContext g) {
        ai = a;
        gc = g;
        pm = p;
        buttonX = new double[pm.getPatternCnt()];
        buttonY = new double[pm.getPatternCnt()];
    }

    private void drawCurve(double posx, double posy, double w, double h, float[] data, float min, float max, boolean boostHi) {
        int cnt = (int) w;
        if (cnt > data.length) cnt = data.length;
        double[] xp = new double[cnt];
        double[] yp = new double[cnt];

        for (int x = 0; x < cnt; x++) {
            xp[x] = posx + x * w / cnt;
            yp[x] = posy + h * (max - data[x * data.length / cnt] * (boostHi ? ((float) 10 * x / cnt + 1) : 1)) / (max - min);
        }
        gc.strokePolyline(xp, yp, cnt);
    }

    public void draw() {
        double w = gc.getCanvas().getWidth(), h = gc.getCanvas().getHeight();

        for (int d = 0; d < 4; d++) {
            double x = 0, y = h * d / 4, ww = w * 0.7, hh = h / 4;

            gc.setStroke(Color.hsb(200 - d * 10, 0.5, 1.0));
            gc.setFill(Color.hsb(200 - d * 10, 0.5, 0.2));
            gc.fillRect(x, y, ww, hh);

            switch (d) {
                case 0: // Audio signal
                    drawCurve(x, y, ww, hh, ai.getSampleData(), -0.1f, 0.1f, false);
                    gc.strokeText("Audiosignal (" + (int) ai.getFrameDuration() + " ms Frame)", x + 20, y + 20);
                    break;

                case 1: // Fourier transformation: Spectre
                    drawCurve(x, y, ww, hh, ai.getSpectrum(), 0f, 5f, true);
                    gc.strokeText("Frequency Spectre + Mel-Coefficients: Triangle Filter (" + (int) (mouseX / ww * ai.sampleFreq / 2) + " Hz)", x + 20, y + 20);

                    // Mel-Filter-bank * MFCC-Values as Overlay
                    gc.setStroke(Color.hsb(200 - d * 10, 0.5, 0.5));
                    int[] melIndices = ai.getMelFreqBands();
                    float[] mfcc = ai.getMFCC();
                    double xx = 0, yy = 0, oldX, oldY;
                    for (int i = 0; i < melIndices.length - 1; i++) {
                        oldX = xx;
                        oldY = yy;
                        xx = x + ww * melIndices[i] / ai.getSpectrum().length;
                        yy = hh * (0.1 + mfcc[i]);
                        gc.strokeLine(oldX, y + hh - 1 - oldY, xx, y + hh - 1);
                        if (i < melIndices.length - 2) gc.strokeLine(oldX, y + hh - 1, xx, y + hh - 1 - yy);
                    }
                    break;

                case 2: // Cepstrum
                    drawCurve(x, y, ww, hh, ai.getCepstrum(), 0f, 1f, true);
                    gc.strokeText(String.format("Cepstrum (Fourier Transformation of the logarithmic spectre) (%4.0f Hz)",
                        ai.sampleFreq / 2 * mouseX / ww), x + 20, y + 20);
                    break;

                case 3: // Lifted Signal components: Excitation + vocal tract -> https://en.wikipedia.org/wiki/Formant
                    drawCurve(x, y, ww, hh, ai.getExcitation(), 0f, 10f, true);
                    drawCurve(x, y, ww, hh, ai.getVocalTract(), 0f, 3f, true);
                    gc.strokeText("Spectre: Excitation + vocal tract (" + (int) (mouseX / ww * ai.sampleFreq / 2) + " Hz)", x + 20, y + 20);
                    break;
            }
        }
        // MFCC: DCT - Characteristics
        gc.setStroke(Color.hsb(170, 0.5, 1.0));
        gc.setFill(Color.hsb(170, 0.5, 0.2));
        gc.fillRect(w * 0.7, 0, w * 0.3, h / 2);
        for (int i = 0; i < ai.getDCT().length; i++) {
            float v = ai.getDCT()[i];
            gc.setFill(Color.hsb(170, 0.5, i > 0 && i <= Pattern.DCT_CLASSIFIERS ? 1.0 : 0.5));
            if (v > 0)
                gc.fillRect(w * (0.7 + 0.3 * i / ai.getDCT().length), h / 4 - h * v / 4, w * 0.3 / ai.getDCT().length - 2, h * v / 4);
            else
                gc.fillRect(w * (0.7 + 0.3 * i / ai.getDCT().length), h / 4, w * 0.3 / ai.getDCT().length - 2, -h * v / 4);
        }
        gc.strokeText("MFCC DCT-Coefficients für characteristics vector", w * 0.7 + 20, 20);

        // Pattern Matching
        gc.setFill(Color.hsb(190, 0.5, 0.2));
        gc.fillRect(w * 0.7, h / 2, w * 0.3, h / 2);
        gc.setStroke(Color.hsb(190, 0.5, 1.0));
        gc.strokeText("Pattern Matching - characteristics vectors", w * 0.7 + 10, h * 0.5 + 10);

        Pattern p = new Pattern(ai.getDCT(), "");
        buttonSize = w * 0.3 / pm.getPatternCnt() - 4;
        for (int i = 0; i < pm.getPatternCnt(); i++) {
            Pattern p2 = pm.getPattern(i);
            float distance = 0;

            if (p2 != null) {
                distance = p.distance(p2);
                if (distance <= 0.4) {
                    gc.setFill(Color.hsb(190, 0.5, 0.8));
                } else {
                    gc.setFill(Color.hsb(190, 0.5, i == buttonClicked ? 0.5 + 0.5 * clickFade / 10f : 0.5));
                }
            } else {
                gc.setFill(Color.hsb(190, 0.5, i == buttonClicked ? 0.5 + 0.5 * clickFade / 10f : 0.5));
            }


            buttonX[i] = w * (0.7 + 0.3 * i / pm.getPatternCnt()) + 2;
            buttonY[i] = h * 0.5 + 20;
            gc.fillRect(buttonX[i], buttonY[i], buttonSize, buttonSize);
            gc.strokeText("" + i, buttonX[i] + 2, buttonY[i] + 10);

            if (p2 != null) {
                gc.strokeText(String.format("%5.2f", distance), buttonX[i] + 2, buttonY[i] + 30);
            }
        }
        if (clickFade > 0) clickFade--;
    }

    public void update() {
        ai.audioUpdate();
        draw();
    }

    public void mouseMoved(MouseEvent evt) {
        mouseX = evt.getX();
        mouseY = evt.getY();
    }

    public void mouseClicked(MouseEvent evt) {
        mouseX = evt.getX();
        mouseY = evt.getY();

        for (int i = 0; i < buttonX.length; i++) {
            if (mouseX > buttonX[i] && mouseX < buttonX[i] + buttonSize &&
                mouseY > buttonY[i] && mouseY < buttonY[i] + buttonSize) {
                buttonClicked = i;
                clickFade = 10;
                pm.storePattern(i, new Pattern(ai.getDCT(), "Name" + i));
                return;
            }
        }
    }
}

