module cloud.client {
    requires cloud.core;
    requires io.netty.all;

    exports clientcore.inboundHandlers;
    exports clientcore.outboundHandlers;
    exports clientcore;
}