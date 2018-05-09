package ui.tabs;

import javax.swing.*;

public interface Tab {

    JComponent generateContent();

    default JComponent getContent() {
        return generateContent();
    }

    String getTitle();

    void onSelected();

}
