package webservice.models.requests;

public class ChangeEmailRequest {
    private String email;

    public ChangeEmailRequest() {
    }

    public ChangeEmailRequest(String email) {
        setEmail(email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
