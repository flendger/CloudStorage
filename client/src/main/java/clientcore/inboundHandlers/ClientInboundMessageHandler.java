package clientcore.inboundHandlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.Message;
import messages.Readable;

public class ClientInboundMessageHandler extends SimpleChannelInboundHandler<Message> {

    private ChannelHandlerContext ctx;
    private final Readable readable;

    public ClientInboundMessageHandler(Readable readable) {
        this.readable = readable;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        readable.read(msg);
    }

    private void send(Message msg) {
        ctx.writeAndFlush(msg);
    }
}
