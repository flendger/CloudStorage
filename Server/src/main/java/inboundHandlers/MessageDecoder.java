package inboundHandlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import messages.Message;

import java.util.List;

public class MessageDecoder extends ReplayingDecoder<Message> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        long len = in.readLong();
        byte[] bytes = new byte[(int) (len)];
        in.resetReaderIndex();
        in.readBytes(bytes);
        out.add(Message.of(bytes));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("read complete");
    }
}
