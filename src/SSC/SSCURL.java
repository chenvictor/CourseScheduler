package SSC;

public enum SSCURL {

    LOGIN("https://cas.id.ubc.ca/ubc-cas/login"),
    TRANSFER_CREDIT("https://ssc.adm.ubc.ca/sscportal/servlets/SRVTransferCredit");

    private final String URL;

    SSCURL(String URL){
        this.URL = URL;
    }


    @Override
    public String toString() {
        return URL;
    }
}
