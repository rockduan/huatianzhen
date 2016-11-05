package com.huatianzhen.server.favorites;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TimeServer {
	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(
					NioServerSocketChannel.class).option(
					ChannelOption.SO_BACKLOG, 128).childOption(
					ChannelOption.SO_KEEPALIVE, true).childHandler(
					new ChildChannelHandler());
			System.out.println("begin to bind.sync");
			// 绑定端口同步等待成功
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			// TODO Auto-generated method stub
			 arg0.pipeline().addLast("LineBasedFrameDecoder",new LineBasedFrameDecoder(1024));
			 arg0.pipeline().addLast("StringDecoder",new StringDecoder());
			 arg0.pipeline().addLast("StringEncoder",new StringEncoder());

			arg0.pipeline().addLast("TimeServerHandler",new TimeServerHandler());
		}

	}

	public static void main(String[] args) throws Exception {
		int port = 8081;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println("begin to bind port server:" + port);
		new TimeServer().bind(port);
	}
}
