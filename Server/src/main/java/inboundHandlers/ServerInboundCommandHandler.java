package inboundHandlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;

public class ServerInboundCommandHandler extends SimpleChannelInboundHandler<CommandMessage> {
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
            case MSG_OK:
                break;
            case MSG_ERR:
                break;
            case MSG_GET_LS:
                //TODO: return ls;
                CommandMessage replyMsg = new CommandMessage(CommandMessageType.MSG_OK);
//                replyMsg.setParameter();
            default:
                break;
        }
        ctx.writeAndFlush(new CommandMessage(CommandMessageType.MSG_OK));
    }
}
