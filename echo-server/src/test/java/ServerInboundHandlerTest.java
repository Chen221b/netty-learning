import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

public class ServerInboundHandlerTest {

    @Test
    public void inboundHandlerTest() {
        EmbeddedChannel channel = new EmbeddedChannel(new EchoServerInboundHandler());

        ByteBuf msg = Unpooled.copiedBuffer("test message", CharsetUtil.UTF_8);

        channel.writeInbound(msg);
    }

}
