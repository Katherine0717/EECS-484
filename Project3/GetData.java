import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import java.util.Vector;

import org.json.JSONObject;
import org.json.JSONArray;

public class GetData {

    static String prefix = "project3.";

    // You must use the following variable as the JDBC connection
    Connection oracleConnection = null;

    // You must refer to the following variables for the corresponding 
    // tables in your database
    String userTableName = null;
    String friendsTableName = null;
    String cityTableName = null;
    String currentCityTableName = null;
    String hometownCityTableName = null;

    // DO NOT modify this constructor
    public GetData(String u, Connection c) {
        super();
        String dataType = u;
        oracleConnection = c;
        userTableName = prefix + dataType + "_USERS";
        friendsTableName = prefix + dataType + "_FRIENDS";
        cityTableName = prefix + dataType + "_CITIES";
        currentCityTableName = prefix + dataType + "_USER_CURRENT_CITIES";
        hometownCityTableName = prefix + dataType + "_USER_HOMETOWN_CITIES";
    }

    // TODO: Implement this function
    @SuppressWarnings("unchecked")
    public JSONArray toJSON() throws SQLException {

        // This is the data structure to store all users' information
        JSONArray users_info = new JSONArray();
        
        try (Statement stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            Statement stmt2 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // Your implementation goes here....
            ResultSet rst = stmt.executeQuery(
                "SELECT U.MONTH_OF_BIRTH, CITY_H.COUNTRY_NAME, CITY_H.CITY_NAME, CITY_H.STATE_NAME, CITY_C.COUNTRY_NAME, CITY_C.CITY_NAME, CITY_C.STATE_NAME, U.GENDER, U.USER_ID, U.DAY_OF_BIRTH, U.LAST_NAME, U.FIRST_NAME, U.YEAR_OF_BIRTH " + 
                "FROM " + userTableName + " U, " + hometownCityTableName + " HOME, " + currentCityTableName + " CURR, " + cityTableName + " CITY_H, " + cityTableName + " CITY_C " +
                "WHERE U.USER_ID = HOME.USER_ID " +
                "AND HOME.HOMETOWN_CITY_ID = CITY_H.CITY_ID " + 
                "AND U.USER_ID = CURR.USER_ID " +
                "AND CURR.CURRENT_CITY_ID = CITY_C.CITY_ID "
            );
            while(rst.next()){
                JSONObject each_user = new JSONObject();
                each_user.put("MOB", rst.getInt(1));

                JSONObject hometown_info = new JSONObject();
                if(rst.getString(2) != null){
                    hometown_info.put("country", rst.getString(2));
                    hometown_info.put("city", rst.getString(3));
                    hometown_info.put("state", rst.getString(4));
                }
                each_user.put("hometown", hometown_info);
                
                JSONObject current_info = new JSONObject();
                if(rst.getString(5) != null){
                    current_info.put("country", rst.getString(5));
                    current_info.put("city", rst.getString(6));
                    current_info.put("state", rst.getString(7));
                }
                each_user.put("current", current_info);

                each_user.put("gender", rst.getString(8));
                each_user.put("user_id", rst.getInt(9));
                each_user.put("DOB", rst.getInt(10));
                each_user.put("last_name", rst.getString(11));
                each_user.put("first_name", rst.getString(12));
                each_user.put("YOB", rst.getInt(13));

                int userId = rst.getInt(9);
                ResultSet rst2 = stmt2.executeQuery(
                    "SELECT F.USER2_ID " +
                    "FROM " + friendsTableName + " F " +
                    "WHERE F.USER1_ID = " + userId
                );

                JSONArray friends_info = new JSONArray();
                while(rst2.next()){
                    friends_info.put(rst2.getInt(1));
                }
                each_user.put("friends", friends_info);
                users_info.put(each_user);

                rst2.close();
            }
            
            stmt2.close();
            rst.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return users_info;
    }

    // This outputs to a file "output.json"
    // DO NOT MODIFY this function
    public void writeJSON(JSONArray users_info) {
        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "/output.json");
            file.write(users_info.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
