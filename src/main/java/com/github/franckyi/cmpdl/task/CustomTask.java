package com.github.franckyi.cmpdl.task;

import com.github.franckyi.cmpdl.CMPDL;
import com.github.franckyi.cmpdl.InterfaceController;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

abstract class CustomTask<V> extends Task<V> {

    @Override
    protected V call() throws Exception {
        try {
            return call0();
        } catch (Exception e) {
            e.printStackTrace();
            CMPDL.exceptions.add(e);
            CMPDL.controller.trace(e);
        }
        return null;
    }

    protected abstract V call0() throws Exception;

    void log(String s) {
        Platform.runLater(() -> getController().log(s));
    }

    void trace(Throwable t) {
        Platform.runLater(() -> getController().trace(t));
    }

    InterfaceController getController() {
        return CMPDL.controller;
    }

    String crawl(String url) throws IOException, IllegalArgumentException {
        String location = getLocation(url);
        return location != null ? crawl(location) : url;
    }

    String crawlAddHost(String url) throws IOException, IllegalArgumentException {
        URL url0 = new URL(url);
        String location = getLocation(url);
        return location != null ? location.contains("://") ? crawlAddHost(location) : crawl(url0.getProtocol() + "://" + url0.getHost() + location) : url;
    }

    private String getLocation(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.getInputStream();
        return connection.getHeaderField("location");
    }
}
