package familymap.branch.Settings;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class Settings
{
    Map<String, Integer> eventTypesColors;


    private Integer generationLineColor;
    private Boolean displayGenerationLine;
    private Integer spousalLineColor;
    private Boolean displaySpousalLine;
    private Integer lifeLineColor;
    private Boolean displayLifeLine;


    int mapType = 0;//maybe change type
    int color_cur;

    static List<Float> colors;
    static List<Integer> color_int;
    static List<String> color_names;

    static
    {
        //initialze them...
        colors = new ArrayList<>();
        color_names = new ArrayList<>();
        color_int = new ArrayList<>();

        addColorToList("Orange", BitmapDescriptorFactory.HUE_ORANGE, Color.argb(0xff, 0xff, 175, 38)); //(255, 175, 38)
        addColorToList("Azure", BitmapDescriptorFactory.HUE_AZURE, Color.argb(0xff, 0x00, 0x7f, 0xff));
        addColorToList("Yellow", BitmapDescriptorFactory.HUE_YELLOW, Color.argb(0xff, 0xff, 0xff, 0x00));//*/
        addColorToList("Violet", BitmapDescriptorFactory.HUE_VIOLET, Color.argb(0xff, 0xee, 0x82, 0xee));
        addColorToList("Blue", BitmapDescriptorFactory.HUE_BLUE, Color.argb(0xff, 0x00, 00, 0xff));
        addColorToList("Red", BitmapDescriptorFactory.HUE_RED, Color.argb(0xff, 0xff, 0x00, 0x00));
        addColorToList("Cyan", BitmapDescriptorFactory.HUE_CYAN, Color.argb(0xff, 0x00, 0xff, 0xff));

        addColorToList("Magenta", BitmapDescriptorFactory.HUE_MAGENTA, Color.argb(0xff, 0xff, 00, 0xff));
        addColorToList("Green", BitmapDescriptorFactory.HUE_GREEN, Color.argb(0xff, 0x00, 0xff, 00));
        addColorToList("Rose", BitmapDescriptorFactory.HUE_ROSE, Color.argb(0xff, 0xff, 0xe4, 0xe1));


    }

    public Settings()
    {
        eventTypesColors = new HashMap<>();
        mapType = 0;
        color_cur = 0;
        //load the default vaules for the colors....
        //randomize eventTypeColors...

        displayGenerationLine = true;
        displayLifeLine = true;
        displaySpousalLine = true;


        generationLineColor = 0;
        lifeLineColor = 1;
        spousalLineColor = 2;
    }

    public boolean addEventType(String eventType)
    {
        eventType = eventType.toLowerCase();
        if(eventTypesColors.get(eventType) ==null)
        {
            eventTypesColors.put(eventType, color_cur++ );
            return true;
        }
        return false;
    }

    public Integer getEventRGB(String eventType)
    {
        eventType = eventType.toLowerCase();
        return color_int.get(eventTypesColors.get(eventType) % colors.size());
    }

    public Integer getEventTypeColorId(String eventType)
    {
        eventType = eventType.toLowerCase();
        return eventTypesColors.get( eventType) % colors.size();//ensure that the color pulled is always bounded within the color array.
    }

    public Float getEventTypeColor(String eventType)
    {
        eventType = eventType.toLowerCase();
        return colors.get(getEventTypeColorId(eventType) );//ensure that the color pulled is always bounded within the color array.
    }

    public static List<String> getColorNames()
    {
        return color_names;
    }
    public static List<Float> getColors()
    {
        return colors;
    }
    public static void addColorToList(String name, Float color, int color2)
    {
        colors.add(color);
        color_names.add(name);
        color_int.add(color2);
    }


    /*--------------enable-disable-------------*/

    public Boolean getDisplayGenerationLine()
    {
        return displayGenerationLine;
    }

    public void setDisplayGenerationLine(Boolean displayGenerationLine)
    {
        this.displayGenerationLine = displayGenerationLine;
    }

    public Boolean getDisplaySpousalLine()
    {
        return displaySpousalLine;
    }

    public void setDisplaySpousalLine(Boolean displaySpousalLine)
    {
        this.displaySpousalLine = displaySpousalLine;
    }

    public Boolean getDisplayLifeLine()
    {
        return displayLifeLine;
    }

    public void setDisplayLifeLine(Boolean displayLifeLine)
    {
        this.displayLifeLine = displayLifeLine;
    }

    /*-----------Getters & Setters------------*/
    public Integer getGenerationLineColor()
    {
        return generationLineColor;
    }

    public void setGenerationLineColor(Integer generationLineColor)
    {
        this.generationLineColor = generationLineColor;
    }

    public Integer getSpousalLineColor()
    {
        return spousalLineColor;
    }

    public void setSpousalLineColor(Integer spousalLineColor)
    {
        this.spousalLineColor = spousalLineColor;
    }

    public Integer getLifeLineColor()
    {
        return lifeLineColor;
    }

    public void setLifeLineColor(Integer lifeLineColor)
    {
        this.lifeLineColor = lifeLineColor;
    }

    public static Float getColor(int pos)
    {
        return colors.get(pos);
    }
    public static Integer getColorInt(int pos)
    {
        return color_int.get(pos);
    }


    public void setMapType(int i)
    {
        mapType = i;
    }
    public int getMapType()
    {
        return mapType;
    }

    public Map<String, Integer> getEventTypesColors() {return  eventTypesColors;}
}
