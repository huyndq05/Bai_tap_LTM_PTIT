import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCP_ByteStream2 {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;

        String studentCode = "B23DCCN393"; // Thay bằng mã sinh viên của bạn
        String qCode = "rdsE0lxi";          // Thay bằng mã câu hỏi của bạn

        try {
            // Kết nối tới server
            Socket socket = new Socket(host, port);

            // Giới hạn thời gian giao tiếp tối đa 5 giây
            socket.setSoTimeout(5000);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            String request = studentCode + ";" + qCode;
            os.write(request.getBytes());
            os.flush();

            // b. Nhận chuỗi số nguyên từ server
            byte[] buffer = new byte[1024];
            int len = is.read(buffer);

            String data = new String(buffer, 0, len).trim();

            System.out.println("Server gửi: " + data);

            // Chuyển chuỗi thành mảng số nguyên
            String[] parts = data.split(",");

            int[] numbers = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {
                numbers[i] = Integer.parseInt(parts[i].trim());
            }

            // c. Tìm giá trị lớn thứ hai và vị trí xuất hiện
            int max1 = Integer.MIN_VALUE;
            int max2 = Integer.MIN_VALUE;

            for (int number : numbers) {
                if (number > max1) {
                    max2 = max1;
                    max1 = number;
                } else if (number > max2 && number < max1) {
                    max2 = number;
                }
            }

            // Tìm vị trí xuất hiện của giá trị lớn thứ hai
            // Vị trí tính từ 0
            int position = -1;

            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] == max2) {
                    position = i;
                    break;
                }
            }

            // Gửi kết quả dạng: giá trị,vị trí
            String result = max2 + "," + position;

            System.out.println("Gửi server: " + result);

            os.write(result.getBytes());
            os.flush();

            // d. Đóng kết nối
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}