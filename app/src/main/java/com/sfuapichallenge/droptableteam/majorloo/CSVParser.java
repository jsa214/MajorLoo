package com.sfuapichallenge.droptableteam.majorloo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seongchanlee on 2017. 1. 7..
 * from http://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio
 */

public class CSVParser {
    InputStream inputStream;

    public CSVParser(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public ArrayList<String[]> read(){
        ArrayList resultList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                resultList.add(row);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }
}
