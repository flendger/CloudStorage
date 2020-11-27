package services;

import utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public static UserProfile findProfile(String login, String password) {
        UserProfile userProfile = null;

        try (Connection conn = DatabaseUtils.connectDB()) {
            PreparedStatement st = conn.prepareStatement("SELECT *" +
                    "  FROM users\n" +
                    " WHERE login = ? AND \n" +
                    "       password = ?;");
            st.setString(1, login);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                userProfile = new UserProfile(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("root")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userProfile;
    }

    public static UserProfile findUserByName(String login) {
        UserProfile userProfile = null;

        try (Connection conn = DatabaseUtils.connectDB()) {
            PreparedStatement st = conn.prepareStatement("SELECT *" +
                    "  FROM users\n" +
                    " WHERE login = ?\n" +
                    "LIMIT 1;");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                userProfile = new UserProfile(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("root")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userProfile;
    }

    public static UserProfile createProfile(String login, String password) throws CreateProfileFailedException{
        UserProfile userProfile;
        if (findUserByName(login) != null) {
            throw new CreateProfileFailedException(String.format("User [%s] already exists.", login));
        }

        try (Connection conn = DatabaseUtils.connectDB()) {
            PreparedStatement st = conn.prepareStatement("INSERT INTO users (\n" +
                    "                      login,\n" +
                    "                      password,\n" +
                    "                      root\n" +
                    "                  )\n" +
                    "                  VALUES (\n" +
                    "                      ?,\n" +
                    "                      ?,\n" +
                    "                      ?\n" +
                    "                  );");
            st.setString(1, login);
            st.setString(2, password);
            st.setString(3, login);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CreateProfileFailedException(String.format("Unable to create profile for user [%s].", login));
        }

        userProfile = findProfile(login, password);
        if (userProfile == null) {
            throw new CreateProfileFailedException(String.format("Unable to create profile for user [%s].", login));
        }
        return userProfile;
    }
}