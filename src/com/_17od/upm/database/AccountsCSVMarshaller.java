package com._17od.upm.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class AccountsCSVMarshaller {

    public void marshal(ArrayList accounts, File file) throws ExportException {

        if (file.exists()) {
            throw new ExportException("The file to export to already exists");
        }

        try {
            FileWriter writer = new FileWriter(file);
    
            CSVWriter csvWriter = new CSVWriter(writer);
            for (int i=0; i<accounts.size(); i++) {
                csvWriter.writeNext(
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
            CSVReader csvReader = new CSVReader(new FileReader(file));
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                accounts.add(new AccountInformation(
                        nextLine[0],
                        nextLine[1].getBytes(),
                        nextLine[2].getBytes(),
                        nextLine[3].getBytes(),
                        nextLine[4].getBytes()));
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
