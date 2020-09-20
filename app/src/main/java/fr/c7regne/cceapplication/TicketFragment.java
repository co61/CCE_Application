package fr.c7regne.cceapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TicketFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    /**
     * Ce fragment gère les réservations des membres et des nouveaux membres pour chaque soirée
     * que ce soit achat de ticket, remboursement de dette ou prise d'un repas pour la soirée (ou plusieurs)
     * ne retourne rien, utilise la class ExcelTable pour uptdate les tableaux ou les enregistrer
     */
    private View v;
    private Spinner[] spinners = null;
    //create evening
    private LinearLayout fillEveningLayout, createEveningLayout;
    private TextView dateTicket;
    private Button createEvening;
    private Calendar calendar;
    private String date_ddMMMMyyyy, dateChiffre, date;

    //acces to component on view to update excel file
    private Button newMemberButton, createMemberButton, buttonValidationAT, buttonValidationST, ajoutticket, buttonRembourserST, buttonRembourserAT;
    private EditText prenomnewMember, montantST, montantDetteRemboursementST, montantDetteRemboursementAT, reducctionTicket;
    private TextView nbRepasAT, nbRepasST, montantAT, nbticketinfo, detteinfoAT, detteinfoST, nbTicketAchat;
    private CheckBox checkBoxST, checkboxAT;
    private ImageView minusAT, minusST, plusAT, plusST, minusAchaTicket, plusAchaTicket;

    private double prixRepasTicket, limitTicket;

    private TicketFragmentListener listener;

    public interface TicketFragmentListener {
        void onInputSent(CharSequence date_ddMMMMyyyy,CharSequence date,CharSequence dateChiffre);
    }

    //firstly, we create the view
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_ticket, container, false);

        //create evening
        fillEveningLayout = v.findViewById(R.id.fillEveningLayout);
        createEveningLayout = v.findViewById(R.id.createEveningLayout);
        //read current date
        dateTicket = v.findViewById(R.id.dateTicket);
        createEvening = v.findViewById(R.id.createEvening);
        calendar = Calendar.getInstance();
        date_ddMMMMyyyy = new SimpleDateFormat("dd/MMMM/yyyy").format(calendar.getTime());
        date = new SimpleDateFormat("dd MMMM yyyy").format(calendar.getTime());
        dateChiffre = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
        dateTicket.setText(date);

        Button button = (Button) v.findViewById(R.id.pickDateButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(); // creating DialogFragment which creates DatePickerDialog
                newFragment.setTargetFragment(TicketFragment.this, 0);  // Passing this fragment DatePickerFragment.
                // As i figured out this is the best way to keep the reference to calling activity when using FRAGMENT.
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        //check if evening create or not, set visible or not the tow layout
        if (ExcelTable.checkEvening(getContext(), date_ddMMMMyyyy)) {
            fillEveningLayout.setVisibility(View.GONE);
            createEveningLayout.setVisibility(View.VISIBLE);
            createEvening.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fillEveningLayout.setVisibility(View.VISIBLE);
                    createEveningLayout.setVisibility(View.GONE);
                    ExcelTable.createNewEvening(getContext(), date_ddMMMMyyyy);
                    Toast.makeText(getContext(), "Soirée créee", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fillEveningLayout.setVisibility(View.VISIBLE);
            createEveningLayout.setVisibility(View.GONE);
        }
        //fill the spinners, see fonction for more details
        spinners = spinnerView();

        final LinearLayout lmemberinfo = v.findViewById(R.id.linfoMember);

        final LinearLayout ldetteSt = v.findViewById(R.id.ldetteSt);
        final LinearLayout lnewMember = v.findViewById(R.id.newMember);

        prenomnewMember = v.findViewById(R.id.prenomnewMember);
        newMemberButton = v.findViewById(R.id.newMemberButton);
        createMemberButton = v.findViewById(R.id.createMemberButton);
        newMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnewMember.setVisibility(View.VISIBLE);
                newMemberButton.setVisibility(View.GONE);
                createMemberButton.setVisibility(View.VISIBLE);
                createMemberButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!prenomnewMember.getText().toString().equals("")) {
                            if (ExcelTable.checkNotMember(getContext(), prenomnewMember.getText().toString())) {
                                //create member in member sheet
                                ExcelTable.createNewMember(getContext(), prenomnewMember.getText().toString(), dateChiffre);

                                //reset value and hide keyboard
                                prenomnewMember.setText(null);
                                spinners = spinnerView();
                                hideKeyboardFrom(getContext(), v);
                                Toast.makeText(getContext(), "Membre crée", Toast.LENGTH_SHORT).show();
                                lnewMember.setVisibility(View.GONE);
                                newMemberButton.setVisibility(View.VISIBLE);
                                createMemberButton.setVisibility(View.GONE);
                            } else {
                                if (ExcelTable.checkNotMember(getContext(), prenomnewMember.getText().toString())) {
                                    //create member in member sheet
                                    ExcelTable.createNewMember(getContext(), prenomnewMember.getText().toString(), dateChiffre);

                                    //reset value and hide keyboard
                                    prenomnewMember.setText(null);
                                    spinners = spinnerView();
                                    hideKeyboardFrom(getContext(), v);
                                    Toast.makeText(getContext(), "Membre crée", Toast.LENGTH_SHORT).show();
                                    lnewMember.setVisibility(View.GONE);
                                    newMemberButton.setVisibility(View.VISIBLE);
                                    createMemberButton.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(getContext(), "Ce membre existe déjà", Toast.LENGTH_SHORT).show();
                                }
                            }


                        } else {
                            Toast.makeText(getContext(), "Entrez un prénom avant de valider", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        //get elements of the view for "with ticket"
        buttonValidationAT = v.findViewById(R.id.buttonValidationAT);
        nbRepasAT = v.findViewById(R.id.nbRepasAT);
        montantAT = v.findViewById(R.id.montantAT);
        nbticketinfo = v.findViewById(R.id.nbticketinfo);
        detteinfoAT = v.findViewById(R.id.detteinfoAT);
        ajoutticket = v.findViewById(R.id.ajoutticket);
        buttonRembourserAT = v.findViewById(R.id.buttonRembourserAT);
        montantDetteRemboursementAT = v.findViewById(R.id.montantDetteRemboursementAT);
        minusAT = v.findViewById(R.id.minusAT);
        plusAT = v.findViewById(R.id.plusAt);
        reducctionTicket = v.findViewById(R.id.reducctionTicket);
        nbTicketAchat = v.findViewById(R.id.nbTicketAchat);
        minusAchaTicket = v.findViewById(R.id.minusAchaTicket);
        plusAchaTicket = v.findViewById(R.id.plusAchaTicket);
        checkboxAT = v.findViewById(R.id.checkboxAT);

        //get elements of the view for "without ticket"
        buttonValidationST = v.findViewById(R.id.buttonValidationST);
        nbRepasST = v.findViewById(R.id.nbRepasST);
        checkBoxST = v.findViewById(R.id.checkboxST);
        montantST = v.findViewById(R.id.montantST);
        detteinfoST = v.findViewById(R.id.detteinfoST);
        buttonRembourserST = v.findViewById(R.id.buttonRembourserST);
        montantDetteRemboursementST = v.findViewById(R.id.montantDetteRemboursementST);
        minusST = v.findViewById(R.id.minusST);
        plusST = v.findViewById(R.id.plusST);

        //AT Part/////////////////////////////////////////////////////////////////////////////////////////////
        spinners[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String item = parent.getSelectedItem().toString();

                //if not "Sélectionner une personne" > action possible
                if (!item.equals("Sélectionner une personne")) {
                    buttonValidationAT.setClickable(true);
                    lmemberinfo.setVisibility(View.VISIBLE);
                    final Workbook workbook = ExcelTable.readFile(getContext());
                    Sheet s = workbook.getSheetAt(getResources().getInteger(R.integer.compte_membre));
                    final Row r = ExcelTable.findMember(s, item);
                    prixRepasTicket = r.getCell(5).getNumericCellValue();
                    limitTicket = r.getCell(2).getNumericCellValue();
                    if (limitTicket == 0) {
                        nbRepasAT.setText("0");
                    }
                    montantAT.setText(String.valueOf(prixRepasTicket * Integer.parseInt(nbRepasAT.getText().toString())));
                    nbticketinfo.setText("Ticket : " + r.getCell(2).getNumericCellValue());
                    //update ticket
                    minusAchaTicket.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int nb = Integer.parseInt(nbTicketAchat.getText().toString());
                            if (nb != 0) {
                                nbTicketAchat.setText(String.valueOf(nb - 1));
                                reducctionTicket.setText(String.valueOf(getContext().getResources().getInteger(R.integer.prix_repas) * (nb - 1)));
                            }
                        }
                    });
                    plusAchaTicket.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int nb = Integer.parseInt(nbTicketAchat.getText().toString());
                            nbTicketAchat.setText(String.valueOf(nb + 1));
                            reducctionTicket.setText(String.valueOf(getContext().getResources().getInteger(R.integer.prix_repas) * (nb + 1)));
                        }
                    });
                    ajoutticket.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {
                            //set a new ticket price with ancient and new
                            // 2*2.90 + 7.2.94
                            // ---------------
                            //        2+7
                            double moyennePrixTicket =
                                    ((r.getCell(2).getNumericCellValue() * r.getCell(5).getNumericCellValue())
                                            + (Integer.parseInt(nbTicketAchat.getText().toString()) *
                                            (Double.parseDouble(reducctionTicket.getText().toString())
                                                    / Integer.parseInt(nbTicketAchat.getText().toString()))))
                                            / (r.getCell(2).getNumericCellValue() + Integer.parseInt(nbTicketAchat.getText().toString()));
                            DecimalFormat f = new DecimalFormat();
                            f.setMaximumFractionDigits(2);
                            moyennePrixTicket = Double.parseDouble(f.format(moyennePrixTicket).replace(',', '.'));

                            Cell cell = r.getCell(5);
                            cell.setCellValue(moyennePrixTicket);
                            prixRepasTicket = moyennePrixTicket;
                            //get ticcket to the member
                            cell = r.getCell(2);
                            cell.setCellValue(cell.getNumericCellValue() + Integer.parseInt(nbTicketAchat.getText().toString()));
                            nbticketinfo.setText("Ticket : " + r.getCell(2).getNumericCellValue());
                            limitTicket = r.getCell(2).getNumericCellValue();
                            nbRepasAT.setText("1");
                            montantAT.setText(String.valueOf(prixRepasTicket));

                            if (!checkboxAT.isChecked()) {
                                cell = r.getCell(4);
                                cell.setCellValue(cell.getNumericCellValue() + Double.parseDouble(reducctionTicket.getText().toString()));
                            }
                            ExcelTable.saveFile(getContext(), workbook, new File(getContext().getExternalFilesDir(null), getContext().getResources().getString(R.string.file_name)));
                            ExcelTable.updateTicket(getContext(), item, date_ddMMMMyyyy,
                                    Integer.parseInt(nbTicketAchat.getText().toString()),
                                    Double.parseDouble(reducctionTicket.getText().toString()),
                                    checkboxAT.isChecked());
                            Log.i("eeeeeeeeeeee", String.valueOf(checkboxAT.isChecked()));
                            ExcelTable.updateEvening(getContext(), date_ddMMMMyyyy, 1000, 1000,
                                    Double.parseDouble(reducctionTicket.getText().toString()), checkboxAT.isChecked(), false);
                            nbTicketAchat.setText("7");
                            reducctionTicket.setText("21");
                            hideKeyboardFrom(getContext(), v);
                            //spinners = spinnerView();
                            Toast.makeText(getContext(), "Tickets ajoutés", Toast.LENGTH_SHORT).show();
                            ajoutticket.setClickable(false);
                            checkboxAT.setChecked(true);
                            detteinfoAT.setText("Dette : " + r.getCell(4).getNumericCellValue());

                        }
                    });
                    //ExcelTable.updateTicket();
                    //update dues
                    detteinfoAT.setText("Dette : " + r.getCell(4).getNumericCellValue());
                    Log.i("Dette : " , String.valueOf(r.getCell(4).getNumericCellValue()));
                    if (r.getCell(4).getNumericCellValue() == 0) {
                        montantDetteRemboursementAT.setEnabled(false);
                        buttonRembourserAT.setClickable(false);
                    } else {
                        montantDetteRemboursementAT.setEnabled(true);
                        buttonRembourserAT.setClickable(true);
                        buttonRembourserAT.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                double montant = Double.parseDouble(montantDetteRemboursementAT.getText().toString());
                                if (montant != 0) {
                                    r.getCell(4).setCellValue(r.getCell(4).getNumericCellValue() - montant);
                                    ExcelTable.saveFile(getContext(), workbook, new File(getContext().getExternalFilesDir(null), getContext().getResources().getString(R.string.file_name)));
                                    ExcelTable.updateEvening(getContext(), date_ddMMMMyyyy, 999, 999, montant, true, true);
                                    detteinfoAT.setText("Dette : " + r.getCell(4).getNumericCellValue());
                                    montantDetteRemboursementAT.setText("0.0");
                                    //spinners = spinnerView();
                                    hideKeyboardFrom(getContext(), v);
                                    Toast.makeText(getContext(), "Remboursement effectué", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Entre un montant", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }


                } else { // if not > no action
                    buttonValidationAT.setClickable(false);
                    lmemberinfo.setVisibility(View.GONE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonValidationAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinners[0].getSelectedItem().toString().equals("Sélectionner une personne")) {
                    Toast.makeText(getContext(), "Veuillez sélectionner une personne éxistante ou Nouvô pour créer un nouveau compte", Toast.LENGTH_SHORT).show();
                } else {

                    if (!nbRepasAT.getText().toString().equals("0")) {
                        //update member sheet
                        ExcelTable.updateMember(getContext(), spinners[0].getSelectedItem().toString(),
                                Integer.parseInt(nbRepasAT.getText().toString()), 0, Double.parseDouble(montantAT.getText().toString()), true, dateChiffre);
                        //update evening sheet
                        ExcelTable.updateEvening(getContext(), date_ddMMMMyyyy,
                                Integer.parseInt(nbRepasAT.getText().toString()), 0,
                                Double.parseDouble(montantAT.getText().toString()), true, false);
                        nbRepasAT.setText("1");
                        montantAT.setText(getResources().getString(R.string

                                .prix_repas));
                        hideKeyboardFrom(getContext(), v);
                        Toast.makeText(getContext(), "Repas enregistré", Toast.LENGTH_SHORT).show();
                        spinners = spinnerView();
                        ajoutticket.setClickable(true);
                    } else {
                        Toast.makeText(getContext(), "La personne ne possède pas assez de ticket", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        //ST Part/////////////////////////////////////////////////////////////////////////////////////////////
        spinners[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getSelectedItem().toString();
                //if not "Sélectionner une personne" > action possible
                if (!item.equals("Sélectionner une personne")) {
                    buttonValidationST.setClickable(true);
                    ldetteSt.setVisibility(View.VISIBLE);
                    final Workbook workbook = ExcelTable.readFile(getContext());
                    Sheet s = workbook.getSheetAt(getResources().getInteger(R.integer.compte_membre));
                    final Row r = ExcelTable.findMember(s, item);

                    detteinfoST.setText("Dette : " + r.getCell(4).getNumericCellValue());
                    if (r.getCell(4).getNumericCellValue() == 0) {
                        montantDetteRemboursementST.setEnabled(false);
                        buttonRembourserST.setClickable(false);
                    } else {
                        montantDetteRemboursementST.setEnabled(true);
                        buttonRembourserST.setClickable(true);
                        buttonRembourserST.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                double montant = Double.parseDouble(montantDetteRemboursementST.getText().toString());
                                if (montant != 0) {
                                    r.getCell(4).setCellValue(r.getCell(4).getNumericCellValue() - montant);
                                    ExcelTable.saveFile(getContext(), workbook, new File(getContext().getExternalFilesDir(null), getContext().getResources().getString(R.string.file_name)));
                                    ExcelTable.updateEvening(getContext(), date_ddMMMMyyyy, 999, 999, montant, true, true);
                                    detteinfoST.setText("Dette : " + r.getCell(4).getNumericCellValue());
                                    montantDetteRemboursementST.setText("0.0");
                                    hideKeyboardFrom(getContext(), v);
                                    Toast.makeText(getContext(), "Remboursement effectué", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Entre un montant", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {// if not > no action
                    buttonValidationST.setClickable(false);
                    ldetteSt.setVisibility(View.GONE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        buttonValidationST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinners[1].getSelectedItem().toString().equals("Sélectionner une personne")) {
                    Toast.makeText(getContext(), "Veuillez sélectionner une personne éxistante ou Nouvô pour créer un nouveau compte", Toast.LENGTH_SHORT).show();
                } else {
                    //update member sheet
                    ExcelTable.updateMember(getContext(), spinners[1].getSelectedItem().toString(),
                            0, Integer.parseInt(nbRepasST.getText().toString()), Double.parseDouble(montantST.getText().toString()),
                            checkBoxST.isChecked(), dateChiffre);
                    //update evening sheet
                    ExcelTable.updateEvening(getContext(), date_ddMMMMyyyy,
                            0, Integer.parseInt(nbRepasST.getText().toString()), Double.parseDouble(montantST.getText().toString()),
                            checkBoxST.isChecked(), false);
                    nbRepasST.setText("1");
                    montantST.setText(getResources().getString(R.string.prix_repas));
                    checkBoxST.setChecked(true);
                    hideKeyboardFrom(getContext(), v);
                    Toast.makeText(getContext(), "Repas enregistré", Toast.LENGTH_SHORT).show();
                    spinners = spinnerView();
                }


            }
        });


        minusAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nb = Integer.parseInt(nbRepasAT.getText().toString());
                if (nb != 0) {
                    nbRepasAT.setText(String.valueOf(nb - 1));
                    montantAT.setText(String.valueOf(prixRepasTicket * (nb - 1)));
                }
            }
        });
        plusAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nb = Integer.parseInt(nbRepasAT.getText().toString());
                if (nb < limitTicket) {
                    nbRepasAT.setText(String.valueOf(nb + 1));
                    montantAT.setText(String.valueOf(prixRepasTicket * (nb + 1)));
                }
            }
        });

        minusST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nb = Integer.parseInt(nbRepasST.getText().toString());
                if (nb != 0) {
                    nbRepasST.setText(String.valueOf(nb - 1));
                    montantST.setText(String.valueOf(Float.parseFloat(getResources().getString(R.string.prix_repas)) * (nb - 1)));
                }
            }
        });
        plusST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nb = Integer.parseInt(nbRepasST.getText().toString());
                nbRepasST.setText(String.valueOf(nb + 1));
                montantST.setText(String.valueOf(Float.parseFloat(getResources().getString(R.string.prix_repas)) * (nb + 1)));
            }
        });

        return v;
    }


    private Spinner[] spinnerView() {
        //create a list of items for the spinner.
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> presence = new ArrayList<>();
        items.add("Sélectionner une personne");
        presence.add(false);
        Workbook workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.compte_membre));

        int nbRow = ExcelTable.numberRow(sheet);
        for (int i = 1; i < nbRow + 1; i++) {
            items.add(ExcelTable.getCellContent(sheet, i, 1));
        }

        for (int i = 1; i < nbRow + 1; i++) {
            if (ExcelTable.getCellContent(sheet, i, 6).equals(dateChiffre)) {
                presence.add(true);
            } else {
                presence.add(false);
            }
        }


        Spinner dropdownAT = v.findViewById(R.id.spinnerAT);
        CustomArrayAdapter<ArrayList<String>> customArrayAdapterAT = new CustomArrayAdapter<ArrayList<String>>(getContext(), items, presence);
        // Specify the layout to use when the list of choices appears
        customArrayAdapterAT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropdownAT.setAdapter(customArrayAdapterAT);

        Spinner dropdownST = v.findViewById(R.id.spinnerST);
        CustomArrayAdapter<ArrayList<String>> customArrayAdapterST = new CustomArrayAdapter<ArrayList<String>>(getContext(), items, presence);
        // Specify the layout to use when the list of choices appears
        customArrayAdapterAT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropdownST.setAdapter(customArrayAdapterAT);

        return new Spinner[]{dropdownAT, dropdownST};
    }

    private static void hideKeyboardFrom(@NotNull Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static class CustomArrayAdapter<T> extends ArrayAdapter<T> {
        ArrayList<Boolean> pres;

        public CustomArrayAdapter(Context ctx, ArrayList<String> objects, ArrayList<Boolean> presence) {
            super(ctx, android.R.layout.simple_spinner_dropdown_item, (List<T>) objects);
            this.pres = presence;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView text = (TextView) view.findViewById(android.R.id.text1);
            //text.setPadding(10,10,10,10);
            if (pres.get(position)) {
                text.setBackgroundColor(Color.GRAY);
            } else {
                text.setBackgroundColor(Color.WHITE);
            }
            return view;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date_ddMMMMyyyy = new SimpleDateFormat("dd/MMMM/yyyy").format(c.getTime());
        date = new SimpleDateFormat("dd MMMM yyyy").format(c.getTime());
        dateChiffre = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
        dateTicket.setText(date);


        createEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExcelTable.checkEvening(getContext(), date_ddMMMMyyyy)) {
                    fillEveningLayout.setVisibility(View.VISIBLE);
                    createEveningLayout.setVisibility(View.GONE);
                    ExcelTable.createNewEvening(getContext(), date_ddMMMMyyyy);
                    Toast.makeText(getContext(), "Soirée créee", Toast.LENGTH_SHORT).show();
                    listener.onInputSent(date_ddMMMMyyyy,date,dateChiffre);

                } else {
                    fillEveningLayout.setVisibility(View.VISIBLE);
                    createEveningLayout.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Soirée chargée", Toast.LENGTH_SHORT).show();
                    listener.onInputSent(date_ddMMMMyyyy,date,dateChiffre);

                }

            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TicketFragmentListener) {
            listener = (TicketFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TicketFragmentListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


}
