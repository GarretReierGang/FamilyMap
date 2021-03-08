package familymap.branch.Filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import familymap.R;

import java.util.ArrayList;

/**
 * Created by Garret R Gang on 11/18/19.
 */


public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {

    // Creating an arraylist of POJO objects
    private ArrayList<Filter> list_members;
    private final LayoutInflater inflater;
    View view;
    MyViewHolder holder;
    private Context context;

    public FilterAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
        list_members = new ArrayList<>();
    }
    //This method inflates view present in the RecyclerView
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view=inflater.inflate(R.layout.filter_item, parent, false);
        holder=new MyViewHolder(view);
        return holder;
    }

    //Binding the data using get() method of POJO object
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Filter list_items=list_members.get(position);
        holder.description.setText(list_items.getDescription());
        holder.filter = list_items;
        holder.enable_disable.setChecked(!list_items.getEnabled());
    }

    //Setting the arraylist
    public void setListContent(ArrayList<Filter> list_members)
    {
        this.list_members=list_members;
        notifyItemRangeChanged(0,list_members.size());
    }

    @Override
    public int getItemCount() {
        return list_members.size();
    }

    //View holder class, where all view components are defined
    class MyViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener
    {
        TextView description;
        ToggleButton enable_disable;
        Filter filter;
        public MyViewHolder(View itemView) {
            super(itemView);
            description=(TextView)itemView.findViewById(R.id.filter_description);
            enable_disable = (ToggleButton)itemView.findViewById(R.id.filter_toggle);
            enable_disable.setOnCheckedChangeListener(this);
        }
        @Override
        public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
            //TODO enable disable filter
            filter.setEnabled(!isChecked);
        }
    }

}