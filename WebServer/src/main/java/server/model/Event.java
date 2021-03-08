package server.model;
import java.util.UUID;


/**
 *Contains important life events about Person's in the database;
 *
 *<pre>
 *<b>Domain</b>
 *  personID		: String
 *  owner	: User
 *  connected	: Person  -person who this event is about
 *  latitude	: double
 *  longitude	: double
 *  country	: String
 *  city	: String
 *  evenType	: String
 *  date	: Date
 *</pre>
 *
 */
public class Event implements Comparable, Searchable
{
    String eventID;
    String associatedUsername;
    String personID;
    double latitude;
    double longitude;
    String country;
    String city;
    String eventType;
    int year;


    public Event()
    {
        this.eventID = UUID.randomUUID().toString();
    }

    /**
     * Main Event Constructer
     * <pre>
     * <b>Constraints on the input</b>
     *    connected, owner are none Null && owner is the owner of connected.
     * @param connected_id personID person who the event effects.
     */
    public Event(String connected_id, String associatedUsername)
    {
        this.personID = connected_id;
        this.associatedUsername = associatedUsername;
        this.eventID = UUID.randomUUID().toString();
        this.year = 0;
        this.longitude =0.0;
        this.latitude = 0.0;
        this.country = "";
        this.city = "";
        this.eventType = "";
    }

    /**
     * Main Event from dao
     * <pre>
     * <b>Constraints on the input</b>
     *    connected, owner are none Null && owner is the owner of connected.
     * @param connected_id personID of person who the event effects.
     */
    public Event(String connected_id, String associatedUsername, String id)
    {
        this.personID = connected_id;
        this.associatedUsername = associatedUsername;
        this.eventID = id;
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.country = "";
        this.city = "";
        this.eventType = "";
        this.year = Integer.MIN_VALUE;
    }

    public Event(String eventID, String associatedUsername, String personID, double latitude, double longitude, String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public String toString()
    {
        return "{" + eventID + "," + associatedUsername + "," + personID + "," + "Longititude:" + longitude + "," + "Lattitude:" + latitude + "," + country + "," + city + "," + eventType + "," + year + "}";
    }
//setters
    /**
     *Id Setter
     * @param id change this events personID.
     * @return this event to enable chaining
     */
    public Event setId(String id)
    {
        //do somethign
        this.eventID = id;
        return this;
    }

    /**
     * latitude setter
     * @param l longitude where the event happened
     * @return this event;
     */
    public Event setLatitude(double l)
    {
        latitude = l;
        return this;
    }

    /**
     * longitude setter
     * @param l longitude where the event happened
     * @return this event;
     */
    public Event setLongitude(double l)
    {
        longitude = l;
        return this;
    }
    /**
     *country setter
     * @param country Country where this occured
     * @return this event;
     */
    public Event setCountry(String country)
    {
        this.country = country;
        return this;
    }
    /**
     *city setter
     * @param city City where this event occurred
     * @return this event;
     */
    public Event setCity(String city)
    {
        this.city = city;
        return this;
    }

    /**
     *date setter
     * @param date time this event occured
     * @return this event;
     */
    public Event setDate(int date)
    {
        this.year = date;
        return this;
    }

    /**
     * EventType setter
     * @param type type of event that needs to be used
     * @return this event
     */
    public Event setEventType(String type)
    {
        this.eventType = type;
        return this;
    }
//getters

    public String getDescription()
    {
        //Type\nCity\nCountry\nYear
        return String.format("%s\nCity: %s\nCountry: %s\nYear: %s",this.eventType, this.city, this.country, getKnownDate() );
    }

    @Override
    public String getType() {
        return this.eventType;
    }

    public String getPrettyLocation()
    {
        if(getCountry() == null)
        {
            //it happened in the middle of the ocean...
            return String.format("%s,%s", getLongitude(), getLatitude());
        }
        else if(getCity() == null)
        {
            //no city, no problem
            if(getCountry() != null) return getCountry();

            return String.format("%s, %s", longitude, latitude);
        }
        return String.format("%s, %s", getCountry(), getCity());//TODO modify to deal with null cities/countries.
    }

    public String getKnownDate()
    {
        if(this.year == Integer.MIN_VALUE) return "?";
        return String.valueOf(this.year);
    }
    /**
     *Owner getter
     * @return user with permision to use modify this
     */
    public String getAssociatedUsername()
    {
        return this.associatedUsername;
    }
    /**
     * personID getter
     * @return the personID of the person this event is about
     */
    public String getConnectedId()
    {
        return this.personID;
    }
    /**
     *latitude getter
     * @return latitude this occured
     */
    public double getLatitude()
    {
        return latitude;
    }
    /**
     *longitude getter
     * @return longitude this occured
     */
    public double getLongitude()
    {
        return longitude;
    }
    /**
     *country getter
     * @return country this happened in
     */
    public String getCountry()
    {
        return country;
    }
    /**
     *City getter
     * @return city this happened in
     */
    public String getCity()
    {
        return city;
    }
    /**
     * EventType getter
     * @return what type of event this is
     */
    public String getEventType()
    {
        return eventType;
    }//just in case as we use gson to load these.
    /**
     *date Getter
     * @return the exact Date this event occured.
     */
    public int getDate()
    {
        return year;
    }

    /**
     *personID getter
     */
    public String getId()
    {
        return this.eventID;
    }

    //inherited
    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        if(o.getClass() != Event.class) return false;

        Event e = (Event) o;

        return e.getId().equals(this.getId());
    }

    @Override
    /**
     * Note in this class, .equals implies .compareTo ==0 however .compareTo == 0 does not imply .equals is true.
     */
    public int compareTo(Object o)
    {
        if(o == null ) throw new NullPointerException();
        Event e = (Event)o;
        if(this.getId().equals( e.getId() ) ) return 0;
        int difference = getDate() - e.getDate();
        //check for birth and death events first...
        if(this.getEventType().equals("birth")) return -1;
        if(this.getEventType().equals("death")) return 1;
        if(e.getEventType().equals("birth")) return 1;
        if(e.getEventType().equals("death")) return -1;

        if(difference != 0) return difference;
        difference = getEventType().toLowerCase().toString().compareTo(e.getEventType().toLowerCase());
        if(difference != 0) return difference;
        return -1;// no two events are equal..
    }
    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = eventID.hashCode();
        result = 31 * result + (associatedUsername != null ? associatedUsername.hashCode() : 0);
        result = 31 * result + (personID != null ? personID.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + year;
        return result;
    }

    public boolean hasIllegalNulls()
    {
        return this.personID == null || this.associatedUsername == null || eventID == null || this.eventType == null;
    }

    public boolean contains(String tag)
    {
        return getDescription().toLowerCase().contains(tag.toLowerCase());
    }
}