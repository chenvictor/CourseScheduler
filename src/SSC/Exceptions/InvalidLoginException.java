package SSC.Exceptions;

public class InvalidLoginException extends Exception{

    private String user;
    private String pass;

    public InvalidLoginException(String user, String pass){
        this.user = user;
        this.pass = pass;
    }

    public String getUser(){
        return user;
    }

    public String getPass(){
        return pass;
    }
}
