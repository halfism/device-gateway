package com.device.gateway.protocol.impl;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.config.ProtocolConfig;
import com.device.gateway.protocol.AbstractProtocolHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import java.util.concurrent.CompletableFuture;

/**
 * TCP协议处理器实现
 */
public class TcpProtocolHandler extends AbstractProtocolHandler {
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private ProtocolConfig config;
    
    @Override
    protected void doInitialize(ProtocolConfig config) {
        this.config = config;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast(new TcpServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            this.serverChannel = bootstrap.bind(config.getHost(), config.getPort()).sync().channel();
            
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to initialize TCP server", e);
        }
    }
    
    @Override
    public CompletableFuture<Message> send(Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        
        // 在实际实现中，这里会将消息发送到对应的TCP连接
        // 由于TCP是面向连接的协议，我们需要找到目标设备的连接
        
        // 模拟发送成功
        future.complete(message);
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> connect(Device device) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        // TCP是服务端监听模式，不需要主动连接设备
        future.complete(true);
        return future;
    }
    
    @Override
    protected void doDisconnect(Device device) {
        // 对于TCP服务器，这里是断开特定客户端连接的逻辑
        // 实际实现中需要维护客户端连接映射
    }
    
    @Override
    public String getProtocolType() {
        return "tcp";
    }
    
    /**
     * TCP服务器处理器
     */
    private class TcpServerHandler extends ChannelInboundHandlerAdapter {
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("TCP client connected: " + ctx.channel().remoteAddress());
            super.channelActive(ctx);
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("TCP client disconnected: " + ctx.channel().remoteAddress());
            super.channelInactive(ctx);
        }
        
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            String messageStr = byteBuf.toString(CharsetUtil.UTF_8);
            
            // 创建消息对象并处理
            Message message = Message.builder()
                    .deviceId(getDeviceIdFromContext(ctx)) // 这里需要从上下文或其他方式获取设备ID
                    .payload(messageStr.getBytes())
                    .build();
            
            handleMessage(message, ctx);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("TCP handler error: " + cause.getMessage());
            ctx.close();
        }
        
        private String getDeviceIdFromContext(ChannelHandlerContext ctx) {
            // 在实际实现中，这里应该从连接上下文或其他方式获取设备ID
            // 可能需要维护连接到设备ID的映射关系
            return ctx.channel().id().asLongText().substring(0, 16);
        }
        
        private void handleMessage(Message message, ChannelHandlerContext ctx) {
            // 处理接收到的消息
            System.out.println("Received TCP message from device: " + message.getDeviceId());
            // 在实际实现中，这里会将消息路由到相应的处理器
        }
    }
}