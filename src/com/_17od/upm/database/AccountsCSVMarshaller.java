package com._17od.upm.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com._17od.upm.util.Translator;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class AccountsCSVMarshaller {

    public void marshal(ArrayList accounts, File file) throws ExportException {

        if (file.exists()) {
            throw new ExportException("The file to export to already exists");
        }

        try {
            FileWriter writer = new FileWriter(file);
    
            CsvWriter csvWriter = new CsvWriter(writer, ',');
            for (int i=0; i<accounts.size(); i++) {
                csvWriter.writeRecord(
                        getAccountAsStringArray(
                                (AccountInformation) accounts.get(i)
                        )
                );
            }
            csvWriter.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }

    }

    public ArrayList unmarshal(File file) throws ImportException {
        ArrayList accounts = new ArrayList();

        try {
            CsvReader csvReader = new CsvReader(new FileReader(file));
            while (csvReader.readRecord()) {
                if (csvReader.getColumnCount() != 5) {
                    throw new ImportException(Translator.translate("notCSVFileError", file.getAbsoluteFile())); 
                }
                accounts.add(new AccountInformation(
                        csvReader.get(0),
                        csvReader.get(1).getBytes(),
                        csvReader.get(2).getBytes(),
                        csvReader.get(3).getBytes(),
                        csvReader.get(4).getBytes()));
            }
        } catch (FileNotFoundException e) {
            throw new ImportException(e);
        } catch (IOException e) {
            throw new ImportException(e);
        }

        return accounts;
    }

    private String[] getAccountAsStringArray(AccountInformation account) {
        String[] arr = new String[5];
        arr[0] = account.getAccountName();
        arr[1] = new String(account.getUserId());
        arr[2] = new String(account.getPassword());
        arr[3] = new String(account.getUrl());
        arr[4] = new String(account.getNotes());
        return arr;
    }

}
