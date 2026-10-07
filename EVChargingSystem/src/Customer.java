import java.util.ArrayList;

public class Customer extends User {

    String vehicleNumber;
    String vehicleType;

    double walletBalance;

    ArrayList<Reservation> reservations;

    Customer(int id,String name,String email,String vehicle,String vType) {
        super(id, name, email);
        vehicleNumber = vehicle;
        vehicleType = vType;
        walletBalance = 0;
        reservations = new ArrayList<>();
    }

    void displayDetails() {

        super.displayDetails();
        System.out.println("Vehicle Number: " + vehicleNumber);
        System.out.println("Vehicle Type: " + vehicleType);
        System.out.println("Wallet Balance: Rs." + walletBalance);
    }

    void addMoney(double amount) {

        if(amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        walletBalance += amount;
        System.out.println("Money added successfully.");
        System.out.println("Wallet Balance: Rs." +walletBalance);
    }

    boolean deductMoney(double amount) {

        if(walletBalance >= amount) {

            walletBalance -= amount;

            return true;
        }

        return false;
    }

    Reservation bookCharger(int reservationID,Charger charger,ChargingStation station,int hours) {

        if(charger == null) {

            System.out.println("Charger not found.");

            return null;
        }

        if(hours <= 0) {

            System.out.println("Invalid charging duration.");

            return null;
        }

        if(!vehicleType.equalsIgnoreCase(charger.vehicleType)) {

            System.out.println("This charger is not compatible with your vehicle.");
            System.out.println("Your Vehicle: " +vehicleType);
            System.out.println("Supported Vehicle: " +charger.vehicleType);
            return null;
        }

        if(!charger.available) {

            System.out.println("Charger is already booked.");
            return null;
        }

        double totalAmount =charger.pricePerHour * hours;

        System.out.println("Rate: Rs." +charger.pricePerHour +" per hour");
        System.out.println("Duration: " +hours +" hours");
        System.out.println("Total Amount: Rs." +totalAmount);

        if(!deductMoney(totalAmount)) {

            System.out.println("Insufficient Wallet Balance.");

            System.out.println("Required: Rs." +totalAmount);

            System.out.println("Available: Rs." +walletBalance);

            return null;
        }

        charger.book();

        Reservation r =
                new Reservation(reservationID,this,charger,station,hours,totalAmount);

        reservations.add(r);

        station.addRevenue(totalAmount);

        System.out.println("Charger booked successfully.");

        System.out.println("Remaining Balance: Rs." +walletBalance);

        return r;
    }

    void displayReservations() {

        if(reservations.isEmpty()) {
            System.out.println("No Reservations Found");
            return;
        }

        for(Reservation r : reservations) {
            r.displayReservation();
            System.out.println();
        }
    }

    void cancelReservation(int reservationID) {
        for(Reservation r : reservations) {
            if(r.reservationID == reservationID && r.status.equals("Booked")) 
            {
                double refund = r.totalAmount * 0.80;

                r.cancelReservation();

                walletBalance += refund;

                r.station.deductRevenue(refund);

                r.charger.release();

                System.out.println("Reservation Cancelled.");
                System.out.println("Original Amount: Rs." +r.totalAmount);
                System.out.println("Refund (80%): Rs." +refund);
                System.out.println("Cancellation Charge (20%): Rs." +(r.totalAmount - refund));
                System.out.println("Wallet Balance: Rs." +walletBalance);

                return;
            }
        }

        System.out.println("Reservation Not Found or Already Cancelled.");
    }
}