public class Admin extends User {

    String role;

    Admin(int id, String name, String email, String r) {
        super(id, name, email);
        role = r;
    }

    void displayDetails() {
        super.displayDetails();
        System.out.println("Role: " + role);
    }
}