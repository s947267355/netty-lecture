package com.wutao.netty.example04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 连接空闲状态 IdleStateHandler
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

            ChannelFuture channelFuture = bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 空闲状态处理器
                            pipeline.addLast(new IdleStateHandler(20, 10, 15, TimeUnit.SECONDS));
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {

                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    if (evt instanceof IdleStateEvent) {
                                        IdleStateEvent event = (IdleStateEvent) evt;

                                        String eventMsg = "";

                                        switch (event.state()){
                                            case READER_IDLE:
                                                eventMsg = "读空闲";
                                                break;
                                            case WRITER_IDLE:
                                                eventMsg = "写空闲";
                                                break;
                                            case ALL_IDLE:
                                                eventMsg = "读写空闲";
                                                break;
                                            default:
                                                break;
                                        }

                                        System.out.println(ctx.channel().remoteAddress() + "超时: "+eventMsg);
                                    }
                                }
                            });

                        }
                    })
                    .bind(8899)
                    .sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
