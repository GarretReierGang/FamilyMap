package server.model;


import java.util.UUID;

/**
 *A person related to a user of the database
 *<pre>
 *<b>Domain</b>
 *  firstName	: String
 *  lastName	: String
 *  personID		: int  -- unique personID of the person
 *  fatherID	: Person
 *  motherID	: Person
 *  spouseID	: Person
 *  owner	: User -- person with read/write access to this user
 *  gender		: char -- 'f' or 'm' gender on the persons birth certificate
 *</pre>
 */

public class Person implements Searchable, Comparable
{
    String firstName;
    String lastName;
    String personID;

    String fatherID;
    String motherID;
    String spouseID;
    String associatedUsername;
    char gender;

//CONSTRUCTERS

    /**
     * included for compatability with gson.
     */
    Person()
    {
        firstName = null;
        lastName = null;
        personID = null;

        fatherID = null;
        motherID = null;
        spouseID = null;
        associatedUsername = null;
        gender = 'm';
    }


    /**
     * This constructer should only be used when creating the Users Person, use {@link #Person(String firstName, String lastName, char gender)} instead.
     * <b>Constraints on the input</b>
     *  gender = 'f' or 'n'
     *
     * @param firstName legal name of the person
     * @param lastName maidenName of the person
     * @param sex Biological gender of the person
     */
    public Person(String firstName, String lastName, char sex)
    {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = sex;
        this.personID = UUID.randomUUID().toString();
    }

    /**
     * Main constructer for the person class
     *
     * <b>Constraints on the input</b>
     *  gender = 'f' or 'n', owner != null.
     *
     * @param firstName legal name of the person
     * @param lastName maidenName of the person
     * @param sex Biological gender of the person
     */
    public Person( String firstName, String lastName, char sex, String userName)
    {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = sex;
        this.personID = UUID.randomUUID().toString();
        this.associatedUsername = userName;
    }



    /**
     * Main constructer for the person class
     *
     * <b>Constraints on the input</b>
     *  gender = 'f' or 'n', owner != null.
     *
     * @param firstName legal name of the person
     * @param lastName maidenName of the person
     * @param sex Biological gender of the person
     */
    public Person( String firstName, String lastName, char sex, String username, String id)
    {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = sex;
        this.personID = UUID.randomUUID().toString();
        this.associatedUsername = username;
        this.personID = id;
    }

    public boolean isMale()
    {
        return gender == 'm';
    }
//GETTERS

    /**
     * gender getter
     * @return
     */
    public char getSex()
    {
        return gender;
    }
    /**
     * firstname getter
     * @return
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * lastname getter
     * @return
     */
    public String getLastName()
    {
        return lastName;
    }


    public String getFullName() {return firstName +" " + lastName; }
    /**
     * fatherID getter
     * @return
     */
    public String getFatherId()
    {
        return fatherID;
    }

    /**
     * spouseID getter
     * @return
     */
    public String getSpouseId()
    {

        return spouseID;
    }

    /**
     * motherID getter
     * @return
     */
    public String getMotherId()
    {

        return motherID;
    }

    /**
     * Owner personID getter
     *
     * @return the user who has access rights to these
     */
    public String getAssociatedUsername()
    {
        return associatedUsername;
    }

    /**
     *Id getter
     *@return the personID of the Person
     */
    public String getPersonID()
    {
        return personID;
    }

//SETTERS
    /**
     *SpouseId Setter
     *
     * @param spouseID the person married to this one
     */
    public Person setSpouseID(String spouseID)
    {
        this.spouseID = spouseID;
        return this;
    }

    public Person setAssociatedUsername(String id)
    {
        this.associatedUsername = id;
        return this;
    }
    /**
     *MotherId Setter
     *
     * @param momId motherID of this person
     */
    public Person setMotherId(String momId)
    {
        this.motherID = momId;
        return this;
    }

    /**
     * fatherID Setter
     * @param dadId person to make the fatherID
     */
    public Person setFatherId(String dadId)
    {
        this.fatherID = dadId;
        return this;
    }

    @Override
    public int hashCode()
    {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + personID.hashCode();
        result = 31 * result + (fatherID != null ? fatherID.hashCode() : 0);
        result = 31 * result + (motherID != null ? motherID.hashCode() : 0);
        result = 31 * result + (spouseID != null ? spouseID.hashCode() : 0);
        result = 31 * result + (associatedUsername != null ? associatedUsername.hashCode() : 0);
        result = 31 * result + (int) gender;
        return result;
    }

    //ovwritten inheritances
    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        if(!o.getClass() .isAssignableFrom(Person.class) ) return false;

        Person p = (Person) o;

        if(p.getPersonID().equals(this.getPersonID() )) return true;


        return false;
    }


    public boolean hasIllegalNulls()
    {
        return firstName == null || lastName == null || personID == null || associatedUsername == null || gender == 0;
    }

    public boolean isFather(Person p)
    {
        if(p.getFatherId() == null) return false;
        return p.getFatherId().equals(getPersonID());
    }
    public boolean isMother(Person p)
    {
        if(p.getMotherId() == null) return false;
        return p.getMotherId().equals(getPersonID());
    }

    public boolean isMarried(Person p)
    {
        if(p.getSpouseId() == null) return false;
        return p.getSpouseId().equals(getPersonID());
    }

    public static String getGenderName(char gender)
    {
        if(gender == 'm')
        {
            return "Male";
        }
        else
        {
            return "Female";
        }
    }

    public String getRelationship(Person other)
    {
        String relationship = "Distantly";
        if( isFather(other))
        {
            relationship = "Father";
        }
        else if( isMother(other))
        {
            relationship = "Mother";
        }
        else if( isMarried(other))
        {
            relationship= "Spouse";
        }
        else if( other.isFather(this) || other.isMother(this) )
        {
            relationship = "Child";
        }
        return  relationship;
    }
    @Override
    public String getDescription()
    {
        return getFullName();
    }

    @Override
    public String getType() {
        return Character.toString(gender);
    }

    @Override
    public boolean contains(String tag)
    {
        return getFullName().toLowerCase().contains(tag.toLowerCase());
    }

    @Override
    public int compareTo(Object o)
    {
        Person p = (Person) o;
        return getFullName().compareTo(p.getFullName());
    }

    public String toString()
    {
        return "{" + firstName + "," + lastName + "," + personID + "," + fatherID + "," + motherID + "," + spouseID + "," + associatedUsername + "," + gender + "}";
    }
}