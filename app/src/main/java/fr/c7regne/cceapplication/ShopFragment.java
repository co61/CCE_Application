package fr.c7regne.cceapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private ArrayList<String> data = new ArrayList<>();
    ListView lv;

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
        lv = (ListView) v.findViewById(R.id.listViewCourse);

        calendar = Calendar.getInstance();
        fullDate = new SimpleDateFormat("dd/MMMM/yyyy").format(calendar.getTime());


        buttonValidationAchatCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!montantAchatCourse.getText().toString().equals("")){
                    int nTicketAchatCourse=0;
                    if(!numTicketAchatCourse.getText().toString().equals("")){
                        nTicketAchatCourse=Integer.parseInt(numTicketAchatCourse.getText().toString());
                    }
                    ExcelTable.createCourse(getContext(), fullDate,
                            nomAchatCourse.getText().toString(),
                            Double.parseDouble(montantAchatCourse.getText().toString()),
                            nTicketAchatCourse,
                            descriptifAchatCourse.getText().toString(),
                            checkboxAchatCourse.isChecked());
                    hideKeyboardFrom(getContext(),v);
                    generateListContent();
                    lv.setAdapter(new MyListAdaper(getContext(), R.layout.list_item, data));
                    Toast.makeText(getContext(), "Course ajoutée", Toast.LENGTH_SHORT).show();
                    nomAchatCourse.setText(null);
                    montantAchatCourse.setText(null);
                    numTicketAchatCourse.setText(null);
                    descriptifAchatCourse.setText(null);
                    checkboxAchatCourse.setChecked(true);
                }else {
                    Toast.makeText(getContext(), "Veuillez rentrer au moins un montant", Toast.LENGTH_SHORT).show();
                }
            }
        });



        generateListContent();
        lv.setAdapter(new MyListAdaper(getContext(), R.layout.list_item, data));

        return v;
    }

    private void generateListContent() {
        Workbook workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.achat_course));

        data=new ArrayList<>();
        int nbRow = ExcelTable.numberRow(sheet);
        for (int i = 2; i < nbRow + 1; i++) {
            data.add(ExcelTable.getCellContent(sheet, i, 0)+"¤"+ExcelTable.getCellContent(sheet, i, 1)+
                    "¤"+"Dette : "+ExcelTable.getCellContent(sheet, i, 2)+"¤"+ExcelTable.getCellContent(sheet, i, 4)
            +"¤"+ExcelTable.getCellContent(sheet,i,6));
        }
    }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.datesoirée = (TextView) convertView.findViewById(R.id.list_item_datesoiree);
                viewHolder.nompersonne = (TextView) convertView.findViewById(R.id.list_item_nomsoiree);
                viewHolder.dette = (TextView) convertView.findViewById(R.id.list_item_dette);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                viewHolder.list_item_layout = (LinearLayout) convertView.findViewById(R.id.list_item_layout);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExcelTable.updateCourse(getContext(),mObjects.get(position).split("¤")[0],
                            mObjects.get(position).split("¤")[1],
                            Double.parseDouble(mObjects.get(position).split("¤")[2].split(" : ")[1]),
                            Double.parseDouble(mObjects.get(position).split("¤")[4]));
                    Toast.makeText(getContext(), "Remboursement effectué", Toast.LENGTH_SHORT).show();
                    generateListContent();
                    lv.setAdapter(new MyListAdaper(getContext(), R.layout.list_item, data));
                }
            });
            mainViewholder.datesoirée.setText(mObjects.get(position).split("¤")[0].split("/")[0]+
                    " "+mObjects.get(position).split("¤")[0].split("/")[1]+
                    " "+mObjects.get(position).split("¤")[0].split("/")[2]);

            mainViewholder.nompersonne.setText(mObjects.get(position).split("¤")[1]);
            if(mObjects.get(position).split("¤")[3].equals("Non Remboursé")) {
                mainViewholder.button.setVisibility(View.VISIBLE);
                mainViewholder.dette.setText(mObjects.get(position).split("¤")[2]);
                mainViewholder.list_item_layout.setBackgroundColor(Color.parseColor("#F14848"));
            }else{
                mainViewholder.dette.setText("Montant : "+mObjects.get(position).split("¤")[2].split(" : ")[1]);
                mainViewholder.button.setVisibility(View.GONE);
                mainViewholder.list_item_layout.setBackgroundColor(Color.parseColor("#9CD74E"));
            }
            return convertView;
        }
    }
    public class ViewHolder {
        LinearLayout list_item_layout;
        TextView datesoirée;
        TextView nompersonne;
        TextView dette;
        Button button;
    }

    private static void hideKeyboardFrom(@NotNull Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
