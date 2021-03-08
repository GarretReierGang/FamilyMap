package server.service.load;
//import service.*;

import server.model.Event;
import server.model.Person;
import server.model.User;
//import some json reader

/**
 *A class containing the necessary information for loading information to the server.
 *
 */
public class LoadRequest
{
    FakeUser[] users;
    Person[] persons;
    Event[] events;


    public LoadRequest()
    {

    }
    public String Serialize()
    {
        String temp = "{\n";
        for (FakeUser u: users)
        {
            temp += u.toString() + ",\n";
        }
        temp += "}\n{\n";
        for (Person p : persons)
        {
            temp += p.toString() + ",\n";
        }
        temp += "}\n{\n";
        for (Event e : events)
        {
            temp += e.toString() + ",\n";
        }
        temp += "}\n";
        return temp;
    }
    /**
     *Only Load Request constructor
     */
    public LoadRequest(Person p[], FakeUser u[], Event e[])
    {
        persons = p;
        users = u;
        events = e;
    }

    //included for testing purposes
    public LoadRequest(Person p[], User u, Event e[])
    {
        persons = p;
        users = from(u);
        events = e;
    }

    /**
     *People getter
     *
     */
    public Person[] getPeople()
    {
        return persons;
    }

    /**
     *User getter
     *
     */
    public User[] getUsers()
    {
        User users[] = new User[this.users.length];
        for(int i =0; i < users.length; ++i)
        {
            users[i] = this.users[i].getUser();
        }
        return users;
    }


    public FakeUser[] from(User u)
    {
        FakeUser users[] = new FakeUser[1];

        users[0] = new FakeUser(u);
        return users;
    }

    /**
     * event getters
     * @return
     */

    boolean hasNullUsers()
    {
        for(FakeUser u: users)
        {
            if(u.hasIllegalNulls()) return true;
        }
        return false;
    }
    public Event[] getEvents()
    {
        return events;
    }
    class FakeUser
    {
        public FakeUser()
        {

        }
        public FakeUser(User u)
        {
            userName = u.getUserName();
            password = u.getPassword();
            email = u.getEmail();
            firstName = u.getMe().getFirstName();
            lastName = u.getMe().getLastName();
            gender = Character.toString(u.getMe().getSex());
            personID = u.getMyPersonId();
        }

        public boolean hasIllegalNulls()
        {
            return userName == null || password == null || email == null || firstName == null || gender == null || personID == null;
        }

        public String userName;

        private String password;
        private String email;
        private String firstName;
        private String lastName;
        private String gender;
        private String personID;


        public User getUser()
        {
            //get the first char from gender
            // Person(String ownerid, String id, String firstName, String lastName, char sex)
            Person p = new Person( firstName, lastName, gender.charAt(0), userName, personID );
            //User(String userName, String email, String password,String identity, Person me)
            User u = new User(userName, email, password, p );
            u.setId(userName);
            p.setAssociatedUsername(u.getUserName());
            return u;
        }
        public String toString()
        {
            return "{" + userName + "," + password + "," + email + "," + firstName + "," + lastName + "," + gender + "," + personID + "}";
        }
    }
}