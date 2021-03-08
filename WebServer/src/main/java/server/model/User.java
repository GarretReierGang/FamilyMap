package server.model;

import java.util.UUID;

/**
 *A user with access rights
 *<pre>
 *<b>Domain</b>
 *  userName	:  String
 *  email	:  String
 *  password	:  String
 *  personID		:  int
 *  me		:  Person -contains the real life name of the user
 *                          and is associated with events that happend to him
 *
 */
public class User
{
    String userName;
    String email;
    String password;

    String id;
    Person me;
    String personID;

    /**
     * Used to create objects from existing users
     *
     * @param userName unique identifying name.
     * @param email should be in format thisIs@anEmail.com
     * @param identity, the string identity of the user.
     * @param me Person which represents the user.
     */
    public User(String userName, String email, String password,String identity, Person me)
    {
        this.userName= userName;
        this.email = email;
        this.id = identity;
        this.password = password;
        this.me = me;
        this.personID = me.getPersonID();
        me.setAssociatedUsername(userName);
    }



    /**
     * Used to create new users for the system
     *
     * @param userName unique identifying name.
     * @param email should be in format thisIs@anEmail.com
     * @param me Person which represents the user.
     */
    public User(String userName, String email, String password, Person me)
    {
        this.userName = userName;
        this.email = email;
        this.me = me;
        this.password = password;
        this.id = UUID.randomUUID().toString();;//generate a unique personID.
        me.setAssociatedUsername(this.userName);
    }


    /**
     * personID setter
     * @param id use the personID
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     *Password getter
     *
     * @return the Password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * me setter
     * @param newMe
     */
    public void setMe(Person newMe)
    {
        this.me = newMe;
    }

    /**
     * getter for the user name
     * @return the user name
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * me Getter
     *
     * @return a Person object that is the user
     */
    public Person getMe()
    {
        return me;
    }

    public String getMyPersonId()
    {
        return me.getPersonID() ;
    }

    /**
     * Id Getter
     * @return the identity
     */
    public String getId()
    {
        return id;
    }

    public String getEmail() { return email;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!userName.equals(user.userName)) return false;
        if (!email.equals(user.email)) return false;
        if (!password.equals(user.password)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (me != null ? me.hashCode() : 0);
        result = 31 * result + (personID != null ? personID.hashCode() : 0);
        return result;
    }
}