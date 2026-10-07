public class EVChargingSystem {

    ChargingStation station;
    Customer customer;

    EVChargingSystem(ChargingStation s) {
        station = s;
    }

    void registerCustomer(int id, String name, String email, String vehicle, String vehicleType) {
        customer = new Customer(id, name, email, vehicle, vehicleType);
        System.out.println("Customer Registered Successfully.");
    }

    void displayCustomer() {
        if(customer == null) {
            System.out.println("No customer registered.");
            return;
        }
        customer.displayDetails();
    }

    void addMoney(double amount) {
        if(customer == null) {
            System.out.println("No customer registered.");
            return;
        }
        customer.addMoney(amount);
    }

    void displayAllChargers() {
        station.displayChargers();
    }

    void displayAvailableChargers() {
        station.displayAvailableChargers();
    }

    Reservation bookCharger(int chargerID, int reservationID, int hours) {
        if(customer == null) {
            System.out.println("No customer registered.");
            return null;
        }

        Charger selected = station.findCharger(chargerID);
        Reservation r = customer.bookCharger(reservationID, selected, station, hours);

        if(r != null) {
            r.displayReservation();
        }

        return r;
    }

    void displayReservations() {
        if(customer == null) {
            System.out.println("No customer registered.");
            return;
        }
        customer.displayReservations();
    }

    void cancelReservation(int reservationID) {
        if(customer == null) {
            System.out.println("No customer registered.");
            return;
        }
        customer.cancelReservation(reservationID);
    }

    void displayStationStatus() {
        station.displayStationStatus();
    }
}