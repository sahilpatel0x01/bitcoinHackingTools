import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.Wallet;
import java.util.Scanner;

public class BitcoinBalanceChecker {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option:");
        System.out.println("1: Check balance with Hex Private Key");
        System.out.println("2: Check balance with WIF Private Key");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            String privateKeyInput;
            ECKey key;

            switch (choice) {
                case 1:
                    System.out.print("Enter your Hexadecimal Private Key: ");
                    privateKeyInput = scanner.nextLine().trim();
                    if (privateKeyInput.length() != 64) {
                        System.out.println("Invalid Hexadecimal Private Key. Must be 64 characters.");
                        return;
                    }
                    key = ECKey.fromPrivate(new BigInteger(privateKeyInput, 16));
                    break;

                case 2:
                    System.out.print("Enter your WIF Private Key: ");
                    privateKeyInput = scanner.nextLine().trim();
                    key = DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKeyInput).getKey();
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1 or 2.");
                    return;
            }

            // Generate the public key and address
            String publicKey = key.getPublicKeyAsHex();
            Address address = key.toAddress(MainNetParams.get());

            System.out.println("Public Key: " + publicKey);
            System.out.println("Bitcoin Address: " + address.toString());

            // Check the balance
            System.out.println("Checking balance...");
            double balance = getBalance(address.toString());
            System.out.printf("Balance for address %s: %.8f BTC\n", address.toString(), balance);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static double getBalance(String bitcoinAddress) {
        try {
            // Replace with a blockchain API of your choice to fetch balance
            // Example API: https://blockchain.info
            String apiUrl = "https://blockchain.info/q/addressbalance/" + bitcoinAddress;
            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream()))) {
                String response = in.readLine();
                // Convert satoshis to BTC (1 BTC = 100,000,000 satoshis)
                return Double.parseDouble(response) / 1e8;
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch balance: " + e.getMessage());
            return 0.0;
        }
    }
}
