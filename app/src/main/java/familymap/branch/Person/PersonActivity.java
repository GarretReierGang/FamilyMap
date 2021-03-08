package familymap.branch.Person;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import familymap.R;
import familymap.branch.Map.MapsActivity;
import familymap.server.Model;
import server.model.Event;
import server.model.Person;

public class PersonActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter adapter;
    TextView firstName, lastName, gender;

    static public void start(Context context, String personId)
    {
        Intent intent = new Intent(context, PersonActivity.class);
        intent.putExtra( "PersonId",personId);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person2);
        expandableListView = findViewById(R.id.person_expandable_list);
        firstName = findViewById(R.id.persons_first_name);
        lastName = findViewById(R.id.persons_last_name);
        gender = findViewById(R.id.persons_gender);

        String personId = getIntent().getExtras().getString("PersonId");
        Person selected = Model.instance().getPerson(personId);
        firstName.setText(selected.getFirstName());
        lastName.setText(selected.getLastName());
        System.out.println(selected.getSex());
        String g = ( selected.getSex() == 'm') ? "Male" : "Female";
        gender.setText(g);
        List<PersonPOJO> family =  PersonPOJO.generate(selected, Model.instance().getDirectRelationsAsPersonList(personId));
        List<Event> tempEvents = new ArrayList<>(Model.instance().getEventSet(selected.getPersonID()));
        List<Event> lifeEvents = new ArrayList<>();
        for (Event e : tempEvents)
        {
            if (Model.instance().filter(e))
            {
                lifeEvents.add(e);
            }
        }
        adapter = new ExpandableListAdapter(this, lifeEvents, family);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(groupPosition == 0) {
                    Event e = (Event)(parent.getExpandableListAdapter().getChild(groupPosition, childPosition));
                    MapsActivity.start(parent.getContext(), e.getId());
                } else {
                    PersonPOJO p = (PersonPOJO)(parent.getExpandableListAdapter()).getChild(groupPosition, childPosition);
                    Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                    intent.putExtra("PersonId", p.getPersonID());
                    startActivity(intent);
                }
                return false;
            }
        });
        expandableListView.expandGroup(0);

        expandableListView.expandGroup(1);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;

        private List<Event> lifeEvents;
        private List<PersonPOJO> family;

        public ExpandableListAdapter(Context context, List<Event> eList, List<PersonPOJO> fList) {
            this.context = context;
            this.lifeEvents = eList;
            this.family = fList;
        }
        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupPosition == 0 ? lifeEvents.size() : family.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition == 0 ? lifeEvents : family;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groupPosition == 0 ? lifeEvents.get(childPosition) : family.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_group, null);
            }
            TextView title = convertView.findViewById(R.id.listTitle);
            int titleId = groupPosition == 0 ? R.string.life_events : R.string.family;
            title.setText(titleId);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.search_result_item, null);
            }
            ImageView img = convertView.findViewById(R.id.search_item_img);
            TextView text = convertView.findViewById(R.id.search_item_text);
            if(groupPosition == 0) {
                Model.instance().getSettings().getEventTypeColorId(lifeEvents.get(childPosition).getEventType());

                //int iconId =  Model.instance().getSettings().getEventTypeColorId(lifeEvents.get(childPosition).getEventType());
                Person current = Model.instance().getPerson(lifeEvents.get(childPosition).getConnectedId());
                Model.instance().getSettings().getEventTypeColor(lifeEvents.get(childPosition).getEventType());
                Drawable drawable = getResources().getDrawable(R.drawable.ic_event);
                drawable = DrawableCompat.wrap(drawable);
//                float hsl[] = {
//                        Model.instance().getSettings().getEventTypeColor(lifeEvents.get(childPosition).getEventType()),
//                        R.dimen.default_saturation,
//                        R.dimen.default_luminance
//                };
//                DrawableCompat.setTint(drawable, ColorUtils.HSLToColor(hsl));
                DrawableCompat.setTint(drawable, Model.instance().getSettings().getEventRGB(lifeEvents.get(childPosition).getEventType()));
                img.setImageDrawable(drawable);
                text.setText(getString(R.string.searchResultEventText, current.getFirstName(),
                        current.getLastName(),
                        lifeEvents.get(childPosition).getEventType().toUpperCase(),
                        lifeEvents.get(childPosition).getCity(),
                        lifeEvents.get(childPosition).getCountry(),
                        lifeEvents.get(childPosition).getDate()));
            }else {
                int iconId = (family.get(childPosition).getSex() == 'm') ? R.drawable.male :
                        R.drawable.female;
                img.setImageResource(iconId);
                String family_member = family.get(childPosition).getRelationship();
                text.setText(getString(R.string.family_item,
                        family_member,
                        family.get(childPosition).getFirstName(),
                        family.get(childPosition).getLastName()));
            }

            return convertView;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
