package dat250.models.UserRequests;

public class UserGetResponse {
    private String username;
    private String email;

    // getters and setters
    public UserGetResponse() {
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
