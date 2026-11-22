// Central configuration for the app.
//
// IMPORTANT — the backend address:
//   The phone is a separate device from your computer, so "localhost" would
//   mean the PHONE itself, not your backend. During development you must use
//   your computer's IP address on your Wi-Fi network.
//
//   Find it with:
//     macOS/Linux:  ifconfig | grep "inet "   (look for 192.168.x.x)
//     Windows:      ipconfig                   (look for IPv4 Address)
//
//   Then set API_BASE_URL below to  http://<that-ip>:8080
//   Example:  http://192.168.1.50:8080
//
//   For a deployed backend (Phase 6) you'd use its public https URL instead.

export const API_BASE_URL = 'http://192.168.1.50:8080';
