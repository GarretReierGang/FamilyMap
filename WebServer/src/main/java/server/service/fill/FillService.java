package server.service.fill;

import server.dao.Database;
import server.dao.DatabaseException;
import server.model.Person;
import server.model.User;

/**
 *Fill handler, deals with loading the the array with random data.
 *
 */
public class FillService {


    Database db ;


    /**
     * Fill Constructer
     */
    public FillService(Database sess)
    {
        db = sess;
    }


    /**
     * fills the table for the user.
     */
    public FillResult fill(FillRequest fr)
    {
        FillResult fill_result = null;
        try
        {
            if(!db.isOpen()) db.open();


            String user_name = fr.getUserName();//Read user from exchange.
            System.out.printf("%s finding\n",user_name);

            User u;
            Person user_person;
            try
            {
                u = db.getUserDao().getUser(user_name);
                System.out.printf("%s finding\n",u.getMyPersonId());
                user_person = db.getPersonDao().getPerson(u.getMyPersonId(), u.getUserName());
                user_person.setSpouseID(null);
            }
            catch (DatabaseException e)
            {
                //there fault bad username.
                db.close(false);
                return new FillResult(String.format("error Could not find %s in database", user_name ));
            }

            //get generation data
            int generations = fr.generations;
            Filler.FillData result = new Filler().createGenerations(user_person, generations);

            //delete all old data.
            System.out.println("Deleting data");

            db.getEventDao().delete(u.getUserName());
            db.getPersonDao().delete(u.getUserName());

            System.out.println("Writing things.");
            int people = db.getPersonDao().writePeople(result.people);
            int events = db.getEventDao().writeEvents(result.events);

            db.close(true);
            return new FillResult(people, events);
        }
        catch (DatabaseException e)
        {
            db.close(false);
            System.out.println("Error could not open Databaese");
            return new FillResult("error could not open Database");
        }
    }
}
