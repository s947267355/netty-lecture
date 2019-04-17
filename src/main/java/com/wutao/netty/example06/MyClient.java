package com.wutao.netty.example06;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Random;

/**
 * @author tao.wu
 * @date 2019-04-17
 */
public class MyClient {
    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture channelFuture = bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(MyDataInfo.MyMessage.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new SimpleChannelInboundHandler<MyDataInfo.MyMessage>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {

                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    int randomInt = new Random().nextInt(3);

                                    MyDataInfo.MyMessage myMessage = null;

                                    switch (randomInt) {
                                        case 0:
                                            myMessage = MyDataInfo.MyMessage.newBuilder()
                                                    .setDataType(MyDataInfo.MyMessage.DataType.PersonType)
                                                    .setPerson(MyDataInfo.Person.newBuilder()
                                                            .setName("张三")
                                                            .setAge(26)
                                                            .setAddress("宝安"))
                                                    .build();
                                            break;
                                        case 1:
                                            myMessage = MyDataInfo.MyMessage.newBuilder()
                                                    .setDataType(MyDataInfo.MyMessage.DataType.DogType)
                                                    .setDog(MyDataInfo.Dog.newBuilder()
                                                            .setName("小狗")
                                                            .setAge(1))
                                                    .build();
                                            break;
                                        case 2:
                                            myMessage = MyDataInfo.MyMessage.newBuilder()
                                                    .setDataType(MyDataInfo.MyMessage.DataType.CatType)
                                                    .setCat(MyDataInfo.Cat.newBuilder()
                                                            .setName("小猫")
                                                            .setCity("深圳"))
                                                    .build();
                                            break;
                                        default:
                                            break;
                                    }

                                    ctx.writeAndFlush(myMessage);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    })
                    .connect("localhost", 8899)
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
