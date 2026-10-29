import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCP_DataStream2 {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2207;

        String studentCode = "B23DCCN393";
        String qCode = "DZ0JFOyh";

        try {
            // Kết nối tới server
            Socket socket = new Socket(host, port);
            socket.setSoTimeout(5000);

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String request = studentCode + ";" + qCode;

            out.writeUTF(request);
            out.flush();

            // b. Nhận chuỗi Caesar và độ dịch chuyển s
            String encrypted = in.readUTF();
            int s = in.readInt();

            System.out.println("Chuỗi mã hóa: " + encrypted);
            System.out.println("s = " + s);

            // c. Giải mã Caesar
            StringBuilder decrypted = new StringBuilder();

            for (char c : encrypted.toCharArray()) {

                if (c >= 'A' && c <= 'Z') {
                    char x = (char) ((c - 'A' - s + 26) % 26 + 'A');
                    decrypted.append(x);

                } else if (c >= 'a' && c <= 'z') {
                    char x = (char) ((c - 'a' - s + 26) % 26 + 'a');
                    decrypted.append(x);

                } else {
                    // Giữ nguyên số, khoảng trắng, ký tự đặc biệt
                    decrypted.append(c);
                }
            }

            String result = decrypted.toString();

            System.out.println("Chuỗi sau khi giải mã: " + result);

            // Gửi thông điệp đã giải mã lên server
            out.writeUTF(result);
            out.flush();

            // d. Đóng kết nối
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
