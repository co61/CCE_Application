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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class TicketFragment extends Fragment {
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
    private String fullDate;

    //acces to component on view to update excel file
    private Button newMemberButton, createMemberButton, buttonValidationAT, buttonValidationST, ajoutticket, buttonRembourserST, buttonRembourserAT;
    private EditText nomAT, prenomAT, nomST, prenomST, nomnewMember, prenomnewMember, montantST, montantDetteRemboursementST, montantDetteRemboursementAT, reducctionTicket;
    private TextView nbRepasAT, nbRepasST, montantAT, nbticketinfo, detteinfoAT, detteinfoST, nbTicketAchat;
    private CheckBox checkBoxST, checkboxAT;
    private ImageView minusAT, minusST, plusAT, plusST, minusAchaTicket, plusAchaTicket;

    private double prixRepasTicket, limitTicket;

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
        fullDate = new SimpleDateFormat("dd/MMMM/yyyy").format(calendar.getTime());
        dateTicket.setText(fullDate);
        //check if evening create or not, set visible or not the tow layout
        if (ExcelTable.checkEvening(getContext(), fullDate)) {
            fillEveningLayout.setVisibility(View.GONE);
            createEveningLayout.setVisibility(View.VISIBLE);
            createEvening.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fillEveningLayout.setVisibility(View.VISIBLE);
                    createEveningLayout.setVisibility(View.GONE);
                    ExcelTable.createNewEvening(getContext(), fullDate);
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

        nomnewMember = v.findViewById(R.id.nomnewMember);
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
                        if (!(nomnewMember.getText().toString().equals("") || prenomnewMember.getText().toString().equals(""))) {
                            if (ExcelTable.checkNotMember(getContext(), nomnewMember.getText().toString(), prenomnewMember.getText().toString())) {
                                //create member in member sheet
                                ExcelTable.createNewMember(getContext(), nomnewMember.getText().toString(), prenomnewMember.getText().toString(),
                                        0, 0, 0, true);

                                //reset value and hide keyboard
                                nomnewMember.setText(null);
                                prenomnewMember.setText(null);
                                spinners = spinnerView();
                                hideKeyboardFrom(getContext(), v);
                                Toast.makeText(getContext(), "Membre crée", Toast.LENGTH_SHORT).show();
                                lnewMember.setVisibility(View.GONE);
                                newMemberButton.setVisibility(View.VISIBLE);
                                createMemberButton.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getContext(), "Ce membre éxiste déjà", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Entrez un nom et un prénom avant de valider", Toast.LENGTH_SHORT).show();
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
                    final Row r = ExcelTable.findMember(s, item.split("-")[0], item.split("-")[1]);
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
                            Cell cell = r.getCell(5);
                            cell.setCellValue(moyennePrixTicket);

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
                                checkboxAT.setChecked(true);
                            }
                            ExcelTable.saveFile(getContext(), workbook, new File(getContext().getExternalFilesDir(null), getContext().getResources().getString(R.string.file_name)));
                            ExcelTable.updateTicket(getContext(), item.split("-")[0], item.split("-")[1], fullDate,
                                    Integer.parseInt(nbTicketAchat.getText().toString()), Double.parseDouble(reducctionTicket.getText().toString()), checkboxAT.isChecked());
                            nbTicketAchat.setText("7");
                            reducctionTicket.setText("21");
                            hideKeyboardFrom(getContext(), v);
                            spinners = spinnerView();
                            Toast.makeText(getContext(), "Ticket ajouté", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //ExcelTable.updateTicket();
                    //update dues
                    detteinfoAT.setText("Dette : " + r.getCell(4).getNumericCellValue());
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
                                    detteinfoAT.setText("Dette : " + r.getCell(4).getNumericCellValue());
                                    montantDetteRemboursementAT.setText("0.0");
                                    spinners = spinnerView();
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
                        ExcelTable.updateMember(getContext(), spinners[0].getSelectedItem().toString().split("-")[0], spinners[0].getSelectedItem().toString().split("-")[1],
                                Integer.parseInt(nbRepasAT.getText().toString()), 0, Double.parseDouble(montantAT.getText().toString()), true);
                        //update evening sheet
                        ExcelTable.updateEvening(getContext(), fullDate,
                                Integer.parseInt(nbRepasAT.getText().toString()), 0, Double.parseDouble(montantAT.getText().toString()), true);
                        nbRepasAT.setText("1");
                        montantAT.setText(getResources().getString(R.string

                                .prix_repas));
                        hideKeyboardFrom(getContext(), v);
                        Toast.makeText(getContext(), "Repas enregistré", Toast.LENGTH_SHORT).show();
                        spinners = spinnerView();
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
                //Toast.makeText(getContext(), item, Toast.LENGTH_SHORT).show();
                //if "nouvô" > newMember creation access

                    //if not "Sélectionner une personne" > action possible
                    if (!item.equals("Sélectionner une personne")) {
                        buttonValidationST.setClickable(true);
                        ldetteSt.setVisibility(View.VISIBLE);
                        final Workbook workbook = ExcelTable.readFile(getContext());
                        Sheet s = workbook.getSheetAt(getResources().getInteger(R.integer.compte_membre));
                        final Row r = ExcelTable.findMember(s, item.split("-")[0], item.split("-")[1]);

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
                        ExcelTable.updateMember(getContext(), spinners[1].getSelectedItem().toString().split("-")[0], spinners[1].getSelectedItem().toString().split("-")[1],
                                0, Integer.parseInt(nbRepasST.getText().toString()), Double.parseDouble(montantST.getText().toString()), checkBoxST.isChecked());
                        //update evening sheet
                        ExcelTable.updateEvening(getContext(), fullDate,
                                0, Integer.parseInt(nbRepasST.getText().toString()), Double.parseDouble(montantST.getText().toString()), checkBoxST.isChecked());
                        nbRepasST.setText("1");
                        montantST.setText(getResources().getString(R.string.prix_repas));
                        checkBoxST.setChecked(true);
                        hideKeyboardFrom(getContext(), v);
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
        items.add("Sélectionner une personne");
        Workbook workbook = ExcelTable.readFile(getContext());
        Sheet sheet = workbook.getSheetAt(getResources().getInteger(R.integer.compte_membre));

        int nbRow = ExcelTable.numberRow(sheet);
        for (int i = 1; i < nbRow + 1; i++) {
            items.add(ExcelTable.getCellContent(sheet, i, 0) + "-" + ExcelTable.getCellContent(sheet, i, 1));
        }


        //get the spinner from the xml.
        Spinner dropdownAT = v.findViewById(R.id.spinnerAT);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterAT = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        // Specify the layout to use when the list of choices appears
        adapterAT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        dropdownAT.setAdapter(adapterAT);


        //get the spinner from the xml.
        Spinner dropdownST = v.findViewById(R.id.spinnerST);
        //Same items than the other spinner

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.

        ArrayAdapter<String> adapterST = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        // Specify the layout to use when the list of choices appears
        adapterST.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        dropdownST.setAdapter(adapterST);

        return new Spinner[]{dropdownAT, dropdownST};
    }

    private static void hideKeyboardFrom(@NotNull Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
