package familymap.branch.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import familymap.R;
import com.google.common.collect.Lists;

import familymap.server.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import server.model.Person;
import server.model.Searchable;


/**
 * Created by Garret R Gang on 11/18/19.
 */

public class SearchActivity extends Activity implements TextWatcher
{

    EditText searchBar;
    RecyclerView results;
    LinearLayoutManager manager;
    SearchAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        searchBar = findViewById(R.id.search_edit_text);
        searchBar.addTextChangedListener(this);
        results = findViewById(R.id.search_results);
        manager = new LinearLayoutManager(this);
        adapter = new SearchAdapter(this);
        results.setLayoutManager(manager);
        results.setAdapter(adapter);

    }

    private ArrayList search(Set<Searchable> data, String text)
    {
        ArrayList<Searchable> output = new ArrayList<>();
        for(Searchable s: data)
        {
            if(s.contains(text))
                output.add(s);
        }
        return output;
    }

    public static void start(Context context)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void afterTextChanged(Editable s)
    {
        Toast.makeText(getBaseContext(),"afterTextChanged",Toast.LENGTH_SHORT);
        //todo implement search activity;
        String text = s.toString();
        Model model =Model.instance();

        TreeSet<Searchable> people = new TreeSet<Searchable>( Arrays.asList(model.getPeople()));
        TreeSet<Searchable> events = new TreeSet<Searchable>( Arrays.asList(model.getEvents()));
        ArrayList<Searchable> searchResults = new ArrayList<>();
        if (text.length() > 0) {
            searchResults.addAll(search(people, text));
            searchResults.addAll(search(events, text));
            //manager.scrollToPositionWithOffset(0,0);
        }
        adapter.setListContent(searchResults);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count)
    {
    }
}
