//it only converts the private key to hex viceversa

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Scanner;

public class HexToWIF {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option:");
        System.out.println("1: Convert Hex to WIF");
        System.out.println("2: Convert WIF to Hex");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (choice) {
            case 1:
                System.out.print("Enter your hexadecimal private key: ");
                String hexPrivateKey = scanner.nextLine().trim();
                if (hexPrivateKey.isEmpty() || !isHexString(hexPrivateKey) || hexPrivateKey.length() != 64) {
                    System.out.println("Invalid hexadecimal private key. Please enter a valid 64-character key.");
                } else {
                    String wifKey = convertHexToWIF(hexPrivateKey);
                    System.out.println("WIF Format: " + wifKey);
                }
                break;

            case 2:
                System.out.print("Enter your WIF private key: ");
                String wifPrivateKey = scanner.nextLine().trim();
                String hexKey = convertWIFToHex(wifPrivateKey);
                if (hexKey == null) {
                    System.out.println("Invalid WIF private key.");
                } else {
                    System.out.println("Hexadecimal Private Key: " + hexKey);
                }
                break;

            default:
                System.out.println("Invalid choice. Please select either 1 or 2.");
        }

        scanner.close();
    }

    public static String convertHexToWIF(String hexPrivateKey) {
        try {
            // Step 1: Prefix with 0x80
            String prefixedKey = "80" + hexPrivateKey;

            // Step 2: Perform double SHA-256 hashing
            byte[] hash = doubleSHA256(hexStringToByteArray(prefixedKey));

            // Step 3: Take the first 4 bytes as checksum
            byte[] checksum = Arrays.copyOfRange(hash, 0, 4);

            // Step 4: Append the checksum to the prefixed key
            byte[] extendedKey = concatenate(hexStringToByteArray(prefixedKey), checksum);

            // Step 5: Encode the result in Base58
            return encodeBase58(extendedKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertWIFToHex(String wifKey) {
        try {
            byte[] decoded = decodeBase58(wifKey);
            if (decoded == null || decoded.length < 37) {
                return null;
            }

            // Remove the prefix and checksum
            byte[] privateKeyWithCompression = Arrays.copyOfRange(decoded, 1, decoded.length - 4);
            boolean isCompressed = privateKeyWithCompression.length == 33 && privateKeyWithCompression[32] == 0x01;

            // Extract the private key
            byte[] privateKey = isCompressed
                    ? Arrays.copyOfRange(privateKeyWithCompression, 0, 32)
                    : privateKeyWithCompression;

            return bytesToHex(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] doubleSHA256(byte[] input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] firstHash = digest.digest(input);
        return digest.digest(firstHash);
    }

    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static String encodeBase58(byte[] input) {
        final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        BigInteger base58 = BigInteger.valueOf(58);
        BigInteger value = new BigInteger(1, input);
        StringBuilder result = new StringBuilder();

        while (value.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divMod = value.divideAndRemainder(base58);
            result.insert(0, ALPHABET.charAt(divMod[1].intValue()));
            value = divMod[0];
        }

        // Add '1' for each leading 0 byte in the input
        for (byte b : input) {
            if (b == 0) {
                result.insert(0, '1');
            } else {
                break;
            }
        }

        return result.toString();
    }

    private static byte[] decodeBase58(String input) {
        final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        BigInteger base58 = BigInteger.valueOf(58);
        BigInteger value = BigInteger.ZERO;

        for (char c : input.toCharArray()) {
            int digit = ALPHABET.indexOf(c);
            if (digit < 0) {
                return null; // Invalid character
            }
            value = value.multiply(base58).add(BigInteger.valueOf(digit));
        }

        // Convert to byte array and remove leading zero padding
        byte[] rawBytes = value.toByteArray();
        if (rawBytes[0] == 0) {
            rawBytes = Arrays.copyOfRange(rawBytes, 1, rawBytes.length);
        }

        // Add leading zeros for '1's in the input
        int leadingZeros = 0;
        for (char c : input.toCharArray()) {
            if (c == '1') {
                leadingZeros++;
            } else {
                break;
            }
        }

        byte[] decoded = new byte[leadingZeros + rawBytes.length];
        System.arraycopy(rawBytes, 0, decoded, leadingZeros, rawBytes.length);
        return decoded;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static boolean isHexString(String str) {
        return str.matches("^[0-9a-fA-F]+$");
    }
}
