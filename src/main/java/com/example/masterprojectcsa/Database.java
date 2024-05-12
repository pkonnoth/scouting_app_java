package com.example.masterprojectcsa;

import java.sql.*;

public class Database {
    private Connection conn;
    private PreparedStatement pst;

    public Database() {
        // Default constructor
    }

    public void Connect() {
        String url = "jdbc:mysql://localhost:3306/Scouting App";
        String user = "root";
        String password = "Irine2012";
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("connected");
        } catch (SQLException e) {
            System.out.println("didnt connect");
            throw new RuntimeException(e);
        }
    }

    public void Disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
            System.out.println("disconnected");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Assuming the 'id' column is part of your table schema
    public void Create(int team, int auton_points, int amp, int cycles, String description, int auton_status, int match_number) {
        try {
            // Prepare the SQL statement without the 'id' field as it is auto-incremented by the database
            pst = conn.prepareStatement("INSERT INTO Data (team, auton_points, amp, cycles, description, auton_status, match_number) VALUES (?, ?, ?, ?, ?, ?, ?)");
            pst.setInt(1, team);
            pst.setInt(2, auton_points);
            pst.setInt(3, amp);
            pst.setInt(4, cycles);
            pst.setString(5, description);
            pst.setInt(6, auton_status);
            pst.setInt(7, match_number);
            pst.executeUpdate();
            System.out.println("Data inserted");
        } catch (SQLException e) {
            System.out.println("Data not inserted");
            throw new RuntimeException(e);
        }
    }


    public ResultSet Read() {
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("SELECT * FROM Data");
            rs = pst.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    public void Update(int id, int team, int auton_points, int amp, int cycles, String description, int auton_status, int match_number) {
        try {
            pst = conn.prepareStatement("UPDATE Data SET team = ?, auton_points = ?, amp = ?, cycles = ?, description = ?, auton_status = ?, match_number = ? WHERE id = ?");
            pst.setInt(1, team);
            pst.setInt(2, auton_points);
            pst.setInt(3, amp);
            pst.setInt(4, cycles);
            pst.setString(5, description);
            pst.setInt(6, auton_status);
            pst.setInt(7, match_number);
            pst.setInt(8, id);
            pst.executeUpdate();
            System.out.println("Data updated");
        } catch (SQLException e) {
            System.out.println("Data not updated");
            throw new RuntimeException(e);
        }
    }


    public void Delete(int id) {
        try {
            pst = conn.prepareStatement("DELETE FROM Data WHERE id = ?");
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Data deleted");
        } catch (SQLException e) {
            System.out.println("Data not deleted");
            throw new RuntimeException(e);
        }
    }
}

