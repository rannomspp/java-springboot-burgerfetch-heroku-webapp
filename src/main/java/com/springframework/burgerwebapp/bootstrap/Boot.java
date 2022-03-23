package com.springframework.burgerwebapp.bootstrap;
import com.springframework.burgerwebapp.controllers.VenueController;
import com.springframework.burgerwebapp.domain.Burger;
import com.springframework.burgerwebapp.domain.Venue;
import com.springframework.burgerwebapp.repositories.BurgerRepository;
import com.springframework.burgerwebapp.repositories.VenueRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Boot implements CommandLineRunner {
    //FSQ API KEY GOES HERE (Public key - If credits fall below zero, you will not be able to make API calls.):
    //Otherwise Encode Key.
    private final String FSQ_API_KEY = "fsq3Py4/87LNNlzaLwnpr7gvjCa/0dfkzLXnMsI9Air4ZH8=";

    private final VenueRepository venueRepository;
    private final BurgerRepository burgerRepository;

    public Boot(VenueRepository venueRepository, BurgerRepository burgerRepository) throws IOException {
        this.venueRepository = venueRepository;
        this.burgerRepository = burgerRepository;
    }

    //FETCH VENUES - venue_id/fsqid + name.
    public String fetchVenues() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.foursquare.com/v3/places/search?query=burger&near=Tartu&limit=50&v=20220320"))
                .header("Accept", "application/json")
                .header("Authorization", FSQ_API_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public List<String> fetchPhotos(String fsqid) throws IOException, InterruptedException {
        //BURGER API - The request body contains a list of URLs to fetch and recognize.
        List<String> photoLinks = new ArrayList<>();

        //url query "sort" - specifies the order in which results are returned.
        String photosURL = "https://api.foursquare.com/v3/places/" + fsqid + "/photos?sort=NEWEST";
        java.net.http.HttpRequest request2 = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(photosURL))
                .header("Accept", "application/json")
                .header("Authorization", FSQ_API_KEY)
                .method("GET", java.net.http.HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());
        JSONArray responseParse2 = new JSONArray(response2.body());
        String prefixURL = null;
        String suffixURL = null;
        for (int i = 0; i < responseParse2.length(); i++) {
            prefixURL = responseParse2.getJSONObject(i).getString("prefix");
            suffixURL = responseParse2.getJSONObject(i).getString("suffix");
            photoLinks.add("\"" + prefixURL + "original" + suffixURL + "\"");
        }
        //System.out.println(photoLinks + " photolinks array");
        return photoLinks;
    }

    @Override
    public void run(String... args) throws Exception {

        JSONObject responseParse = new JSONObject(fetchVenues());
        JSONArray arr = responseParse.getJSONArray("results");
        Venue venues[] = new Venue[50]; //fetchVenues method url query limit=50.
        for (int i = 0; i < arr.length(); i++) {
            String venue_id = arr.getJSONObject(i).getString("fsq_id");
            String name = arr.getJSONObject(i).getString("name");
            venues[i] = new Venue(name);
            String bodyReq = "{\"urls\": " + fetchPhotos(venue_id) + "}";
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("https://pplkdijj76.execute-api.eu-west-1.amazonaws.com/prod/recognize"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyReq))
                    .build();
            HttpResponse<String> response3 = HttpClient.newHttpClient().send(request3, HttpResponse.BodyHandlers.ofString());
            JSONObject responseParse2 = new JSONObject(response3.body());
            for (int j = 0; j < responseParse2.length(); j++) {
                if (responseParse2.has("urlWithBurger")) {
                    //Currently Burger API responeParse returns only a single link (first found match) "urlWithBurger" not an array,
                    //Although there can be multiple urls with a burger match!
                    //As my fetchVenues method url query sorts "newest" photos first, we still get the latest/newest single photo.
                    venues[i].getBurgers().add(new Burger("", (String) responseParse2.get("urlWithBurger")));
                }
            }
            venueRepository.save(venues[i]);
            //System.out.println(venue_id + " " + name);
        }

        //TESTING Objects, methods:
        /*Venue venue1 = new Venue("McDonalds");
        Burger burger1 = new Burger("BigTasty", "testimg1");
        Burger burger2 = new Burger("BigToast","testimg2");
        Burger burger3 = new Burger("BigMac", "testimg3");
        venue1.getBurgers().add(burger1);
        venue1.getBurgers().add(burger2);
        venue1.getBurgers().add(burger3);

        venueRepository.save(venue1);
        burgerRepository.save(burger1);
        burgerRepository.save(burger2);
        burgerRepository.save(burger3);*/
        //System.out.println("Number of Burgers " + burgerRepository.count());
        System.out.println("Started Bootstrap - Final Page Loaded!");
    }
}
