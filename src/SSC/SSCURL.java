package SSC;

public enum SSCURL {

    LOGIN("https://cas.id.ubc.ca/ubc-cas/login"),
    LOGOUT("https://ssc.adm.ubc.ca/sscportal/servlets/SSCMain.jsp?logout=logout"),
    COURSE_CREDIT("https://ssc.adm.ubc.ca/sscportal/servlets/SRVAcademicRecord"),
    TRANSFER_CREDIT("https://ssc.adm.ubc.ca/sscportal/servlets/SRVTransferCredit"),
    COURSE_MAINPAGE("https://courses.students.ubc.ca/cs/main"),
    COURSE_SUBJECTS("https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=0");

    private final String URL;

    SSCURL(String URL){
        this.URL = URL;
    }


    @Override
    public String toString() {
        return URL;
    }
}
