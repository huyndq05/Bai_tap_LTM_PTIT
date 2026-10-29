
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP_String_1 {
    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN393";
        String qCode = "nNSPMCq9";

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        InetAddress address = InetAddress.getByName("36.50.135.242");

        try {
            // a. Gui yeu cau
            String message = ";" + studentCode + ";" + qCode;
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, address, 2208);
            socket.send(sendPacket);

            // b. Nhan du lieu
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);

            socket.receive(receivePacket);

            String response = new String(
                    receivePacket.getData(), 0,
                    receivePacket.getLength(),
                    StandardCharsets.UTF_8).trim();

            System.out.println("Server: " + response);

            String[] parts = response.split(";", 2);
            String requestId = parts[0];
            String data = parts[1];

            // c. Tim ky tu xuat hien nhieu nhat
            int[] count = new int[Character.MAX_VALUE + 1];

            for (int i = 0; i < data.length(); i++) {
                count[data.charAt(i)]++;
            }

            int maxCount = 0;
            char maxChar = '\0';

            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);

                if (count[c] > maxCount) {
                    maxCount = count[c];
                    maxChar = c;
                }
            }

            // Tim cac vi tri xuat hien cua ky tu nhieu nhat
            StringBuilder positions = new StringBuilder();

            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == maxChar) {
                    positions.append(i + 1).append(",");
                }
            }

            String result = requestId + ";" + maxChar + ":" + positions;
            byte[] resultData = result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket = new DatagramPacket(
                    resultData, resultData.length, address, 2208);
            socket.send(resultPacket);

            System.out.println("Da gui: " + result);

        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } finally {
            // d. Dong socket
            socket.close();
        }
    }
}
