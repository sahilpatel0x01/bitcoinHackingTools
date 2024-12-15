from ecdsa import SigningKey, SECP256k1
from ecdsa.ellipticcurve import Point
from ecdsa.util import number_to_string

# Function to verify if a public key is on the elliptic curve
def is_point_on_curve(public_key_point, curve):
    """
    Verifies if a given point (x, y) lies on the elliptic curve.
    Equation: y^2 = x^3 + ax + b (mod p)
    """
    x, y = public_key_point.x(), public_key_point.y()
    a, b = curve.a(), curve.b()
    p = curve.p()
    return (y * y - x * x * x - a * x - b) % p == 0

# Function to derive the public key from a given private key
def get_public_key_from_private(private_key_hex):
    try:
        # Convert the private key from hex to bytes
        private_key_bytes = bytes.fromhex(private_key_hex)

        # Create a SigningKey instance from the private key
        private_key = SigningKey.from_string(private_key_bytes, curve=SECP256k1)

        # Get the public key point
        public_key = private_key.get_verifying_key()
        public_key_point = public_key.pubkey.point

        return public_key_point, SECP256k1.curve
    except Exception as e:
        print(f"Error: {e}")
        return None, None

# Input: Private key in hexadecimal format
private_key_hex = input("Enter your private key (hexadecimal): ").strip()

# Get the public key from the private key
public_key_point, curve = get_public_key_from_private(private_key_hex)

if public_key_point:
    print(f"Public Key Point: (x: {public_key_point.x()}, y: {public_key_point.y()})")

    # Check if the public key is on the elliptic curve
    is_valid = is_point_on_curve(public_key_point, curve)

    if is_valid:
        print("The public key is valid and lies on the elliptic curve.")
    else:
        print("The public key is invalid and does not lie on the elliptic curve.")
else:
    print("Failed to derive the public key from the given private key.")
