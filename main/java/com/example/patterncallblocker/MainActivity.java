package com.example.patterncallblocker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends ListActivity {

    public ArrayList<String> data;

    public SelectionAdapter mAdapter;

    public PatternDao patternDao;

    @Override
    protected void onStart() {
        super.onStart();

        patternDao = AppDatabase.getDatabase(getApplicationContext()).patternDao();
        for(Pattern pattern: patternDao.getAll()) {
            data.add(pattern.regexPattern);
        }
        mAdapter.notifyDataSetChanged();
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.MODIFY_PHONE_STATE,
                Manifest.permission.ANSWER_PHONE_CALLS,
        }, 0);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();

        setContentView(R.layout.activity_main);

        mAdapter = new SelectionAdapter(this,
                R.layout.row_list_item, R.id.textView1, data);
        setListAdapter(mAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        getListView().setEmptyView(findViewById(R.id.emptyElement));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(view);
            }
        });

        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                nr = 0;
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.item_delete:
                        nr = 0;
                        for(Integer index:mAdapter.mSelection.keySet()) {
                            if(mAdapter.mSelection.get(index)) {
                                patternDao.deleteByRegexPattern(data.get(index.intValue()));
                                data.remove(index.intValue());
                            }
                        }
                        mAdapter.clearSelection();
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(getListView(), "Deleted sucessfully", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        actionMode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {
                mAdapter.clearSelection();
            }

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
                if (b) {
                    nr++;
                    mAdapter.setNewSelection(i, true);
                } else {
                    nr--;
                    mAdapter.removeSelection(i);
                }
                actionMode.setTitle(nr + " selected");
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                // TODO Auto-generated method stub

                getListView().setItemChecked(position, !mAdapter.isPositionChecked(position));
                return false;
            }
        });
    }

    private class SelectionAdapter extends ArrayAdapter<String> {

        private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

        public SelectionAdapter(Context context, int resource,
                                int textViewResourceId, ArrayList<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public void changeData() {
            notifyDataSetChanged();
        }

        public void setNewSelection(int position, boolean value) {
            mSelection.put(position, value);
            notifyDataSetChanged();
        }

        public boolean isPositionChecked(int position) {
            Boolean result = mSelection.get(position);
            return result == null ? false : result;
        }

        public Set<Integer> getCurrentCheckedPosition() {
            return mSelection.keySet();
        }

        public void removeSelection(int position) {
            mSelection.remove(position);
            notifyDataSetChanged();
        }

        public void clearSelection() {
            mSelection = new HashMap<Integer, Boolean>();
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);//let the adapter handle setting up the row views
            v.setBackgroundColor(getResources().getColor(android.R.color.background_light)); //default color

            if (mSelection.get(position) != null) {
                v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
            }
            return v;
        }
    }

    public void showPopupWindow(final View view) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText test2 = popupView.findViewById(R.id.edit_text);


        Button buttonEdit = popupView.findViewById(R.id.messageButton);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //As an example, display the message
                Pattern pattern = new Pattern();
                pattern.regexPattern = String.valueOf(test2.getText());
                patternDao.insert(pattern);
                data.add(pattern.regexPattern);
                mAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                Snackbar.make(view, "Added sucessfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
