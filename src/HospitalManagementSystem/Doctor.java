package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {
    private Connection dbConnection;

    // Constructor to initialize the database connection
    public Doctor(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Method to display a list of all doctors in the database
    public void displayAllDoctors() {
        String sqlQuery = "SELECT * FROM doctors";
        try (PreparedStatement statement = dbConnection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = statement.executeQuery();
            
            System.out.println("Available Doctors:");
            System.out.println("+------------+--------------------+------------------+");
            System.out.println("| Doctor ID  | Name               | Specialization   |");
            System.out.println("+------------+--------------------+------------------+");

            while (resultSet.next()) {
                int doctorId = resultSet.getInt("id");
                String doctorName = resultSet.getString("name");
                String doctorSpecialization = resultSet.getString("specialization");

                System.out.printf("| %-10d | %-18s | %-16s |\n", doctorId, doctorName, doctorSpecialization);
            }
            System.out.println("+------------+--------------------+------------------+");
        } catch (SQLException e) {
            System.err.println("Error retrieving doctor information: " + e.getMessage());
        }
    }

    // Method to check if a doctor exists by their ID
    public boolean isDoctorExists(int doctorId) {
        String sqlQuery = "SELECT COUNT(*) AS count FROM doctors WHERE id = ?";
        try (PreparedStatement statement = dbConnection.prepareStatement(sqlQuery)) {
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verifying doctor existence: " + e.getMessage());
        }
        return false;
    }
}
