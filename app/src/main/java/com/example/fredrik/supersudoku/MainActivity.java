package com.example.fredrik.supersudoku;

import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.example.fredrik.supersudoku.asdflaksd.EventListener;
import com.example.fredrik.supersudoku.sudokulogic.Board;
import com.example.fredrik.supersudoku.sudokulogic.MarkMode;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventListener {

    LinearLayout mainLayout;
    SudokuSurfaceView sudokuSurfaceView;

    Button newGameButton;
    Button undoButton;
    Button hintButton;

    ToggleButton fillToggleButton;
    ToggleButton candidateToggleButton;
    ToggleButton[] modeToggleButtons;

    ToggleButton toggleButton1;
    ToggleButton toggleButton2;
    ToggleButton toggleButton3;
    ToggleButton toggleButton4;
    ToggleButton toggleButton5;
    ToggleButton toggleButton6;
    ToggleButton toggleButton7;
    ToggleButton toggleButton8;
    ToggleButton toggleButton9;
    ToggleButton[] numberToggleButtons;

    SudokuMain sudokuMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sudokuMain = new SudokuMain(this);

        sudokuMain.board.addEventListener(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        sudokuSurfaceView = new SudokuSurfaceView(this);
        mainLayout.addView(sudokuSurfaceView, 0);

        initButtons();
        setDefaultViewState();

        sudokuMain.newGame();
    }

    private void setDefaultViewState() {
        fillToggleButton.setChecked(true);
    }

    private void initButtons() {
        newGameButton = (Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(view -> sudokuMain.newGame());

        undoButton = (Button) findViewById(R.id.undoButton);
        undoButton.setOnClickListener(view -> sudokuMain.board.undo());

        hintButton = (Button) findViewById(R.id.hintButton);
        hintButton.setOnClickListener(view -> sudokuMain.board.hint());

        fillToggleButton = (ToggleButton) findViewById(R.id.fillToggleButton);
        candidateToggleButton = (ToggleButton) findViewById(R.id.candidateToggleButton);

        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton) findViewById(R.id.toggleButton3);
        toggleButton4 = (ToggleButton) findViewById(R.id.toggleButton4);
        toggleButton5 = (ToggleButton) findViewById(R.id.toggleButton5);
        toggleButton6 = (ToggleButton) findViewById(R.id.toggleButton6);
        toggleButton7 = (ToggleButton) findViewById(R.id.toggleButton7);
        toggleButton8 = (ToggleButton) findViewById(R.id.toggleButton8);
        toggleButton9 = (ToggleButton) findViewById(R.id.toggleButton9);

        modeToggleButtons = new ToggleButton[]{fillToggleButton, candidateToggleButton};
        numberToggleButtons = new ToggleButton[]{toggleButton1, toggleButton2,
                toggleButton3, toggleButton4, toggleButton5, toggleButton6, toggleButton7,
                toggleButton8, toggleButton9};
        makeToggleButtonsExclusive();
    }

    private void makeToggleButtonsExclusive() {
        CompoundButton.OnCheckedChangeListener modeChangeChecker = (buttonView, isChecked) -> {
            if (isChecked) {
                sudokuMain.markMode = MarkMode.parseText(buttonView.getText().toString());
                for (ToggleButton tb : modeToggleButtons) {
                    if (tb != buttonView)
                        tb.setChecked(false);
                }
            }
        };
        for (ToggleButton tb : modeToggleButtons) {
            tb.setOnCheckedChangeListener(modeChangeChecker);
        }
        CompoundButton.OnCheckedChangeListener numberChangeChecker = (buttonView, isChecked) -> {
            if (isChecked) {
                int buttonNumber = Integer.parseInt(buttonView.getText().toString());
                sudokuMain.selectedNumber = sudokuMain.selectedNumber == buttonNumber ? 0 : buttonNumber;
                for (ToggleButton tb : numberToggleButtons) {
                    if (tb != buttonView)
                        tb.setChecked(false);
                }
            }
        };
        for (ToggleButton tb : numberToggleButtons) {
            tb.setOnCheckedChangeListener(numberChangeChecker);
            tb.setOnLongClickListener(v -> {
                int buttonNumber = Integer.parseInt(((ToggleButton) v).getTextOff().toString());
                sudokuMain.highlightNumber = sudokuMain.highlightNumber == buttonNumber ? 0 : buttonNumber;
                sudokuSurfaceView.invalidate();
                return false;
            });
        }
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

    @Override
    public void onChangeEvent() {}

    @Override
    public void onHintFoundEvent(int number) {
        if (number != 0) {
            numberToggleButtons[number - 1].setChecked(true);
        }
    }
}

