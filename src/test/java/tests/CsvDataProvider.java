package tests;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.testng.annotations.DataProvider;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import static utilities.Configurations.*;


public class CsvDataProvider
{

    @DataProvider(name = "bookingDataFromCsv")
    public Object[][] bookingDataFromCsv() throws IOException
    {
        List<Object[]> data = new ArrayList<>();
        Reader reader = new FileReader(csvFilePath); // Update path to your CSV file
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

        for (CSVRecord record : csvParser) {
            String firstname = record.get("firstname");
            String lastname = record.get("lastname");
            int totalprice = Integer.parseInt(record.get("totalprice"));
            boolean depositpaid = Boolean.parseBoolean(record.get("depositpaid"));
            String checkin = record.get("checkin");
            String checkout = record.get("checkout");
            String additionalneeds = record.get("additionalneeds");

            data.add(new Object[]{firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds});
        }

        csvParser.close();
        return data.toArray(new Object[0][]);
    }
}
