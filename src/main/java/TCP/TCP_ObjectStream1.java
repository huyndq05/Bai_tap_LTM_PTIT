
package TCP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

class Laptop implements Serializable {

    private static final long serialVersionUID = 20150711L;

    private int id;
    private String code;
    private String name;
    private int quantity;

    public Laptop(int id, String code, String name, int quantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

public class TCP_ObjectStream1 {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2209;

        String studentCode = "B23DCCN393";
        String qCode = "iMo5r7Df";

        try {
            Socket socket = new Socket(host, port);
            socket.setSoTimeout(5000);

            ObjectOutputStream out =
                    new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            ObjectInputStream in =
                    new ObjectInputStream(socket.getInputStream());

            // 1. Gửi mã sinh viên và mã câu hỏi
            out.writeObject(studentCode + ";" + qCode);
            out.flush();

            // 2. Nhận Laptop
            Laptop laptop = (Laptop) in.readObject();

            // 3. Đảo thứ tự các từ trong tên
            String[] words = laptop.getName().split(" ");

            StringBuilder newName = new StringBuilder();

            for (int i = words.length - 1; i >= 0; i--) {
                newName.append(words[i]);

                if (i > 0) {
                    newName.append(" ");
                }
            }

            laptop.setName(newName.toString());

            // Đảo ngược số lượng
            int quantity = laptop.getQuantity();
            int reversed = 0;

            while (quantity > 0) {
                reversed = reversed * 10 + quantity % 10;
                quantity /= 10;
            }

            laptop.setQuantity(reversed);

            // Gửi Laptop đã sửa
            out.writeObject(laptop);
            out.flush();

            // 4. Đóng kết nối
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
