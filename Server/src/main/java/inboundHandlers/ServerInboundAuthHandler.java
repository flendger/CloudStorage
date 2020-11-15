package inboundHandlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.MessageUtils;
import messages.auth.AuthMessage;
import services.CreateProfileFailedException;
import services.AuthService;
import services.UserProfile;

public class ServerInboundAuthHandler extends SimpleChannelInboundHandler<AuthMessage> {
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
    protected void channelRead0(ChannelHandlerContext ctx, AuthMessage msg) throws Exception {
        System.out.println(msg.toString());
        if (msg.isRegistration()) {
            try {
                AuthService.createProfile(msg.getUser(), msg.getPass());
                ctx.writeAndFlush(MessageUtils.getOKMessage(String.format("User [%s] registration succeed.", msg.getUser())));
            } catch (CreateProfileFailedException e) {
                ctx.writeAndFlush(MessageUtils.getErrorMessage(e.getMessage()));
                return;
            }
        } else {
            UserProfile userProfile = AuthService.findProfile(msg.getUser(), msg.getPass());
            if (userProfile == null) {
                ctx.writeAndFlush(MessageUtils.getErrorMessage(String.format("User [%s] authorization failed.", msg.getUser())));
                return;
            }

            //TODO: add authorization
            ctx.writeAndFlush(MessageUtils.getOKMessage(String.format("User [%s] authorized.", msg.getUser())));
        }
    }
}
