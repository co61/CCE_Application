package fr.c7regne.cceapplication;

import android.content.Context;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTable {
    private Workbook wb;

    public ExcelTable(Workbook workbook) {
        Log.i("Excel", "2ndconstructor");
        if (workbook == null)
            throw new IllegalArgumentException("Book title can't be null");
        this.wb = workbook;
    }

    public ExcelTable() {
        Log.i("Exceltest", "1stconstructor");
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

        //Table for management
        Sheet ticketSheet;
        ticketSheet = workbook.createSheet("Contrôle achat ticket");
        row = ticketSheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Date achat");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Nom, Prénom");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Nombre ticket acheté");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Quantité ticket acheté au dernier achat");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Montant");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyleTotal);

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
        } catch (java.io.IOException e) {
            e.printStackTrace();

            Toast.makeText(context, "Failed to update XLS file", Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
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
    public static boolean checkNotMember(Context context, String nom, String prenom) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_membre));
        for (Row r : sheet) {
            if (getCellContent(r, 0).equals(nom)) {
                if (getCellContent(r, 1).equals(prenom)) {
                    return false;
                }
            }
        }
        return true;
    }


    @NotNull
    public static Workbook createNewMember(Context context, String nom, String prenom, int repasAT, int repasST, double montant, boolean dette) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_membre));
        CellStyle styleDouble = workbook.createCellStyle();
        styleDouble.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));
        Row row;
        Cell cell;
        int lastRow = sheet.getLastRowNum() + 1;
        row = sheet.createRow(lastRow);
        cell = row.createCell(0);
        cell.setCellValue(nom);
        cell = row.createCell(1);
        cell.setCellValue(prenom);
        //Repas nb at
        cell = row.createCell(2);
        cell.setCellValue(0);
        //st
        cell = row.createCell(3);
        cell.setCellValue(repasST);
        //dette
        cell = row.createCell(4);
        cell.setCellStyle(styleDouble);
        if (!dette) {
            cell.setCellValue(montant);
        } else {
            cell.setCellValue(0);
        }

        cell = row.createCell(5);
        cell.setCellStyle(styleDouble);
        cell.setCellValue(3.0);
        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;

    }

    @NotNull
    public static Workbook updateMember(Context context, String nom, String prenom, int repasAT, int repasST, double montant, boolean dette) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_membre));

        Cell cell;
        Row row = findMember(sheet, nom, prenom);
        //Repas nb at

        cell = row.getCell(2);
        int varAT= (int) cell.getNumericCellValue();
        if (repasAT != 0) {
            if ((int) cell.getNumericCellValue() > 0) {
                varAT = (int) (cell.getNumericCellValue() - repasAT);
            } else {
            }
        }
        cell.setCellValue(varAT);

        //st
        cell = row.getCell(3);
        int varST=(int) cell.getNumericCellValue();
        if (repasST != 0) {
            varST=(int)(cell.getNumericCellValue() + repasST);
        }
        cell.setCellValue(varST);

        //dette
        cell = row.getCell(4);
        double val=cell.getNumericCellValue();
        if (!dette) {
             val= cell.getNumericCellValue() + montant;

        }
        cell.setCellValue(val);
        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;

    }

    public static void updateDues(Context context, double montant,String nom, String prenom) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_membre));

        Cell cell;
        Row row = findMember(sheet, nom, prenom);
    }

    public static Row findMember(Sheet sheet, String nom, String prenom) {
        Row row = null;
        for (Row r : sheet) {
            if (getCellContent(r, 0).equals(nom)) {
                if (getCellContent(r, 1).equals(prenom)) {
                    row = r;
                    break;
                }
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
        //recette par soirée
        cell = row.createCell(3);
        cell.setCellStyle(styleDouble);
        cell.setCellValue(0.0);


        saveFile(context, workbook, new File(context.getExternalFilesDir(null), context.getResources().getString(R.string.file_name)));
        return workbook;

    }

    @NotNull
    public static Workbook updateEvening(Context context, String date, int repasAT, int repasST, double montant, boolean dette) {
        Workbook workbook = readFile(context);
        Sheet sheet = workbook.getSheetAt(context.getResources().getInteger(R.integer.compte_rendu_soiree));
        Row row;
        Cell cell;

        row = findEvening(sheet, date);

        //Repas nb at
        cell = row.getCell(1);
        cell.setCellValue(cell.getNumericCellValue() + repasAT);
        //st
        cell = row.getCell(2);
        cell.setCellValue(cell.getNumericCellValue() + repasST);
        //recette soirée
        cell = row.getCell(3);
        double val = cell.getNumericCellValue() + montant;
        cell.setCellValue(val);
        //tjrs a la ligne 1
        row = sheet.getRow(1);
        //recette totale
        cell = row.getCell(5);
        val = cell.getNumericCellValue() + montant;
        cell.setCellValue(val);
        //non payé
        if (!dette) {
            cell = row.getCell(6);
            val = cell.getNumericCellValue() + montant;
            cell.setCellValue(val);
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

    private static Row findEvening(Sheet sheet, String date) {
        Row row = null;
        for (Row r : sheet) {
            if (getCellContent(r, 0).equals(date)) {
                row = r;
                break;
            }
        }
        return row;
    }


//////////////////////////////////////////////////////////Manipulation tableau///////////////////////////////////////////////////////////////////////////////////////::

    public static String getCellContent(@NotNull Workbook workbook, int sheet, int row, int cell) {
        String content;
        DataFormatter dataFormatter = new DataFormatter();
        Cell c = workbook.getSheetAt(sheet).getRow(row).getCell(cell);
        content = dataFormatter.formatCellValue(c);
        return content;
    }

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
            workbook = WorkbookFactory.create(file);
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

    public static int numberCell(Row row) {
        return row.getLastCellNum();
    }









    /*
    public static Sheet sizeColumn(Sheet sheet){
        int columnNumber = sheet.getRow(0).getLastCellNum();
        Log.i("column",String.valueOf(columnNumber));
        for(int i=0; i==columnNumber;i++){
            sheet.autoSizeColumn(i, true);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 600);
        }
        return  sheet;
    }*/
}
