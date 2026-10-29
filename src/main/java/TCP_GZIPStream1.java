
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TCP_GZIPStream1{

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2210;

        String studentCode = "B23DCCN393";
        String qCode = "pdJdh7hv";

        try (Socket socket = new Socket(host, port)) {

            socket.setSoTimeout(5000);

            // =========================================
            // 1. Tạo GZIPOutputStream
            // syncFlush = true để flush dữ liệu ngay
            // =========================================

            GZIPOutputStream gzipOut =
                    new GZIPOutputStream(
                            socket.getOutputStream(),
                            true
                    );

            BufferedWriter writer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    gzipOut,
                                    StandardCharsets.UTF_8
                            )
                    );

            // =========================================
            // 2. Gửi MSSV + mã câu hỏi
            // =========================================

            String request =
                    studentCode + ";" + qCode;

            writer.write(request);
            writer.newLine();
            writer.flush();

            // =========================================
            // 3. SAU KHI ĐÃ GỬI REQUEST
            //    mới tạo GZIPInputStream
            // =========================================

            GZIPInputStream gzipIn =
                    new GZIPInputStream(
                            socket.getInputStream()
                    );

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    gzipIn,
                                    StandardCharsets.UTF_8
                            )
                    );

            // =========================================
            // 4. Nhận chuỗi từ server
            // =========================================

            String data = reader.readLine();

            // =========================================
            // 5. Đảo ngược chuỗi
            // =========================================

            String reversed =
                    new StringBuilder(data)
                            .reverse()
                            .toString();

            // =========================================
            // 6. Mã hóa chuỗi đảo ngược bằng Base64
            // =========================================

            String base64 =
                    Base64.getEncoder()
                            .encodeToString(
                                    reversed.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            );

            // =========================================
            // 7. Tạo kết quả
            // =========================================

            String result =
                    reversed + "|" + base64;

            // =========================================
            // 8. Gửi kết quả về server
            // =========================================

            writer.write(result);
            writer.newLine();
            writer.flush();

            // =========================================
            // 9. Kết thúc
            // socket tự động đóng
            // =========================================

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
