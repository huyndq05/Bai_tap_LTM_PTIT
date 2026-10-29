
package UDP;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UDP_Object_2{
    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN393";
        String qCode = "SU0ljgUu";

        InetAddress address = InetAddress.getByName("36.50.135.242");
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        try {
            // 1. Gui yeu cau
            String request = ";" + studentCode + ";" + qCode;
            byte[] requestData = request.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                    requestData, requestData.length, address, 2209));

            // 2. Nhan goi tin
            byte[] buffer = new byte[65507];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            int len = packet.getLength();
            if (len <= 8) {
                throw new IOException("Goi tin khong chua doi tuong Customer");
            }

            byte[] requestId = new byte[8];
            System.arraycopy(packet.getData(), 0, requestId, 0, 8);

            ByteArrayInputStream bin = new ByteArrayInputStream(
                    packet.getData(), 8, len - 8);
            ObjectInputStream oin = new ObjectInputStream(bin);

            Customer customer = (Customer) oin.readObject();
            oin.close();

            // 3. Chuan hoa ten
            String name = customer.getName().trim()
                    .replaceAll("\\s+", " ")
                    .toLowerCase(Locale.ROOT);

            String[] words = name.split(" ");

            StringBuilder formattedName = new StringBuilder();
            formattedName.append(words[words.length - 1].toUpperCase(Locale.ROOT));
            formattedName.append(", ");

            for (int i = 0; i < words.length - 1; i++) {
                if (i > 0) {
                    formattedName.append(" ");
                }

                formattedName.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    formattedName.append(words[i].substring(1));
                }
            }

            customer.setName(formattedName.toString());

            // 4. Chuyen ngay sinh mm-dd-yyyy sang dd/mm/yyyy
            String[] date = customer.getDayOfBirth().split("-");
            if (date.length != 3) {
                throw new IllegalArgumentException("Ngay sinh khong dung dinh dang");
            }

            customer.setDayOfBirth(date[1] + "/" + date[0] + "/" + date[2]);

            // 5. Tao username: chu cai dau ho, ten dem + ten
            StringBuilder username = new StringBuilder();

            for (int i = 0; i < words.length - 1; i++) {
                username.append(words[i].charAt(0));
            }

            username.append(words[words.length - 1]);
            customer.setUserName(username.toString().toLowerCase(Locale.ROOT));

            // 6. Serialize Customer
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(customer);
            oout.flush();

            byte[] objectData = bout.toByteArray();
            byte[] result = new byte[8 + objectData.length];

            System.arraycopy(requestId, 0, result, 0, 8);
            System.arraycopy(objectData, 0, result, 8, objectData.length);

            // 7. Gui ket qua
            socket.send(new DatagramPacket(
                    result, result.length, address, 2209));

            System.out.println("Da gui Customer: " + customer);
            oout.close();

        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } finally {
            socket.close();
        }
    }
}
