package familymap.branch.Filter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import familymap.R;
import familymap.server.Model;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class FilterActivity extends Activity
{
    RecyclerView filters;
    FilterAdapter filterAdapter;
    LinearLayoutManager llm;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        filters = findViewById(R.id.filter_recycle);
        llm = new LinearLayoutManager(this);
        filterAdapter = new FilterAdapter(this);
        filterAdapter.setListContent(Model.instance().getFilters());
        filters.setLayoutManager(llm);
        filters.setAdapter(filterAdapter);
    }

    public static void start(Context context)
    {
        Intent intent = new Intent(context, FilterActivity.class);
        context.startActivity(intent);
    }
}
