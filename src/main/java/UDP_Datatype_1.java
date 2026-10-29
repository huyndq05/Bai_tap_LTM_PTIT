
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UDP_Datatype_1 {
    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN393";
        String qCode = "u7EGruY1";

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        InetAddress address = InetAddress.getByName("36.50.135.242");

        // a. Gui ma sinh vien va ma cau hoi
        String message = ";" +studentCode + ";" + qCode;
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
                receivePacket.getData(), 0,
                receivePacket.getLength(), StandardCharsets.UTF_8).trim();

        System.out.println("Server: " + response);

        String[] parts = response.split(";", 3);
        String requestId = parts[0];
        int n = Integer.parseInt(parts[1]);

        Set<Integer> existing = new HashSet<>();

        if (!parts[2].trim().isEmpty()) {
            String[] values = parts[2].split(",");
            for (String value : values) {
                existing.add(Integer.parseInt(value.trim()));
            }
        }

        // c. Tim cac gia tri con thieu tu 1 den n
        List<Integer> missing = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            if (!existing.contains(i)) {
                missing.add(i);
            }
        }

        StringBuilder result = new StringBuilder(requestId).append(";");

        for (int i = 0; i < missing.size(); i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append(missing.get(i));
        }

        // Gui ket qua len server
        byte[] resultData = result.toString()
                .getBytes(StandardCharsets.UTF_8);

        DatagramPacket resultPacket = new DatagramPacket(
                resultData, resultData.length, address, 2207);
        socket.send(resultPacket);

        System.out.println("Sent: " + result);

        // d. Dong socket
        socket.close();
    }
}
