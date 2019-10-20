import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {

        //创建一个线程组
        NioEventLoopGroup group = new NioEventLoopGroup();

        //创建客户端的启动助手，完成相关配置
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)  //设置线程组
                .channel(NioSocketChannel.class)    //设置客户端通道的实现类
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new NettyClientHandler());
                    }
                });
        System.out.println("。。。客户端已经就绪。。。");

        ChannelFuture cf = bootstrap.connect("127.0.0.1", 9999).sync();
        cf.channel().closeFuture().sync();
    }
}
