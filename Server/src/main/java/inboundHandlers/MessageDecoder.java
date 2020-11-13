package inboundHandlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import messages.MessageUtils;

import java.util.List;

public class MessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int len = in.readInt();
        byte[] bytes = new byte[len];
        in.resetReaderIndex();
        in.readBytes(bytes);
        out.add(MessageUtils.getMessageFromBytes(bytes));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("read complete");
    }
}
