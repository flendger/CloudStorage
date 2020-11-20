package inboundHandlers;

import files.FileTransferRecord;
import files.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.MessageUtils;
import messages.dataTransfer.DataTransferMessage;
import services.UserProfile;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;


public class ServerInboundDataTransferHandler extends SimpleChannelInboundHandler<DataTransferMessage> {
    private final UserProfile userProfile;
    private final ConcurrentHashMap<Integer, FileTransferRecord> incomingFiles;

    public ServerInboundDataTransferHandler(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.incomingFiles = new ConcurrentHashMap<>();
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

        //isFirst -> create tmp file -> add record to active file list
            //record:
                // fileId - msg.fileId = Key
                // fullTmpName - "tmp" + fileId + generateId
                // destDir - curDir
                // fileName - msg.fileName
        if (msg.isFirst()) {
            String curDir = Path.of(userProfile.curDir).toAbsolutePath().toString();
            String tmp = FileUtils.createTmpFile(msg.getFileId(), curDir);

            incomingFiles.put(msg.getFileId(),
                    new FileTransferRecord(msg.getFileId(), msg.getFileName(), tmp, curDir));
        }

        //is file in list -> write part or ignore
        FileTransferRecord rec = incomingFiles.get(msg.getFileId());
        if (rec == null) {
            return;
        }
        FileUtils.writeDataToFile(rec.tmpFileName, msg.getData());

        //EOF -> rename file + remove from list or ignore
        if (! msg.isEOF()) {
            return;
        }
        incomingFiles.remove(rec.fileId);
        FileUtils.renameTmpToFile(rec.tmpFileName, rec.dir, rec.fileName);
        ctx.writeAndFlush(MessageUtils.getOKMessage(String.format("File %s has transferred", rec.fileName)));
    }
}
