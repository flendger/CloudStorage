package inboundHandlers;

import files.FileList;
import files.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.MessageUtils;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import services.UserProfile;

public class ServerInboundCommandHandler extends SimpleChannelInboundHandler<CommandMessage> {
    private final UserProfile userProfile;

    public ServerInboundCommandHandler(UserProfile userProfile) {
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
    protected void channelRead0(ChannelHandlerContext ctx, CommandMessage msg) throws Exception {
        System.out.println(msg.toString());
        switch (msg.getCommand()) {
            case MSG_GET_LS:
                CommandMessage replyMsg = new CommandMessage(CommandMessageType.MSG_PUT_LS);
                FileList fl = FileUtils.getFilesList(userProfile.curDir);
                replyMsg.setData(MessageUtils.ObjectToBytes(fl));
                ctx.writeAndFlush(replyMsg);
                break;
            default:
                ctx.writeAndFlush(new CommandMessage(CommandMessageType.MSG_OK));
        }
    }
}
