import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient2 {
    private static String username;
    private static SocketChannel socketChannel;
    public static void main(String[] args) throws Exception {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress ia = new InetSocketAddress("127.0.0.1",10086);
        if(!socketChannel.connect(ia)){
            while (!socketChannel.finishConnect()){
                System.out.println("client: 连接服务端的同时，我还可以做别的事情。。");
            }
        }
        username = socketChannel.getLocalAddress().toString().substring(1);

        new Thread(new Runnable() {
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line;
                try {

                    while(!(line = br.readLine()).equals("end")){
                        sendMsg(line);
                    }

                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        getMsg();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private static void sendMsg(String msg) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(byteBuffer);
    }

    private static void getMsg() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);
        if (read > 0){
            String msg = new String(byteBuffer.array());
            System.out.println(msg.trim());
        }
    }
}
