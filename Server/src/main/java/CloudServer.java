import inboundHandlers.MessageDecoder;
import inboundHandlers.ServerInboundAuthHandler;
import inboundHandlers.ServerInboundCommandHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import outboundHandlers.MessageEncoder;

public class CloudServer {

    private final static int SERVER_PORT = 8780;

    public CloudServer() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new MessageDecoder(),
                                    new MessageEncoder(),
                                    new ServerInboundAuthHandler(),

                                    //TODO: добавить динамическое добавление хендлеров после авторизации
                                    new ServerInboundCommandHandler());
                        }
                    });

            b.bind(SERVER_PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
