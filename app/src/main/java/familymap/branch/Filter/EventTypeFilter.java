package familymap.branch.Filter;


import server.model.Event;
import server.model.Person;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class EventTypeFilter extends Filter
{
    String eventType;
    public EventTypeFilter(String eventType)
    {
        super(eventType);
        this.eventType = eventType;
    }

    @Override
    public boolean fails(Event e)
    {
        if(!super.fails(e)) return false; //this should be at the top...
        //check to see if this one fails...
        return eventType.equals(e.getEventType());
    }


    @Override
    public boolean fails(Person p)
    {
        return false; //This filter does not touch persons
    }
}
