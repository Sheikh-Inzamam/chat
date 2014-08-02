package com.rekoe.client.test;

import com.rekoe.msg.codec.GameMessageToMessageCodec;
import com.rekoe.msg.codec.MessageRecognizer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

public class MessageTest {
	private static final int PORT = 8888;
	private static final String HOST = "127.0.0.1";

	public void init() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
				private final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new GameMessageToMessageCodec(new MessageRecognizer()));
					pipeline.addLast("LOGGING_HANDLER", LOGGING_HANDLER);
				}
			});
			ChannelFuture f = b.connect(HOST, PORT).sync();
			Channel channel = f.channel();
			//(long uid, String leageID, int serverid, String name, String token, int vipLevel, int stageProperty, boolean isLock, int lockMinute)
			//GSTestRegisteMessage msg = new GSTestRegisteMessage(123, "1", 1, "abc", "token",10,2,false,1);
			GSLoginMessage msg = new GSLoginMessage("abc");
			channel.write(msg);
			channel.flush();
			channel.closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		MessageTest client = new MessageTest();
		client.init();
	}

}