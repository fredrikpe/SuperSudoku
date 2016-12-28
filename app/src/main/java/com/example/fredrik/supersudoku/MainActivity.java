package com.example.fredrik.supersudoku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.fredrik.supersudoku.cameraimport.CameraActivity;
import com.example.fredrik.supersudoku.customs.EventListener;
import com.example.fredrik.supersudoku.customs.PadButton;
import com.example.fredrik.supersudoku.sudokulogic.Hint;
import com.example.fredrik.supersudoku.sudokulogic.MarkMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventListener{

    List<PadButton> numberButtons;

    LinearLayout mainLayout;
    SudokuSurfaceView sudokuSurfaceView;

    SudokuMain sudokuMain;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sudokuMain = new SudokuMain(this);
        sudokuMain.board.addEventListener(this);

        setContentView(R.layout.activity_main);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        sudokuSurfaceView = new SudokuSurfaceView(this);

        mainLayout.addView(sudokuSurfaceView, 0);

        init_buttons();
        findViewById(R.id.button1).performClick();

        sudokuMain.newGame(3);
    }

    void init_buttons() {
        int[] buttonIds = new int[]{R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9};

        numberButtons = new ArrayList<>();
        for (int bId : buttonIds) {
            PadButton button = (PadButton) findViewById(bId);
            numberButtons.add(button);
        }

        for (PadButton button : numberButtons) {
            button.setOnClickListener(view -> {
                int num = Integer.parseInt(button.getText().toString());
                sudokuMain.selectedNumber = sudokuMain.selectedNumber == num ? 0 : num;
                if (sudokuMain.selectedNumber == num) {
                    button.color = ContextCompat.getColor(this, R.color.colorSecondaryDark);
                    //button.invalidate();
                    for (PadButton b : numberButtons) {
                        if (b == button) continue;
                        b.color = ContextCompat.getColor(this, R.color.colorSecondary);
                    }
                } else {
                    button.color = ContextCompat.getColor(this, R.color.colorSecondary);
                }
                sudokuSurfaceView.invalidate();
            });
        }

        findViewById(R.id.hintButton).setOnClickListener(view -> sudokuMain.board.hint());
        findViewById(R.id.undoButton).setOnClickListener(view -> sudokuMain.board.undo());
        for (int bId : new int[] {R.id.clearButton, R.id.candidateButton}) {
            PadButton button = (PadButton) findViewById(bId);
            button.setOnClickListener(view -> {
                MarkMode mode = MarkMode.CLEAR;
                if (bId == R.id.candidateButton) {
                    mode = MarkMode.CANDIDATE;
                }

                sudokuMain.markMode = sudokuMain.markMode == mode ? MarkMode.FILL : mode;
                if (sudokuMain.markMode == mode) {
                    button.color = ContextCompat.getColor(this, R.color.colorSecondaryDark);
                    PadButton other = (PadButton) findViewById(R.id.candidateButton);
                    if (bId == R.id.candidateButton) {
                        other = (PadButton) findViewById(R.id.clearButton);
                    }
                    other.color = ContextCompat.getColor(this, R.color.colorSecondary);
                } else {
                    button.color = ContextCompat.getColor(this, R.color.colorSecondary);
                }
            });
        }
        findViewById(R.id.optionsButton).setOnClickListener(view -> openDrawer());
    }

    public void openDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_game) {
            // Handle the camera action
            CharSequence colors[] = new CharSequence[] {"Super easy", "Very easy", "Easy", "Medium", "Hard"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose difficulty");
            builder.setItems(colors, (dialog, which) -> sudokuMain.newGame(which));
            builder.show();
        } else if (id == R.id.nav_import) {
            // Import from camera
            startActivity(new Intent(this, CameraActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onChangeEvent() {}

    @Override
    public void onHintFoundEvent(Hint hint) {
        if (hint.number != 0) {
            if (sudokuMain.selectedNumber != hint.number)
                numberButtons.get(hint.number - 1).performClick();
        }
        findViewById(R.id.padLayout).setVisibility(View.GONE);
        findViewById(R.id.hintLayout).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.hintEditText)).setText(hint.string());
    }

    public void onHintOkClick(View v) {
        findViewById(R.id.padLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.hintLayout).setVisibility(View.GONE);

    }
}

