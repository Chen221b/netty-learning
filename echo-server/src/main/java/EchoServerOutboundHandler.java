import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EchoServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("Outbound: Before write");
        ctx.write(msg, promise);
        System.out.println("Outbound: After write");
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Outbound: Before flush");
        ctx.flush();
        System.out.println("Outbound: After flush");
    }
}
