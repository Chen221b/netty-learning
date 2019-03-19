import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.RandomAccessFile;
import java.net.URISyntaxException;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String WSURI;
    private static String INDEX;

    //如果请求的URL中有WSRUI会把该协议升级为 WebSocket
    public HttpRequestHandler(String wsUri) {
        WSURI = wsUri;
    }

    //index.html的运行时路径
    static {
        try {
            INDEX = HttpRequestHandler.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI() + "index.html";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(WSURI.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request);
        } else {
            if (WSURI.equalsIgnoreCase(request.getUri())) {
                ctx.fireChannelRead(request.retain());
            } else {
                if (HttpHeaders.is100ContinueExpected(request)) {
                    send100Continue(ctx);
                }
                RandomAccessFile file = new RandomAccessFile(INDEX, "r");
                HttpResponse response = new DefaultHttpResponse(
                        request.getProtocolVersion(), HttpResponseStatus.OK);
                response.headers().set(
                        HttpHeaders.Names.CONTENT_TYPE,
                        "text/html; charset=UTF-8");
                boolean keepAlive = HttpHeaders.isKeepAlive(request);
                if (keepAlive) {
                    response.headers().set(
                            HttpHeaders.Names.CONTENT_LENGTH, file.length());
                    response.headers().set( HttpHeaders.Names.CONNECTION,
                            HttpHeaders.Values.KEEP_ALIVE);
                }
                ctx.write(response);
                if (ctx.pipeline().get(SslHandler.class) == null) {
                    ctx.write(new DefaultFileRegion(
                            file.getChannel(), 0, file.length()));
                } else {
                    ctx.write(new ChunkedNioFile(file.getChannel()));
                }
                ChannelFuture future = ctx.writeAndFlush(
                        LastHttpContent.EMPTY_LAST_CONTENT);
                if (!keepAlive) {
                    future.addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
