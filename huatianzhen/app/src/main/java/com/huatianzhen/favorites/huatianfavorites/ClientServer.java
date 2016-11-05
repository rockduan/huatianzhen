package com.huatianzhen.favorites.huatianfavorites;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by duanhongxiu on 2016/11/1.
 */
public class ClientServer extends Service {
    private String TAG = "huatian";

    public void connect(int port, String host) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).
                    channel(NioSocketChannel.class).
                    option(ChannelOption.SO_KEEPALIVE, true).
                    handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //LineBasedFrameDecoder 用于解决 TCP粘包问题
                            ch.pipeline().addLast("LineBasedFrameDecoder",new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast("StringEncoder", new StringEncoder());
                            ch.pipeline().addLast("StringDecoder", new StringDecoder());
                            ch.pipeline().addLast("ClientHandler", new ClientHandler());
                        }
                    });
            Log.d(TAG, "connect begin");
            ChannelFuture f = b.connect(host, port).sync();
            Log.d(TAG, "wait connect close");
            f.channel().closeFuture().sync();
            Log.d(TAG, "closed!!!!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("huatianzhen", "ClientServer onCreate begin");
        super.onCreate();


        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                int port = 8081;
                //String host = "120.76.22.172";
                String host = "192.168.1.103";
                try {
                    Log.d("huatianzhen", "connect to " + host + ":" + port);
                    new ClientServer().connect(port, host);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();


    }
}
