package fr.c7regne.cceapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ShopFragment extends Fragment {
    private View v;
    private EditText nomAchatCourse,montantAchatCourse,numTicketAchatCourse,descriptifAchatCourse,remboursementAchatCourse;
    private CheckBox checkboxAchatCourse;
    private Button buttonValidationAchatCourse,buttonRemboursementAchatCourse;

    private Calendar calendar;
    private String fullDate;

    private Spinner spinner;
    private ListView listViewCourse;

    private ArrayList<String> data = new ArrayList<String>();

    //firstly, we create the view
    @SuppressLint("SimpleDateFormat")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_shop, container, false);

        nomAchatCourse=v.findViewById(R.id.nomAchatCourse);
        montantAchatCourse=v.findViewById(R.id.montantAchatCourse);
        numTicketAchatCourse=v.findViewById(R.id.numTicketAchatCourse);
        descriptifAchatCourse=v.findViewById(R.id.descriptifAchatCourse);
        checkboxAchatCourse=v.findViewById(R.id.checkboxAchatCourse);
        buttonValidationAchatCourse=v.findViewById(R.id.buttonValidationAchatCourse);

        final LinearLayout lremboursement = v.findViewById(R.id.lremboursement);
        remboursementAchatCourse=v.findViewById(R.id.remboursementAchatCourse);
        buttonRemboursementAchatCourse=v.findViewById(R.id.buttonRemboursementAchatCourse);

        calendar = Calendar.getInstance();
        fullDate = new SimpleDateFormat("dd/MMMM/yyyy").format(calendar.getTime());

        spinner=spinnerView();
        buttonValidationAchatCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(montantAchatCourse.getText().toString() != null){
                    int nTicketAchatCourse=0;
                    if(numTicketAchatCourse.getText().toString()!=null){
                        nTicketAchatCourse=Integer.parseInt(numTicketAchatCourse.getText().toString());
                    }
                    ExcelTable.createCourse(getContext(), fullDate,
                            nomAchatCourse.getText().toString(),
                            Double.parseDouble(montantAchatCourse.getText().toString()),
                            nTicketAchatCourse,
                            descriptifAchatCourse.getText().toString(),
                            checkboxAchatCourse.isChecked());
                    hideKeyboardFrom(getContext(),v);
                    Toast.makeText(getContext(), "Course ajoutée", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Veuillez rentrer au moins un montant", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getSelectedItem().toString();
                //if not "Sélectionner une personne" > action possible
                if (!item.equals("Sélectionner une course")) {
                    lremboursement.setVisibility(View.VISIBLE);
                    final Workbook workbook = ExcelTable.readFile(getContext());
                    Sheet s = workbook.getSheetAt(getResources().getInteger(R.integer.achat_course));
                    final Row r = ExcelTable.findMember(s, item.split(" ~ ")[0], item.split(" ~ ")[1]);

                } else {// if not > no action
                    lremboursement.setVisibility(View.GONE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        ListView lv = (ListView) v.findViewById(R.id.listViewCourse);
        generateListContent();
        lv.setAdapter(new MyListAdaper(getContext(), R.layout.list_item, data));

        return v;
    }

    private void generateListContent() {
        Workbook workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.achat_course));

        

        int nbRow = ExcelTable.numberRow(sheet);
        for (int i = 2; i < nbRow + 1; i++) {
            data.add(ExcelTable.getCellContent(sheet, i, 0) + " ~ " + ExcelTable.getCellContent(sheet, i, 1));
        }
    }

    private Spinner spinnerView() {
        //create a list of items for the spinner.
        ArrayList<String> items = new ArrayList<>();
        items.add("Sélectionner une course");
        Workbook workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.achat_course));

        int nbRow = ExcelTable.numberRow(sheet);
        for (int i = 2; i < nbRow + 1; i++) {
            items.add(ExcelTable.getCellContent(sheet, i, 0) + " ~ " + ExcelTable.getCellContent(sheet, i, 1));
        }

        //get the spinner from the xml.
        Spinner dropdown = v.findViewById(R.id.spinner);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterAT = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        // Specify the layout to use when the list of choices appears
        adapterAT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapterAT);


        return dropdown;
    }


    private ListView listViewShow(Context c,View v){

        ArrayList<String> items = new ArrayList<>();

        Workbook workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.achat_course));

        int nbRow = ExcelTable.numberRow(sheet);
        for (int i = 1; i < nbRow + 1; i++) {
            items.add(ExcelTable.getCellContent(sheet, i, 0) + "  " + ExcelTable.getCellContent(sheet, i, 1));
        }


        //get the listview from the xml.
        listViewCourse=v.findViewById(R.id.listViewCourse);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterAT = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        // Specify the layout to use when the list of choices appears
        adapterAT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        listViewCourse.setAdapter(adapterAT);

        return listViewCourse;
    }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.titresoirée = (TextView) convertView.findViewById(R.id.list_item_nomsoiree);
                viewHolder.dette = (TextView) convertView.findViewById(R.id.list_item_dette);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                }
            });
            mainViewholder.titresoirée.setText(mObjects.get(position));

            return convertView;
        }
    }
    public class ViewHolder {

        TextView titresoirée;
        TextView dette;
        Button button;
    }

    private static void hideKeyboardFrom(@NotNull Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
