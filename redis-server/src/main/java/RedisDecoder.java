import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class RedisDecoder extends ReplayingDecoder<Void> {
    private static final char STAR = '*';
    private static final char DOLLAR = '$';
    private static final char CR = '\r';
    private static final char LF = '\n';


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {

        System.out.printf(in.toString(CharsetUtil.UTF_8));

        if(in.readChar() == STAR) {
            int wordNumber = in.readInt();
            in.skipBytes(2);    //skip '\r\n'

            String[] command = new String[wordNumber];
            for (int i = 0; i < wordNumber; i++) {
                in.skipBytes(1);    //skip '$'
                int letterNumber = in.readInt();
                in.skipBytes(2);    //skip '\r\n'
                command[i] = String.valueOf(in.readBytes(letterNumber));
                in.skipBytes(2);    //skip '\r\n'
            }

            list.add(new Command(command));
        }
    }
}
