package com.xiaowang.netty.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

//Sharable表示一个ChannelHandler可以被多个 Channel 安全地共享
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收消息时调用
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("服务端接收到消息：" + in.toString(Charset.forName("UTF-8")));
    }

    /**
     * channel read消费完读取的数据的时候被触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将未解决决消息冲刷到远程节点，并且关闭该 Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        //打印异常栈跟踪
        cause.printStackTrace();
        //关闭该Channel
        ctx.close();
    }
}
