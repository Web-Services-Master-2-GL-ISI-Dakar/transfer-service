package sn.ondmoney.txe.service.dto;
import java.io.Serializable;
public class UserDTO implements Serializable {
    private String id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean activated;

    public String getId(){return id;}
    public void setId(String id){this.id=id;}
    public String getLogin(){return login;}
    public void setLogin(String login){this.login=login;}
    public String getFirstName(){return firstName;}
    public void setFirstName(String firstName){
        this.firstName=firstName;
    }
    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName=lastName;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email=email;}
    public String getPhone(){return phone;}
    public void setPhone(String phone){this.phone=phone;}
    public boolean isActivated(){return activated;}
    public void setActivated(boolean activated){this.activated=activated;}
}
