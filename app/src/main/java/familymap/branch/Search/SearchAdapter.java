package familymap.branch.Search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import familymap.R;
import familymap.branch.Map.MapsActivity;

import java.util.ArrayList;

import familymap.branch.Person.PersonActivity;
import familymap.server.Model;
import server.model.Event;
import server.model.Person;
import server.model.Searchable;

/**
 * Created by Garret R Gang on 11/18/19.
 */


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    // Creating an arraylist of POJO objects
    private ArrayList<Searchable> list_members;
    private final LayoutInflater inflater;
    View view;
    MyViewHolder holder;
    private Context context;

    public SearchAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
        list_members = new ArrayList<>();
    }
    //This method inflates view present in the RecyclerView
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view=inflater.inflate(R.layout.search_item, parent, false);
        holder=new MyViewHolder(view);
        return holder;
    }

    //Binding the data using get() method of POJO object
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Searchable list_items=list_members.get(position);
        holder.description.setText(list_items.getDescription());

        if (list_items.getType().equals("m"))
        {
            holder.icon.setImageResource(R.drawable.male);
        }
        else if (list_items.getType().equals("f"))
        {
            holder.icon.setImageResource(R.drawable.female);
        }
        else
        {
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_event, null);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Model.instance().getSettings().getEventRGB(list_items.getType()));
            holder.icon.setImageDrawable(drawable);
        }
        holder.searchable = list_items;
    }

    //Setting the arraylist
    public void setListContent(ArrayList<Searchable> list_members)
    {
        this.list_members=list_members;
        notifyItemRangeChanged(0,list_members.size());
    }

    @Override
    public int getItemCount() {
        return list_members.size();
    }

    //View holder class, where all view components are defined
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView description;
        Searchable searchable;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            description=(TextView)itemView.findViewById(R.id.search_description);
            icon = itemView.findViewById(R.id.search_item_icon);

        }
        @Override
        public void onClick(View view) {
            //TODO enable disable filter
            if(searchable.getClass().equals(Event.class))
            {
                MapsActivity.start(context, ((Event)searchable).getId() );
            }
            else if(searchable.getClass().equals(Person.class))
            {
                PersonActivity.start(context, ((Person)searchable).getPersonID());
            }
        }
    }
    public void removeAt(int position) {
        list_members.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, list_members.size());
    }
}