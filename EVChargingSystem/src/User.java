public class User {

    int userID;
    String name;
    String email;

    User(int id, String n, String e) {
        userID = id;
        name = n;
        email = e;
    }

    void displayDetails() {
        System.out.println("User ID: " + userID);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
    }
}