package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    // Database connection details
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "Admin@123";

    public static void main(String[] args) {
        // Load the MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
            return;
        }

        // Initialize scanner and connect to the database
        Scanner scanner = new Scanner(System.in);
        try (Connection dbConnection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            Patient patientModule = new Patient(dbConnection, scanner);
            Doctor doctorModule = new Doctor(dbConnection);

            while (true) {
                // Display menu options
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int userChoice = scanner.nextInt();

                // Process user input
                switch (userChoice) {
                    case 1:
                        patientModule.addPatient();
                        break;

                    case 2:
                        patientModule.displayAllPatients();
                        break;

                    case 3:
                        doctorModule.displayAllDoctors();
                        break;

                    case 4:
                        scheduleAppointment(patientModule, doctorModule, dbConnection, scanner);
                        break;

                    case 5:
                        System.out.println("Thank you for using the Hospital Management System!");
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                System.out.println(); // Add spacing for better readability
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    // Method to book an appointment between a patient and a doctor
    private static void scheduleAppointment(Patient patientModule, Doctor doctorModule, Connection dbConnection, Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        // Validate patient and doctor existence
        if (patientModule.isPatientExists(patientId) && doctorModule.isDoctorExists(doctorId)) {
            if (isDoctorAvailable(doctorId, appointmentDate, dbConnection)) {
                String insertQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";

                try (PreparedStatement stmt = dbConnection.prepareStatement(insertQuery)) {
                    stmt.setInt(1, patientId);
                    stmt.setInt(2, doctorId);
                    stmt.setString(3, appointmentDate);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        System.out.println("Appointment successfully booked!");
                    } else {
                        System.out.println("Failed to book the appointment.");
                    }
                } catch (SQLException e) {
                    System.err.println("Error booking appointment: " + e.getMessage());
                }
            } else {
                System.out.println("The selected doctor is unavailable on the specified date.");
            }
        } else {
            System.out.println("Invalid Patient ID or Doctor ID. Please try again.");
        }
    }

    // Method to check if a doctor is available on a specific date
    private static boolean isDoctorAvailable(int doctorId, String date, Connection dbConnection) {
        String availabilityQuery = "SELECT COUNT(*) AS appointment_count FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(availabilityQuery)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, date);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("appointment_count") == 0; // Return true if no appointments are found
            }
        } catch (SQLException e) {
            System.err.println("Error checking doctor availability: " + e.getMessage());
        }
        return false;
    }
}
