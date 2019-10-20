
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();

        serverSocketChannel.bind(new InetSocketAddress(10086));
        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        int count = 1;

        //干活
        while (true){
            //监控客户端
            if (selector.select(2000)==0){
                System.out.println("server:没有客户端搭理，我干点别的事情先。。");
                continue;
            }

            //得到SlectionKey，判断通道里的事件
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            System.out.println("第"+count+"次的大小："+keys.size());
            count += 1;
            while (it.hasNext()){

                SelectionKey key = it.next();

                //客户端连接请求事件
                if (key.isAcceptable()){
                    System.out.println("OP_ACCEPT");
                    SocketChannel accept = serverSocketChannel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                //读取客户端数据事件
                if (key.isReadable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer o = (ByteBuffer) key.attachment();
                    channel.read(o);
                    System.out.println("客户端发来的数据：" + new java.lang.String(o.array()));
                }

                //手动从集合中移除当前key，防止重复处理
                it.remove();

            }
        }
    }
}
