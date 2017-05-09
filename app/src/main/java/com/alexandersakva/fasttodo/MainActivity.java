package com.alexandersakva.fasttodo;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alexandersakva.fasttodo.data.CVDatabaseHelper;
import com.alexandersakva.fasttodo.data.ToDosContract;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CVDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private InputMethodManager imm;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private RadioGroup radioGroupUrgency;
    private ImageButton imageButtonFire;
    private DrawerLayout drawer;
    private int urgency = 0;
    private int importance;
    private ToDosFragment toDosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = CVDatabaseHelper.getInstance(this.getApplicationContext());
        db = dbHelper.getWritableDatabase();
        initViews();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setFitsSystemWindows(false);

        toDosFragment  = new ToDosFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment, toDosFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();




    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initViews() {
        slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_panel);
        slidingUpPanelLayout.setAnchorPoint(0.63f);
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                imm.hideSoftInputFromWindow(editTextTitle.getWindowToken(), 0);
            }
        });

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        slidingUpPanelLayout.setDragView((FrameLayout)findViewById(R.id.dragview_slider));
        editTextTitle = (EditText)findViewById(R.id.edit_title);
        editTextDescription = (EditText)findViewById(R.id.edit_description);
        radioGroupUrgency = (RadioGroup) findViewById(R.id.radio_group);
        imageButtonFire = (ImageButton)findViewById(R.id.image_button);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });


    }

    private void insertToDo() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        ToDoCalendar c = new ToDoCalendar();
        int endTime;
        switch (radioGroupUrgency.getCheckedRadioButtonId()) {
            case R.id.radioButtonRed :
                endTime = c.getEndRed();
                urgency = 3;
                break;
            case R.id.radioButtonOrange :
                endTime = c.getEndOrange();
                urgency = 2;
                break;
            case R.id.radioButtonYellow :
                endTime = c.getEndYellow();
                urgency = 1;
                break;
            default:
                endTime = c.getEndGreen();
                break;
        }

        ContentValues values = new ContentValues();
        values.put(ToDosContract.ToDos.COLUMN_NAME, title);
        values.put(ToDosContract.ToDos.COLUMN_COMMENT, description);
        values.put(ToDosContract.ToDos.COLUMN_DATE_OF_SET, c.getCurrentTime());
        values.put(ToDosContract.ToDos.COLUMN_DATE_OF_END, endTime);
        values.put(ToDosContract.ToDos.COLUMN_URGENCY, importance);
        db.insert(ToDosContract.ToDos.TABLE_NAME, null, values);

    }

    public void onClickCreate(View view) {
        if (editTextTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Необходимо заполнить название!", Toast.LENGTH_SHORT).show();
        } else {
            insertToDo();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            //imm.hideSoftInputFromWindow(editTextDescription.getWindowToken(), 0);
            editTextDescription.setText("");
            editTextTitle.setText("");
            toDosFragment.onCreateToDo();
        }
        imm.hideSoftInputFromWindow(editTextTitle.getWindowToken(), 0);

    }


    public void onClickFire(View view) {
        importance = (importance - 1) * -1;
        if (importance == 1) {
            imageButtonFire.setImageResource(R.drawable.fire_on);
        } else imageButtonFire.setImageResource(R.drawable.fire_off);
    }

    public void onClickHidden(View view) {
        Toast.makeText(this, "О нет, вы нашли кнопку!", Toast.LENGTH_SHORT).show(); //TODO Не забыть убрать
    }
}
