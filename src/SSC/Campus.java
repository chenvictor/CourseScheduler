package SSC;

public enum Campus {

    VANCOUVER("UBC", 1, 0),
    OKANAGAN("UBCO", 2, 1);

    private String code;
    private int transferIndex;
    private int courseIndex;

    public static Campus get(String code) {
        for (Campus c : Campus.values()) {
            if (code.equalsIgnoreCase(c.code)) {
                return c;
            }
        }
        return null;
    }

    Campus(String code, int transferIndex, int courseIndex){
        this.code = code;
        this.transferIndex = transferIndex;
        this.courseIndex = courseIndex;
    }

    public String getCode() {
        return code;
    }

    public int getTransferIndex() {
        return transferIndex;
    }

    public int getCourseIndex() {
        return courseIndex;
    }
}
