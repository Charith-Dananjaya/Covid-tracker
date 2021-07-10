package Charith.Covid19.Tracker.services;

import Charith.Covid19.Tracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service //Marking the Method as a Spring Service
public class CoronaVirusDataService {

    private static String Virus_Data_Url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();
    @PostConstruct //Executing at the beginning of the application
    @Scheduled( cron = "5 * * * * *")//Runs the method on Regular Basis
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();// New Arraylist is created instead of deleting the data within previous array is done in order to prevent users getting errors while we fetch new data
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Virus_Data_Url)).build();

            HttpResponse<String> httpResponse =client.send(request,HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationstat  = new LocationStats();

            locationstat.setState("Province/State");
            locationstat.setCountry(record.get("Country/Region"));
            locationstat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
            System.out.println(locationstat);
            newStats.add(locationstat);


        }
        this.allStats = newStats;



    }
}
