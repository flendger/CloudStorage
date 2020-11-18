package inboundHandlers;

import files.FileList;
import files.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.MessageUtils;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import services.ServerConf;
import services.UserProfile;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;

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
            case MSG_GET_CD:
                try {
                    String newDir = FileUtils.changeDir(msg.getParameter(), userProfile.curDir, userProfile.getAbsoluteRoot(ServerConf.SERVER_ROOT_DIR));
                    userProfile.curDir = newDir;
                    CommandMessage putCd = new CommandMessage(CommandMessageType.MSG_PUT_CD);
                    putCd.setParameter(newDir);
                    ctx.writeAndFlush(putCd);
                } catch (NotDirectoryException e) {
                    ctx.writeAndFlush(MessageUtils.getErrorMessage("Destination is not directory: " + e.getMessage()));
                } catch (NoSuchFileException e) {
                    ctx.writeAndFlush(MessageUtils.getErrorMessage("Directory doesn't exist: " + e.getMessage()));
                }
                break;
            case MSG_GET_MD:
                try {
                    String newDir = FileUtils.makeDir(msg.getParameter(), userProfile.curDir);
                    userProfile.curDir = newDir;
                    CommandMessage putMd = new CommandMessage(CommandMessageType.MSG_PUT_MD);
                    putMd.setParameter(newDir);
                    ctx.writeAndFlush(putMd);
                } catch (FileAlreadyExistsException e) {
                    ctx.writeAndFlush(MessageUtils.getErrorMessage("Destination already exists: " + e.getMessage()));
                }
                break;
            default:
                ctx.writeAndFlush(msg);
        }
    }
}
