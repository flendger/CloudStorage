package clientcore;

import clientcore.inboundHandlers.ClientInboundMessageHandler;
import clientcore.inboundHandlers.MessageDecoder;
import clientcore.outboundHandlers.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import messages.Message;
import messages.MessageUtils;
import messages.Readable;

import java.io.IOException;

public class CloudClientNetty {

    private Readable readable;
    private Channel channel;
    private EventLoopGroup workerGroup;

    public CloudClientNetty(Readable readable) {
        this.readable = readable;
    }

    public CloudClientNetty(){}

    public void setReadable(Readable readable) {
        this.readable = readable;
    }

    public void connect(String host, int port) throws Exception {
         workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new MessageDecoder(),
                            new MessageEncoder(),
                            new ClientInboundMessageHandler(readable));
                }
            });

            ChannelFuture f = b.connect(host, port).sync();
            this.channel = f.channel();
            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void send(Message msg) {
        channel.writeAndFlush(msg);
    }

    public void sendFile(String filePath) throws IOException {
        MessageUtils.sendFile(filePath, this::send);
    }

    public void close() {
        workerGroup.shutdownGracefully();
    }

    public Channel getChannel() {
        return channel;
    }
}
