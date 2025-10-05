import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import SQLConnection.SQLConnection;

public class Main {
    public static void main(String[] args) {
        Connection connection = SQLConnection.connect();
        System.out.println("----------------------------");
        about(connection);
        finder(connection);
    }

    public static void help() {
        System.out.println("[HELP]: In this program you can use the following commands:");
        System.out.println("[HELP]: add, remove, edit, get, help, exit");
        System.out.println("[HELP]: Please make sure you're entering the commands in lowercase!");
    }

    public static void exit(Connection connection) {
        Scanner s = new Scanner(System.in);
        System.out.println("[EXIT]: Are you sure you want to exit? Type Y if yes, N if no.");
        String ans = s.nextLine();
        if(ans.equalsIgnoreCase("Y")) {
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("[SQLConnection]: Connection closed.");
                } catch (SQLException e) {
                    System.out.println("[SQLConnection]: Failed to close the connection: " + e.getMessage());
                }
            }
            System.out.println("EXITING....");
            System.exit(0);
        } else if(ans.equalsIgnoreCase("N")){
          System.out.println("[EXIT]: Process cancelled.");
          finder(connection);
        } else {
            System.out.println("[EXIT]: Invalid choice, please use Y or N.");
            exit(connection);
        }
    }

    public static void finder(Connection connection) {
        boolean error = false;

        System.out.println("[FINDER]: What do you want to do?");
        System.out.println("[FINDER]: Type help to get the command list.");

        Scanner s1 = new Scanner(System.in);
        String ans1 = s1.nextLine();

        switch(ans1) {
            case "add" -> add(connection);
            case "remove" -> remove(connection);
            case "edit" -> edit(connection);
            case "help" -> help();
            case "exit" -> exit(connection);
            case "about" -> about(connection);
            case "get" -> get(connection);
            default -> error = true;
        }

        if(error){
            System.out.println("[FINDER ERROR]: Unknown command. Try again.");
            finder(connection);
        }
    }

    public static void about(Connection connection) {
        System.out.println("INVENTORY MANAGEMENT SYSTEM Public Demo Edition\nBY: GOCSÁL MÁTYÁS & KOVÁCS GÁSPÁR");
        finder(connection);
    }

    public static void add(Connection connection) {
        Scanner scanner = new Scanner(System.in);

            System.out.println("[ADD]: Enter the manufacturer of the item: ");
            String made = scanner.nextLine();

            System.out.println("[ADD]: Enter the model of the item: ");
            String type = scanner.nextLine();

            System.out.println("[ADD]: Enter the owner of the item ");
            String owner = scanner.nextLine();

            System.out.println("[ADD]: Enter the date of buy (YYYY-MM-DD): ");
            String buydate = scanner.nextLine();

            System.out.println("[ADD]: Enter the value of the item: ");
            String buyvalue = scanner.nextLine();

            System.out.println("[ADD]: Is this item in use? (yes or no): ");
            String input = scanner.nextLine();
            boolean inuse = false;
            if(input.equals("yes")){
                inuse = true;}

            System.out.println("[ADD]: Which band uses it?: ");
            String band = scanner.nextLine();

            System.out.println("[ADD]: Add notes: ");
            String notes = scanner.nextLine();


        String sql = "INSERT INTO db (made, type, owner, buydate, buyvalue, inuse, band, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, made);
            pstmt.setString(2, type);
            pstmt.setString(3, owner);
            pstmt.setString(4, buydate);
            pstmt.setString(5, buyvalue);
            pstmt.setBoolean(6, inuse);
            pstmt.setString(7, band);
            pstmt.setString(8, notes);
            pstmt.executeUpdate();
            System.out.println("[SUCCESS]: Item added successfully.");
        } catch (SQLException e) {
            System.out.println("[ERROR]: Failed to add item: " + e.getMessage());
        }
        finder(connection);
    }

    public static void get(Connection connection) {
        Scanner criteria = new Scanner(System.in);

        System.out.println("[GET]: What criteria do you want to search by?");
        System.out.println("[GET]: 1: made, 2: type, 3: owner, 4: buydate, 5: buyvalue, 6: inuse, 7: band");
        System.out.println("[GET]: Type 9 to quit.");

        String choice = criteria.nextLine();

        String query;

        switch (choice) {
            case "1":
                query = "SELECT * FROM db WHERE made = ?";
                break;
            case "2":
                query = "SELECT * FROM db WHERE type = ?";
                break;
            case "3":
                query = "SELECT * FROM db WHERE owner = ?";
                break;
            case "4":
                query = "SELECT * FROM db WHERE buydate = ?";
                break;
            case "5":
                query = "SELECT * FROM db WHERE buyvalue = ?";
                break;
            case "6":
                query = "SELECT * FROM db WHERE inuse = ?";
                break;
            case "7":
                query = "SELECT * FROM db WHERE band = ?";
                break;
            case "9":
                finder(connection);
                return;
            default:
                System.out.println("[GET]: Invalid choice. Please select a valid option.");
                get(connection);
                return;
        }

        System.out.println("[GET]: Please enter the value for your criteria:");
        String value = criteria.nextLine();

        boolean found = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String made = resultSet.getString("made");
                String type = resultSet.getString("type");
                String owner = resultSet.getString("owner");
                String buydate = resultSet.getString("buydate");
                String buyvalue = resultSet.getString("buyvalue");
                boolean inuse = resultSet.getBoolean("inuse");
                String band = resultSet.getString("band");
                String notes = resultSet.getString("notes");

                found = true;
                System.out.println("ID: " + id + " | made: " + made + " | type: " + type + " | owner: " + owner +
                        " | buydate: " + buydate + " | buyvalue: " + buyvalue + " | inuse: " + inuse +
                        " | band: " + band + " | notes: " + notes);
            }

        } catch (SQLException e) {
            System.out.println("[GET]: Database error: " + e.getMessage());
            finder(connection);
        }

        if (!found) {
            System.out.println("[GET]: No matching records found based on the given data.");
        }

        finder(connection);
    }

    public static void remove(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("[REMOVE]: What's the manufacturer of the item you want to remove?");
        String made = scanner.nextLine();

        System.out.println("[REMOVE]: What's the model of the item you want to remove?");
        String type = scanner.nextLine();

        System.out.println("[REMOVE]: Who owns this item?");
        String owner = scanner.nextLine();

        String query = "SELECT * FROM db WHERE made = ? AND type = ? AND owner = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the parameters for the query
            preparedStatement.setString(1, made);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, owner);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean found = false;
            List<Integer> ids = new ArrayList<>();

            System.out.println("[REMOVE]: Gear matching the description provided:");
            while (resultSet.next()) {
                found = true;
                int id = resultSet.getInt("id");
                String dbMade = resultSet.getString("made");
                String dbType = resultSet.getString("type");
                String dbOwner = resultSet.getString("owner");
                String dbBuydate = resultSet.getString("buydate");
                String dbBuyvalue = resultSet.getString("buyvalue");
                boolean dbInuse = resultSet.getBoolean("inuse");
                String dbBand = resultSet.getString("band");
                String dbNotes = resultSet.getString("notes");

                ids.add(id);
                System.out.println("ID: " + id + ", Manufacturer: " + dbMade + ", Type: " + dbType + ", Owner: " + dbOwner + ", BuyDate: " + dbBuydate + ", BuyValue: " + dbBuyvalue + ", Use: " + dbInuse + ", Band: " + dbBand + ", Notes: " + dbNotes);
            }

            if (found) {
                if (ids.size() > 1) {
                    System.out.println("[REMOVE]: More than one item found. Please enter the ID of the item you want to delete:");
                    int itemIdToDelete = scanner.nextInt();
                    scanner.nextLine();

                    if (ids.contains(itemIdToDelete)) {
                        System.out.println("[REMOVE]: Are you sure you want to delete item with ID " + itemIdToDelete + "? (yes/no)");
                        String confirmation = scanner.nextLine();

                        if ("yes".equalsIgnoreCase(confirmation)) {
                            deleteItem(connection, itemIdToDelete);
                        } else {
                            System.out.println("[REMOVE]: Deletion canceled.");
                        }
                    } else {
                        System.out.println("[REMOVE]: Invalid ID.");
                    }
                } else {
                    // If only one item is found, confirm deletion
                    int idToDelete = ids.getFirst();
                    System.out.println("[REMOVE]: Are you sure you want to delete item with ID " + idToDelete + "? (yes/no)");
                    String confirmation = scanner.nextLine();

                    if ("yes".equalsIgnoreCase(confirmation)) {
                        deleteItem(connection, idToDelete);
                    } else {
                        System.out.println("[REMOVE]: Deletion canceled.");
                        finder(connection);
                    }
                }
            } else {
                System.out.println("No matching records found.");
                finder(connection);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR]: SQL error during removal: " + e.getMessage());
            finder(connection);
        }
    }

    private static void deleteItem(Connection connection, int id) {
        String deleteQuery = "DELETE FROM db WHERE id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, id);
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[REMOVE]: Item with ID " + id + " has been successfully deleted.");
                finder(connection);
            } else {
                System.out.println("[REMOVE]: No item found with ID " + id + ".");
                finder(connection);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: SQL error during deletion: " + e.getMessage());
            finder(connection);
        }
    }

    public static void edit(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("[EDIT]: What's the manufacturer of the item you want to edit?");
        String made = scanner.nextLine();

        System.out.println("[EDIT]: What's the model of the item you want to edit?");
        String type = scanner.nextLine();

        System.out.println("[EDIT]: Who owns this item?");
        String owner = scanner.nextLine();

        String query = "SELECT * FROM db WHERE made = ? AND type = ? AND owner = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, made);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, owner);
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean found = false;
            List<Integer> ids = new ArrayList<>();

            System.out.println("[EDIT]: Gear matching the description provided:");
            while (resultSet.next()) {
                found = true;
                int id = resultSet.getInt("id");
                String dbMade = resultSet.getString("made");
                String dbType = resultSet.getString("type");
                String dbOwner = resultSet.getString("owner");
                String dbBuydate = resultSet.getString("buydate");
                String dbBuyvalue = resultSet.getString("buyvalue");
                boolean dbInuse = resultSet.getBoolean("inuse");
                String dbInUseStr = null;
                if(dbInuse){
                    dbInUseStr = "Yes";
                } else {
                    dbInUseStr = "No";}
                String dbBand = resultSet.getString("band");
                String dbNotes = resultSet.getString("notes");

                ids.add(id);
                System.out.println("ID: " + id + ", Manufacturer: " + dbMade + ", Type: " + dbType + ", Owner: " + dbOwner +
                        ", BuyDate: " + dbBuydate + ", BuyValue: " + dbBuyvalue + ", Use: " + dbInUseStr +
                        ", Band: " + dbBand + ", Notes: " + dbNotes);
            }

            if (found) {
                if (ids.size() > 1) {
                    System.out.println("[EDIT]: More than one item found. Please enter the ID of the item you want to edit:");
                    int itemIdToEdit = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    if (ids.contains(itemIdToEdit)) {
                        editItem(connection, itemIdToEdit); // Proceed to editing the selected item
                    } else {
                        System.out.println("[EDIT]: Invalid ID.");
                        finder(connection); // Go back to the main menu
                    }
                } else {
                    // If only one item is found, proceed directly to editing
                    int idToEdit = ids.get(0);
                    editItem(connection, idToEdit); // Proceed to editing the single found item
                }
            } else {
                System.out.println("[EDIT]: No matching records found.");
                finder(connection);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR]: SQL error during search: " + e.getMessage());
            finder(connection);
        }
    }

    private static void editItem(Connection connection, int id) {
        Scanner scanner = new Scanner(System.in);

        String query = "SELECT * FROM db WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("[EDIT]: No item found with ID " + id);
                finder(connection);
                return;
            }

            System.out.println("[CURRENT DATA]: Manufacturer: " + resultSet.getString("made"));
            System.out.println("[EDIT]: Enter new manufacturer (or press enter to keep current):");
            String newMade = scanner.nextLine();
            if (newMade.isEmpty()) newMade = resultSet.getString("made");

            System.out.println("[CURRENT DATA]: Model: " + resultSet.getString("type"));
            System.out.println("[EDIT]: Enter new model (or press enter to keep current):");
            String newType = scanner.nextLine();
            if (newType.isEmpty()) newType = resultSet.getString("type");

            System.out.println("[CURRENT DATA]: Owner: " + resultSet.getString("owner"));
            System.out.println("[EDIT]: Enter new owner (or press enter to keep current):");
            String newOwner = scanner.nextLine();
            if (newOwner.isEmpty()) newOwner = resultSet.getString("owner");

            System.out.println("[CURRENT DATA]: Buy Date: " + resultSet.getString("buydate"));
            System.out.println("[EDIT]: Enter new buy date (YYYY-MM-DD) (or press enter to keep current):");
            String newBuyDate = scanner.nextLine();
            if (newBuyDate.isEmpty()) newBuyDate = resultSet.getString("buydate");

            System.out.println("[CURRENT DATA]: Value: " + resultSet.getString("buyvalue"));
            System.out.println("[EDIT]: Enter new value (or press enter to keep current):");
            String newBuyValue = scanner.nextLine();
            if (newBuyValue.isEmpty()) newBuyValue = resultSet.getString("buyvalue");

            String inUseString = null;
            if(resultSet.getBoolean("inuse")){
                inUseString = "Yes";
            } else {
                inUseString = "No";}
            System.out.println("[CURRENT DATA]: In Use: " + inUseString);
            System.out.println("[EDIT]: Is this item in use? (yes or no):");
            String inUseInput = scanner.nextLine();
            boolean newInUse = inUseInput.equalsIgnoreCase("yes");
            newInUse = inUseInput.isEmpty() ? resultSet.getBoolean("inuse") : Boolean.parseBoolean(inUseInput);

            System.out.println("[CURRENT DATA]: Band: " + resultSet.getString("band"));
            System.out.println("[EDIT]: Which band uses it? (or press enter to keep current):");
            String newBand = scanner.nextLine();
            if (newBand.isEmpty()) newBand = resultSet.getString("band");

            System.out.println("[CURRENT DATA]: Notes: " + resultSet.getString("notes"));
            System.out.println("[EDIT]: Add new notes (or press enter to keep current):");
            String newNotes = scanner.nextLine();
            if (newNotes.isEmpty()) newNotes = resultSet.getString("notes");

            // Update the item in the database
            String updateQuery = "UPDATE db SET made = ?, type = ?, owner = ?, buydate = ?, buyvalue = ?, inuse = ?, band = ?, notes = ? WHERE id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, newMade);
                updateStatement.setString(2, newType);
                updateStatement.setString(3, newOwner);
                updateStatement.setString(4, newBuyDate);
                updateStatement.setString(5, newBuyValue);
                updateStatement.setBoolean(6, newInUse);
                updateStatement.setString(7, newBand);
                updateStatement.setString(8, newNotes);
                updateStatement.setInt(9, id);

                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("[EDIT]: Item updated successfully.");
                } else {
                    System.out.println("[EDIT]: Failed to update item.");
                }
            } catch (SQLException e) {
                System.out.println("[ERROR]: SQL error during update: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("[ERROR]: SQL error during retrieval: " + e.getMessage());
        }

        finder(connection);
    }
}