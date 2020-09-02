package fr.c7regne.cceapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements TicketFragment.TicketFragmentListener {
    Toolbar toolbar;
    private ViewPager viewPager; //ViewPager is a pattern used to swipe horizontally between fragments
    private MenuItem previousSelectedItemId;

    private HomeFragment homeFragment;
    private TicketFragment ticketFragment;
    private ShopFragment shopFragment;


    private BottomNavigationView bottomNavigationView;
    private ExcelTable excelTable;

    private int STORAGE_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        requestStoragePermission();


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File file = new File(getExternalFilesDir(null), getResources().getString(R.string.file_name));

            //check if the file exist
            if (!file.exists()) {

                excelTable = new ExcelTable();
                Workbook wb = excelTable.getOriginal();
                //save file
                ExcelTable.createFile(this, wb, file);

            } else { //if yes load the file
                try {
                    Workbook workbook = WorkbookFactory.create(file);
                    excelTable = new ExcelTable(workbook);
                    Toast.makeText(this, "Ficher Excel Chargé", Toast.LENGTH_SHORT).show();
                } catch (IOException | InvalidFormatException e) {
                    Toast.makeText(this, "Erreur pour charger le fichier Excel", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Vous devez autoriser l'accés au stockage", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onInputSent(CharSequence date_ddMMMMyyyy, CharSequence date, CharSequence dateChiffre) {
        homeFragment.updateEditText( date_ddMMMMyyyy,  date,  dateChiffre);
    }

    //create the different Fragments to switch between
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        ticketFragment = new TicketFragment();
        shopFragment = new ShopFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(ticketFragment);
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
                            break;
                        case R.id.nav_ticket:
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.nav_shop:
                            viewPager.setCurrentItem(2);
                            break;
                    }
                    return true;
                }
            };

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission nécessaire")
                    .setMessage("Cette permission est nécessaire pour remplir le fichier Excel")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permission Refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
