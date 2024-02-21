package sh.yannick.dhbw.interactive.audio;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

class InteractiveAudioOutput {
    private final int bufLen = 4410;    // TODO: Welche Funktion hat dieser Parameter? 4410
    private final byte[] wavOutput = new byte[bufLen];
    private final Stage stage;
    private double mouseSpeed = 1;
    private double mousePitch = 0;
    private SourceDataLine line;
    private byte[] wavBuffer;
    private int bufPointer = 0;

    public InteractiveAudioOutput(Stage stage) {
        this.stage = stage;
    }

    void listen() {
        try {
            InputStream in = Objects.requireNonNull(InteractiveAudioOutput.class.getResourceAsStream("/sh/yannick/dhbw/interactive/audio/water.wav"));
            BufferedInputStream bufIn = new BufferedInputStream(in);
            AudioInputStream stream = AudioSystem.getAudioInputStream(bufIn);

            AudioFormat format = stream.getFormat();
            System.out.println("Wav File: " + format.getSampleRate() + " Hz, " + format.getSampleSizeInBits() + " Bits pro Sample"
                + " FrameSize: " + format.getFrameSize());
            wavBuffer = new byte[(int) (format.getFrameSize() * stream.getFrameLength())];
            if (stream.read(wavBuffer) >= -1) {
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, bufLen * 2);
                line.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        mouseSpeed = (stage.getHeight() - y) / stage.getHeight();
        mousePitch = x / stage.getWidth();
    }

    void audioUpdate() {
        if (line.available() >= bufLen) {
            for (int i = 0; i < bufLen; i++) {
                // TODO: Lineare Interpolation zwischen 2 Samples
                int val = (int) wavBuffer[bufPointer] & 255;

                bufPointer = (bufPointer + (int) (mousePitch * 10 + 1)) % (wavBuffer.length - 1);
                wavOutput[i] = (byte) ((val - 128) * mouseSpeed + 128); // val = 128 is base line, range 0-255
            }

            line.write(wavOutput, 0, bufLen);
        }

        mouseSpeed *= 0.5; // Fade out
    }
}

