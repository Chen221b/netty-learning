import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Main {

    private static int port = 10002;

    public static void main(String[] args) throws InterruptedException {

        final EchoServerInboundHandler inboundHandler = new EchoServerInboundHandler();
        final EchoServerOutboundHandler outboundHandler = new EchoServerOutboundHandler();

        final EchoServerInboundHandler inboundHandler2 = new EchoServerInboundHandler();

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(inboundHandler).addLast(outboundHandler).addLast(inboundHandler2);
                        }
                    });

            ChannelFuture future = b.bind().sync();
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }

    }
}
