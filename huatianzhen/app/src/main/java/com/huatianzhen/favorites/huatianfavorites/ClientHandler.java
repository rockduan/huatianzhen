package com.huatianzhen.favorites.huatianfavorites;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by duanhongxiu on 2016/11/1.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private int counter;
    private byte[] req;

    public ClientHandler() {
        req = ("QUERY TIME ORDER"+ System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ByteBuf message = null;
        logger.info("begin write 100 msg");
        for(int i = 0;i < 100;i++)
        {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        String body = (String)msg;
        counter++;
        logger.info("Now is :"+body+"; the counter is:"+counter);
        System.out.println("Now is :" + body + "; the counter is:" + counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        logger.warning("Unexpected exception from downstream : "+ cause.getMessage());
        ctx.close();
    }
}
