import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NIOServer2 {
    private static Selector selector;

    public static void main(String[] args) throws IOException {
        ServerSocketChannel sc = ServerSocketChannel.open();

        selector = Selector.open();
        sc.bind(new InetSocketAddress(10086));
        sc.configureBlocking(false);

        sc.register(selector, SelectionKey.OP_ACCEPT);

        //干活
        while (true){
            //监控客户端
            if (selector.select(3000)==0){
                System.out.println("server:没有客户端搭理，我干点别的事情先。。");
                continue;
            }

            //得到SlectionKey，判断通道里的事件
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()){

                SelectionKey key = it.next();

                //客户端连接请求事件
                if (key.isAcceptable()){
                    SocketChannel accept = sc.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ);
                    System.out.println(accept.getRemoteAddress().toString().substring(1) + " 上线了。。。");
                }

                //读取客户端数据事件
                if (key.isReadable()){
                    //广播信息
                    readMsg(key);
                }

                //手动从集合中移除当前key，防止重复处理
                it.remove();

            }
        }
    }

    //读取客户端发来的消息，并且广播出去
    private static void readMsg(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer dst = ByteBuffer.allocate(1024);
        int count = sc.read(dst);
        if (count > 0){
            String msg = new String(dst.array());
            sop(sc.getRemoteAddress().toString().substring(1) + "发过来的消息：" + msg);
            broadCast(sc, msg);
        }
    }

    private static void broadCast(SocketChannel sc, String msg) throws IOException {
        Iterator<SelectionKey> it = selector.keys().iterator();
        while (it.hasNext()){
            SelectionKey key = it.next();
            SelectableChannel sc1 = key.channel();

            if (sc1 != sc && sc1 instanceof SocketChannel){
                ByteBuffer buffer = ByteBuffer.wrap((sc.getRemoteAddress().toString().substring(1) + "说：" + msg).getBytes());
                ((SocketChannel)sc1).write(buffer);
            }
        }
    }

    private static void sop(Object msg){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(time);
        System.out.println(msg);
        System.out.println();
    }
}
