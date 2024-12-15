import hashlib
import base58

def hex_to_wif(hex_private_key, network_byte=0x80):
    try:
        # Convert hex to bytes
        private_key_bytes = bytes.hex(hex_private_key)
        
        # Add the network byte
        extended_key = bytes([network_byte]) + private_key_bytes
        
        # Double SHA256 hash for checksum
        checksum = hashlib.sha256(hashlib.sha256(extended_key).digest()).digest()[:4]
        
        # Concatenate key with checksum
        wif_key = base58.b58encode(extended_key + checksum).decode('utf-8')
        return wif_key
    except Exception as e:
        print(f"Error: {e}")
        return ""

# Example usage:
hex_key = "your_hex_key_here"
wif_key = hex_to_wif(hex_key)
print(wif_key)
