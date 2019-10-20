import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress ia = new InetSocketAddress("127.0.0.1",10086);
        if(!socketChannel.connect(ia)){
            while (!socketChannel.finishConnect()){
                System.out.println("client: 连接服务端的同时，我还可以做别的事情。。");
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap("hello,server".getBytes());
        socketChannel.write(byteBuffer);

        System.in.read();
    }
}
