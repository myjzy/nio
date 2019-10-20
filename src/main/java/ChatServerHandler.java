import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    private static List<Channel> channelList = new ArrayList<Channel>();
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel cn = ctx.channel();
        channelList.add(cn);
        System.out.println("[server]:" + cn.remoteAddress().toString().substring(1) + " 上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel cn = ctx.channel();
        channelList.remove(cn);
        System.out.println("[server]:" + cn.remoteAddress().toString().substring(1) + "离线了");
    }

    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        Channel cn = ctx.channel();
        for(Channel cnel : channelList){
            if (cn == cnel)continue;
            cnel.writeAndFlush("["+cn.remoteAddress().toString().substring(1)+"]说:" + s +"\n\r");
        }
    }
}
