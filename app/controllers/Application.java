package controllers;


import model.Model.Reader;
import model.Model.AspectAggregation;
import model.Model.ReviewOutput;
import model.RestaurantRatings.Main;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import views.html.home;
import views.html.index;
import views.html.output;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Application extends Controller {

    public static Result index() {

        System.out.println("");

        return ok(index.render("Your new application is ready."));
    }

    public static Result home() {

        System.out.println("");

        return ok(home.render());
    }

    public static Result upload() {

        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart data = body.getFile("file");
        if (data != null) {
            String fileName = data.getFilename();
            String contentType = data.getContentType();
            File file = data.getFile();

            String newFileName = "public/file/" + fileName;
            File newFile = new File(newFileName);
            try (InputStream isFile = new FileInputStream(file)) {
                byte[] byteFile = IOUtils.toByteArray(isFile);
                FileUtils.writeByteArrayToFile(newFile, byteFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String restaurantName = "Restaurant";
            try {
                restaurantName = Reader.readRestaurantName(newFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ArrayList<ReviewOutput> outputs = Main.startApp(newFileName);

            int totalReviews = outputs.size();

            LinkedHashMap<String, ArrayList<Double>> totalRatings = new LinkedHashMap<>();
            ArrayList<String> labels = AspectAggregation.getAspectCategories();

            for(String label : labels) {
                for (ReviewOutput ro: outputs) {
                    if (ro.getRating().containsKey(label)) {
                        if (totalRatings.containsKey(label)) {
                            ArrayList<Double> newRatings = new ArrayList<>(totalRatings.get(label));
                            newRatings.add(ro.getRating().get(label));
                            totalRatings.put(label, newRatings);
                        } else {
                            ArrayList<Double> newRatings = new ArrayList<>();
                            newRatings.add(ro.getRating().get(label));
                            totalRatings.put(label, newRatings);
                        }
                    }
                }
            }

            LinkedHashMap<String, Double> summaryRatings = new LinkedHashMap<>();
            for (String categoryName : totalRatings.keySet()) {
                Double sum = 0.0;
                for (Double rating : totalRatings.get(categoryName)){
                    sum+=rating;
                }
                summaryRatings.put(categoryName, sum/totalRatings.get(categoryName).size());

            }

            return ok(output.render(restaurantName, totalReviews, outputs, summaryRatings));

        } else {
            flash("error", "Missing file");
            return badRequest();
        }

    }

    public static Result test() {

        System.out.println("");

        return ok("Got request " + request() + "!");
    }

}
