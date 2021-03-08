package familymap.branch.Filter;

import familymap.server.Model;
import server.model.Person;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class FatherFilter extends MotherFilter
{
    public FatherFilter()
    {
        super("Father's Side");
    }

    @Override
    public boolean isSide(Person p)
    {
        if(Model.instance().getUserPerson().equals(p)) return false;
        Person father = Model.instance().getPerson(Model.instance().getUserPerson().getFatherId());
        return helper(father, p);
    }
}