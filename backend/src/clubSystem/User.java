package clubSystem;


public abstract class User {
    private String email;
    private String phoneNumber;
    private String password;

    public User() {}

    public User(String email, String phoneNumber, String password) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }


    public String getPhone() { return getPhoneNumber(); }
    public void setPhone(String phone) { setPhoneNumber(phone); }

    
    public boolean verifyEmailAndPhone(String email, String phone) {
        return this.email != null && this.phoneNumber != null && this.email.equals(email) && this.phoneNumber.equals(phone);
    }

    public boolean verifyEmailAndPassword(String email, String password) {
        return this.email != null && this.password != null && this.email.equals(email) && this.password.equals(password);
    }

    public boolean verifyPhoneAndPassword(String phone, String password) {
        return this.phoneNumber != null && this.password != null && this.phoneNumber.equals(phone) && this.password.equals(password);
    }

    // authentication must be implemented by concrete types
    public abstract boolean authenticate(String email, String password);
}
