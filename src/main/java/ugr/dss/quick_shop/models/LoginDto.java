package ugr.dss.quick_shop.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class LoginDto {

    @Email(message = "Invalid email")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}