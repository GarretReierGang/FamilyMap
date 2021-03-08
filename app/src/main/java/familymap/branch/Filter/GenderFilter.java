package familymap.branch.Filter;

import familymap.server.Model;

import server.model.Event;
import server.model.Person;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class GenderFilter extends Filter
{

    char gender;
    public GenderFilter(char s)
    {
        super(Person.getGenderName(s));
        this.gender= s;
    }

    @Override
    public boolean fails(Event e)
    {
        if(!super.fails(e)) return false; //this should be at the top...
        //check to see if this one fails...
        return gender == Model.instance().getPerson(e.getConnectedId()).getSex();
    }

    @Override
    public boolean fails(Person p)
    {
        if(!super.fails(p)) return false; //this should be at the top...
        return gender == p.getSex();
    }
}
