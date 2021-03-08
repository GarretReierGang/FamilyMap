package server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import server.model.Person;

public class PersonDao
{


    final int PERSON_ID = 1;
    final int ASSOCIATED_USER =2;
    final int FIRST_NAME =3;
    final int LAST_NAME =4;
    final int FATHER = 5;
    final int MOTHER = 6;
    final int SPOUSE = 7;
    final int GENDER = 8;
    final int COUNT_COLUMN = 9;



    final String SELECT_ALL_SQL = "select PERSON_ID, ASSOCIATED_USER, FIRST_NAME,LAST_NAME, FATHER, MOTHER, SPOUSE, GENDER\n" +
            "from PERSON\n" +
            "where ASSOCIATED_USER = ?\n";

    final String SELECT_SQL = "select PERSON_ID, ASSOCIATED_USER, FIRST_NAME, LAST_NAME, FATHER, MOTHER, SPOUSE , GENDER\n" +
            "from PERSON\n" +
            "where PERSON_ID = ? AND ASSOCIATED_USER = ?";


    final String INSERT_SQL =  "insert into PERSON(PERSON_ID, ASSOCIATED_USER, FIRST_NAME, LAST_NAME, FATHER, MOTHER, SPOUSE, GENDER)" +
            "values(?,?,?,?,?,?,?, ?)";


    Database boss;
    /**
     *@param ses the connection to pipe the information through;
     */
    public PersonDao(Database ses)
    {
        boss = ses;
    }
    /**
     * creates an entry in the database.
     *@param P the person that you want to be written to the Database.
     */
    public void writePerson(Person P) throws DatabaseException
    {
        String temp;
        try {
            PreparedStatement pS = boss.getConnection().prepareStatement(INSERT_SQL);
            pS.setString(PERSON_ID,P.getPersonID());
            temp = P.getAssociatedUsername();
            pS.setString(ASSOCIATED_USER,P.getAssociatedUsername());
            pS.setString(FIRST_NAME,P.getFirstName());
            pS.setString(GENDER,Character.toString(P.getSex()) );
            pS.setString(LAST_NAME,P.getLastName());
            pS.setString(FATHER,P.getFatherId());
            pS.setString(MOTHER,P.getMotherId());
            pS.setString(SPOUSE,P.getSpouseId());
            pS.executeUpdate();
            pS.close();
        }
        catch(SQLException e)
        {
            Database.displayError(e);
            throw new DatabaseException(e.getMessage());
        }
    }
    public int writePeople(Person people[]) throws DatabaseException
    {
        int count = 0;
        String temp = "";
        try {
            PreparedStatement pS = boss.getConnection().prepareStatement(INSERT_SQL);
            for(Person P : people) {
                pS.setString(PERSON_ID, P.getPersonID());
                String userName = P.getAssociatedUsername();
                pS.setString(ASSOCIATED_USER, P.getAssociatedUsername());
                pS.setString(FIRST_NAME, P.getFirstName());
                pS.setString(GENDER, Character.toString(P.getSex()));
                pS.setString(LAST_NAME, P.getLastName());
                pS.setString(FATHER, P.getFatherId());
                pS.setString(MOTHER, P.getMotherId());
                pS.setString(SPOUSE, P.getSpouseId());

                temp = P.getPersonID();
                count += pS.executeUpdate();
            }
            pS.close();
        }

        catch(SQLException e)
        {
            Database.displayError(e);
            if (e.getMessage().contains("SQLITE_CONSTRAINT_PRIMARYKEY"))
            {
                getPeople(people[0].getAssociatedUsername());
            }
            System.out.println(temp);
            throw new DatabaseException(e.getMessage());
        }
        return count;
    }

    /**
     * returns an array of all people in the database belonging to the user.
     *
     * @param owner_id the authority used to access the system
     */
    public Person[] getPeople(String owner_id) throws DatabaseException
    {
        Person people[] = null;
        try
        {
            PreparedStatement pS = boss.getConnection().prepareStatement(SELECT_ALL_SQL);
            //select the users belonging to the user.
            pS.setString(1, owner_id);


            ResultSet rs = pS.executeQuery();

            /*
             * This will give a count of all things that we selected.
             *
             */


            ArrayList<Person> result = new ArrayList();
            //loop through the Result Set to generate people
            while( rs.next() )
            {
                Person p = new Person(  rs.getString(FIRST_NAME),
                        rs.getString(LAST_NAME), rs.getString(GENDER).charAt(0), owner_id, rs.getString(PERSON_ID));

                p.setFatherId(rs.getString(FATHER));
                p.setMotherId(rs.getString(MOTHER));
                p.setSpouseID(rs.getString(SPOUSE));
                result.add(p);
            }

            people = new Person[result.size()];
            result.toArray(people);

            rs.close();
        }
        catch(SQLException exception)
        {
            Database.displayError(exception);
            throw new DatabaseException(exception.getMessage());
        }
        return people;
    }

    /**
     *gets a person from the database
     * @return
     */
    public Person getPerson(String person_id, String userName) throws DatabaseException
    {
        Person found = null;
        try
        {
            PreparedStatement pS = boss.getConnection().prepareStatement(SELECT_SQL);

            pS.setString(PERSON_ID, person_id);
            pS.setString(2, userName);
            ResultSet rs = pS.executeQuery();

            rs.next();
            found = new Person(rs.getString(FIRST_NAME), rs.getString(LAST_NAME), rs.getString(GENDER).charAt(0), rs.getString(ASSOCIATED_USER), rs.getString(PERSON_ID));
            found.setFatherId(rs.getString(FATHER)).setMotherId(rs.getString(MOTHER)).setSpouseID(rs.getString(SPOUSE));
            //close everything..
            pS.close();
            rs.close();
        }
        catch(SQLException e)
        {
            Database.displayError(e);
            throw new DatabaseException("Person not found");
        }
        return found;
    }


    static final String  DELETE_BELONGING_TO_USER = "delete \n" +
            "from PERSON \n" +
            "where ASSOCIATED_USER = ?";
    public int delete(String user_id)
    {
        try {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(DELETE_BELONGING_TO_USER);
            stmnt.setString(1, user_id);
            return stmnt.executeUpdate();
        }
        catch(SQLException except)
        {
            return 0;
        }
    }
}