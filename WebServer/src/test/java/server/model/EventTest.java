package server.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Garret R Gang on 10/14/19.
 */
public class EventTest
{
    @Test
    public void compareTo() throws Exception
    {
        Event birth = new Event().setDate(2014).setEventType("birth");
        Event death = new Event().setDate(1400).setEventType("death");
        Event year1992 = new Event().setDate(1992).setEventType("fisher");
        Event year1993 = new Event().setDate(1993).setEventType("1993");
        Event year1320 = new Event().setDate(1320).setEventType("1993");

        //birth years are sorted to the top.
        assertTrue(birth.compareTo(death) < 0);
        assertTrue(birth.compareTo(year1320) < 0);
        assertTrue(birth.compareTo(year1992) < 0);

        //death events come last
        assertTrue(death.compareTo(year1992) > 0);
        assertTrue(death.compareTo(year1320) > 0);
        assertTrue(death.compareTo(birth) > 0);

        assertTrue(death.compareTo(death) == 0);

        //year tests
        assertTrue(year1992.compareTo(year1993) < 0);
        assertTrue(year1992.compareTo(year1320) > 0);

        //test symmetry,
        assertTrue(year1992.compareTo(year1320) > 0 && year1320.compareTo(year1992) < 0);
    }

}