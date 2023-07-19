/* Code for COMP103 - 2021T2, Assignment 2
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

import ecs100.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    private Map<String, Station> stations = new HashMap<String, Station>();
    private Map<String, TrainLine> trainLines = new HashMap<String, TrainLine>();

    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about

    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        // The following is only needed for the Completion and Challenge
        loadTrainServicesData();
        UI.println("Loaded Train Services");
        UI.println();
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
        UI.addButton("Stations by name",    this::listStationsByName);
        UI.addButton("All Lines",           this::listAllTrainLines);
        UI.addTextField("Station",          (String name) -> {this.stationName=name;});
        UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
        UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
        UI.addTextField("Time (24hr)",      (String time) ->
            {try{this.startTime=Integer.parseInt(time);}catch(Exception e){UI.println("Enter four digits");}});
        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
        UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
        if (stations.isEmpty()){
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
        else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }

    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            closestStations(x, y);
        }
    }

    // Methods for loading data and answering queries

    /**
     * Loads the station data into the station map
     */
    public void loadStationData(){
        try{
            List<String> allLines = Files.readAllLines(Path.of("data/stations.data"));
            for (String line: allLines){
                Scanner sc = new Scanner(line);
                String name = sc.next();
                int zone = sc.nextInt();
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                Station placeholder = new Station(name, zone, x, y);
                stations.put(name, placeholder);
            }
        }
        catch (java.io.IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    /**
     * Loads the train line data into the train line map
     */
    public void loadTrainLineData(){
        try{
            List<String> allLines = Files.readAllLines(Path.of("data/train-lines.data"));
            for (String line: allLines){
                Scanner sc = new Scanner(line);
                String name = sc.next();
                TrainLine trainLine = new TrainLine(name);
                trainLines.put(name, trainLine);
                
                List<String> allLinesFile = Files.readAllLines(Path.of("data/" + name + "-stations.data"));
                for (String line2: allLinesFile){
                    Station placeholder = stations.get(line2);
                    trainLine.addStation(placeholder);
                    placeholder.addTrainLine(trainLine);
                    stations.replace(line, placeholder);
                }
            }
        }
        catch (java.io.IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    /**
     * Loads the train services data into objects
     */
    public void loadTrainServicesData(){
        try{
            List<String> allLines = Files.readAllLines(Path.of("data/train-lines.data"));
            for (String line: allLines){
                Scanner sc = new Scanner(line);
                String trainLineName = sc.next();
                
                List<String> allLinesFile = Files.readAllLines(Path.of("data/" + trainLineName + "-services.data"));
                // this opens up for the certain trainLine we are on right now all the times that the trainline has
                for (String line2: allLinesFile){
                    Scanner scan = new Scanner(line2);
                    TrainService service = new TrainService(trainLines.get(trainLineName));
                    TrainLine trainLine = trainLines.get(trainLineName);
                    trainLine.addTrainService(service);
                    while (scan.hasNextInt()){
                        service.addTime(scan.nextInt());
                    }
                }
            }
        }
        catch (java.io.IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    
    
    /**
     * Query 1: List all the stations
     */
    public void listAllStations(){
        UI.clearText();
        UI.println("All stations:");
        Collection<Station> allStations = stations.values();
        for (Station s: allStations){
            UI.println(s.toString());
        }
    }
    
    /**
     * Query 2: List all the stations alphabetically
     */
    public void listStationsByName(){
        UI.clearText();
        UI.println("All Stations Alphabetically:");
        Collection<Station> allStations = stations.values();
        List<String> allNames = new ArrayList<String>();
        for (Station s: allStations){
            allNames.add(s.toString());
        }
        Collections.sort(allNames, String.CASE_INSENSITIVE_ORDER);
        for (int i=0; i<allNames.size(); i++){
            UI.println(allNames.get(i));
        }
    }
    
    /**
     * Query 3: List all the train lines
     */
    public void listAllTrainLines(){
        UI.clearText();
        UI.println("All Train Lines:");
        Collection<TrainLine> allTrainLines = trainLines.values();
        for (TrainLine t: allTrainLines){
            UI.println(t.toString());
        }
    }
    
    /**
     * Query 4: Lists all the train lines with the specified station
     */
    public void listLinesOfStation(String station){
        UI.clearText();
        UI.println("Train Lines with " + station + " Station:");
        if (stations.get(station) != null){
            Collection<TrainLine> allTrainLines = trainLines.values();
            for (TrainLine t: allTrainLines){
                boolean stationFound = false;
                List<Station> trainStations = t.getStations();
                for (Station s: trainStations){
                    if (s.getName().equals(station) && stationFound == false){
                        UI.println(t.toString());
                    }
                }
                stationFound = true;
            }
        }
        else{
            UI.println(station + " is unknown.");
        }
    }
    
    /**
     * Query 5: Lists all the stations along one given line
     */
    public void listStationsOnLine(String line){
        UI.clearText();
        UI.println("Stations along " + line + " Line:");
        if (trainLines.get(line) != null){
            TrainLine t = trainLines.get(line);
            List<Station> trainStations = t.getStations();
            for (Station s: trainStations){
                UI.println(s.toString());
            }
            UI.println();
        }
        else{
            UI.println(line + " is unknown.");
        }
    }
    
    /**
     * Query 6: Checks if two stations are connected
     */
    public void checkConnected(String start, String destination){
        UI.clearText();
        UI.println(start + " to " + destination);
        if (!stations.containsKey(start) || !stations.containsKey(destination)){
            Collection<TrainLine> allTrainLines = trainLines.values();
            for (TrainLine t: allTrainLines){
                boolean startFound = false;
                List<Station> trainStations = t.getStations();
                int startZones = 0;
                for (Station s: trainStations){
                    if (s.getName().equals(start)){
                        startZones = s.getZone();
                        startFound = true;
                    }
                    else if (s.getName().equals(destination) && startFound == true){
                        UI.println("The " + t.getName() + " line goes from " + start + " to " + destination + ".");
                        UI.println("The trip goes through " + Math.abs(s.getZone() - startZones) + " zones.");
                    }
                }
            }
        }
    }
    
    /**
     * Query 7: Find the next train service for each line at a station after the specified time
     */
    public void findNextServices(String stationName, int startTime){
        UI.println("Next services for " + stationName + " after " + startTime);
        if (!stations.containsKey(stationName)){
            UI.println(stationName + " is unknown.");
        }
        else{
            Station start = stations.get(stationName);
            Set<TrainLine> allTrainLines = start.getTrainLines();
            for (TrainLine t: allTrainLines){ 
                List<Station> trainStations = t.getStations();
                int startIndex = trainStations.indexOf(start); // finds the index of the station in the trainLine
                
                List<TrainService> allTrainServices = t.getTrainServices(); // we grab all the trainservices that includes the station
                boolean timeFound = false;                                  // if the first time has already been found for that train line
                for (TrainService s: allTrainServices){  // for each of the services
                    List<Integer> times = s.getTimes(); // returns all the time 
                    if (times.get(startIndex) >= startTime && timeFound == false){
                        UI.println("Next service on " + t.getName() + " from " + stationName + " is at " + times.get(startIndex));
                        timeFound = true;
                    }
                }
            }
        }
    }
    
    /**
     * Query 8: Find a trip between two stations (on the same line), after the specified time.
     * Find the train line, the time that next service on that line will leave the first station, 
     * the time that the service will arrive at the destination station, 
     * and the number of fare zones the trip goes through.
     */
    public void findTrip(String stationName, String destination, int startTime){
        UI.clearText(); UI.println(stationName + " to " + destination); UI.println("----------");
        boolean tripFound = false;
        if (!stations.containsKey(stationName) || !stations.containsKey(destination)){ // if either station does not exist
            UI.println(stationName + " or " + destination + " is unknown.");
        }
        else{
            Station start = stations.get(stationName);
            Station dest = stations.get(destination);
            Set<TrainLine> startLines = start.getTrainLines();   // gets all the lines that has the start station in it
            for (TrainLine t: startLines){
                int startIndex = 0;
                List<Station> trainStations = t.getStations();   // find all the stations in that line
                for (Station s: trainStations){
                    if (s.getName().equals(stationName)){
                        startIndex = trainStations.indexOf(s);
                    }
                    if (s.getName().equals(destination) && trainStations.indexOf(s) > startIndex){
                        int endIndex = trainStations.indexOf(s);
                        List<TrainService> services = t.getTrainServices();  // grab all the services for the valid train line
                        for (TrainService ts: services){
                            List<Integer> times = ts.getTimes();
                            if (times.get(startIndex) >= startTime && tripFound == false && times.get(endIndex) != -1){
                                UI.println("Service: " + ts.getTrainID());
                                UI.println("Number of Zones: " + Math.abs(start.getZone() - dest.getZone()));
                                UI.println("Leaves " + stationName + " at " + times.get(startIndex) + " and arrives at " + destination + " at " + times.get(endIndex));
                                tripFound = true;
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Find the 10 closest stations to a point on the map, listing the names and distances from closest to furthest.
     */
    public void closestStations(double x, double y){
        UI.clearText(); UI.println("Closest Stations");
        Map<Double, Station> closest = new HashMap<Double, Station>();
        List<Double> distances = new ArrayList<Double>();
        Collection<Station> allStations = stations.values();
        for (Station s: allStations){
            double xDiff = Math.abs(x-s.getXCoord());
            double yDiff = Math.abs(y-s.getYCoord());
            double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff); // finds the distance between points
            closest.put(distance, s);
            distances.add(distance);
        }
        Collections.sort(distances);
        for (int i=0; i<10; i++){
            UI.println(Math.round(distances.get(i) * 100.0) / 100.0 + "km: " + closest.get(distances.get(i)).getName());
        }
    }
}
