module sh.yannick.dhbw.interactive {
    requires static lombok;

    requires java.desktop;
    requires javafx.controls;

    exports sh.yannick.dhbw.interactive;
    exports sh.yannick.dhbw.interactive.keyboard;
    exports sh.yannick.dhbw.interactive.mouse;
    exports sh.yannick.dhbw.interactive.physics;
    exports sh.yannick.dhbw.interactive.audio;
    exports sh.yannick.dhbw.interactive.speech;
    exports sh.yannick.dhbw.interactive.ui;
    exports sh.yannick.dhbw.interactive.undo;
}
