import java.sql.*;
import java.util.Scanner;

public class WindBay {
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/windbay", "root", "");
        Scanner scan = new Scanner(System.in);

        System.out.println("\nWelcome to WindBay!");

        while (true) {
            System.out.print("Please enter your username (or 0 to exit): ");
            String username = scan.nextLine();
            if (username.equals("0")) {
                System.out.println("Goodbye!");
                return; // Exit the program
            }

            String query = "SELECT * FROM USER WHERE Username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            // Check if the user exists
            if (rs.next()) {
                System.out.println("\n------------------------------------------------");
                System.out.println("Login successful!");
                System.out.println("Welcome " + username + "!");
                int userId = rs.getInt("UserID");

                //Check if seller or buyer
                if (isSeller(conn, userId)) {
                    sellerID(conn, userId);
                } else if (isBuyer(conn, userId)) {
                    buyerID(conn, userId);
                } else {
                    System.out.println("User not found!");
                }
                // Close the ResultSet
                rs.close();
                // Close the PreparedStatement
                pstmt.close();
                break; //logout

            } else {
                System.out.println("User not found! Please try again.");
            }
            // Close the ResultSet
            rs.close();
            // Close the PreparedStatement
            pstmt.close();
        }
        // Close the Scanner
        scan.close();
        // Close the Connection
        conn.close();
    }

    /**
     * Check if the user is a seller
     * @param conn Connection to the database
     * @param userId User ID to check
     * @return true if the user is a seller, false otherwise
     * @throws SQLException if there is an error executing the query
     */
    public static boolean isSeller(Connection conn, int userId) throws SQLException {
        String query = "SELECT * FROM SELLER WHERE UserID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        boolean found = rs.next();
        rs.close();
        pstmt.close();
        return found;
    }

    /**
     * Check if the user is a buyer
     * @param conn Connection to the database
     * @param userId User ID to check
     * @return true if the user is a buyer, false otherwise
     * @throws SQLException if there is an error executing the query
     */
    public static boolean isBuyer(Connection conn, int userId) throws SQLException {
        String query = "SELECT * FROM BUYER WHERE UserID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        boolean found = rs.next();
        rs.close();
        pstmt.close();
        return found;
    }

    /**
     * Buyer functionalities
     * @param conn Connection to the database
     * @param userId User ID of the buyer
     * @throws SQLException if there is an error executing the query
     */
    public static void buyerID(Connection conn, int userId) throws SQLException {
        System.out.println("You are logged in as a Buyer with UserID: " + userId);

        // Implement buyer functionalities here
        Scanner userInput = new Scanner(System.in);

        int choice;
        do {
            printBuyerMenu();
            System.out.print("Please select an option from the menu by entering a number between 1 to 4: ");
            choice = userInput.nextInt();

            switch (choice) {
                case 1: {
                    //Add search items to buy logic here!
                    searchItem(conn, userId, userInput);
                    break;
                }
                case 2: {
                    //Shipping info logic here
                    viewShippingInfo(conn, userId, userInput);
                    break;
                }
                case 3: {
                    // Review item logic here
                    leaveReview(conn, userId, userInput);
                    break;
                }
                case 4: {
                    System.out.println("Goodbye!");
                    break;
                }
                default: {
                    System.out.println("Invalid choice. Please choose a number between 1 to 4!");
                }
            }
        } while (choice != 4);
        System.out.println("Thank you for using WindBay!");
        userInput.close();
    }

    /**
     * Seller functionalities
     * @param conn Connection to the database
     * @param userId User ID of the seller
     * @throws SQLException if there is an error executing the query
     */
    public static void sellerID(Connection conn, int userId) throws SQLException {
        System.out.println("You are logged in as a Seller with UserID: " + userId);

        // Implement buyer functionalities here
        System.out.println("Please select an option from the menu by entering a number between 1 to 8: ");

        Scanner userInput = new Scanner(System.in);

        int choice;
        do {
            printSellerMenu();
            choice = userInput.nextInt();

            switch (choice) {
                case 1: {
                    // List inventory logic here
                    listInventory(conn, userInput);
                    break;
                }
                case 2: {
                    // Create new product logic here
                    createNewProduct(conn, userId);
                    break;
                }
                case 3: {
                    // Modify inventory logic here
                    modifyInventory(conn);
                    break;
                }
                case 4: {
                    // Delete product logic here
                    deleteProduct(conn);
                    break;
                }
                case 5: {
                    getMostPopularProducts(conn, userInput);
                    break;
                }
                case 6: {
                    getLeastPopularProducts(conn, userInput);
                    break;
                }
                case 7: {
                    getInactiveUsers(conn, userInput);
                    break;
                }
                case 8: {
                    System.out.println("Goodbye!");
                    break;
                }
                default: {
                    System.out.println("Invalid choice. Please choose a number between 1 to 8!");
                }
            }
        } while (choice != 8);
        userInput.close();
    }

    /**
     * Print the buyer menu
     */
    public static void printBuyerMenu() {
        System.out.println("================================================");
        System.out.println("----- WINDBAY MARKETPLACE MENU -----");
        System.out.println("1. Search item(s) to buy");
        System.out.println("2. Purchased item(s) shipping info");
        System.out.println("3. Review Item(s)");
        System.out.println("4. Exit");
        System.out.println("================================================");
    }

    /**
     * Search for items to buy
     * @param conn Connection to the database
     * @param userId User ID of the buyer
     * @param userInput Scanner for user input
     * @throws SQLException if there is an error executing the query
     */
    public static void searchItem(Connection conn, int userId, Scanner userInput) throws SQLException {
        System.out.println("Enter the name of the item you want to search for: ");
        String searchItem = userInput.next();
        System.out.println("Searching for items to buy...");

        // Prepare the SQL query to search for items
        String searchQuery = "SELECT PRODUCT.*, INVENTORY.Quantity FROM PRODUCT JOIN INVENTORY ON PRODUCT.ProductID = INVENTORY.ProductID WHERE PRODUCT.Name LIKE ?";
        PreparedStatement pstmt = conn.prepareStatement(searchQuery);
        pstmt.setString(1, "%" + searchItem + "%");
        ResultSet rs = pstmt.executeQuery();

        // Check if any items were found
        if (rs.next()) {
            // Loop through the results and display item details
            do {
                // Check if the item is available for purchase
                if (rs.getInt("Quantity") <= 0) {
                    System.out.println("Item with Product ID " + rs.getInt("ProductID") + " is currently out of stock.");
                    System.out.println("Returning to the main menu...");
                    return;
                }else{
                    System.out.println("Item with Product ID " + rs.getInt("ProductID") + " is available for purchase.");
                    // Display item details
                    System.out.println("===============================================");
                    System.out.println("Product ID: " + rs.getInt("ProductID"));
                    System.out.println("Category: " + rs.getString("Category"));
                    System.out.println("Product Name: " + rs.getString("Name"));
                    System.out.println("Price: " + rs.getDouble("Price"));
                    System.out.println("Quantity Available: " + rs.getInt("Quantity"));
                    System.out.println("Description: " + rs.getString("Description"));
                    System.out.println("Seller ID: " + rs.getInt("SellerID"));
                    System.out.println("===============================================");
                    System.out.println("Do you want to buy the item? yes/no");
                    String buyChoice = userInput.next();

                    if (buyChoice.equals("yes")) {
                        System.out.println("===============================================");
                        System.out.println("Please enter the quantity you want to buy: ");
                        int quantity = userInput.nextInt();
                        // Check if the quantity is valid
                        if (quantity <= 0 || quantity > rs.getInt("Quantity")) {
                            System.out.println("Invalid quantity! Please enter a valid quantity between 1 and " + rs.getInt("Quantity"));
                            return; // Return to the main menu
                        }
                        // Check if the item exists
                        int itemId = rs.getInt("ProductID");
                        String checkItemQuery = "SELECT * FROM PRODUCT WHERE ProductID = ?";
                        PreparedStatement checkItemPstmt = conn.prepareStatement(checkItemQuery);
                        checkItemPstmt.setInt(1, itemId);

                        ResultSet checkItemRs = checkItemPstmt.executeQuery();

                        // If the item does not exist, inform the user and return to the main menu
                        if (!checkItemRs.next()) {
                            System.out.println("Item with Product ID " + itemId + " not found!");
                            checkItemPstmt.close();
                            System.out.println("Returning to the main menu...");
                            return;
                        }
                        checkItemRs.close();
                        checkItemPstmt.close();

                        // Confirm the purchase
                        System.out.println("================================================");
                        System.out.println("You are about to purchase");
                        System.out.println("Product ID: " + itemId);
                        System.out.println("Are you sure you want to buy this item? (yes/no)");
                        String confirmPurchase = userInput.next();
                        if (!confirmPurchase.equals("yes")) {
                            System.out.println("Purchase cancelled.");
                            return;
                        }

                        // Proceed with the purchase
                        System.out.println("Processing your purchase...");
                        CallableStatement cstmt = conn.prepareCall("{CALL PurchaseItem(?, ?, ?)}");
                        cstmt.setInt(1, userId); // BuyerID
                        cstmt.setInt(2, itemId); // ProductID
                        cstmt.setInt(3, quantity); // Quantity
                        cstmt.execute();

                        System.out.println("\nThank you for your purchase!");
                        System.out.println("================================================");
                        System.out.println("TRANSACTION DETAILS:");
                        System.out.println("Product ID: " + itemId + ", Quantity: " + quantity);
                        System.out.println("Item Name: " + rs.getString("Name") + ", Price: " + rs.getDouble("Price"));
                        System.out.println("================================================");
                        cstmt.close(); // Close the CallableStatement
                    } else {
                        System.out.println("You chose not to buy the item.");
                    }

                }
            } while (rs.next());
             // Continue looping through the results

        } else {
            System.out.println("=================================================");
            System.out.println("No items found for: " + searchItem);
            System.out.println("Returning to the main menu...");
            return; // Return to the main menu if no items found
        }

        // Close the ResultSet and PreparedStatement
        rs.close();
        pstmt.close();


        // Wait for user to press Enter to return to the main menu
        System.out.println("Press Enter to return to the main menu.");
        userInput.nextLine(); // Wait for user to press Enter
        userInput.nextLine(); // Consume the newline character
        System.out.println("Returning to the main menu...");


    }

    /**
     * View shipping information for purchased items
     * @param conn Connection to the database
     * @param userId User ID of the buyer
     * @throws SQLException if there is an error executing the query
     */
    public static void viewShippingInfo(Connection conn, int userId, Scanner userInput) throws SQLException {
        System.out.println("Fetching purchased item(s) shipping info...");
        String shippingQuery = "SELECT * FROM SHIPPINGINFO WHERE BuyerID = ?";
        PreparedStatement shippingPstmt = conn.prepareStatement(shippingQuery);
        shippingPstmt.setInt(1, userId);
        ResultSet shippingRs = shippingPstmt.executeQuery();
        if (shippingRs.next()) {
            System.out.println("Purchased items shipping info:");
            do {
                System.out.println("================================================");
                // Display shipping information
                System.out.println("-----Shipping Information for UserID " + userId + "-----");
                System.out.println("Shipping ID: " + shippingRs.getInt("ShippingID"));
                System.out.println("Transaction ID: " + shippingRs.getInt("TransactionID"));
                System.out.println("Shipping Status: " + shippingRs.getString("ShippingStatus"));
                System.out.println("Shipping Address: " + shippingRs.getString("Address"));
                System.out.println("Shipping Date: " + shippingRs.getDate("ShippingDate"));
                System.out.println("Estimated Delivery Date: " + shippingRs.getDate("DeliveryDate"));
                System.out.println("Tracking Number: " + shippingRs.getString("TrackingNumber"));
                System.out.println("================================================");
                // You can also fetch item details if needed
            } while (shippingRs.next());
        } else {
            System.out.println("No purchased items found for UserID: " + userId);
        }
        System.out.println("Press Enter to return to the main menu.");
        userInput.nextLine(); // Wait for user to press Enter
        userInput.nextLine(); // Consume the newline character
        System.out.println("Returning to the main menu...");
        // Close the ResultSet and PreparedStatement
        shippingPstmt.close();
        shippingRs.close();
    }

    /**
     * Leave a review for an item
     * @param conn Connection to the database
     * @param userId User ID of the buyer
     * @throws SQLException if there is an error executing the query
     */
    public static void leaveReview(Connection conn, int userId, Scanner userInput ) throws SQLException {
        // Logic to leave a review for an item
        System.out.println("Do you want to leave a review for an item you purchased? (yes/no)");
        String reviewChoice = userInput.next();

        // Check if the user wants to leave a review
        if (reviewChoice.equalsIgnoreCase("yes")) {
            System.out.println("Here are the items you have purchased:");

            // Query to get the items purchased by the user
            String purchasedItemsQuery = "SELECT PRODUCT.ProductID, PRODUCT.Name, TRANSACTION.TransactionID "
            + "FROM PRODUCT JOIN TRANSACTION ON PRODUCT.ProductID = TRANSACTION.ProductID "
                + "WHERE TRANSACTION.BuyerID = ?";

            PreparedStatement purchasedItemsPstmt = conn.prepareStatement(purchasedItemsQuery);
            purchasedItemsPstmt.setInt(1, userId);
            ResultSet purchasedItemsRs = purchasedItemsPstmt.executeQuery();

            boolean hasPurchasedItems = false;
            boolean hasLeftReview = false;

            // Loop through the purchased items
            while (purchasedItemsRs.next() && !hasLeftReview) {
                hasPurchasedItems = true; // At least one item is purchased
                int transactionID = purchasedItemsRs.getInt("TransactionID");
                int productID = purchasedItemsRs.getInt("ProductID");
                String productName = purchasedItemsRs.getString("Name");

                System.out.println("ProductID: " + purchasedItemsRs.getInt("ProductID") + ", Name: " + purchasedItemsRs.getString("Name"));
                System.out.println("Do you want to leave a review for this item? (yes/no)");
                String leaveReviewChoice = userInput.nextLine();

                // Check if the user wants to leave a review for this item
                if (leaveReviewChoice.equalsIgnoreCase("yes")) {
                    System.out.println("You have chosen to leave a review for ProductID: " + purchasedItemsRs.getInt("ProductID"));

                    userInput.nextLine(); // Consume the newline character
                    System.out.println("Please enter your review for " + productName + ": ");
                    String reviewText = userInput.nextLine();

                    System.out.println("Please enter your rating (1-5): ");
                    int rating = userInput.nextInt();

                    System.out.println("Please enter the review date (YYYY-MM-DD): ");
                    String reviewDateInput = userInput.next();
                    Date reviewDate = Date.valueOf(reviewDateInput); // Convert string to Date



                    // Validate rating
                    while (rating < 1 || rating > 5) {
                        System.out.println("Invalid rating! Please enter a rating between 1 and 5: ");
                        rating = userInput.nextInt();
                    }

                    // Confirm the review
                    System.out.println("You are about to leave a review for ProductID: " + productID);
                    System.out.println("Product Name: " + productName);
                    System.out.println("Review Text: " + reviewText);
                    System.out.println("Rating: " + rating);
                    System.out.println("Are you sure you want to leave this review? (yes/no)");
                    String confirmReview = userInput.next();

                    if (confirmReview.equalsIgnoreCase("yes")) {
                        System.out.println("Thank you for your review! Your feedback is valuable to us.");
                        // Prepare the SQL query to insert the review
                        String insertReviewQuery = "INSERT INTO REVIEW (UserID, ProductID, TransactionID, Rating, Comment, ReviewDate) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement reviewPstmt = conn.prepareStatement(insertReviewQuery);
                        reviewPstmt.setInt(1, userId);
                        reviewPstmt.setInt(2, productID);
                        reviewPstmt.setInt(3, transactionID);
                        reviewPstmt.setInt(4, rating);
                        reviewPstmt.setString(5, reviewText);
                        reviewPstmt.setDate(6, reviewDate);

                        reviewPstmt.executeUpdate();
                        reviewPstmt.close();
                        System.out.println("Review successfully added for ProductID: " + productID);
                        hasLeftReview = true; // Exit the loop after leaving a review
                        } else {
                        System.out.println("You chose not to leave a review for this item.");
                    }
                } else {
                    System.out.println("You chose not to leave a review for ProductID: " + purchasedItemsRs.getInt("ProductID"));
                }
                System.out.println("------------------------------------------------");
            }
        } else {
            System.out.println("No review left.");
            System.out.println("Returning to the main menu...");
        }
    }

    /**
     * Print the seller menu
     */
    public static void printSellerMenu() {
        System.out.println("================================================");
        System.out.println("----- WINDBAY MARKETPLACE SELLER MENU -----");
        System.out.println("1. List Inventory");
        System.out.println("2. Create New Product");
        System.out.println("3. Modify Inventory");
        System.out.println("4. Delete Product");
        System.out.println("5. Get Most Popular Products");
        System.out.println("6. Get Least Popular Products");
        System.out.println("7. Get Inactive Users");
        System.out.println("8. Exit");
        System.out.println("================================================");

    }

    /**
     * lists all products in the inventory
     * @param conn Connection to the database
     * @throws SQLException if there is an error executing the query
     */
    public static void listInventory(Connection conn, Scanner userInput) throws SQLException {
        String query = "SELECT p.ProductID, p.name, p.price, p.description, p.category, p.sellerID," +
                        "i.quantity FROM PRODUCT p JOIN INVENTORY i ON p.ProductID = i.ProductID";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\"----- Current Inventory -----\"");
        while (rs.next()) {
            int id = rs.getInt("ProductID");
            String name = rs.getString("Name");
            double price = rs.getDouble("Price");
            String description = rs.getString("Description");
            String category = rs.getString("Category");
            int sellerID = rs.getInt("sellerID");
            int quantity = rs.getInt("Quantity");

            System.out.printf("ID: %d | Name: %s | Price: $%.2f | Qty: %d | Description: %s | Category: %s | SellerID: %d\n",
                    id, name, price, quantity, description, category, sellerID);

        }
        System.out.println("================================");
        System.out.println("Press Enter to return to the main menu.");
        userInput.nextLine(); // Wait for user to press Enter
        userInput.nextLine(); // Consume the newline character
        // Close the ResultSet and Statement
        rs.close();
        stmt.close();
    }

    /**
     * creates a new product
     * @param conn Connection to the database
     * @throws SQLException if there is an error executing the query
     */
    public static void createNewProduct(Connection conn, int userID) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter product name: ");
        String name = scanner.nextLine();

        System.out.println("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Enter description: ");
        String description = scanner.nextLine();

        System.out.println("Enter category: ");
        String category = scanner.nextLine();

        System.out.println("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();


        // Call insertProduct stored procedure
        System.out.println("Inserting new product into the database...");
        String insertProd = "{CALL insertProduct(?, ?, ?, ?, ?)}";
        CallableStatement productStmt = conn.prepareCall(insertProd);

        // Set parameters for the stored procedure
        productStmt.setString(1, name);
        productStmt.setDouble(2, price);
        productStmt.setString(3, description);
        productStmt.setString(4, category);
        productStmt.setInt(5, userID); // Use the provided userID as SellerID


        // Get generated ProductID
        productStmt.executeUpdate();
        String getKeysQuery = "SELECT LAST_INSERT_ID()"; // Get the last inserted ProductID
        PreparedStatement keysStmt = conn.prepareStatement(getKeysQuery);
        ResultSet keys = keysStmt.executeQuery();


        // Check if the keys ResultSet has a row
        int productId = -1;
        if (keys.next()) {
            productId = keys.getInt(1);
        }
        keys.close();
        productStmt.close();
        keysStmt.close();

        if (productId == -1) {
            System.out.println("Product could not be created.");
            return; // Exit the method if product creation failed
        }

        // Insert into INVENTORY
        String insertInventory = "INSERT INTO INVENTORY (ProductID, Quantity) VALUES (?, ?)";
        PreparedStatement inventoryStmt = conn.prepareStatement(insertInventory);
        inventoryStmt.setInt(1, productId);
        inventoryStmt.setInt(2, quantity);
        inventoryStmt.executeUpdate();
        inventoryStmt.close();

        System.out.println("================================================");
        System.out.println("Product succesfully added!");
        System.out.println("\nProduct ID: " + productId);
        System.out.println("Product Name: " + name);
        System.out.println("Price: $" + price);
        System.out.println("Description: " + description);
        System.out.println("Category: " + category);
        System.out.println("Quantity: " + quantity);
        System.out.println("Seller ID: " + userID);
        System.out.println("================================================");

        System.out.println("Press Enter to return to the main menu.");
        scanner.nextLine(); // Wait for user to press Enter

    }

    /**
     * modify products in the inventory
     * @param conn Connection to the database
     * @throws SQLException if there is an error executing the query
     */
    public static void modifyInventory(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter ProductID to modify: ");
        int productID = scanner.nextInt();

        System.out.println("Enter new quantity: ");
        int quantity = scanner.nextInt();

        String updateInventory = "UPDATE INVENTORY SET Quantity = ? WHERE ProductID = ?";
        PreparedStatement pstmt = conn.prepareStatement(updateInventory);
        pstmt.setInt(1, quantity);
        pstmt.setInt(2, productID);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Success! Inventory updated");
        } else {
            System.out.println("ProductID not found. No update performed");
        }
        scanner.nextLine(); // Consume the newline character
        System.out.println("Press Enter to return to the main menu.");
        scanner.nextLine(); // Wait for user to press Enter
        // Close the PreparedStatement
        pstmt.close();

    }


    /**
     * delete products in the inventory
     * @param conn Connection to the database
     * @throws SQLException if there is an error executing the query
     */
    public static void deleteProduct(Connection conn) throws SQLException {
        Scanner scanner = new Scanner (System.in);

        System.out.println("Enter ProductID to delete: ");
        int productID = scanner.nextInt();

        String deleteProduct = "DELETE FROM PRODUCT WHERE ProductID = ?";
        PreparedStatement pstmt = conn.prepareStatement(deleteProduct);
        pstmt.setInt(1, productID);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Success! Product deleted");
        } else {
            System.out.println("ProductID not found. No deletion performed");
        }
        System.out.println("================================================");
        System.out.println("Product with ID " + productID + " has been deleted from the inventory.");
        System.out.println("================================================");
        // Wait for user to press Enter to return to the main menu
        scanner.nextLine(); // Consume the newline character
        System.out.println("Press Enter to return to the main menu.");
        scanner.nextLine(); // Wait for user to press Enter
        // Close the PreparedStatement
        pstmt.close();

    }


    /**
     * Update the quantity of a product in the inventory
     * @param conn Connection to the database
     * @throws SQLException if there is an error executing the query
     */
    public static void updateQuantity(Connection conn) throws SQLException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter productID to update quantity: ");
        int productID = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter new quantity: ");
        int new_quantity = Integer.parseInt(scanner.nextLine());

        CallableStatement stmt = conn.prepareCall("{CALL UpdateInventoryQty(?, ?)}");
        stmt.setInt(1, productID);
        stmt.setInt(10, new_quantity);

        int rowsAffected = stmt.executeUpdate();
        System.out.println("Inventory updated for Product ID: " + productID);

        stmt.close();

    }


    /**
     * Get most popular products based on purchase count within a date range
     * @param conn Connection to the database
     * @param userInput Scanner for user input
     * @throws SQLException if there is an error executing the query
     */
    public static void getMostPopularProducts(Connection conn, Scanner userInput) throws SQLException {
        // Get most popular products logic here
        System.out.println("Fetching most popular products...");
        System.out.println("Enter the start date (YYYY-MM-DD): ");
        String startDate = userInput.next();
        System.out.println("Enter the end date (YYYY-MM-DD): ");
        String endDate = userInput.next();
        String mostPopularQuery = "SELECT PRODUCT.*, COUNT(TRANSACTION.ProductID) AS PurchaseCount " +
            "FROM PRODUCT JOIN TRANSACTION ON PRODUCT.ProductID = TRANSACTION.ProductID " +
            "WHERE TRANSACTION.TransactionDate BETWEEN ? AND ? " +
            "GROUP BY PRODUCT.ProductID ORDER BY PurchaseCount DESC LIMIT 5";
        PreparedStatement mostPopularPstmt = conn.prepareStatement(mostPopularQuery);
        mostPopularPstmt.setString(1, startDate);
        mostPopularPstmt.setString(2, endDate);
        ResultSet mostPopularRs = mostPopularPstmt.executeQuery();
        if (mostPopularRs.next()) {
            System.out.println("Most popular products between " + startDate + " and " + endDate + ":");
            do {
                System.out.println("================================================");
                System.out.println("Product ID: " + mostPopularRs.getInt("ProductID"));
                System.out.println("Category: " + mostPopularRs.getString("Category"));
                System.out.println("Product Name: " + mostPopularRs.getString("Name"));
                System.out.println("Price: " + mostPopularRs.getDouble("Price"));
                System.out.println("Purchase Count: " + mostPopularRs.getInt("PurchaseCount"));
                System.out.println("================================================");
            } while (mostPopularRs.next());
        } else {
            System.out.println("No popular products found for the given date range.");
        }
        System.out.println("Press Enter to return to the main menu.");
        userInput.nextLine(); // Wait for user to press Enter
        userInput.nextLine(); // Consume the newline character
        // Close the ResultSet and PreparedStatement
        mostPopularPstmt.close();
        mostPopularRs.close();
    }

    /**
     * Get least popular products based on purchase count within a date range
     * @param connection Connection to the database
     * @param userInput Scanner for user input
     * @throws SQLException if there is an error executing the query
     */
    public static void getLeastPopularProducts(Connection connection, Scanner userInput) throws SQLException {
        // Get least popular products logic here
        System.out.println("Fetching least popular products...");
        System.out.println("Enter the start date (YYYY-MM-DD): ");
        String startDate = userInput.next();
        System.out.println("Enter the end date (YYYY-MM-DD): ");
        String endDate = userInput.next();
        String leastPopularQuery = "SELECT PRODUCT.*, COUNT(TRANSACTION.ProductID) AS PurchaseCount " +
            "FROM PRODUCT LEFT JOIN TRANSACTION ON PRODUCT.ProductID = TRANSACTION.ProductID " +
            "WHERE TRANSACTION.TransactionDate BETWEEN ? AND ? " +
            "GROUP BY PRODUCT.ProductID ORDER BY PurchaseCount ASC LIMIT 5";
        PreparedStatement leastPopularPstmt = connection.prepareStatement(leastPopularQuery);
        leastPopularPstmt.setString(1, startDate);
        leastPopularPstmt.setString(2, endDate);
        ResultSet leastPopularRs = leastPopularPstmt.executeQuery();
        if (leastPopularRs.next()) {
            System.out.println("Least popular products between " + startDate + " and " + endDate + ":");
            do {
                System.out.println("================================================");
                System.out.println("Product ID: " + leastPopularRs.getInt("ProductID"));
                System.out.println("Category: " + leastPopularRs.getString("Category"));
                System.out.println("Product Name: " + leastPopularRs.getString("Name"));
                System.out.println("Price: " + leastPopularRs.getDouble("Price"));
                System.out.println("Purchase Count: " + leastPopularRs.getInt("PurchaseCount"));
                System.out.println("================================================");
            } while (leastPopularRs.next());
        } else {
            System.out.println("No least popular products found for the given date range.");
        }
        System.out.println("Press Enter to return to the main menu.");
        userInput.nextLine(); // Wait for user to press Enter
        userInput.nextLine(); // Consume the newline character
        // Close the ResultSet and PreparedStatement
        leastPopularPstmt.close();
        leastPopularRs.close();

    }

    /**
     * Get inactive users based on months of inactivity
     * @param conn Connection to the database
     * @param userInput Scanner for user input
     * @throws SQLException if there is an error executing the query
     */
    public static void getInactiveUsers(Connection conn, Scanner userInput) throws SQLException {

        System.out.println("Fetching inactive users...");
        System.out.println("Enter the number of months of inactivity: ");
        int months = userInput.nextInt();

        // Query to find inactive users
        String inactiveUsersQuery = "SELECT BUYER.UserID, USER.Username, COUNT(TRANSACTION.TransactionID) AS PurchaseCount, "
            + "MAX(TRANSACTION.TransactionDate) AS LastPurchaseDate "
            + "FROM BUYER JOIN USER ON BUYER.UserID = USER.UserID "
            + "LEFT JOIN TRANSACTION ON BUYER.UserID = TRANSACTION.BuyerID "
            + "GROUP BY BUYER.UserID, USER.Username "
            + "HAVING COUNT(TRANSACTION.TransactionID) = 0 "
            + "OR (MAX(TRANSACTION.TransactionDate) < CURDATE() - INTERVAL ? MONTH)";

        PreparedStatement inactiveUsersPstmt = conn.prepareStatement(inactiveUsersQuery);
        inactiveUsersPstmt.setInt(1, months);
        ResultSet inactiveUsersRs = inactiveUsersPstmt.executeQuery();
        // Check if there are any user who have not made a single purchase


        // If there are inactive users, display their details
        if (inactiveUsersRs.next()) {
            System.out.println("Inactive users for the last " + months + " months:");
            do {
                System.out.println("================================================");
                System.out.println("User ID: " + inactiveUsersRs.getInt("UserID"));
                System.out.println("Username: " + inactiveUsersRs.getString("Username"));
                System.out.println("Purchase Count: " + inactiveUsersRs.getInt("PurchaseCount"));

                if( inactiveUsersRs.getInt("PurchaseCount") == 0) {
                    System.out.println("This user has never made a purchase!");
                    System.out.println("\nSending promotional email with recommendations...");

                    // Logic to send promotional email with recommendations
                    String recommendationsQuery = "SELECT PRODUCT.*, COUNT(TRANSACTION.ProductID) AS PurchaseCount " +
                        "FROM PRODUCT LEFT JOIN TRANSACTION ON PRODUCT.ProductID = TRANSACTION.ProductID " +
                        "GROUP BY PRODUCT.ProductID ORDER BY PurchaseCount DESC LIMIT 5";
                    PreparedStatement recommendationsPstmt = conn.prepareStatement(recommendationsQuery);
                    ResultSet recommendationsRs = recommendationsPstmt.executeQuery();

                    if (recommendationsRs.next()) {
                        System.out.println("Top 5 most popular products:");
                        do {
                            System.out.println("------------------------------------------------");
                            System.out.println("Product ID: " + recommendationsRs.getInt("ProductID"));
                            System.out.println("Category: " + recommendationsRs.getString("Category"));
                            System.out.println("Product Name: " + recommendationsRs.getString("Name"));
                            System.out.println("Price: " + recommendationsRs.getDouble("Price"));
                            System.out.println("Purchase Count: " + recommendationsRs.getInt("PurchaseCount"));
                            System.out.println("------------------------------------------------");
                        } while (recommendationsRs.next());
                    } else {
                        System.out.println("No recommendations available.");
                    }
                } else {
                    System.out.println("This user has not made a purchase in the last " + months + " months!");
                    System.out.println("Sending promotional email with recommendations...");
                    // Logic to send promotional email with recommendations

                    // Query to get items that this user has purchased in the past
                    String recommendationsQuery = "SELECT PRODUCT.*, COUNT(TRANSACTION.ProductID) AS PurchaseCount " +
                        "FROM PRODUCT LEFT JOIN TRANSACTION ON PRODUCT.ProductID = TRANSACTION.ProductID " +
                        "WHERE TRANSACTION.BuyerID = ? " +
                        "GROUP BY PRODUCT.ProductID ORDER BY PurchaseCount DESC";
                    PreparedStatement recommendationsPstmt = conn.prepareStatement(recommendationsQuery);

                    // Set the user ID for the recommendations query
                    recommendationsPstmt.setInt(1, inactiveUsersRs.getInt("UserID"));
                    ResultSet recommendationsRs = recommendationsPstmt.executeQuery();

                    if (recommendationsRs.next()) {
                        System.out.println("Items that this user has purchased in the past:");
                        do {
                            System.out.println("------------------------------------------------");
                            System.out.println("Product ID: " + recommendationsRs.getInt("ProductID"));
                            System.out.println("Category: " + recommendationsRs.getString("Category"));
                            System.out.println("Product Name: " + recommendationsRs.getString("Name"));
                            System.out.println("Price: " + recommendationsRs.getDouble("Price"));
                            System.out.println("Purchase Count: " + recommendationsRs.getInt("PurchaseCount"));
                            System.out.println("-------------------------------------------------");
                        } while (recommendationsRs.next());
                    } else {
                        System.out.println("No recommendations available.");
                    }
                }
            } while (inactiveUsersRs.next());
        } else {
            System.out.println("No inactive users found for the last " + months + " months.");
        }
        userInput.nextLine(); // Wait for user to press Enter
        System.out.println("Press Enter to return to the main menu.");
        userInput.nextLine(); // Consume the newline character
        System.out.println("Returning to the main menu...");
        // Close the ResultSet and PreparedStatement
        inactiveUsersPstmt.close();
        inactiveUsersRs.close();
    }
}