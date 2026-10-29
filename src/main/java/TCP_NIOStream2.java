
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCP_NIOStream2 {

    // Đọc đủ dữ liệu vào ByteBuffer
    static void readFully(SocketChannel socket, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int n = socket.read(buffer);
            if (n == -1) {
                throw new IOException("Server đóng kết nối");
            }
        }
    }

    // Đọc một frame: 4 byte length + payload
    static String readFrame(SocketChannel socket) throws IOException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);

        readFully(socket, lengthBuffer);
        lengthBuffer.flip();

        int length = lengthBuffer.getInt();

        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
        readFully(socket, dataBuffer);
        dataBuffer.flip();

        return StandardCharsets.UTF_8.decode(dataBuffer).toString();
    }

    // Gửi một frame
    static void writeFrame(SocketChannel socket, String data) throws IOException {
        byte[] payload = data.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(4 + payload.length);

        buffer.putInt(payload.length);
        buffer.put(payload);
        buffer.flip();

        while (buffer.hasRemaining()) {
            socket.write(buffer);
        }
    }

    // Lấy giá trị String từ JSON
    static String getString(String json, String key) {
        Pattern pattern = Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]*)\""
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    // Lấy giá trị boolean từ JSON
    static boolean getBoolean(String json, String key) {
        Pattern pattern = Pattern.compile(
            "\"" + key + "\"\\s*:\\s*(true|false)"
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }

        return false;
    }

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2211;

        String studentCode = "B23DCCN393";
        String qCode = "W4Dp1GJZ";

        try (SocketChannel socket =
                     SocketChannel.open(new InetSocketAddress(host, port))) {

            // =========================
            // a. Gửi MSSV + mã câu hỏi
            // =========================
            String request = studentCode + ";" + qCode;

            writeFrame(socket, request);

            // =========================
            // b. Nhận đúng 2 frame
            // =========================
            String frame1 = readFrame(socket);
            String frame2 = readFrame(socket);

            // Ghép 2 payload theo đúng thứ tự
            String json = frame1 + frame2;

            // =========================
            // c. Phân tích JSON
            // =========================

            String event = getString(json, "event");
            String user = getString(json, "user");
            boolean ok = getBoolean(json, "ok");

            int okValue = ok ? 1 : 0;

            String result =
                    "event=" + event +
                    ";user=" + user +
                    ";ok=" + okValue;

            // Gửi kết quả dưới dạng frame
            writeFrame(socket, result);

            // =========================
            // d. Đóng kết nối
            // =========================
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

