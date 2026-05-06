module com.logandhillon {
    requires javafx.base;
    requires javafx.media;
    requires javafx.graphics;

    requires org.apache.logging.log4j.core;
    requires com.google.protobuf;
    requires javafx.controls;
    requires java.desktop;
    requires static lombok;

    opens com.logandhillon.fptgame to javafx.fxml;

    exports com.logandhillon.fptgame;
    exports com.logandhillon.fptgame.entity.ui;
    exports com.logandhillon.fptgame.scene;
    exports com.logandhillon.fptgame.networking;
    exports com.logandhillon.fptgame.networking.proto;
    exports com.logandhillon.fptgame.entity.ui.component;
    exports com.logandhillon.fptgame.scene.menu;
    exports com.logandhillon.fptgame.scene.component;

    exports com.logandhillon.logangamelib.engine;
    exports com.logandhillon.logangamelib.engine.disk;
    exports com.logandhillon.logangamelib.entity.physics;
    exports com.logandhillon.logangamelib.entity;
    exports com.logandhillon.logangamelib.entity.ui;
    exports com.logandhillon.logangamelib.resource;
}