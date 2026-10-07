public class ChargingStation {

    int stationID;
    String stationName;
    String location;
    Charger[] chargers;
    int chargerCount;
    double totalRevenue;

    ChargingStation(int id,String name,String loc,int capacity) {

        stationID = id;
        stationName = name;
        location = loc;
        chargers = new Charger[capacity];
        chargerCount = 0;
        totalRevenue = 0;
    }

    void addCharger(Charger c) {
        if(chargerCount < chargers.length) {
            chargers[chargerCount] = c;
            chargerCount++;
            System.out.println("\nCharger added successfully.");

        } else {

            System.out.println("\nStation is full.");
        }
    }

    void displayChargers() {

        System.out.println("\nStation: " + stationName);
        System.out.println("Location: " + location);

        for(int i = 0; i < chargerCount; i++) {

            chargers[i].display();

            System.out.println();
        }
    }

    void displayAvailableChargers() {

        boolean found = false;

        for(int i = 0; i < chargerCount; i++) {

            if(chargers[i].available) {

                chargers[i].display();

                System.out.println();

                found = true;
            }
        }

        if(!found) {
            System.out.println("No chargers available.");
        }
    }

    Charger findCharger(int id) {

        for(int i = 0; i < chargerCount; i++) {

            if(chargers[i].chargerID == id) {
                return chargers[i];
            }
        }

        return null;
    }

    void addRevenue(double amount) {
        totalRevenue += amount;
    }

    void deductRevenue(double amount) {

        totalRevenue -= amount;

        if(totalRevenue < 0) {
            totalRevenue = 0;
        }
    }

    void displayRevenue() {

        System.out.println(
                "Total Revenue: " +
                totalRevenue +
                " Rupees"
        );
    }

    void displayStationStatus() {

        System.out.println("\n===== STATION STATUS =====");

        System.out.println("Station Name: " + stationName);

        System.out.println("Location: " + location);

        System.out.println("Total Chargers: " + chargerCount);

        int available = 0;
        int booked = 0;
        for(int i = 0; i < chargerCount; i++) {

            if(chargers[i].available) {
                available++;
            } else {
                booked++;
            }
        }

        System.out.println("Available Chargers: " + available);

        System.out.println("Booked Chargers: " + booked);

        System.out.println("Revenue: Rs." + totalRevenue);
    }

    void displayCompatibleAvailableChargers(String vehicleType) {
        boolean found = false;
    
        for(int i = 0; i < chargerCount; i++) {
            if(chargers[i].available && chargers[i].vehicleType.equalsIgnoreCase(vehicleType)) {
                chargers[i].display();
                System.out.println();
                found = true;
            }
        }
    
    if(!found) {
        System.out.println("No compatible chargers available.");
        }
    }
}