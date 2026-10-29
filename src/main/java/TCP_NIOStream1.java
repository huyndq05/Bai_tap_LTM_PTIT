
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TCP_NIOStream1 {

    // Đọc đủ số byte yêu cầu
    static void readFully(SocketChannel socket, ByteBuffer buffer)
            throws IOException {

        while (buffer.hasRemaining()) {
            int n = socket.read(buffer);

            if (n == -1) {
                throw new IOException("Server đóng kết nối");
            }
        }
    }

    // Đọc một frame:
    // 4 byte độ dài + payload
    static String readFrame(SocketChannel socket)
            throws IOException {

        // Đọc 4 byte độ dài
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        readFully(socket, lengthBuffer);

        lengthBuffer.flip();

        int length = lengthBuffer.getInt();

        // Đọc payload
        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
        readFully(socket, dataBuffer);

        dataBuffer.flip();

        return StandardCharsets.UTF_8.decode(dataBuffer).toString();
    }

    // Gửi một frame
    static void writeFrame(SocketChannel socket, String data)
            throws IOException {

        byte[] payload =
                data.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer =
                ByteBuffer.allocate(4 + payload.length);

        // 4 byte độ dài
        buffer.putInt(payload.length);

        // payload
        buffer.put(payload);

        buffer.flip();

        // Ghi đủ dữ liệu
        while (buffer.hasRemaining()) {
            socket.write(buffer);
        }
    }

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2211;

        String studentCode = "B23DCCN393";
        String qCode = "T7r9tzII";

        try (SocketChannel socket =
                     SocketChannel.open()) {

            // Kết nối server
            socket.connect(
                    new InetSocketAddress(host, port)
            );

            // a. Gửi studentCode;qCode
            String request =
                    studentCode + ";" + qCode;

            writeFrame(socket, request);

            // b. Nhận đúng 3 frame
            String frame1 = readFrame(socket);
            String frame2 = readFrame(socket);
            String frame3 = readFrame(socket);

            // Nối đúng thứ tự
            String httpRequest =
                    frame1 + frame2 + frame3;

            System.out.println("HTTP Request:");
            System.out.println(httpRequest);

            // c. Phân tích HTTP request

            // Tách các dòng bằng CRLF
            String[] lines =
                    httpRequest.split("\\r\\n");

            // Dòng đầu tiên:
            // METHOD PATH HTTP/1.1
            String[] requestLine =
                    lines[0].split(" ");

            String method = requestLine[0];
            String path = requestLine[1];

            String hostHeader = "";

            // Tìm Host
            for (String line : lines) {

                if (line.regionMatches(
                        true, 0, "Host:", 0, 5)) {

                    hostHeader =
                            line.substring(5).trim();

                    break;
                }
            }

            // Kết quả:
            // METHOD;PATH;HOST
            String result =
                    method + ";" +
                    path + ";" +
                    hostHeader;

            System.out.println("Kết quả:");
            System.out.println(result);

            // Gửi kết quả lên server
            writeFrame(socket, result);

            // d. SocketChannel tự đóng nhờ try-with-resources

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
