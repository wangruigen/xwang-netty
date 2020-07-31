package com.xiaowang.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class NettyClient {
    private String host;
    private Integer port;

    NettyClient(){
        this.host = "localhost";
        this.port = 9092;
    };

    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        //指定NioEventLoopGroup以处理客户端事件，需要适用于NIO的实现
        bootstrap.group(group)
                //适用于NIO传输的channel类型
                .channel(NioSocketChannel.class)
                //设置服务器的InetSocketAddress
                .remoteAddress(new InetSocketAddress(host,port))
                //在创建channel时，向 ChannelPipeline中添加一个 EchoClientHandler实例
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        try {
            //连接到远程节点，阻塞等待直到连接完成
            ChannelFuture channelFuture = bootstrap.connect().sync();
            //阻塞，直到Channel 关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //关闭线程池并且释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NettyClient nettyClient = new NettyClient();
        nettyClient.start();
    }
}
