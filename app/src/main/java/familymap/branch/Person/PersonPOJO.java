package familymap.branch.Person;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import server.model.Person;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class PersonPOJO
{
    String firstName;
    String lastName;
    String relationship;
    String personID;
    char sex;

    public char getSex()
    {
        return sex;
    }

    static ArrayList<PersonPOJO> generate(Person focus, List<Person> related)
    {
        ArrayList<PersonPOJO> out = new ArrayList<>();
        Log.d("generatePersonPOJO", "running");
        TreeSet<Person> listOfAddedPeople = new TreeSet<>();
        for(Person p: related)
        {
            if (listOfAddedPeople.contains(p))
            {
                continue;
            }
            Log.d("generatePersonPOJO", p.getFirstName());
            out.add(new PersonPOJO(p, focus));
            listOfAddedPeople.add(p);
        }
        return out;
    }
    public PersonPOJO(Person p, Person focus)
    {
        //we will make an assumption, if a person is not mother or father they are a child
        relationship = p.getRelationship(focus);

        firstName = p.getFirstName();
        lastName = p.getLastName();
        personID = p.getPersonID();
        sex     = p.getSex();
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getRelationship()
    {
        return relationship;
    }

    public void setRelationship(String relationship)
    {
        this.relationship = relationship;
    }
    public String getPersonID()
    {
        return personID;
    }
}
