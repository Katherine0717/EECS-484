package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }

    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                    "SELECT COUNT(*) AS Birthed, Month_of_Birth " + // select birth months and number of uses with that birth month
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth IS NOT NULL " + // for which a birth month is available
                            "GROUP BY Month_of_Birth " + // group into buckets by birth month
                            "ORDER BY Birthed DESC, Month_of_Birth ASC"); // sort by users born in that month, descending; break ties by birth month

            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    mostMonth = rst.getInt(2); //   it is the month with the most
                }
                if (rst.isLast()) { // if last record
                    leastMonth = rst.getInt(2); //   it is the month with the least
                }
                total += rst.getInt(1); // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);

            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + mostMonth + " " + // born in the most popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + leastMonth + " " + // born in the least popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }

    @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                FirstNameInfo info = new FirstNameInfo();
                ResultSet rst = stmt.executeQuery(
                "SELECT FIRST_NAME " + // select first name and number of uses with first name
                        "FROM " + UsersTable + " "+ // from all users
                        "WHERE FIRST_NAME IS NOT NULL " + // for which first name is available
                        "GROUP BY FIRST_NAME " +// group into buckets by first name
                        "ORDER BY LENGTH(FIRST_NAME) DESC, First_Name ASC"); 
            String mostCommonFirstNames="";
            String longestFirstNames="";
            String shortestFirstNames="";
            int commonCount=0;
            // get longest first name
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    longestFirstNames=rst.getString(1);
                }
                if (rst.isLast()) {
                    shortestFirstNames = rst.getString(1);
                }

            }
            // get shortest first name
            rst.beforeFirst();
            while (rst.next()) {
                if (rst.getString(1).length() == longestFirstNames.length()) {
                    info.addLongName(rst.getString(1));
                }
                if (rst.getString(1).length() == shortestFirstNames.length()) {
                    info.addShortName(rst.getString(1));
                }
                    
            }
            //get most common name
            rst = stmt.executeQuery(
                "SELECT COUNT(*) AS CNUM, FIRST_NAME " +// select first name and number of uses with first name
                        "FROM " + UsersTable + " " + // from all users
                        "GROUP BY FIRST_NAME " +// group into buckets by first name
                        "ORDER BY CNUM DESC, First_Name ASC"); 
        
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    commonCount = rst.getInt(1);
                }
            }
            info.setCommonNameCount(commonCount);
            rst.beforeFirst();
            while (rst.next()) { // step through result rows/records one by one
                if (rst.getInt(1)==commonCount) { // if first record
                    info.addCommonName(rst.getString(2));    // it is the name with the most
                }
            }

            rst.close();
            stmt.close();

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                FirstNameInfo info = new FirstNameInfo();
                info.addLongName("Aristophanes");
                info.addLongName("Michelangelo");
                info.addLongName("Peisistratos");
                info.addShortName("Bob");
                info.addShortName("Sue");
                info.addCommonName("Harold");
                info.addCommonName("Jessica");
                info.setCommonNameCount(42);
                return info;
            */
            return info; // placeholder for compilation
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FirstNameInfo();
        }
    }

    @Override
    // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                
            ResultSet rst = stmt.executeQuery(
                "SELECT USER_ID, FIRST_NAME, LAST_NAME " +
                "FROM " + UsersTable + " " + " allUsers " +
                "WHERE USER_ID NOT IN (SELECT USER1_ID FROM " + FriendsTable + 
                " UNION SELECT DISTINCT USER2_ID FROM " + FriendsTable + " ) " +
                "ORDER BY USER_ID ASC"
            );
            
            while (rst.next()) {
                UserInfo lonelyUsers = new UserInfo(rst.getInt(1), rst.getString(2), 
                                            rst.getString(3));
                results.add(lonelyUsers);
            }
            
            rst.close();
            stmt.close();
                    
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(15, "Abraham", "Lincoln");
                UserInfo u2 = new UserInfo(39, "Margaret", "Thatcher");
                results.add(u1);
                results.add(u2);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {

            ResultSet rst = stmt.executeQuery(
                "SELECT U.User_ID, U.First_Name, U.Last_Name " + 
                "FROM " + UsersTable + " U, " + HometownCitiesTable + " H, " + 
                          CurrentCitiesTable + " C " +
                "WHERE U.User_ID = H.User_ID AND U.User_ID = C.User_ID " + 
                "AND H.HOMETOWN_CITY_ID IS NOT NULL AND C.CURRENT_CITY_ID IS NOT NULL " +
                "AND H.HOMETOWN_CITY_ID <> C.CURRENT_CITY_ID " + 
                "ORDER BY U.User_ID ASC" );
            while (rst.next()) 
            {
            UserInfo liveAway = new UserInfo(rst.getInt(1), rst.getString(2), 
                                        rst.getString(3));
            results.add(liveAway);
            }

            rst.close();
            stmt.close();

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(9, "Meryl", "Streep");
                UserInfo u2 = new UserInfo(104, "Tom", "Hanks");
                results.add(u1);
                results.add(u2);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");

        Statement stmt2 = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly);
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {

            ResultSet rst = stmt.executeQuery(
                "SELECT * FROM " + 
                "(SELECT P.PHOTO_ID, P.PHOTO_LINK, P.ALBUM_ID, A.ALBUM_NAME " + 
                "FROM " + AlbumsTable + " A, " + PhotosTable + " P, " + 
                    "(SELECT T.TAG_PHOTO_ID " +
                    "FROM " + TagsTable + " T " +
                    "WHERE T.TAG_PHOTO_ID IS NOT NULL " +
                    "GROUP BY T.TAG_PHOTO_ID " + 
                    "ORDER BY COUNT(T.TAG_SUBJECT_ID) DESC, TAG_PHOTO_ID ASC) P2 " + 
                "WHERE A.ALBUM_ID = P.ALBUM_ID " +
                "AND P.PHOTO_ID = P2.TAG_PHOTO_ID) " +
                "WHERE ROWNUM <= " + num
            );
            

            // int count = 0;
            while (rst.next()) {
                // count++;

                PhotoInfo p = new PhotoInfo(rst.getInt(1), rst.getInt(3), rst.getString(2), rst.getString(4));

                ResultSet rst2 = stmt2.executeQuery(
                    "SELECT U.USER_ID, U.FIRST_NAME, U.LAST_NAME " + 
                    "FROM " + TagsTable + " T, " + UsersTable + " U " +
                    "WHERE T.TAG_PHOTO_ID = " + rst.getInt(1) + " " +
                    "AND T.TAG_SUBJECT_ID = U.USER_ID " +
                    "ORDER BY USER_ID ASC "
                );

                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                while (rst2.next()) {
                    UserInfo u1 = new UserInfo(rst2.getInt(1), rst2.getString(2), rst2.getString(3));
                    tp.addTaggedUser(u1);
                }

                results.add(tp);

                rst2.close();
            }

            rst.close();
            stmt.close();

            stmt2.close();

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together

    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
        FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(93103, "Romeo", "Montague");
                UserInfo u2 = new UserInfo(93113, "Juliet", "Capulet");
                MatchPair mp = new MatchPair(u1, 1597, u2, 1597);
                PhotoInfo p = new PhotoInfo(167, 309, "www.photolink.net", "Tragedy");
                mp.addSharedPhoto(p);
                results.add(mp);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U1.YEAR_OF_BIRTH, U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME, U2.YEAR_OF_BIRTH, T1.TAG_PHOTO_ID, A.ALBUM_ID, A.ALBUM_NAME, P.PHOTO_LINK " + 
                    " FROM " + UsersTable + " U1, " + UsersTable + " U2, " + TagsTable + " T1, " + TagsTable + " T2, "  + PhotosTable + " P, " + AlbumsTable + " A, " + 
                        "(SELECT U1.USER_ID AS USER1ID, U2.USER_ID AS USER2ID " + 
                        "FROM " + UsersTable + " U1, " + UsersTable + " U2, " + TagsTable + " T1, " + TagsTable + " T2, " + AlbumsTable + " A, " + PhotosTable + " P " + 
                        "WHERE U1.USER_ID < U2.USER_ID " + 
                        "AND U1.gender = U2.gender " + 
                        "AND U1.USER_ID = T1.TAG_SUBJECT_ID " + 
                        "AND T1.TAG_PHOTO_ID = T2.TAG_PHOTO_ID " +
                        "AND ABS(U1.YEAR_OF_BIRTH - U2.YEAR_OF_BIRTH) <= " + yearDiff + " " +
                        "AND (U1.USER_ID, U2.USER_ID) NOT IN (SELECT DISTINCT * FROM " + FriendsTable + ") " +
                        "AND U1.USER_ID = T1.TAG_SUBJECT_ID " + 
                        "AND U2.USER_ID = T2.TAG_SUBJECT_ID " +
                        "GROUP BY (U1.User_ID, U2.User_ID) " +
                        "ORDER BY COUNT(T1.TAG_PHOTO_ID) DESC, U1.USER_ID, U2.USER_ID " +
                        "FETCH FIRST " + num + " ROWS ONLY ) T  " + 
                    "WHERE U1.USER_ID = T1.TAG_SUBJECT_ID " + 
                    "AND U2.USER_ID = T2.TAG_SUBJECT_ID " + 
                    "AND T1.TAG_PHOTO_ID = T2.TAG_PHOTO_ID " + 
                    "AND A.ALBUM_ID = P.ALBUM_ID " + 
                    "AND T1.TAG_PHOTO_ID = P.PHOTO_ID " + 
                    "AND U1.USER_ID = T.USER1ID " + 
                    "AND U2.USER_ID = T.USER2ID " + 
                    "ORDER BY TAG_PHOTO_ID ");

            while (rst.next()) {
                int u1_id = rst.getInt(1);
                int u2_id = rst.getInt(5);
                UserInfo U1 = new UserInfo(u1_id, rst.getString(2), rst.getString(3));
                UserInfo U2 = new UserInfo(u2_id, rst.getString(6), rst.getString(7));
                MatchPair mp = new MatchPair(U1, rst.getInt(4), U2, rst.getInt(8));
                PhotoInfo p = new PhotoInfo(rst.getInt(9), rst.getInt(10), rst.getString(12), rst.getString(11));
                mp.addSharedPhoto(p);
                results.add(mp);
            }

            rst.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            Statement stmt2 = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly);

            ResultSet rst = stmt.executeQuery(
                "SELECT U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME FROM " +
                    "(SELECT Common.U1 AS U1, Common.U2 AS U2, COUNT(*) AS total_num " + 
                    "FROM " +  
                        "(SELECT F1.user2_id AS U1, F2.user2_id AS U2 " + 
                        "FROM " + FriendsTable + " F1 " + 
                        "JOIN " + FriendsTable + " F2 " + 
                        "ON F1.user1_id = F2.user1_id " + 
                        "WHERE F1.user2_id < F2.user2_id " + 
                        "AND (F1.user2_id, F2.user2_id) NOT IN (SELECT DISTINCT * FROM " + FriendsTable + " F3) " +
                        "UNION ALL " + 
                        "SELECT F1.user1_id AS U1, F2.user2_id AS U2 " + 
                        "FROM " + FriendsTable + " F1 " + 
                        "JOIN " + FriendsTable + " F2 " + 
                        "ON F1.user2_id = F2.user1_id " + 
                        "WHERE F1.user1_id < F2.user2_id " + 
                        "AND (F1.user1_id, F2.user2_id) NOT IN (SELECT DISTINCT * FROM " + FriendsTable + " F3) " +
                        "UNION ALL " + 
                        "SELECT F1.user1_id AS U1, F2.user1_id AS U2 " + 
                        "FROM " + FriendsTable + " F1 " + 
                        "JOIN " + FriendsTable + " F2 " + 
                        "ON F1.user2_id = F2.user2_id " + 
                        "WHERE F1.user1_id < F2.user1_id " + 
                        "AND (F1.user1_id, F2.user1_id) NOT IN (SELECT DISTINCT * FROM " + FriendsTable + " F3) " +
                    " ) Common " +
                    "GROUP BY Common.U1, Common.U2 " +
                    "ORDER BY COUNT(*) DESC, Common.U1 ASC, Common.U2 ASC " + 
                    "FETCH FIRST " + num + " ROWS ONLY ) Common " + 
                "JOIN " + UsersTable + " U1 " + 
                "ON Common.U1 = U1.USER_ID " + 
                "JOIN "+ UsersTable + " U2 " + 
                "ON Common.U2 = U2.USER_ID " + 
                "ORDER BY Common.total_num DESC, U1.USER_ID, U2.USER_ID"
            );

            while (rst.next()) {
                int u1_id = rst.getInt(1);
                String u1_first = rst.getString(2);
                String u1_last = rst.getString(3);
                int u2_id = rst.getInt(4);
                String u2_first = rst.getString(5);
                String u2_last = rst.getString(6);

                UserInfo u1 = new UserInfo(u1_id, u1_first, u1_last);
                UserInfo u2 = new UserInfo(u2_id, u2_first, u2_last);
                UsersPair up = new UsersPair(u1, u2);

                ResultSet rst2 = stmt2.executeQuery(
                "SELECT UT.USER_ID, UT.FIRST_NAME, UT.LAST_NAME " +
                    "FROM " + UsersTable + " UT, " + 
                        "(SELECT F1.USER2_ID AS U3_ID " + 
                        "FROM " + FriendsTable + " F1 " +
                        "JOIN " + FriendsTable + " F2 " + 
                        "ON F1.USER2_ID = F2.USER1_ID " + 
                        "WHERE F1.USER1_ID =  " + u1_id + " " + 
                        "AND F2.USER2_ID =  " + u2_id + " " +
                        "UNION " + 
                        "SELECT F1.USER1_ID AS U3_ID " + 
                        "FROM " + FriendsTable + " F1 " +
                        "JOIN " + FriendsTable + " F2 " + 
                        "ON F1.USER1_ID = F2.USER1_ID " + 
                        "WHERE F1.USER2_ID =  " + u1_id + " " + 
                        "AND F2.USER2_ID =  " + u2_id + " " + 
                        "UNION " + 
                        "SELECT F1.USER2_ID AS U3_ID " + 
                        "FROM " + FriendsTable + " F1 " +
                        "JOIN " + FriendsTable + " F2 " + 
                        "ON F1.USER2_ID = F2.USER2_ID " + 
                        "WHERE F1.USER1_ID =  " + u1_id + " " + 
                        "AND F2.USER1_ID =  " + u2_id + " " + 
                    ") U3 " +
                    "WHERE UT.User_ID = U3.U3_ID " + 
                    "ORDER BY UT.User_ID " 
                ); 

                while(rst2.next()){
                    UserInfo u3 = new UserInfo(rst2.getInt(1), rst2.getString(2), rst2.getString(3));
                    up.addSharedFriend(u3);
                }
                results.add(up);
                rst2.close();

                rst2.close();
            }

            
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(16, "The", "Hacker");
                UserInfo u2 = new UserInfo(80, "Dr.", "Marbles");
                UserInfo u3 = new UserInfo(192, "Digit", "Le Boid");
                UsersPair up = new UsersPair(u1, u2);
                up.addSharedFriend(u3);
                results.add(up);
            */
            rst.close();
            stmt.close();

            stmt2.close();
            

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            ResultSet rst = stmt.executeQuery(
                "SELECT C.STATE_NAME, COUNT(*) " + 
                    " FROM " + EventsTable + " E, " + CitiesTable + " C " + 
                    " WHERE E.EVENT_CITY_ID = C.CITY_ID " + 
                    " GROUP BY C.STATE_NAME " + 
                    " ORDER BY COUNT(*) DESC"
            );

            int num = -1;
            EventStateInfo info = null;
            while (rst.next()) {
                if (rst.isFirst()) { 
                    info = new EventStateInfo(rst.getInt(2));
                    info.addState(rst.getString(1));
                }
            }

            rst.close();
            stmt.close();

            return info;

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                EventStateInfo info = new EventStateInfo(50);
                info.addState("Kentucky");
                info.addState("Hawaii");
                info.addState("New Hampshire");
                return info;
            */
            // return new EventStateInfo(num); // placeholder for compilation
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }

    @Override
    // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {

            ResultSet rst = stmt.executeQuery(
                "SELECT User0.USER_ID, User0.FIRST_NAME, User0.LAST_NAME " + 
                    " FROM " + UsersTable + " User0, " + FriendsTable + " F " + 
                    " WHERE (F.USER2_ID = User0.USER_ID " + "AND F.USER1_ID = " + userID + ") OR (F.USER2_ID = " + userID + " AND F.USER1_ID = User0.USER_ID) " + 
                    " ORDER BY User0.YEAR_OF_BIRTH ASC, USER0.MONTH_OF_BIRTH ASC, User0.DAY_OF_BIRTH ASC, User0.USER_ID DESC "
            );

            int OLD_ID = 0;
            String OLD_FIRST = "";
            String OLD_LAST = "";

            int YOUNG_ID = 0;
            String YOUNG_FIRST = "";
            String YOUNG_LAST = "";

            while (rst.next()) {
                if (rst.isFirst()) {
                    OLD_ID = rst.getInt(1);
                    OLD_FIRST = rst.getString(2);
                    OLD_LAST = rst.getString(3);
                }
                if(rst.isLast()){
                    YOUNG_ID = rst.getInt(1);
                    YOUNG_FIRST = rst.getString(2);
                    YOUNG_LAST = rst.getString(3);
                }
            }

            rst.close();
            stmt.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo old = new UserInfo(12000000, "Galileo", "Galilei");
                UserInfo young = new UserInfo(80000000, "Neil", "deGrasse Tyson");
                return new AgeInfo(old, young);
            */
            return new AgeInfo(new UserInfo(OLD_ID, OLD_FIRST, OLD_LAST), new UserInfo(YOUNG_ID, YOUNG_FIRST, YOUNG_LAST)); // placeholder for compilation
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }

    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {

            ResultSet rst = stmt.executeQuery(
                "SELECT User1.USER_ID, User1.FIRST_NAME, User1.LAST_NAME, User2.USER_ID, User2.FIRST_NAME, User2.LAST_NAME " +  
                    "FROM " + UsersTable + " User1, " + UsersTable + " User2, " + HometownCitiesTable + " HC1, " + HometownCitiesTable + " HC2, " + FriendsTable + " F " + 
                    "WHERE F.USER1_ID = USER1.USER_ID " + 
                    "AND F.USER2_ID = USER2.USER_ID " + 
                    "AND User1.USER_ID = HC1.USER_ID " + 
                    "AND User2.USER_ID = HC2.USER_ID " + 
                    "AND User1.LAST_NAME = User2.LAST_NAME " + 
                    "AND ABS(User1.YEAR_OF_BIRTH - User2.YEAR_OF_BIRTH) < 10 " +
                    "AND HC1.HOMETOWN_CITY_ID = HC2.HOMETOWN_CITY_ID " + 
                    "ORDER BY User1.USER_ID ASC, User2.USER_ID ASC");


            while (rst.next()) {
                UserInfo u1 = new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3));
                UserInfo u2 = new UserInfo(rst.getLong(4), rst.getString(5), rst.getString(6));
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            }

            rst.close();
            stmt.close();

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(81023, "Kim", "Kardashian");
                UserInfo u2 = new UserInfo(17231, "Kourtney", "Kardashian");
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}
