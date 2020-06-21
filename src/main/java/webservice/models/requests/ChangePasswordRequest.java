package webservice.models.requests;

public class ChangePasswordRequest {
    private String password;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String password) {
        setPassword(password);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
