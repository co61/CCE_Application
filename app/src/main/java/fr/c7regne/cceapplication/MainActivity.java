package fr.c7regne.cceapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Intent launchIntent;
    Toolbar toolbar;
    private ViewPager viewPager; //ViewPager is a pattern used to swipe horizontally between fragments
    private MenuItem previousSelectedItemId;

    private HomeFragment homeFragment;
    private TicketFragment ticketFragment;
    private PaymentFragment paymentFragment;
    private ShopFragment shopFragment;


    private BottomNavigationView bottomNavigationView;
    ExcelTable excelTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            File file = new File(getExternalFilesDir(null), getResources().getString(R.string.file_name));

            Log.i("path", getResources().getString(R.string.file_name));
            //check if the file exist
            if (!file.exists()) {

                excelTable = new ExcelTable();
                Workbook wb = excelTable.getOriginal();
                //save file
                ExcelTable.createFile(this, wb,file);

            } else { //if yes load the file
                try {
                    Workbook workbook = WorkbookFactory.create(file);
                    excelTable = new ExcelTable(workbook);
                    Toast.makeText(this, "XLS File load", Toast.LENGTH_SHORT).show();
                } catch (IOException | InvalidFormatException e) {
                    Toast.makeText(this, "Failed to load XLS File", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "You must authorised storage access", Toast.LENGTH_SHORT).show();
        }

        //initialize the swipe
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //initialize the bottom navigation bar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_bar_navigation);
        //watch if an item is selected on this bar and set the current view to it
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);


        //initialize the Top toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("CCE Home");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (previousSelectedItemId != null) {
                    previousSelectedItemId.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                previousSelectedItemId = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });

        setupViewPager(viewPager);
    }

    //create the different Fragments to switch between
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        ticketFragment = new TicketFragment();
        paymentFragment = new PaymentFragment();
        shopFragment = new ShopFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(ticketFragment);
        adapter.addFragment(paymentFragment);
        adapter.addFragment(shopFragment);
        viewPager.setAdapter(adapter);
    }

    //switch between the different fragment when an item is click on bottom menu
    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    //switching fragment depending on the item selected on the bottom bar
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            viewPager.setCurrentItem(0);
                            toolbar.setTitle("CCE Home");

                            break;
                        case R.id.nav_ticket:
                            viewPager.setCurrentItem(1);
                            toolbar.setTitle("Ticket");
                            break;
                        case R.id.nav_pay:
                            viewPager.setCurrentItem(2);
                            toolbar.setTitle("Payement");
                            break;
                        case R.id.nav_shop:
                            viewPager.setCurrentItem(3);
                            toolbar.setTitle("Course");
                            break;
                    }
                    return true;
                }
            };

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
