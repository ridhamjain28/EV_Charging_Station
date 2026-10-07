public class Reservation {

    int reservationID;
    Customer customer;
    Charger charger;
    ChargingStation station;

    int hours;
    double totalAmount;

    String status;

    Reservation(int id,Customer c,Charger ch,ChargingStation s,int hrs,double amount) {

        reservationID = id;
        customer = c;
        charger = ch;
        station = s;

        hours = hrs;
        totalAmount = amount;

        status = "Booked";
    }

    void displayReservation() {

        System.out.println("Reservation ID: " + reservationID);
        System.out.println("Customer: " + customer.name);
        System.out.println("Station: " + station.stationName);

        System.out.println("Charger ID: " + charger.chargerID);
        System.out.println("Charger Type: " + charger.chargerType);
        System.out.println("Vehicle Type: " + charger.vehicleType);

        System.out.println("Duration: " + hours + " Hours");
        System.out.println("Rate: Rs." + charger.pricePerHour + " per hour");
        System.out.println("Total Amount: Rs." + totalAmount);

        System.out.println("Status: " + status);
    }

    void cancelReservation() {
        status = "Cancelled";
    }
}