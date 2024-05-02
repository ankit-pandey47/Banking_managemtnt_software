import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.*;
import java.sql.ResultSet;

class BankUser {
    Connection con;
    Statement stmt;
    PreparedStatement pstmt;
    Scanner sc = new Scanner(System.in);
    Random rand = new Random();

    public BankUser() {
        try {
            // load the driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // establish connection
            String url = "jdbc:mysql://localhost:3306/bank";
            String username = "root";
            String password = "Password@1ankitraj";

            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createTable() {
        try {
            stmt.executeUpdate(
                    "create table if not exists bankbb(Name varchar(20) , Mobile_number varchar(12) , Aadhar_number varchar(20) ,Account_number varchar(14) ,  PIN varchar(10) , Balance int  )");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createAccount() {
        createTable();
        clearScreen();
        System.out.println("\t\t\tAccount create Portal(D&G BANK OF INDIA)");
        System.out.println("\t\t__________________________________________________");
        System.out.print("\t\tEnter your full name : ");

        String name = sc.nextLine();
        // sc.nextInt();

        // Mobile number
        String mobnum = "";
        while (mobnum.length() != 10) {
            System.out.print("\t\tEnter Your Mobile_number : ");
            mobnum = sc.nextLine();
            if (mobnum.length() != 10) {
                System.out.println("Enter valid Mobile number");
            }
        }

        // Aadhar number
        String aadharnum = "";
        while (aadharnum.length() != 12) {
            System.out.print("\t\tEnter Your Aadhar_number(without spacing) : ");
            aadharnum = sc.nextLine();
            if (aadharnum.length() != 12) {
                System.out.println("Enter valid Aadhar number");
            }
        }

        // Create PIN for your account
        String pin = "";
        while (pin.length() != 8) {
            System.out.print("\t\tEnter Your Pin(without spacing) : ");
            pin = sc.nextLine();
            if (pin.length() != 8) {
                System.out.println("Enter valid Pin");
            }
        }

        // Random bank number generate
        String digit = "012346789";
        String accountnum = "";
        for (int i = 0; i < 14; i++) {
            char ch = digit.charAt(rand.nextInt(digit.length()));
            accountnum += ch;
        }

        clearScreen();
        System.out.println("\tCongrats " + name
                + " your account has been created succcesfully and Account number is : " + accountnum);

        // inserting data

        String q = "insert into bankbb (Name , Mobile_number , Aadhar_number ,\r\n" + //
                "        Account_number , PIN , Balance) values (?,?,?,?,?,?)";

        try {
            int a = 0;
            pstmt = con.prepareStatement(q);
            pstmt.setString(1, name);
            pstmt.setString(2, mobnum);
            pstmt.setString(3, aadharnum);
            pstmt.setString(4, accountnum);
            pstmt.setString(5, pin);
            pstmt.setInt(6, a);

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // to login the account
    void loginaccount() {
        System.out.println("\t\tLogin to Your Existing Account(D&G BANK OF INDIA)");
        System.out.println("\t\t___________________________________________________________");

        String mobnum = "";
        while (mobnum.length() != 10) {
            System.out.print("\t\tEnter Your Mobile_number : ");
            mobnum = sc.next();
            if (mobnum.length() != 10) {
                System.out.println("Enter valid Mobile number");
            }
        }

        String pin = "";
        while (pin.length() != 8) {
            System.out.print("\t\tEnter Your Pin(without spacing) : ");
            pin = sc.next();
            if (pin.length() != 8) {
                System.out.println("Enter valid Pin");
            }
        }

        String q = "select Name from bankbb where Mobile_number = ? AND PIN =?";
        String accname = "";
        try {
            pstmt = con.prepareStatement(q);
            pstmt.setString(1, mobnum);
            pstmt.setString(2, pin);

            ResultSet set = pstmt.executeQuery();
            while (set.next()) {
                accname = set.getString(1);
                clearScreen();
                System.out.println("\tWelcome " + accname + " , You have been Successfully Loged In");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // going inside login
        if (accname != "") {
            System.out.println("\t\t\tD&G BANK OF INDIA");
            System.out.println("\t\t_______________________________________________");
            insidelogin(mobnum, accname);

        } else {
            clearScreen();
            System.out.println("\tNo such account exist as per your given account details !!");

        }

    }

    void loginmenu() {
        System.out.println("\n\t\t 1. Deposit Money  ");
        System.out.println("\t\t 2. Withdraw Money ");
        System.out.println("\t\t 3. Transfer Money ");
        System.out.println("\t\t 4. CheckBalance");
        System.out.println("\t\t 5. Go to previous menu");
        System.out.print("\tEnter the task code to perform : ");
    }

    // mobile and aadhar verify
    void transfermoney(String mobnum, String accname) {
        System.out.print("\t\tEnter the amount to transfer: ");
        int amount = sc.nextInt();
        String p = "select Balance from bankbb where Mobile_number=? ";
        try {
            pstmt = con.prepareStatement(p);
            pstmt.setString(1, mobnum);

            ResultSet set = pstmt.executeQuery();
            int balance = 0;

            while (set.next()) {
                balance = set.getInt(1);

            }
            if (amount > balance) {
                System.out.println(
                        "\t\tSorry ! " + accname + " ,Your ccount doesn't have this amount of money to trasfer");

            } else {

                // get receiver mobilenum
                String receivermob = "";
                while (receivermob.length() != 10) {
                    System.out.print("\t\tEnter Receiver's Mobile_number : ");
                    receivermob = sc.next();
                    if (receivermob.length() != 10) {
                        System.out.println("Enter valid Mobile number");
                    }
                }

                // update the reciever
                pstmt = con.prepareStatement("UPDATE bankbb SET Balance = Balance + ? WHERE Mobile_number = ?");
                pstmt.setInt(1, amount);
                pstmt.setString(2, receivermob);
                pstmt.executeUpdate();

                // update the sender
                pstmt = con.prepareStatement("UPDATE bankbb SET Balance = Balance - ? WHERE Mobile_number = ?");
                pstmt.setInt(1, amount);
                pstmt.setString(2, mobnum);
                pstmt.executeUpdate();

            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // inside login function
    void insidelogin(String mobnum, String accname) {
        loginmenu();
        int tc = sc.nextInt();

        do {
            switch (tc) {
                case 1:// to deposit

                    System.out.print("\n\t\tEnter the amount to Deposit : ");
                    int deposit = sc.nextInt();
                    if (deposit < 0) {
                        System.out.println("negative amount cant be deposited");
                        tc = 1;
                        break;
                    }

                    String q = "update bankbb set Balance = Balance +  ? where Mobile_number=? AND Name=?";

                    try {
                        pstmt = con.prepareStatement(q);
                        pstmt.setInt(1, deposit);
                        pstmt.setString(2, mobnum);
                        pstmt.setString(3, accname);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Deposit successful.");
                        } else {
                            System.out.println("Failed to deposit amount.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    clearScreen();
                    System.out.println("Deposited ** Bank Balance of " + accname + " has been updated successfully");
                    loginmenu();
                    tc = sc.nextInt();
                    break;

                case 2: // to withdraw
                    String p = "select Balance from bankbb where Mobile_number=? AND Name=? ";
                    try {
                        pstmt = con.prepareStatement(p);
                        pstmt.setString(1, mobnum);
                        pstmt.setString(2, accname);
                        ResultSet set = pstmt.executeQuery();
                        int draw = 0;
                        int balance = 0;

                        while (set.next()) {
                            balance = set.getInt(1);
                            System.out.print("\n\t\tEnter the amount to withdraw : ");
                            draw = sc.nextInt();

                        }
                        if (draw > balance) {
                            clearScreen();
                            System.out
                                    .println("\t\tNot much Balance to withdraw amount of " + draw
                                            + " in your account\n");
                            loginmenu();
                            tc = sc.nextInt();
                            break;
                        } else {
                            String z = "update bankbb set Balance =  ? where Mobile_number=? AND Name=?";
                            pstmt = con.prepareStatement(z);

                            int newamo = balance - draw;
                            pstmt.setInt(1, newamo);
                            pstmt.setString(2, mobnum);
                            pstmt.setString(3, accname);
                            pstmt.executeUpdate();
                            clearScreen();
                            System.out.println(
                                    "\twithdrawed amount " + draw + " ,Updates has been done in account of " + accname);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    loginmenu();
                    tc = sc.nextInt();
                    break;

                case 3:
                    System.out.println("\n\t\tProvide REciever's Details : ");
                    transfermoney(mobnum, accname);
                    clearScreen();
                    System.out.println("Amount has been Succesfully updated in both sender and receiver account");
                    loginmenu();
                    tc = sc.nextInt();
                    break;

                case 4:// to check the balance
                    try {
                        pstmt = con.prepareStatement("select Balance from bankbb where Mobile_number=? AND Name=?");
                        pstmt.setString(1, mobnum);
                        pstmt.setString(2, accname);
                        ResultSet set = pstmt.executeQuery();
                        int balance = 0;
                        while (set.next()) {
                            balance = set.getInt(1);
                        }
                        clearScreen();
                        System.out
                                .println("\t\t Hey " + accname + " , Current Balance in your account is : " + balance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loginmenu();
                    tc = sc.nextInt();
                    break;

                case 5:
                    clearScreen();

                    System.out.println("\n\t\t\tTHankyou");
                    break;
            }
        } while (tc != 5);

    }

    // get the account detail
    // void getdetailmenu() {
    // System.out.println("\n\t\t\tD&G BANK OF INDIA");
    // System.out.println("\t\t1.Get Account Number");

    // }

    // void getaccountdetail() {
    // getdetailmenu();
    // }

    // To clear the screen
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

public class Banking_system {
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static int menu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n\n\t\t\tD&G BANK OF INDIA");
        System.out.println("\t\t___________________________________________");
        System.out.println("\t\t\t1.Create new Account");
        System.out.println("\t\t\t2.Login to your account : ");
        // System.out.println("\t\t\t3. Get Account Details");
        System.out.println("\t\t\t3.Exit");
        System.out.print("\n\t\tEnter the task code to perform : ");
        int code = sc.nextInt();
        return code;
    }

    public static void main(String[] args) {
        // int code = menu();
        BankUser u = new BankUser();
        int code;
        do {
            code = menu();

            switch (code) {
                case 1:
                    u.createAccount();
                    break;

                case 2:
                    clearScreen();
                    u.loginaccount();
                    break;

                // case 3:
                // getaccountdetail();
                // break;

                case 3:
                    clearScreen();
                    System.out.println("\t\t\tExited......");
                    System.out.println("\t\t\tThankYou");
                    break;

                default:
                    System.out.println("Invalid option. Please choose again.");
                    break;
            }
        } while (code != 3);

    }
}
