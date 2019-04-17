package com.wutao.netty.example05;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.time.LocalDateTime;

/**
 * webSocket hello world
 *
 * @author tao.wu
 * @date 2019-04-16
 */
public class MyServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // http 编解码
                            pipeline.addLast(new HttpServerCodec());
                            // 分块写处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            // 对分块消息聚合处理器 聚合成FullHttpRequest or FullHttpResponse
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            // 这个处理程序为您运行websocket服务器做了所有繁重的工作。它负责websocket握手以及控制框架的处理（Close，Ping，Pong）。
                            // Text和Binary数据帧将传递给管道中的下一个处理程序（由您实现）进行处理。
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

                            pipeline.addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
                                    System.out.println("收到消息："+ msg.text());
                                    ctx.writeAndFlush(new TextWebSocketFrame("已接收到消息,time:" + LocalDateTime.now()));
                                }

                                @Override
                                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("handlerAdded:" + ctx.channel().id().asLongText());
                                }

                                @Override
                                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("handlerRemoved:" + ctx.channel().id().asLongText());
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.out.println("异常："+cause.getMessage());
                                    ctx.close();
                                }
                            });
                        }
                    })
                    .bind(8899)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
