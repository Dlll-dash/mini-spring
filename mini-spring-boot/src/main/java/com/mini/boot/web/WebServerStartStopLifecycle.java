package com.mini.boot.web;

import com.mini.spring.Lifecycle;
import com.sun.net.httpserver.HttpServer;

public class WebServerStartStopLifecycle implements Lifecycle {
    private HttpServer server;

    WebServerStartStopLifecycle(HttpServer server){
        this.server=server;
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop(){
        //不等在途请求
        server.stop(0);
    }
}
