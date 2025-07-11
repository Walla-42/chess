package model;

public class AuthData {
    private final String authToken;
    private final String userName;

    public AuthData(String authToken, String userName){
        this.authToken = authToken;
        this.userName = userName;
    }

    public String getAuthToken(){
        return authToken;
    }

    public String getUserName(){
        return userName;
    }


}
