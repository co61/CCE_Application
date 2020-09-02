package fr.c7regne.cceapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    /**
     * affichage des information de la soirée tel que les recettes sur un premier volet
     * affichage d'un résumé rapide des membres sur un second volet
     * utilisation d'un viw pager custom pour avoir plusieurs item et controler leur couleur ou forme
     */
    private View v;

    private TextView dateEveningHome, nbpepoleEveningHome, nbticketEveningHome, nbsticketEveningHome, gainEveningHome;
    private Button button_reload_home;

    private Workbook workbook;
    private Calendar calendar;
    private String fullDate, dateChiffre;
    //firstly, we create the view

    private ListView lvho, lvhp;


    private ArrayList<String> data = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        lvho = (ListView) v.findViewById(R.id.listViewHomeOther);
        lvhp = (ListView) v.findViewById(R.id.listViewHomePresent);

        dateEveningHome = (TextView) v.findViewById(R.id.dateEveningHome);
        nbpepoleEveningHome = (TextView) v.findViewById(R.id.nbpepoleEveningHome);
        nbticketEveningHome = (TextView) v.findViewById(R.id.nbticketEveningHome);
        nbsticketEveningHome = (TextView) v.findViewById(R.id.nbsticketEveningHome);
        gainEveningHome = (TextView) v.findViewById(R.id.gainEveningHome);
        button_reload_home = (Button) v.findViewById(R.id.button_reload_home);


        calendar = Calendar.getInstance();
        fullDate = new SimpleDateFormat("dd/MMMM/yyyy").format(calendar.getTime());
        dateChiffre = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

        workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.compte_rendu_soiree));
        Row row = ExcelTable.findEvening(sheet, fullDate);
        if (row != null) {
            nbpepoleEveningHome.setVisibility(View.VISIBLE);
            nbticketEveningHome.setVisibility(View.VISIBLE);
            nbsticketEveningHome.setVisibility(View.VISIBLE);
            gainEveningHome.setVisibility(View.VISIBLE);
            dateEveningHome.setText("Date : " + row.getCell(0).getStringCellValue());
            nbpepoleEveningHome.setText("Repas réservé : " + (Integer.parseInt(ExcelTable.getCellContent(sheet, row.getRowNum(), 1))+
                    Integer.parseInt(ExcelTable.getCellContent(sheet, row.getRowNum(), 2))));
            nbticketEveningHome.setText("Avec ticket : " + ExcelTable.getCellContent(sheet, row.getRowNum(), 1));
            nbsticketEveningHome.setText("Sans ticket : " + ExcelTable.getCellContent(sheet, row.getRowNum(), 2));
            gainEveningHome.setText("Gain réel de la soirée : " + row.getCell(6).getNumericCellValue());
        } else {
            dateEveningHome.setText("Pas de soirée enregistrée pour aujourd'hui");
            nbpepoleEveningHome.setVisibility(View.GONE);
            nbticketEveningHome.setVisibility(View.GONE);
            nbsticketEveningHome.setVisibility(View.GONE);
            gainEveningHome.setVisibility(View.GONE);
        }
        button_reload_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workbook = ExcelTable.readFile(getContext());
                Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.compte_rendu_soiree));
                Row row = ExcelTable.findEvening(sheet, fullDate);
                if (row != null) {
                    nbpepoleEveningHome.setVisibility(View.VISIBLE);
                    nbticketEveningHome.setVisibility(View.VISIBLE);
                    nbsticketEveningHome.setVisibility(View.VISIBLE);
                    gainEveningHome.setVisibility(View.VISIBLE);
                    dateEveningHome.setText("Date : " + row.getCell(0).getStringCellValue());
                    nbpepoleEveningHome.setText("Repas réservé : " + (Integer.parseInt(ExcelTable.getCellContent(sheet, row.getRowNum(), 1))+
                            Integer.parseInt(ExcelTable.getCellContent(sheet, row.getRowNum(), 2))));
                    nbticketEveningHome.setText("Avec ticket : " + ExcelTable.getCellContent(sheet, row.getRowNum(), 1));
                    nbsticketEveningHome.setText("Sans ticket : " + ExcelTable.getCellContent(sheet, row.getRowNum(), 2));
                    gainEveningHome.setText("Recette de la soirée : " + row.getCell(6).getNumericCellValue());
                } else {
                    dateEveningHome.setText("Pas de soirée enregistrée pour aujourd'hui");
                    nbpepoleEveningHome.setVisibility(View.GONE);
                    nbticketEveningHome.setVisibility(View.GONE);
                    nbsticketEveningHome.setVisibility(View.GONE);
                    gainEveningHome.setVisibility(View.GONE);
                }

                generateListContent();

                lvho.setAdapter(new HomeListAdapter(getContext(), R.layout.list_item2, data));

            }
        });

        generateListContent();

        lvho.setAdapter(new HomeListAdapter(getContext(), R.layout.list_item2, data));



        return v;


    }

    public void updateEditText(CharSequence d_ddmmyy,CharSequence date,CharSequence d_dMy) {
        fullDate=(String)d_ddmmyy;
        dateChiffre=(String)d_dMy;

    }

    private void generateListContent() {
        Workbook workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.compte_membre));

        data = new ArrayList<>();
        data.add("Personnes présentes :");

        int nbRow = ExcelTable.numberRow(sheet);
        for (int i = 1; i < nbRow + 1; i++) {
            if (ExcelTable.getCellContent(sheet, i, 6).equals(dateChiffre)) {

                data.add(ExcelTable.getCellContent(sheet, i, 1) +
                        "¤" + "Dette : " + ExcelTable.getCellContent(sheet, i, 4) +
                        "¤" + "Ticket : " + ExcelTable.getCellContent(sheet, i, 2) +
                        "¤" + "Repas le : " + ExcelTable.getCellContent(sheet, i, 6)
                );
            } else {
                data.add(0,ExcelTable.getCellContent(sheet, i, 0) + " " + ExcelTable.getCellContent(sheet, i, 1) +
                        "¤" + "Dette : " + ExcelTable.getCellContent(sheet, i, 4) +
                        "¤" + "Ticket : " + ExcelTable.getCellContent(sheet, i, 2) +
                        "¤" + "Repas le : " + ExcelTable.getCellContent(sheet, i, 6)
                );
            }
        }
    }

    private class HomeListAdapter extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;

        private HomeListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HomeFragment.ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();

                viewHolder.nompersonnehome = (TextView) convertView.findViewById(R.id.list_item2_nomsoiree);
                viewHolder.dettehome = (TextView) convertView.findViewById(R.id.list_item2_dette);
                viewHolder.nombreticket = (TextView) convertView.findViewById(R.id.list_item2_nombreticket);
                viewHolder.dernierrepas = (TextView) convertView.findViewById(R.id.list_item2_dernierrepas);

                viewHolder.list_item_layouthome = (LinearLayout) convertView.findViewById(R.id.list_item2_layout);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (HomeFragment.ViewHolder) convertView.getTag();

            if(mObjects.get(position).equals("Personnes présentes :")){
                mainViewholder.nompersonnehome.setText("Personnes présentes :");
                mainViewholder.dettehome.setText("");
                mainViewholder.nombreticket.setVisibility(View.GONE);
                mainViewholder.dernierrepas.setVisibility(View.GONE);
                mainViewholder.list_item_layouthome.setBackgroundColor(Color.parseColor("#ACF2FB"));
            }else {
                mainViewholder.nompersonnehome.setText(mObjects.get(position).split("¤")[0]);
                mainViewholder.dettehome.setText(mObjects.get(position).split("¤")[1]);
                mainViewholder.nombreticket.setText(mObjects.get(position).split("¤")[2]);
                mainViewholder.dernierrepas.setText(mObjects.get(position).split("¤")[3]);
                if (Double.parseDouble(mObjects.get(position).split("¤")[1].split(" : ")[1]) == 0) {
                    mainViewholder.list_item_layouthome.setBackgroundColor(Color.parseColor("#9CD74E"));
                } else {
                    mainViewholder.list_item_layouthome.setBackgroundColor(Color.parseColor("#F14848"));
                }
            }
            return convertView;
        }
    }

    public class ViewHolder {
        TextView nombreticket;
        TextView dernierrepas;
        LinearLayout list_item_layouthome;
        TextView nompersonnehome;
        TextView dettehome;
    }
}
