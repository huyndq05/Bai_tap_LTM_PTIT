import java.io.*;
import java.net.*;

public class TCP_Character {
    public static void main(String[] args) {
        ////
        String host = "36.50.135.242";
        int port = 2208;
        String studentCode = "B23DCCN393";
        String qCode = "dxgcuR9c";
        ////
        ///
        try (Socket socket = new Socket(host, port)) {
            
            socket.setSoTimeout(5000);
            
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            String request = studentCode + ";" + qCode;
            bw.write(request);
            bw.newLine();
            bw.flush();

            
            String data = br.readLine();
            StringBuilder result = new StringBuilder();
            // Danh sách trong đề được ngăn cách bởi dấu phẩy
            String[] domains = data.split(",");

            for (String domain : domains) {
                domain = domain.trim();

                if (domain.endsWith(".edu")) {
                    if (result.length() > 0) {
                        result.append(", ");
                    }

                    result.append(domain);
                }
            }

            bw.write(result.toString());
            bw.newLine();
            bw.flush();


        } catch (IOException e) {
            System.out.println();
        }
    }
}