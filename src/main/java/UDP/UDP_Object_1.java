package UDP;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UDP_Object_1 {
    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN393";
        String qCode = "pMzCBlkL";

        InetAddress address = InetAddress.getByName("36.50.135.242");
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        try {
            // a. Gui yeu cau
            String message = ";" + studentCode + ";" + qCode;
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, address, 2209);
            socket.send(sendPacket);

            // b. Nhan requestId (8 byte) va doi tuong Student
            byte[] buffer = new byte[65507];
            DatagramPacket receivePacket =
                    new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);

            byte[] packetData = receivePacket.getData();
            int length = receivePacket.getLength();

            if (length <= 8) {
                throw new IOException("Goi tin khong chua doi tuong Student");
            }

            byte[] requestIdBytes = new byte[8];
            System.arraycopy(packetData, 0, requestIdBytes, 0, 8);

            String requestId = new String(
                    requestIdBytes, StandardCharsets.UTF_8);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(
                    packetData, 8, length - 8);
            ObjectInputStream objectIn = new ObjectInputStream(byteIn);

            Student student = (Student) objectIn.readObject();
            objectIn.close();

            System.out.println("Request ID: " + requestId);
            System.out.println("Student nhan duoc: " + student);

            // c1. Chuan hoa ho ten
            String name = student.getName().trim()
                    .replaceAll("\\s+", " ")
                    .toLowerCase(Locale.ROOT);

            String[] words = name.split(" ");
            StringBuilder normalizedName = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    if (normalizedName.length() > 0) {
                        normalizedName.append(" ");
                    }

                    normalizedName.append(
                            Character.toUpperCase(word.charAt(0)));

                    if (word.length() > 1) {
                        normalizedName.append(word.substring(1));
                    }
                }
            }

            student.setName(normalizedName.toString());

            // c2. Tao email: ten + chu cai dau ho va ten dem
            StringBuilder email = new StringBuilder(words[words.length - 1]);

            for (int i = 0; i < words.length - 1; i++) {
                email.append(words[i].charAt(0));
            }

            email.append("@ptit.edu.vn");
            student.setEmail(email.toString());

            // c3. Serialize Student va ghep voi 8 byte requestId
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(student);
            objectOut.flush();

            byte[] objectBytes = byteOut.toByteArray();
            byte[] result = new byte[8 + objectBytes.length];

            System.arraycopy(requestIdBytes, 0, result, 0, 8);
            System.arraycopy(objectBytes, 0, result, 8, objectBytes.length);

            DatagramPacket resultPacket = new DatagramPacket(
                    result, result.length, address, 2209);
            socket.send(resultPacket);

            System.out.println("Student sau khi sua: " + student);
            System.out.println("Da gui doi tuong Student len server.");

            objectOut.close();

        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } finally {
            // d. Dong socket
            socket.close();
        }
    }
}
