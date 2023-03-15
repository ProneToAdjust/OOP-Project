import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataStorage {
    private static String FILENAME = "BankDB";

    // Creates a db file with sample data if none is found in the current directory
    private static void createSampleDB() {
        Bank bank = new Bank("Fleeca");

        // add two users, which also creates a Savings account
        User testUser = bank.addUser("Sibei", "Suei", "4444");
        User testUser2 = bank.addUser("Ryan", "Ong", "5555");

        // add a checking account for our user
        Account checkingAccount = new Account("Checking", testUser, bank);
        Account checkingAccount2 = new Account("Checking", testUser2, bank);

        testUser.addAccount(checkingAccount);
        testUser2.addAccount(checkingAccount2);

        bank.addAccount(checkingAccount);
        bank.addAccount(checkingAccount2);

        try {
            FileOutputStream f = new FileOutputStream(new File(FILENAME));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(bank);

            o.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Bank
     *         Reads and returns the bank object from the db file
     */
    public static Bank loadDatabase() {
        Bank bank = null;
        try {
            FileInputStream fi = new FileInputStream(new File(FILENAME));
            ObjectInputStream oi = new ObjectInputStream(fi);

            bank = (Bank) oi.readObject();

            oi.close();
            fi.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            createSampleDB();
            bank = loadDatabase();
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return bank;
    }

    /**
     * @param bank
     *             Updates the db file with the input bank object
     */
    public static void saveDatabase(Bank bank) {
        try {
            FileOutputStream f = new FileOutputStream(new File(FILENAME));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(bank);

            o.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
