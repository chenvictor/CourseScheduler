package ui.tabs;

import SSC.SSCClient;

import javax.swing.*;

public class OptionsTab implements Tab {

    private final SSCClient client;

    public OptionsTab(SSCClient client) {
        this.client = client;
    }

    @Override
    public JComponent generateContent() {
        JComponent content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(new JLabel("Options"));

        return content;
    }

    @Override
    public String getTitle() {
        return "Options";
    }

    @Override
    public void onSelected() {

    }
}
