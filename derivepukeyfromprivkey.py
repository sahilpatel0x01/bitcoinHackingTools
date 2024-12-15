import requests
from bitcoinlib.wallets import Wallet
from bitcoinlib.keys import Key
import base58

def get_balance(address):
    # API URL to get Bitcoin balance for an address
    api_url = f"https://blockchain.info/q/addressbalance/{address}"
    try:
        response = requests.get(api_url)
        response.raise_for_status()  # Raise an exception for HTTP errors
        balance_satoshis = int(response.text)
        balance_btc = balance_satoshis / 1e8  # Convert satoshis to BTC
        return balance_btc
    except requests.exceptions.RequestException as e:
        print(f"Error fetching balance: {e}")
        return None

def get_address_from_private_key(private_key: str, key_type: str = "wif"):
    """
    Convert a private key (Hex or WIF) to a Bitcoin address
    :param private_key: Hex or WIF private key
    :param key_type: Type of key, either 'hex' or 'wif'
    :return: Bitcoin address
    """
    if key_type == "hex":
        # Create a key from the Hex private key
        key = Key.from_hex(private_key)
    elif key_type == "wif":
        # Create a key from the WIF private key
        key = Key.from_wif(private_key)
        print(key)
        
    else:
        raise ValueError("Invalid key type. Use 'hex' or 'wif'.")
    
    # Generate address from the private key
    address = key.address()
    print(address)
    return address


def main():
    print("Choose an option:")
    print("1: Check balance using Hex private key")
    print("2: Check balance using WIF private key")
    
    choice = input("Enter your choice (1 or 2): ").strip()
    private_key = input("Enter your private key: ").strip()

    if choice == "1":
        key_type = "hex"
    elif choice == "2":
        key_type = "wif"
    else:
        print("Invalid choice. Exiting...")
        return
    
    try:
        # Get Bitcoin address from the private key
        address = get_address_from_private_key(private_key, key_type)
        print(f"Generated Bitcoin Address: {address}")

        # Get balance for the address
        balance = get_balance(address)
        if balance is not None:
            print(f"Balance for {address}: {balance} BTC")
        else:
            print("Could not retrieve balance.")
    except ValueError as ve:
        print(f"Error: {ve}")
    except Exception as e:
        print(f"Unexpected error: {e}")

if __name__ == "__main__":
    main()
