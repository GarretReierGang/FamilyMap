package server.service.fill;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import server.model.Event;
import server.model.Person;

/**
 * Created by Garret R Gang on 10/23/19.
 */

public class Filler
{
    static final int EVENTS_PER_PERSON = 4;
    public static final int DEFAULT_GENERATIONS = 4;

    final Random random = new Random(new Date().getTime());

    final String FNAMES_PATH = "json/fnames.json";
    final String MMNAMES_PATH = "json/mnames.json";
    final String SNAMES_PATH = "json/snames.json";
    final String LOCATIONS_PATH = "json/locations.json";
    final int USER_BIRTH_YEAR = 1958;
    final int MAX_MOTHER_AGE = 40;
    final int MIN_MOTHER_AGE = 20;
    final int MAX_FATHER_AGE = 50;
    final int MIN_FATHER_AGE = 20;
    final int MAX_AGE = 10;
    final int MIN_TIME_FROM_MARRIAGE = 0;
    final int MAX_TIME_FROM_MARRIAGE = 10;

    final int BAPTISM_MIN_AGE = 0;
    final int BAPTISM_MAX_AGE = 60;
    final int DEATH_MIN_AGE = 20;
    final int DEATH_MAX_AGE = 60;
    final int AGE_AT_BAPTISM = 8;

    final int WEDDING = 2;
    final int DEATH = 1;
    final int BIRTH = 0;
    final int BAPTISM = 3;

    private Names snames;
    private Names fnames;
    private Names mnames;
    private Locations locations;
    private Person currentUserPerson;
    private int currentYear;

    private void GenerateCouple(Person child, int childBirthday, FillData output, int gensLeft) {
        Person Mother = generatePerson(fnames, snames, currentUserPerson.getAssociatedUsername(), 'f');
        Person Father = generatePerson(mnames, snames, currentUserPerson.getAssociatedUsername(), 'm');
        output.addPerson(Father);
        output.addPerson(Mother);
        //Setting child's parents
        child.setFatherId(Father.getPersonID());
        child.setMotherId(Mother.getPersonID());
        //setting Fathers wife
        Father.setSpouseID(Mother.getPersonID());
        //Setting Mother's husband
        Mother.setSpouseID(Father.getPersonID());
        // create birth, marriage, death, baptism
        int motherBirthday = childBirthday - (random.nextInt(MAX_MOTHER_AGE - MIN_MOTHER_AGE) + MIN_MOTHER_AGE);
        int fatherBirthday = childBirthday - (random.nextInt(MAX_FATHER_AGE - MIN_FATHER_AGE) + MIN_FATHER_AGE);

        int weddingDate = childBirthday - (random.nextInt(MAX_TIME_FROM_MARRIAGE - MIN_TIME_FROM_MARRIAGE) + MIN_TIME_FROM_MARRIAGE);
        if (weddingDate - motherBirthday < MIN_MOTHER_AGE) { //mother was to young, increasing her age.
            weddingDate += (weddingDate - motherBirthday);
        }

        int fatherDeathDate = fatherBirthday + (random.nextInt(DEATH_MAX_AGE - DEATH_MIN_AGE)) + DEATH_MIN_AGE;
        if (fatherDeathDate < childBirthday)    //Father dies before child is born, can happen, but not likely
        {
            fatherDeathDate = childBirthday + (random.nextInt(9) + 1);
        }
        int motherDeathDate = motherBirthday + (random.nextInt(DEATH_MAX_AGE - DEATH_MIN_AGE)) + DEATH_MIN_AGE;
        if (motherDeathDate < childBirthday)   //Mother cannot die before child
        {
            motherDeathDate = childBirthday + (random.nextInt(9) + 1);
        }

        int fatherBaptism = fatherBirthday + (random.nextInt(BAPTISM_MAX_AGE - BAPTISM_MIN_AGE) + BAPTISM_MIN_AGE);
        if (fatherBaptism > weddingDate) { //assuming that marriage happens after baptism
            fatherBaptism = fatherBirthday + random.nextInt(12);
        }
        int motherBaptism = motherBirthday + (random.nextInt(BAPTISM_MAX_AGE - BAPTISM_MIN_AGE) + BAPTISM_MIN_AGE);
        if (motherBaptism > weddingDate) { //assuming that marriage happens after baptism
            motherBaptism = motherBirthday + random.nextInt(12);
        }

        wedding(Father, Mother, weddingDate, output);
        standardEvent(BIRTH, Father, fatherBirthday, output);
        standardEvent(BIRTH, Mother, motherBirthday, output);
        if (fatherDeathDate <= currentYear) {
            standardEvent(DEATH, Father, fatherDeathDate, output);
        }
        if (motherDeathDate <= currentYear) {
            standardEvent(DEATH, Mother, motherDeathDate, output);
        }
        standardEvent(BAPTISM, Father, fatherBaptism, output);
        standardEvent(BAPTISM, Mother, motherBaptism, output);

        gensLeft--;
        if (gensLeft > 0) {
            GenerateCouple(Father, fatherBirthday, output, gensLeft);
            GenerateCouple(Mother, motherBirthday, output, gensLeft);
        }
    }
    private void standardEvent(int type, Person p, int date, FillData output)
    {

        int realLifeLocationIndex = random.nextInt(locations.data.length);
        Location location = locations.data[realLifeLocationIndex];
        String e_type = getEventType(type);
        Event e = new Event(p.getPersonID(), p.getAssociatedUsername());
        setLocation(e, location);
        e.setEventType(e_type);
        e.setDate(date);
        output.addEvent(e);
    }
    private void wedding(Person husband, Person wife, int date , FillData output)
    {
        int realLifeLocationIndex = random.nextInt(locations.data.length);
        Location location = locations.data[realLifeLocationIndex];
        String e_type = getEventType(WEDDING);
        Event wedFather = new Event(husband.getPersonID(), husband.getAssociatedUsername());
        Event wedMother = new Event(wife.getPersonID(), wife.getAssociatedUsername());
        setLocation(wedFather, location);
        setLocation(wedMother, location);
        wedFather.setEventType(e_type);
        wedMother.setEventType(e_type);
        wedFather.setDate(date);
        wedMother.setDate(date);
        output.addEvent(wedFather);
        output.addEvent(wedMother);
    }
    private void setLocation(Event e, Location l)
    {
        e.setLatitude(l.latitude);
        e.setLongitude(l.longitude);
        e.setCity(l.city);
        e.setCountry(l.country);
    }

    public FillData createGenerations(Person me, int generations) {
        File fnames_file = new File(FNAMES_PATH);
        File mnames_file = new File(MMNAMES_PATH);
        File snames_file = new File(SNAMES_PATH);
        File location_file = new File(LOCATIONS_PATH);
        System.out.println("Loading files for filler..");
        currentUserPerson = me;
        Gson gson = new Gson();
        try {
            snames = gson.fromJson(new FileReader(snames_file), Names.class);
            fnames = gson.fromJson(new FileReader(fnames_file), Names.class);
            mnames = gson.fromJson(new FileReader(mnames_file), Names.class);
            locations = gson.fromJson(new FileReader(location_file), Locations.class);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("could not load files");
            return null;
        }


        System.out.println("Loaded files..");
        FillData output = new FillData(generations);

        int dateOfBirth = USER_BIRTH_YEAR + random.nextInt(MAX_AGE);
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        output.addPerson(me);
        standardEvent(BIRTH, me, dateOfBirth, output);
        standardEvent(BAPTISM, me, dateOfBirth +AGE_AT_BAPTISM, output);
        if (generations == 0)
        {   //do not generate more generations
            return output;
        }
        //recursively generate ancestors
        GenerateCouple(currentUserPerson, dateOfBirth, output, generations);
        output.shrinkArrays();
        return output;
    }

    private Person generatePerson(Names first_names, Names last_names, String userName, char gender) {

        String first_name = first_names.data[random.nextInt(first_names.data.length)];
        String last_name = last_names.data[random.nextInt(last_names.data.length)];
        return new Person(first_name, last_name, gender, userName);
    }

    private String getEventType(int i) {
        final String array[] = {"birth", "death", "marriage", "baptism"};
        return array[i];
    }

    //this will be used to call a load request.
    public class FillData {

        private int getPeopleCount(int generations) {
            //this is 2^generations -1
            return (1 << (generations + 1)) - 1;
        }

        FillData(int generations) {
            people = new Person[getPeopleCount(generations)];
            events = new Event[getPeopleCount(generations) * EVENTS_PER_PERSON];
        }

        public void addEvent(Event e) {
            events[event_dex++] = e;
        }
        public void addPerson(Person p)
        {
            people[person_dex++] = p;
        }
        public void shrinkArrays()
        {
            if (person_dex < people.length)
            {
                Person[] temp = new Person[person_dex];
                for (int i = 0; i < person_dex; ++i)
                {
                    temp[i] = people[i];
                }
                people = temp;
            }
            if (event_dex < events.length)
            {
                Event[] temp = new Event[event_dex];
                for (int i = 0; i < event_dex; ++i)
                {
                    temp[i] = events[i];
                }
                events=temp;
            }
        }

        public int event_dex = 0;
        public int person_dex = 0;
        public Person people[];
        public Event events[];
    }

    //used to create all of the information
    private class Names {
        public String data[];

    }

    private class Locations {
        public Location data[];
    }

    private class Location {
        public String country;
        public String city;
        public double latitude;
        public double longitude;
    }
}
