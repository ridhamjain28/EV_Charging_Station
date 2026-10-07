import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Charger c1 = new Charger(101, "AC", "2-Wheeler", 7.4, 150);
        Charger c2 = new Charger(102, "DC Fast", "4-Wheeler", 50, 350);
        Charger c3 = new Charger(103, "DC Fast", "4-Wheeler", 100, 500);

        ChargingStation s1 = new ChargingStation(1, "Pune EV Station", "Pune", 5);

        s1.addCharger(c1);
        s1.addCharger(c2);
        s1.addCharger(c3);

        EVChargingSystem system = new EVChargingSystem(s1);

        int reservationCounter = 1001;
        int choice;

        do {

            System.out.println("\n===== EV CHARGING SYSTEM =====");
            System.out.println("1. Register Customer");
            System.out.println("2. View All Chargers");
            System.out.println("3. View Available Chargers");
            System.out.println("4. Add Money");
            System.out.println("5. Book Charger");
            System.out.println("6. View Customer Details");
            System.out.println("7. View Reservation History");
            System.out.println("8. Cancel Reservation");
            System.out.println("9. Station Status");
            System.out.println("0. Exit");
            System.out.println("Enter choice: ");

            choice = sc.nextInt();

            switch(choice) {

                case 1:
                    sc.nextLine();

                    System.out.println("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.println("Enter Email: ");
                    String email = sc.nextLine();

                    System.out.println("Enter Vehicle Number: ");
                    String vehicle = sc.nextLine();

                    System.out.println("Select Vehicle Type:");
                    System.out.println("1. 2-Wheeler");
                    System.out.println("2. 4-Wheeler");
                    System.out.println("Enter choice: ");

                    int vehicleChoice = sc.nextInt();
                    String vehicleType;

                    if(vehicleChoice == 1) {
                        vehicleType = "2-Wheeler";
                    } else if(vehicleChoice == 2) {
                        vehicleType = "4-Wheeler";
                    } else {
                        System.out.println("Invalid vehicle type.");
                        break;
                    }

                    system.registerCustomer(1, name, email, vehicle, vehicleType);
                    break;

                case 2:
                    system.displayAllChargers();
                    break;

                case 3:
                    system.displayAvailableChargers();
                    break;

                case 4:
                    System.out.println("Enter Amount: ");
                    double amount = sc.nextDouble();
                    system.addMoney(amount);
                    break;

                case 5:
                    if(system.customer == null) {
                        System.out.println("Register a customer first.");
                        break;
                    }

                    System.out.println("Your Vehicle Type: " + system.customer.vehicleType);
                    System.out.println("Compatible Available Chargers:");
                    system.station.displayCompatibleAvailableChargers(system.customer.vehicleType);

                    System.out.println("Enter Charger ID: ");
                    int chargerID = sc.nextInt();
                    Charger selected = system.station.findCharger(chargerID);

                    if(selected==null){
                        System.out.println("Charger not found.");
                        break;
                    }

                    if(!system.customer.vehicleType.equalsIgnoreCase(selected.vehicleType)) {
                        System.out.println("This charger is not compatible with your vehicle.");
                        System.out.println("Your Vehicle: " + system.customer.vehicleType);
                        System.out.println("Supported Vehicle: " + selected.vehicleType);
                        break;
                    }

                    if(!selected.available) {
                        System.out.println("Charger is already booked.");
                        break;
                    }
                
                    System.out.println("Enter Number of Hours: ");
                    int hours = sc.nextInt();
                
                    Reservation booking = system.bookCharger(chargerID, reservationCounter, hours);
                
                    if(booking != null) {
                        reservationCounter++;
                    }
                
                    break;

                case 6:
                    system.displayCustomer();
                    break;

                case 7:
                    system.displayReservations();
                    break;

                case 8:
                    System.out.println("Enter Reservation ID: ");
                    int id = sc.nextInt();
                    system.cancelReservation(id);
                    break;

                case 9:
                    system.displayStationStatus();
                    break;

                case 0:
                    System.out.println("Thank You!");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }

        } while(choice != 0);

        sc.close();
    }
}