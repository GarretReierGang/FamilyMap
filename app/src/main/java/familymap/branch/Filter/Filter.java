package familymap.branch.Filter;

import server.model.Event;
import server.model.Person;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class Filter
{
    private boolean enabled = false;
    private String description;

    public Filter(String description)
    {
        this.description = description;
    }
    public void setEnabled(boolean b)
    {
        enabled=b;
    }
    public boolean getEnabled()
    {
        return enabled;
    }


    public boolean fails(Event e)
    {
        return enabled;
    }
    public boolean fails(Person p) {return enabled; }

    public String getDescription()
    {
        return description;
    }
}
