import java.io.*;
import java.net.Socket;
import java.util.*;

public class TCP_CharacterStream2 {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;

        String studentCode = "B23DCCN393";
        String qCode = "84S3MRTa";

        try {
            // Kết nối đến server
            Socket socket = new Socket(host, port);

            // Timeout 5 giây
            socket.setSoTimeout(5000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );

            // a. Gửi MSSV;qCode
            String request = studentCode + ";" + qCode;
            out.write(request);
            out.newLine();
            out.flush();

            // b. Nhận chuỗi từ server
            String data = in.readLine();

            System.out.println("Server gửi: " + data);

            // c. Đếm số lần xuất hiện của các ký tự chữ hoặc số
            Map<Character, Integer> count = new LinkedHashMap<>();

            for (char c : data.toCharArray()) {

                // Chỉ xét chữ hoặc số
                if (Character.isLetterOrDigit(c)) {
                    count.put(c, count.getOrDefault(c, 0) + 1);
                }
            }

            // Tạo kết quả
            StringBuilder result = new StringBuilder();

            for (Map.Entry<Character, Integer> entry : count.entrySet()) {
                if (entry.getValue() > 1) {
                    result.append(entry.getKey())
                          .append(":")
                          .append(entry.getValue())
                          .append(",");
                }
            }

            // Gửi kết quả lên server
            System.out.println("Gửi server: " + result);

            out.write(result.toString());
            out.newLine();
            out.flush();

            // d. Đóng kết nối
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
