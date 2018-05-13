package ui;

import model.*;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.time.DayOfWeek;
import java.util.*;

public class TimetableViewer {

    /*
     *  Maximum number of course distributions to consider
     */
    private static final int DISTRIBUTION_LIMIT = 5;

    private int oldTabIdx = 0;
    private JTabbedPane courseDistribution;
    private JPanel termPanel;
    private JComboBox<String> term1Selector;
    private JComboBox<String> term2Selector;
    private TimetableVisual visual;

    private List<Course> term1;
    private List<Course> term2;
    private List<CourseDistribution> distributions;
    private LinkedList<CourseCombination> baseTerm1;
    private LinkedList<CourseCombination> baseTerm2;

    private Map<CourseDistribution, List<CourseCombination>> term1Combinations;
    private Map<CourseDistribution, List<CourseCombination>> term2Combinations;

    public TimetableViewer(TreeSet<CourseTermCombo> combos) {
        //clone so we can work with it
        term1 = new LinkedList<>();
        term2 = new LinkedList<>();
        term1Combinations = new LinkedHashMap<>();
        term2Combinations = new LinkedHashMap<>();
        distributions = generateCourseDistribution((TreeSet<CourseTermCombo>) combos.clone());
        generateBaseTerms();
        generateTermCombinations();
        initGUI(combos);
        selectedDistribution(0);
    }

    private void initGUI(Set<CourseTermCombo> combos) {
        initTermSelectionTabs();
        initDistributionTabs();
        visual = new TimetableVisual();
        StringBuilder coursesString = new StringBuilder();
        for (CourseTermCombo combo : combos)
            coursesString.append(" ").append(combo.getCourse());
        JFrame frame = new JFrame(String.format("Courses[%d]:%s", combos.size(), coursesString));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        JPanel distributionPanel = new JPanel();
        distributionPanel.setLayout(new BoxLayout(distributionPanel, BoxLayout.Y_AXIS));
        //EVENT HANDLING
        courseDistribution.addChangeListener(e -> selectedDistribution(courseDistribution.getSelectedIndex()));
        //
        distributionPanel.add(courseDistribution);
        wrapper.add(new JLabel("Course Distribution:", SwingConstants.CENTER));
        wrapper.add(distributionPanel);
        wrapper.add(visual.getContent());
        frame.add(wrapper);
        frame.pack();
        frame.setVisible(true);
    }

    private void initDistributionTabs() {
        courseDistribution = new JTabbedPane();
        courseDistribution.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        int idx = 0;
        Collections.sort(distributions);
        distributions = distributions.subList(0, Math.min(DISTRIBUTION_LIMIT, distributions.size()));
        for (CourseDistribution dis : distributions) {
            courseDistribution.addTab(String.valueOf(++idx), new JLabel());
            //initially blank
        }
    }

    private void initTermSelectionTabs() {
        termPanel = new JPanel();
        termPanel.setLayout(new BoxLayout(termPanel, BoxLayout.X_AXIS));
        term1Selector = new JComboBox<>();
        term2Selector = new JComboBox<>();
        term1Selector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedTerm1(term1Selector.getSelectedIndex());
            }
        });
        term2Selector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedTerm2(term2Selector.getSelectedIndex());
            }
        });
        termPanel.add(term1Selector);
        termPanel.add(term2Selector);
    }

    private List<CourseDistribution> generateCourseDistribution(TreeSet<CourseTermCombo> combos) {
        CourseTermCombo first = combos.pollFirst(); //reduction step
        if (first == null) {
            //base case
            List<CourseDistribution> init = new ArrayList<>();
            init.add(new CourseDistribution());
            return init;
        }
        List<CourseDistribution> all = new ArrayList<>();
        for (CourseDistribution distrib : generateCourseDistribution(combos)) {
            switch (first.getTerm()) {
                case "1":
                    if (!term1.contains(first.getCourse()))
                        term1.add(first.getCourse());
                    all.add(distrib);
                    break;
                case "2":
                    if (!term2.contains(first.getCourse()))
                        term2.add(first.getCourse());
                    all.add(distrib);
                    break;
                case "Any":
                    all.add(distrib.addTerm1(first.getCourse()));
                    all.add(distrib.addTerm2(first.getCourse()));
                    break;
            }
        }
        return all;
    }

    private void generateBaseTerms() {
        LinkedList<List<Section>> queue1 = new LinkedList<>();
        LinkedList<List<Section>> queue2 = new LinkedList<>();
        for (Course course : term1) {
            queue1.addAll(course.getSectionsByType("1").values());
        }
        for (Course course : term2) {
            queue2.addAll(course.getSectionsByType("2").values());
        }
        LinkedList<CourseCombination> base = new LinkedList<>();
        base.add(new CourseCombination());
        baseTerm1 = generateCombinations(queue1, base);
        baseTerm2 = generateCombinations(queue2, base);
    }

    private void generateTermCombinations() {
        for (CourseDistribution distribution : distributions) {
            LinkedList<List<Section>> queue1 = new LinkedList<>();
            LinkedList<List<Section>> queue2 = new LinkedList<>();
            for (Course course : distribution.getTerm1()) {
                queue1.addAll(course.getSectionsByType("1").values());
            }
            for (Course course : distribution.getTerm2()) {
                queue2.addAll(course.getSectionsByType("2").values());
            }
            queue1.sort(Comparator.comparingInt(List::size));   //Sort smallest to largest size (least flexible courses are picked first)
            queue2.sort(Comparator.comparingInt(List::size));   //

            //generate term1s
            LinkedList<CourseCombination> term1Combos = generateCombinations(queue1, baseTerm1);
            Collections.sort(term1Combos);
            term1Combinations.put(distribution, term1Combos);
            //generate term2s
            LinkedList<CourseCombination> term2Combos = generateCombinations(queue2, baseTerm2);
            Collections.sort(term2Combos);
            term2Combinations.put(distribution, term2Combos);
        }
    }

    private LinkedList<CourseCombination> generateCombinations(LinkedList<List<Section>> toAdd, LinkedList<CourseCombination> rsf) {
        if (toAdd.isEmpty()) {
            //base case
            return rsf;
        }
        toAdd = (LinkedList<List<Section>>) toAdd.clone();
        List<Section> first = toAdd.poll();
        LinkedList<CourseCombination> all = new LinkedList<>();
        for (CourseCombination combo : rsf) {
            LinkedList<CourseCombination> nextCombos = new LinkedList<>();
            for (Section section : first) {
                CourseCombination next = new CourseCombination(combo);
                if (next.add(section)) {
                    nextCombos.add(next);
                }
            }
            all.addAll(generateCombinations(toAdd, nextCombos));
        }
        return all;
    }

    private void selectedDistribution(int idx) {
        final String itemFormat = "Term %d: %15s";
        CourseDistribution selected = distributions.get(idx);
        courseDistribution.setComponentAt(oldTabIdx, new JLabel());
        courseDistribution.setComponentAt(idx, termPanel);
        //update term1 and term2 selectors
        term1Selector.removeAllItems();
        term2Selector.removeAllItems();
        for (int i = 1; i <= term1Combinations.get(selected).size(); i++) {
            term1Selector.addItem(String.format(itemFormat, 1, ("Option " + i)));
        }
        if (term1Selector.getItemCount() == 0) {
            //no options found for term1
            term1Selector.addItem(String.format(itemFormat, 1, "Invalid"));
        }
        for (int i = 1; i <= term2Combinations.get(selected).size(); i++) {
            term2Selector.addItem(String.format(itemFormat, 2, ("Option " + i)));
        }
        if (term2Selector.getItemCount() == 0) {
            //no options found for term2
            term2Selector.addItem(String.format(itemFormat, 2, "Invalid"));
        }
        term1Selector.revalidate();
        term1Selector.repaint();
        term2Selector.revalidate();
        term2Selector.repaint();
        oldTabIdx = idx;
    }

    private void selectedTerm1(int idx) {
        updateVisual();
    }

    private void selectedTerm2(int idx) {
        updateVisual();
    }

    private void updateVisual() {
        List<Section> sections = new LinkedList<>();
        int distribIdx = courseDistribution.getSelectedIndex();
        CourseDistribution selected = distributions.get(distribIdx);
        List<CourseCombination> t1 = term1Combinations.get(selected);
        List<CourseCombination> t2 = term2Combinations.get(selected);
        int term1Idx = term1Selector.getSelectedIndex();
        if (term1Idx >= 0 && t1.size() != 0) {
            sections.addAll(t1.get(term1Idx).sections);
        }
        int term2Idx = term2Selector.getSelectedIndex();
        if (term2Idx >= 0 && t2.size() != 0) {
            sections.addAll(t2.get(term2Idx).sections);
        }
        Collections.sort(sections);
        visual.update(sections);
    }

    private class CourseDistribution implements Comparable<CourseDistribution>{
        private SortedSet<Course> term1;
        private SortedSet<Course> term2;

        CourseDistribution() {
            this.term1 = new TreeSet<>();
            this.term2 = new TreeSet<>();
        }

        CourseDistribution(CourseDistribution origin) {
            this.term1 = new TreeSet<>(origin.term1);
            this.term2 = new TreeSet<>(origin.term2);
        }

        CourseDistribution addTerm1(Course course) {
            CourseDistribution newDis = new CourseDistribution(this);
            newDis.term1.add(course);
            return newDis;
        }

        CourseDistribution addTerm2(Course course) {
            CourseDistribution newDis = new CourseDistribution(this);
            newDis.term2.add(course);
            return newDis;
        }

        SortedSet<Course> getTerm1() {
            return this.term1;
        }

        SortedSet<Course> getTerm2() {
            return this.term2;
        }

        private int getDiff() {
            int term1Credits = 0;
            int term2Credits = 0;
            for (Course c : term1)
                term1Credits += c.getCredits();
            for (Course c : TimetableViewer.this.term1)
                term1Credits += c.getCredits();
            for (Course c : term2)
                term2Credits += c.getCredits();
            for (Course c : TimetableViewer.this.term2)
                term2Credits += c.getCredits();
            return Math.abs(term1Credits - term2Credits);
        }

        @Override
        public String toString() {
            StringBuilder string = new StringBuilder("Term 1:");
            for (Course course : term1) {
                string.append(" ").append(course.toString());
            }
            string.append("  |  Term 2:");
            for (Course course : term2) {
                string.append(" ").append(course.toString());
            }
            return string.toString();
        }

        @Override
        public int compareTo(CourseDistribution o) {
            return this.getDiff() - o.getDiff();
        }
    }

    private class CourseCombination implements Iterable<Section>, Comparable<CourseCombination>{

        private List<Section> sections;

        CourseCombination() {
            sections = new LinkedList<>();
        }

        CourseCombination(CourseCombination origin) {
            sections = new LinkedList<>(origin.sections);
        }

        public boolean add(Section section) {
            for (Section sec : sections) {
                if (section.conflicts(sec))
                    return false;
            }
            sections.add(section);
            return true;
        }

        @Override
        public Iterator<Section> iterator() {
            return sections.iterator();
        }

        //higher number is better
        private int goodness() {
            int level = 0;

            level -= dayHourDeviation();
            level -= totalDayLengths();

            return level;
        }

        private int dayHourDeviation() {
            int[] dayHours = new int[5];
            Arrays.fill(dayHours, 0);
            for (Section sec : sections) {
                for (TimeBlock blo : sec.getBlocks()) {
                    switch (blo.getDay()) {
                        case MONDAY:    dayHours[0] += blo.getLength();
                            break;
                        case TUESDAY:   dayHours[1] += blo.getLength();
                            break;
                        case WEDNESDAY: dayHours[2] += blo.getLength();
                            break;
                        case THURSDAY:  dayHours[3] += blo.getLength();
                            break;
                        case FRIDAY:    dayHours[4] += blo.getLength();
                    }
                }
            }
            double d = stdev(dayHours);
            return Math.toIntExact(Math.round(d * 10));
        }

        private int totalDayLengths() {
            int[] starts = new int[5];
            int[] ends = new int[5];
            Arrays.fill(starts , 50);   //impossible values
            Arrays.fill(ends , -1);     //impossible values

            for (Section sec : sections) {
                for (TimeBlock blo : sec.getBlocks()) {
                    int dayIdx = dayToIdx(blo.getDay());
                    starts[dayIdx] = Math.min(starts[dayIdx], blo.getStartIndex());
                    ends[dayIdx] = Math.max(ends[dayIdx], blo.getEndIndex());
                }
            }
            //
            int total = 0;
            for (int i = 0; i < 5; i++) {
                if (ends[i] == -1){
                    continue;   //no classes on this day
                }
                total += (ends[i] - starts[i]);
            }
            return total;
        }

        @Override
        public int compareTo(CourseCombination o) {
            return o.goodness() - this.goodness();
        }
    }

    private static double stdev(int[] list){
        double sum = 0.0;
        double mean;
        double num=0.0;
        double numi;
        double deno = 0.0;

        for (int i : list) {
            sum+=i;
        }
        mean = sum/list.length;

        for (int i : list) {
            numi = Math.pow((double) i - mean, 2);
            num+=numi;
        }

        return Math.sqrt(num/list.length);
    }

    private static int dayToIdx(DayOfWeek day) {
        return day.getValue() - 1;
    }

}