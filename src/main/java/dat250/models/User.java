package dat250.models;

public class User {

    private String userId; // Also serves as username, as username has to be unique.
    private String email;
    private String password;

    public User() {
    }

    // UserID
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password)   {
        this.password = password;
    }
}

