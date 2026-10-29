
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP_Datatype_2 {
    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN393";
        String qCode = "L5PknQZb";

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        InetAddress address = InetAddress.getByName("36.50.135.242");

        try {
            // a. Gui yeu cau den server
            String message = ";" + studentCode + ";" + qCode;
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, address, 2207);
            socket.send(sendPacket);

            // b. Nhan du lieu tu server
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);

            socket.receive(receivePacket);

            String response = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength(),
                    StandardCharsets.UTF_8).trim();

            System.out.println("Server: " + response);

            // Tach requestId va danh sach 50 so
            String[] parts = response.split(";", 2);
            String requestId = parts[0];
            String[] numbers = parts[1].split(",");

            // c. Tim gia tri lon nhat va nho nhat
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;

            for (String number : numbers) {
                int value = Integer.parseInt(number.trim());

                if (value > max) {
                    max = value;
                }

                if (value < min) {
                    min = value;
                }
            }

            // Gui ket qua theo dinh dang requestId;max,min
            String result = requestId + ";" + max + "," + min;
            byte[] resultData = result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket = new DatagramPacket(
                    resultData, resultData.length, address, 2207);
            socket.send(resultPacket);

            System.out.println("Ket qua gui len server: " + result);

        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } finally {
            // d. Dong socket
            socket.close();
        }
    }
}
