package familymap.branch.Filter;

import android.util.Log;

import familymap.server.Model;

import server.model.Event;
import server.model.Person;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class MotherFilter extends Filter
{
    protected MotherFilter(String label)
    {
        super(label);
    }
    public MotherFilter()
    {
        super("Mothers Side");
    }


    protected boolean helper(Person root, Person p)
    {
        if(root.equals(p))
        {
            return true;
        }
        if(root.getMotherId() != null)
        {
            Person mother = Model.instance().getPerson(root.getMotherId());
            Person father = Model.instance().getPerson(root.getFatherId());
            return (mother != null && helper(mother, p) )
                    || ( father != null &&helper(father, p) );
        }
        return false;
    }

    protected boolean isSide(Person p)
    {
        Log.i("user is", Model.instance().getUserPerson().getFirstName());
        Person mother = Model.instance().getPerson(Model.instance().getUserPerson().getMotherId());
        return !Model.instance().getUserPerson().equals(p) && helper(mother, p);
    }

    @Override
    public boolean fails(Event e)
    {
        if(!super.fails(e)) return false; //this should be at the top...
        //check to see if this one fails...
        Person p = Model.instance().getPerson(e.getConnectedId());
        return isSide(p);
    }

    @Override
    public boolean fails(Person p)
    {
        if(!super.fails(p)) return false; //this should be at the top...
        return isSide(p);
    }
}