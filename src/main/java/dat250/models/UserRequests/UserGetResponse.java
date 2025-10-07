package dat250.models.UserRequests;

public class UserGetResponse {
    private String userId;
    private String email;

    // getters and setters
    public UserGetResponse() {}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}
