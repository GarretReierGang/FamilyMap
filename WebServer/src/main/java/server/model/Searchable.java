package server.model;

/**
 * Created by Garret R Gang on 10/23/19.
 */

public interface Searchable
{
    public boolean contains(String tag);
    public String getDescription();
    public String getType();
}
