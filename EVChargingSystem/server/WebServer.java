import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class WebServer {

    static EVChargingSystem system;
    static int reservationCounter = 1001;

    public static void main(String[] args) throws Exception {

        Charger c1 = new Charger(101, "AC", "2-Wheeler", 7.4, 150);
        Charger c2 = new Charger(102, "DC Fast", "4-Wheeler", 50, 350);
        Charger c3 = new Charger(103, "DC Fast", "4-Wheeler", 100, 500);

        ChargingStation station = new ChargingStation(1, "Pune EV Station", "Pune", 5);

        station.addCharger(c1);
        station.addCharger(c2);
        station.addCharger(c3);

        system = new EVChargingSystem(station);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", WebServer::handleHome);
        server.createContext("/style.css", WebServer::handleCSS);
        server.createContext("/script.js", WebServer::handleJS);
        server.createContext("/api/state", WebServer::handleState);
        server.createContext("/api/register", WebServer::handleRegister);
        server.createContext("/api/wallet", WebServer::handleWallet);
        server.createContext("/api/book", WebServer::handleBook);
        server.createContext("/api/cancel", WebServer::handleCancel);

        server.setExecutor(null);
        server.start();

        System.out.println("EV Charging System running");
        System.out.println("http://localhost:8080");
    }

    static void handleHome(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            send(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }
        sendFile(exchange, "web/index.html", "text/html");
    }

    static void handleCSS(HttpExchange exchange) throws IOException {
        sendFile(exchange, "web/style.css", "text/css");
    }

    static void handleJS(HttpExchange exchange) throws IOException {
        sendFile(exchange, "web/script.js", "application/javascript");
    }

    static void handleState(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            send(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }
        send(exchange, 200, createStateJSON(), "application/json");
    }

    static void handleRegister(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            send(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }

        String body = readBody(exchange);
        String name = getValue(body, "name");
        String email = getValue(body, "email");
        String vehicle = getValue(body, "vehicle");
        String vehicleType = getValue(body, "vehicleType");

        if(name.isEmpty() || email.isEmpty() || vehicle.isEmpty() || vehicleType.isEmpty()) {
            send(exchange, 400, "{\"success\":false,\"message\":\"All fields are required.\"}", "application/json");
            return;
        }

        system.registerCustomer(1, name, email, vehicle, vehicleType);
        send(exchange, 200, "{\"success\":true,\"message\":\"Customer registered successfully.\"}", "application/json");
    }

    static void handleWallet(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            send(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }

        if(system.customer == null) {
            send(exchange, 400, "{\"success\":false,\"message\":\"Register a customer first.\"}", "application/json");
            return;
        }

        String body = readBody(exchange);

        try {
            double amount = Double.parseDouble(getValue(body, "amount"));

            if(amount <= 0) {
                send(exchange, 400, "{\"success\":false,\"message\":\"Enter a valid amount.\"}", "application/json");
                return;
            }

            system.addMoney(amount);
            send(exchange, 200, "{\"success\":true,\"message\":\"Money added successfully.\"}", "application/json");

        } catch(Exception e) {
            send(exchange, 400, "{\"success\":false,\"message\":\"Invalid amount.\"}", "application/json");
        }
    }

    static void handleBook(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            send(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }

        if(system.customer == null) {
            send(exchange, 400, "{\"success\":false,\"message\":\"Register a customer first.\"}", "application/json");
            return;
        }

        String body = readBody(exchange);

        try {
            int chargerID = Integer.parseInt(getValue(body, "chargerID"));
            int hours = Integer.parseInt(getValue(body, "hours"));

            Charger charger = system.station.findCharger(chargerID);

            if(charger == null) {
                send(exchange, 400, "{\"success\":false,\"message\":\"Charger not found.\"}", "application/json");
                return;
            }

            if(!system.customer.vehicleType.equalsIgnoreCase(charger.vehicleType)) {
                send(exchange, 400, "{\"success\":false,\"message\":\"This charger is not compatible with your vehicle.\"}", "application/json");
                return;
            }

            if(!charger.available) {
                send(exchange, 400, "{\"success\":false,\"message\":\"Charger is already booked.\"}", "application/json");
                return;
            }

            if(hours <= 0) {
                send(exchange, 400, "{\"success\":false,\"message\":\"Invalid charging duration.\"}", "application/json");
                return;
            }

            Reservation booking = system.bookCharger(chargerID, reservationCounter, hours);

            if(booking == null) {
                send(exchange, 400, "{\"success\":false,\"message\":\"Booking failed. Check your wallet balance.\"}", "application/json");
                return;
            }

            reservationCounter++;

            send(exchange, 200, "{\"success\":true,\"message\":\"Charger booked successfully.\"}", "application/json");

        } catch(Exception e) {
            send(exchange, 400, "{\"success\":false,\"message\":\"Invalid booking details.\"}", "application/json");
        }
    }

    static void handleCancel(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            send(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }

        if(system.customer == null) {
            send(exchange, 400, "{\"success\":false,\"message\":\"Register a customer first.\"}", "application/json");
            return;
        }

        String body = readBody(exchange);

        try {
            int reservationID = Integer.parseInt(getValue(body, "reservationID"));
            system.cancelReservation(reservationID);
            send(exchange, 200, "{\"success\":true,\"message\":\"Cancellation processed.\"}", "application/json");

        } catch(Exception e) {
            send(exchange, 400, "{\"success\":false,\"message\":\"Invalid reservation ID.\"}", "application/json");
        }
    }

    static String createStateJSON() {

        StringBuilder json = new StringBuilder();
        json.append("{");

        if(system.customer == null) {
            json.append("\"customer\":null,");
        } else {
            Customer c = system.customer;

            json.append("\"customer\":{");
            json.append("\"id\":").append(c.userID).append(",");
            json.append("\"name\":\"").append(escape(c.name)).append("\",");
            json.append("\"email\":\"").append(escape(c.email)).append("\",");
            json.append("\"vehicle\":\"").append(escape(c.vehicleNumber)).append("\",");
            json.append("\"vehicleType\":\"").append(escape(c.vehicleType)).append("\",");
            json.append("\"wallet\":").append(c.walletBalance);
            json.append("},");
        }

        json.append("\"station\":{");
        json.append("\"id\":").append(system.station.stationID).append(",");
        json.append("\"name\":\"").append(escape(system.station.stationName)).append("\",");
        json.append("\"location\":\"").append(escape(system.station.location)).append("\",");
        json.append("\"revenue\":").append(system.station.totalRevenue);
        json.append("},");

        json.append("\"chargers\":[");

        for(int i = 0; i < system.station.chargerCount; i++) {
            Charger c = system.station.chargers[i];

            if(i > 0) {
                json.append(",");
            }

            json.append("{");
            json.append("\"id\":").append(c.chargerID).append(",");
            json.append("\"type\":\"").append(escape(c.chargerType)).append("\",");
            json.append("\"vehicleType\":\"").append(escape(c.vehicleType)).append("\",");
            json.append("\"power\":").append(c.powerKW).append(",");
            json.append("\"price\":").append(c.pricePerHour).append(",");
            json.append("\"available\":").append(c.available);
            json.append("}");
        }

        json.append("],");

        json.append("\"reservations\":[");

        if(system.customer != null) {
            for(int i = 0; i < system.customer.reservations.size(); i++) {
                Reservation r = system.customer.reservations.get(i);

                if(i > 0) {
                    json.append(",");
                }

                json.append("{");
                json.append("\"id\":").append(r.reservationID).append(",");
                json.append("\"chargerID\":").append(r.charger.chargerID).append(",");
                json.append("\"chargerType\":\"").append(escape(r.charger.chargerType)).append("\",");
                json.append("\"vehicleType\":\"").append(escape(r.charger.vehicleType)).append("\",");
                json.append("\"hours\":").append(r.hours).append(",");
                json.append("\"amount\":").append(r.totalAmount).append(",");
                json.append("\"status\":\"").append(escape(r.status)).append("\"");
                json.append("}");
            }
        }

        json.append("],");

        int available = 0;
        int booked = 0;

        for(int i = 0; i < system.station.chargerCount; i++) {
            if(system.station.chargers[i].available) {
                available++;
            } else {
                booked++;
            }
        }

        json.append("\"availableCount\":").append(available).append(",");
        json.append("\"bookedCount\":").append(booked);
        json.append("}");

        return json.toString();
    }

    static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    static String getValue(String body, String key) {
        String[] parts = body.split("&");

        for(String part : parts) {
            String[] pair = part.split("=", 2);

            if(pair.length == 2 && URLDecoder.decode(pair[0], StandardCharsets.UTF_8).equals(key)) {
                return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            }
        }

        return "";
    }

    static String escape(String value) {
        if(value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    static void sendFile(HttpExchange exchange, String path, String contentType) throws IOException {

        File file = new File(path);

        if(!file.exists()) {
            send(exchange, 404, "File not found: " + path, "text/plain");
            return;
        }

        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());

        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.sendResponseHeaders(200, data.length);

        OutputStream output = exchange.getResponseBody();
        output.write(data);
        output.close();
    }

    static void send(HttpExchange exchange, int status, String response, String contentType) throws IOException {

        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        exchange.sendResponseHeaders(status, data.length);

        OutputStream output = exchange.getResponseBody();
        output.write(data);
        output.close();
    }
}