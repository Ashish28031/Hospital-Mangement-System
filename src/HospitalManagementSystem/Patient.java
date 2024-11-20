package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Patient {
    private final Connection dbConnection;
    private final Scanner inputScanner;

    // Constructor to initialize database connection and scanner
    public Patient(Connection dbConnection, Scanner inputScanner) {
        this.dbConnection = dbConnection;
        this.inputScanner = inputScanner;
    }

    // Method to add a new patient
    public void addPatient() {
        System.out.print("Enter Patient Name: ");
        String name = inputScanner.next();
        System.out.print("Enter Patient Age: ");
        int age = inputScanner.nextInt();
        System.out.print("Enter Patient Gender: ");
        String gender = inputScanner.next();

        String insertQuery = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient added successfully!");
            } else {
                System.out.println("Failed to add patient. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
        }
    }

    // Method to view all patients
    public void displayAllPatients() {
        String selectQuery = "SELECT * FROM patients";
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(selectQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients List:");
            System.out.println("+------------+--------------------+----------+------------+");
            System.out.println("| Patient ID | Name               | Age      | Gender     |");
            System.out.println("+------------+--------------------+----------+------------+");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");

                System.out.printf("| %-10d | %-18s | %-8d | %-10s |\n", id, name, age, gender);
                System.out.println("+------------+--------------------+----------+------------+");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patient list: " + e.getMessage());
        }
    }

    // Method to check if a patient exists by their ID
    public boolean isPatientExists(int patientId) {
        String selectQuery = "SELECT COUNT(*) FROM patients WHERE id = ?";
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking patient existence: " + e.getMessage());
        }
        return false;
    }
}
