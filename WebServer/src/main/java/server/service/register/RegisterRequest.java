package server.service.register;

import server.model.Person;
import server.model.User;

/**
 *A class containing all information needed to create a new user.
 */
public class RegisterRequest
{
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;

    public RegisterRequest()
    {

    }
    /**
     *create the Request
     *<pre>
     *<b>Constraints on input</p>
     *  user must be nonNull
     *  personUser, must be nonNull
     *</pre>
     */
    public RegisterRequest(User user, Person personUser)
    {
        userName = user.getUserName();
        password =user.getPassword();
        email = user.getEmail();
        firstName = personUser.getFirstName();
        lastName = personUser.getLastName();
        gender = Character.toString(personUser.getSex());
    }



    public RegisterRequest(String userName, String password, String email, String firstName, String lastName, String gender)
    {
        this.userName = userName;
        this.password =password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public User getUser()
    {
        //get the first char from gender
        Person p = new Person(firstName, lastName, gender.charAt(0) );
        //User(String userName, String email, String password,String identity, Person me)
        User u = new User(userName, email, password, p );
        p.setAssociatedUsername(u.getUserName());
        return u;
    }
}