package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainBDD {
    private static final String URL = "jdbc:mysql://localhost:1024/ChessUsers?useSSL=false";
    private static final String USER = "guillaume";
    private static final String PASSWORD = "guillaume";

    public static void main(String[] args) {
        MainBDD app = new MainBDD();
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur de connexion à la base de données.");
            return null;
        }
    }

    public void addUser(String nom, String email) {
        String query = "INSERT INTO utilisateurs (nom) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nom);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Un nouvel utilisateur a été ajouté avec succès.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}