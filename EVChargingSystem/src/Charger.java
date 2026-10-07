public class Charger {

    int chargerID;
    String chargerType;
    String vehicleType;
    double powerKW;
    boolean available;
    double pricePerHour;

    Charger(int id, String type, String vehicle, double power, double price) {

        chargerID = id;
        chargerType = type;
        vehicleType = vehicle;
        powerKW = power;
        pricePerHour = price;
        available = true;
    }

    void display() {

        System.out.println("Charger ID: " + chargerID);
        System.out.println("Charger Type: " + chargerType);
        System.out.println("Vehicle Type: " + vehicleType);
        System.out.println("Power: " + powerKW + " kW");
        System.out.println("Price Per Hour: Rs." + pricePerHour);
        System.out.println("Available: " + available);
    }

    void book() {
        available = false;
    }

    void release() {
        available = true;
    }
}