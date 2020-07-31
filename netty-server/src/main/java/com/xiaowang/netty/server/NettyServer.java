package com.xiaowang.netty.server;

import com.xiaowang.netty.handle.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {

    private String host;
    private Integer port;

    NettyServer(){
        this.host = "localhost";
        this.port = 9092;
    };

    NettyServer(String host, Integer port){
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        final ServerHandler serverHandler = new ServerHandler();
        //创建NioEventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                //指定所使用的NIOc传输Channel
                .channel(NioServerSocketChannel.class)
                .localAddress(host,port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //EchoServerHandler 被标注为@Shareable，所以我们可以总是使用同样的实例
                        //这里对于所有的客户端连接来说，都会使用同一个 ServerHandler，因为其被标注为@Sharable，
                        socketChannel.pipeline().addLast(serverHandler);
                    }
                });
        try {
            //绑定服务器，调用sync()方法阻塞等待直到绑定完成
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("服务端启动成功！"+future.channel().localAddress());
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //，该应用程序将会阻塞等待直到服务器的 Channel 关闭（因为你在 Channel 的 CloseFuture 上调用了 sync()方法）。然后，你将可以关闭 EventLoopGroup，并释放所有的资源，包括所有被创建的线程
            group.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }
}
