package model;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableVisual {

    private JPanel content;
    private JTable table;
    private JScrollPane legend;
    private JPanel term1;
    private JPanel term2;

    private List<Section> sections = Collections.emptyList();
    private Map<Section, Color> colors = new HashMap<>();
    private Color[] colorArray = {
            Color.LIGHT_GRAY, Color.BLUE, Color.DARK_GRAY, Color.GREEN, Color.YELLOW,
            Color.RED, Color.PINK, Color.ORANGE, Color.MAGENTA
    };
    private int colorIndex = 0;

    public TimetableVisual() {
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        initTable();
        initLegend();
        content.add(table);
        content.add(legend);
    }

    private void initLegend() {
        JPanel legendPanel = new JPanel();
        term1 = new JPanel();
        term1.setLayout(new BoxLayout(term1, BoxLayout.Y_AXIS));
        term2 = new JPanel();
        term2.setLayout(new BoxLayout(term2, BoxLayout.Y_AXIS));
        legendPanel.add(term1);
        legendPanel.add(Box.createHorizontalStrut(30));
        legendPanel.add(term2);
        legend = new JScrollPane(legendPanel);
        legend.setPreferredSize(new Dimension(300, 150));
    }

    private void initTable() {
        table = new JTable(23,12);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(15);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new ColorRenderer());
            table.getColumnModel().getColumn(i).setPreferredWidth(30);
            ColorTextCombo TIME_LABEL = headerLabel("Time");
            ColorTextCombo BLANK_LABEL = headerLabel("");
            ColorTextCombo MON_LABEL = headerLabel("Mon");
            ColorTextCombo TUE_LABEL = headerLabel("Tue");
            ColorTextCombo WED_LABEL = headerLabel("Wed");
            ColorTextCombo THU_LABEL = headerLabel("Thu");
            ColorTextCombo FRI_LABEL = headerLabel("Fri");
            switch(i) {
                case 0: //set up times
                    //header
                    table.setValueAt(TIME_LABEL, 0, i);
                    for (int j = 1; j < table.getRowCount(); j++) {
                        table.setValueAt(headerLabel(getTimeString(j-1)), j, i);
                    }
                    break;
                //term 1
                case 1:
                    table.setValueAt(MON_LABEL, 0, i);
                    break;
                case 2:
                    table.setValueAt(TUE_LABEL, 0, i);
                    break;
                case 3:
                    table.setValueAt(WED_LABEL, 0, i);
                    break;
                case 4:
                    table.setValueAt(THU_LABEL, 0, i);
                    break;
                case 5:
                    table.setValueAt(FRI_LABEL, 0, i);
                    break;
                case 7:
                    table.setValueAt(MON_LABEL, 0, i);
                    break;
                case 8:
                    table.setValueAt(TUE_LABEL, 0, i);
                    break;
                case 9:
                    table.setValueAt(WED_LABEL, 0, i);
                    break;
                case 10:
                    table.setValueAt(THU_LABEL, 0, i);
                    break;
                case 11:
                    table.setValueAt(FRI_LABEL, 0, i);
                    break;
                default:
                    for (int j = 0; j < table.getRowCount(); j++) {
                        table.setValueAt(BLANK_LABEL, j, i);
                    }
            }
        }
    }

    private void updateLegend() {
        term1.removeAll();
        term2.removeAll();
        term1.add(new JLabel("Term 1"));
        term2.add(new JLabel("Term 2"));
        for (Section section : sections) {
            String sectionString = section.getCourse() + " " + section.getType() + ": " + section.getSectionCode();
            JLabel sectionLabel = new JLabel(sectionString);
            sectionLabel.setBackground(colors.get(section));
            sectionLabel.setOpaque(true);
            if(section.getBlocks().get(0).getTerm().equals("1")) {
                term1.add(sectionLabel);
            } else {
                term2.add(sectionLabel);
            }
        }
    }

    public JComponent generateContent(List<Section> sections) {
        this.sections = sections;
        clearContent();
        updateContent();
        updateLegend();
        return content;
    }

    private void clearContent() {
        for (int col = 1; col < 6; col++) {
            clearColumn(col);
        }
        for (int col = 7; col < 12; col++){
            clearColumn(col);
        }
    }

    private void clearColumn(int col) {
        ColorTextCombo BLANK_LABEL = new ColorTextCombo(Color.WHITE, "");
        for (int row = 1; row < table.getRowCount(); row++) {
            table.setValueAt(BLANK_LABEL, row, col);
        }
    }

    private void updateContent() {
        colorIndex = 0;
        colors.clear();
        for (Section section : sections) {
            addSection(section);
        }
    }

    private Color getColor(Section section) {
        if (colors.containsKey(section)) {
            return getColor(section);
        }
        Color newColor = colorArray[colorIndex++ % colorArray.length];
        colors.put(section, newColor);
        return newColor;
    }

    private void addSection(Section section) {
        //each section gets it's own timeblock
        Color sectionColor = getColor(section);
        for (TimeBlock block : section.getBlocks()) {
            addBlock(block, sectionColor);
        }
    }

    private void addBlock(TimeBlock block, Color sectionColor) {
        int colIdx = 1;
        colIdx += termIndex(block.getTerm());
        colIdx += dayIndex(block.getDay());
        for (int i = block.getStartIndex(); i < block.getEndIndex(); i++) {
            table.setValueAt(new ColorTextCombo(sectionColor, ""), i + 1, colIdx);
        }
    }

    private int termIndex(String term) {
        return (Integer.parseInt(term) - 1) * 6;    //term 1 -> 0
                                                    //term 2 -> 6
    }

    private int dayIndex(DayOfWeek day) {
        switch(day){
            case MONDAY: return 0;
            case TUESDAY: return 1;
            case WEDNESDAY: return 2;
            case THURSDAY: return 3;
            case FRIDAY: return 4;
            default: return 5;
        }
    }

    private class ColorRenderer extends JLabel implements TableCellRenderer {

        public ColorRenderer() {
            this.setFont(new Font("Comic Sans", Font.PLAIN, 10));
            this.setOpaque(true);
            this.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object colorText, boolean isSelected, boolean hasFocus, int row, int column) {
            ColorTextCombo combo = (ColorTextCombo) colorText;
            String text = "";
            Color color = Color.white;
            if(combo != null) {
                text = combo.getText();
                color = combo.getColor();
            }
            setText(text);
            setBackground(color);
            return this;
        }
    }

    private class ColorTextCombo {

        private Color color;
        private String text;

        public ColorTextCombo(Color color, String text) {
            this.color = color;
            this.text = text;
        }

        public Color getColor() {
            return color;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return getText();
        }

    }

    private static String getTimeString(int row) {
        int time = 800;
        time += 100 * (row / 2);
        time += 30 *  (row % 2);
        return String.format("%04d", time);
    }

    private ColorTextCombo headerLabel(String text) {
        return new ColorTextCombo(Color.CYAN, text);
    }

}
