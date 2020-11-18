package inboundHandlers;

import files.FileList;
import files.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.MessageUtils;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import messages.dataTransfer.DataTransferMessage;
import services.ServerConf;
import services.UserProfile;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;

public class ServerInboundDataTransferHandler extends SimpleChannelInboundHandler<DataTransferMessage> {
    private final UserProfile userProfile;

    public ServerInboundDataTransferHandler(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
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
    protected void channelRead0(ChannelHandlerContext ctx, DataTransferMessage msg) throws Exception {
        System.out.println(msg.toString());
        ctx.writeAndFlush(msg);
    }
}
