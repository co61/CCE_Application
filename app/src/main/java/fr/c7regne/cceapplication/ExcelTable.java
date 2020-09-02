package fr.c7regne.cceapplication;

import android.content.Context;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTable {
    private Workbook wb;

    public ExcelTable(Workbook workbook) {
        if (workbook == null)
            throw new IllegalArgumentException("Book title can't be null");
        this.wb = workbook;
    }

    public ExcelTable() {
        Workbook workbook = new HSSFWorkbook();

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle cellStyleTotal = workbook.createCellStyle();
        cellStyleTotal.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        cellStyleTotal.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle styleDouble = workbook.createCellStyle();
        styleDouble.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));

        Cell cell;
        Row row;

        //Table for people account
        Sheet peopleAccountSheet;
        peopleAccountSheet = workbook.createSheet("Compte Membres");

        row = peopleAccountSheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Nom");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Prénom");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Nombre ticket");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Nombre sans ticket");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Dette");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Ticket prix");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Date dernier repas");
        cell.setCellStyle(cellStyle);

        //Table for evening account
        Sheet eveningAccountSheet;
        eveningAccountSheet = workbook.createSheet("Compte rendu des soirées");
        row = eveningAccountSheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Date soirée");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Nombre repas avec ticket");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Nombre repas sans ticket");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Recette soirée(fictif)");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Dette soirée(fictif)");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Gain soirée(fictif)");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Recette soirée(Réel)");
        cell.setCellStyle(cellStyle);


        cell = row.createCell(8);
        cell.setCellValue("Recette totale réelle");
        cell.setCellStyle(cellStyleTotal);

        cell = row.createCell(9);
        cell.setCellValue("Dette");
        cell.setCellStyle(cellStyleTotal);

        row = eveningAccountSheet.createRow(1);
        cell = row.createCell(8);
        cell.setCellStyle(styleDouble);
        cell.setCellValue(0.0);

        cell = row.createCell(9);
        cell.setCellStyle(styleDouble);
        cell.setCellValue(0.0);

        //Table for management ticket
        Sheet ticketSheet;
        ticketSheet = workbook.createSheet("Contrôle achat ticket");
        row = ticketSheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Nom");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Prénom");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Date dernier achat");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Nombre tickets achetés");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Quantité ticket acheté au dernier achat");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Montant");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyleTotal);

        row = ticketSheet.createRow(1);
        cell = row.createCell(7);
        cell.setCellValue(0);
        cell.setCellStyle(styleDouble);

        //Table for management shopping
        Sheet shopSheet;
        shopSheet = workbook.createSheet("Course");
        row = shopSheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Date");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Nom");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Montant");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("N° Ticket de caisse");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Remboursement");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Descriptif");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("ID");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyleTotal);

        cell = row.createCell(8);
        cell.setCellValue("Total Remboursé");
        cell.setCellStyle(cellStyleTotal);

        row = shopSheet.createRow(1);
        cell = row.createCell(7);
        cell.setCellValue(0.0);
        cell.setCellStyle(styleDouble);

        cell = row.createCell(8);
        cell.setCellValue(0.0);
        cell.setCellStyle(styleDouble);


        this.wb = workbook;
    }


    public Workbook getOriginal() {
        return this.wb;
    }

    public static void saveFile(Context context, Workbook wb, File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(context, "XLS File Updated", Toast.LENGTH_LONG).show();
            wb.close();
            outputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to update XLS file", Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                wb.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void createFile(Context context, Workbook wb, File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(context, "XLS File Generated", Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            Toast.makeText(context, "Failed to create XLS file", Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //////////////////////////////////////////////////////////Compte membre///////////////////////////////////////////////////////////////////////////////////////::



    public static boolean checkNotMember(Context context, String prenom) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_membre));
        for (Row r : sheet) {
            if (getCellContent(r, 1).equals(prenom)) {
                return false;
            }

        }
        return true;
    }


    @NotNull
    public static Workbook createNewMember(Context context, String prenom, String date) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_membre));
        CellStyle styleDouble = workbook.createCellStyle();
        styleDouble.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));
        Row row;
        Cell cell;
        int lastRow = sheet.getLastRowNum() + 1;
        row = sheet.createRow(lastRow);

        //surname
        cell = row.createCell(1);
        cell.setCellValue(prenom);
        //Repas nb at
        cell = row.createCell(2);
        cell.setCellValue(0);
        //st
        cell = row.createCell(3);
        cell.setCellValue(0);
        //dette
        cell = row.createCell(4);
        cell.setCellStyle(styleDouble);
        cell.setCellValue(0);


        cell = row.createCell(5);
        cell.setCellStyle(styleDouble);
        cell.setCellValue(3.0);

        cell = row.createCell(6);
        cell.setCellValue(date);

        Sheet sheetTicket = workbook.getSheetAt(context.getResources().getInteger(R.integer.controle_achat_ticket));

        lastRow = sheetTicket.getLastRowNum() + 1;
        row = sheetTicket.createRow(lastRow);

        //surname
        cell = row.createCell(1);
        cell.setCellValue(prenom);
        //date last purchase
        cell = row.createCell(2);
        cell.setCellValue(0);
        //nb ticket
        cell = row.createCell(3);
        cell.setCellValue(0);
        //last time quantity
        cell = row.createCell(4);
        cell.setCellValue(0);
        //Montant
        cell = row.createCell(5);
        cell.setCellValue(0);
        cell.setCellStyle(styleDouble);

        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;

    }

    @NotNull
    public static Workbook updateMember(Context context, String prenom, int repasAT, int repasST, double montant, boolean dette, String date) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_membre));


        Cell cell;
        Row row = findMember(sheet, prenom);
        //Repas nb at

        cell = row.getCell(2);
        int varAT = (int) cell.getNumericCellValue();
        if (repasAT != 0) {
            if ((int) cell.getNumericCellValue() > 0) {
                varAT = (int) (cell.getNumericCellValue() - repasAT);
            } else {
            }
        }
        cell.setCellValue(varAT);

        //st
        cell = row.getCell(3);
        int varST = (int) cell.getNumericCellValue();
        if (repasST != 0) {
            varST = (int) (cell.getNumericCellValue() + repasST);
        }
        cell.setCellValue(varST);

        //dette
        cell = row.getCell(4);
        double val = cell.getNumericCellValue();
        if (!dette) {
            val = cell.getNumericCellValue() + montant;

        }
        cell.setCellValue(val);

        cell = row.getCell(6);
        cell.setCellValue(date);

        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;

    }




    public static Row findMember(@NotNull Sheet sheet, String prenom) {
        Row row = null;
        for (Row r : sheet) {

                if (getCellContent(r, 1).equals(prenom)) {
                    row = r;
                    break;

            }
        }
        return row;
    }

    //////////////////////////////////////////////////////////Compte soirée///////////////////////////////////////////////////////////////////////////////////////::
    @NotNull
    public static Workbook createNewEvening(Context context, String date) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_rendu_soiree));
        CellStyle styleDouble = workbook.createCellStyle();
        styleDouble.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));
        Row row;
        Cell cell;
        int lastRow = sheet.getLastRowNum() + 1;
        row = sheet.createRow(lastRow);
        cell = row.createCell(0);
        cell.setCellValue(date);
        //Repas nb at
        cell = row.createCell(1);
        cell.setCellValue(0);
        //Repas nb st
        cell = row.createCell(2);
        cell.setCellValue(0);
        //recette par soirée fictif
        cell = row.createCell(3);
        cell.setCellValue(0.0);
        //dette par soirée fictif
        cell = row.createCell(4);
        cell.setCellValue(0.0);
        //gain par soirée fictif
        cell = row.createCell(5);
        cell.setCellValue(0.0);
        //reccette  soirée reel
        cell = row.createCell(6);
        cell.setCellValue(0.0);


        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;

    }

    @NotNull
    public static Workbook updateEvening(Context context, String date, int repasAT, int repasST, double montant, boolean dette, boolean remboursement) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_rendu_soiree));
        Row row;
        Cell cell;
        //search row of the evening
        row = findEvening(sheet, date);
        //Repas nb at
        cell = row.getCell(1);
        cell.setCellValue(cell.getNumericCellValue() + repasAT);
        //st
        cell = row.getCell(2);
        cell.setCellValue(cell.getNumericCellValue() + repasST);
        //recette soirée fictif

        if (dette && !remboursement && (repasAT!=0 || repasST!=0)) {
            cell = row.getCell(3);
            cell.setCellValue(cell.getNumericCellValue() + montant);
        }
        //dette soirée fictif
        if (!dette && !remboursement &&  repasST!=0) {
            cell = row.getCell(4);
            cell.setCellValue(cell.getNumericCellValue() + montant);
        }
        //gain soirée fictif
        if (!remboursement && (repasAT!=0 || repasST!=0)) {
            cell = row.getCell(5);
            cell.setCellValue(cell.getNumericCellValue() + montant);
        }
        //recette soirée réel
        if (remboursement || (repasAT==0 && repasST==0 && dette )  || repasST!=0 && dette ) {
            cell = row.getCell(6);
            cell.setCellValue(cell.getNumericCellValue() + montant);
        }

        //tjrs a la ligne 1
        row = sheet.getRow(1);
        //recette totale
        if ((repasAT==0 && repasST==0 && dette )  || repasST!=0 && dette || remboursement) {
            cell = row.getCell(8);
            cell.setCellValue(cell.getNumericCellValue() + montant);
        }
        //Dette totale
        if (!dette && !remboursement && (repasST!=0 || (repasAT==0 && repasST==0))) {
            cell = row.getCell(9);
            cell.setCellValue(cell.getNumericCellValue() + montant);
        }
        if (dette && remboursement && repasAT==0 && repasST==0 ) {
            cell = row.getCell(9);
            cell.setCellValue(cell.getNumericCellValue() - montant);
        }

        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;

    }

    public static boolean checkEvening(Context context, String fullDate) {
        /**
         * retrun true if the evening is not register yet
         */
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_rendu_soiree));
        for (Row r : sheet) {
            if (getCellContent(r, 0).equals(fullDate)) {
                return false;
            }
        }
        return true;
    }

    public static Row findEvening(@NotNull Sheet sheet, String date) {
        Row row = null;
        for (Row r : sheet) {
            if (getCellContent(r, 0).equals(date)) {
                return r;
            }
        }
        return null;
    }

    //////////////////////////////////////////////////////////Comptes ticketé"///////////////////////////////////////////////////////////////////////////////////////::
    public static Workbook updateTicket(Context context,String prenom, String date, int nbTicketAchat, double montantTicket, boolean b) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.controle_achat_ticket));

        Row row;
        Cell cell;

        row = FindMemberTicketSheet(sheet, prenom);

        //date last purchase
        cell = row.getCell(2);
        cell.setCellValue(date);
        //nb ticket bought
        cell = row.getCell(3);
        int val = (int) cell.getNumericCellValue() + nbTicketAchat;
        cell.setCellValue(val);
        //last time ticket quantity
        cell = row.getCell(4);
        cell.setCellValue(nbTicketAchat);
        //total ticket money
        cell = row.getCell(5);
        cell.setCellValue(cell.getNumericCellValue() + montantTicket);

        //total always at 1st line
        row = sheet.getRow(1);
        cell = row.getCell(7);
        cell.setCellValue(cell.getNumericCellValue() + montantTicket);

        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;
    }

    private static Row FindMemberTicketSheet(Sheet sheet, String prenom) {
        Row row = null;
        for (Row r : sheet) {

                if (getCellContent(r, 1).equals(prenom)) {
                    row = r;
                    break;
                }

        }
        return row;
    }

//////////////////////////////////////////////////////////Course tableau///////////////////////////////////////////////////////////////////////////////////////::

    public static void createCourse(Context context, String fullDate, String nomAchatCourse, double montantAchatCourse, int numTicketAchatCourse, String descriptifAchatCourse, boolean checkboxAchatCourse) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.achat_course));
        CellStyle styleDouble = workbook.createCellStyle();
        styleDouble.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));
        Row row;
        Cell cell;

        CellStyle styleColorTrue = workbook.createCellStyle();
        styleColorTrue.setFillBackgroundColor(HSSFColor.GREEN.index);
        styleColorTrue.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle styleColorFalse = workbook.createCellStyle();
        styleColorFalse.setFillBackgroundColor(HSSFColor.RED.index);
        styleColorFalse.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        int lastRow = sheet.getLastRowNum() + 1;
        row = sheet.createRow(lastRow);
        //date
        cell = row.createCell(0);
        cell.setCellValue(fullDate);
        //nom
        cell = row.createCell(1);
        cell.setCellValue(nomAchatCourse);
        //montant
        cell = row.createCell(2);
        cell.setCellValue(montantAchatCourse);
        //num ticket
        cell = row.createCell(3);
        cell.setCellValue(numTicketAchatCourse);
        //remboursement
        if (checkboxAchatCourse) {
            cell = row.createCell(4);
            cell.setCellValue("Remboursé");
            cell.setCellStyle(styleColorTrue);
        } else {
            cell = row.createCell(4);
            cell.setCellValue("Non Remboursé");
            cell.setCellStyle(styleColorFalse);
        }
        //descriptif
        cell = row.createCell(5);
        cell.setCellValue(descriptifAchatCourse);

        //id
        cell = row.createCell(6);
        cell.setCellValue(lastRow-1);

        row = sheet.getRow(1);
        //Total course
        cell = row.getCell(7);
        cell.setCellValue(cell.getNumericCellValue() + montantAchatCourse);
        if (checkboxAchatCourse) {
            cell = row.getCell(8);
            cell.setCellValue(cell.getNumericCellValue() + montantAchatCourse);
        }

        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
    }

    public static void updateCourse(Context context, String fullDate, String nomAchatCourse, double montantAchatCourse, double id) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.achat_course));
        Row row;
        Cell cell;
        CellStyle styleColorTrue = workbook.createCellStyle();
        styleColorTrue.setFillBackgroundColor(HSSFColor.GREEN.index);
        styleColorTrue.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        row = findCourse(sheet, fullDate, nomAchatCourse, montantAchatCourse, id);

        //remboursement
        cell = row.getCell(4);
        cell.setCellValue("Remboursé");
        cell.setCellStyle(styleColorTrue);

        row = sheet.getRow(1);
        //Total course remboursé
        cell = row.getCell(8);
        cell.setCellValue(cell.getNumericCellValue() + montantAchatCourse);


        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
    }

    private static Row findCourse(Sheet sheet, String date, String prenom, double montant, double id) {
        Row row = null;
        for (Row r : sheet) {
            if (getCellContent(r, 0).equals(date)) {
                if (getCellContent(r, 1).equals(prenom)) {
                    if (r.getCell(2).getNumericCellValue() == montant) {
                        if (r.getCell(6).getNumericCellValue() == id) {
                            row = r;
                            break;
                        }
                    }
                }
            }
        }
        return row;
    }
//////////////////////////////////////////////////////////Manipulation tableau///////////////////////////////////////////////////////////////////////////////////////::


    public static String getCellContent(@NotNull Sheet sheet, int row, int cell) {
        String content;
        DataFormatter dataFormatter = new DataFormatter();
        Cell c = sheet.getRow(row).getCell(cell);
        content = dataFormatter.formatCellValue(c);
        return content;
    }

    public static String getCellContent(@NotNull Row row, int cell) {
        String content;
        DataFormatter dataFormatter = new DataFormatter();
        Cell c = row.getCell(cell);
        content = dataFormatter.formatCellValue(c);
        return content;
    }

    public static Workbook readFile(@NotNull Context context) {
        File file;
        Workbook workbook = null;
        file = new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name));
        try {
            FileInputStream inputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public static int numberRow(Sheet sheet) {
        return sheet.getLastRowNum();
    }


}
