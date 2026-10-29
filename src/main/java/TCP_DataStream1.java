import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCP_DataStream1 {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;

        String studentCode = "B23DCCN393";
        String qCode = "23fVB0Sk";

        try {
            // Kết nối tới server
            Socket socket = new Socket(host, port);

            // Timeout 5 giây
            socket.setSoTimeout(5000);

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());

            // a. Gửi studentCode;qCode
            String request = studentCode + ";" + qCode;
            out.writeUTF(request);
            out.flush();

            // b. Nhận hai số nguyên a và b
            int a = in.readInt();
            int b = in.readInt();

            System.out.println("a = " + a);
            System.out.println("b = " + b);

            // c. Tính tổng và tích
            int sum = a + b;
            int product = a * b;

            System.out.println("Tổng = " + sum);
            System.out.println("Tích = " + product);

            // Gửi lần lượt tổng rồi tích
            out.writeInt(sum);
            out.writeInt(product);
            out.flush();

            // d. Đóng kết nối
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
