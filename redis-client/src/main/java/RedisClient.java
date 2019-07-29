import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Scanner;

public class RedisClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 10000;

    public static void main(String[] args) throws InterruptedException {

        Bootstrap b = new Bootstrap();

        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new DisplayResponseHandler())
                                .addLast(new DisplaySendHandler());
                    }
                });


        Channel ch = b.connect(HOST, PORT).sync().channel();

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            String input = scanner.nextLine();

            ChannelFuture future = ch.writeAndFlush(input);

            future.addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        System.out.printf("Write failed");
                        channelFuture.cause().printStackTrace(System.err);
                    }
                }
            });
        }
    }

}
