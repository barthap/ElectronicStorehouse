package com.hapex.electrostore;

import com.hapex.electrostore.util.FXMLFactory;
import com.hapex.electrostore.util.database.HibernateUtil;
import com.hapex.electrostore.util.di.ApplicationContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {
    private static Stage mainStage;
    private static ApplicationContext context;
    private static App instance;

    public static ApplicationContext getContext() {
        return context;
    }
    public static Stage getMainStage() {
        return mainStage;
    }
    public static App getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        log.trace("Bootstrapping");
        context = new ApplicationContext();
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLFactory.load("/layout/app.fxml");

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Electronic Storehouse");
        primaryStage.onCloseRequestProperty().setValue(e -> Platform.exit());
        primaryStage.show();
        mainStage = primaryStage;
    }

    @Override
    public void stop() throws Exception {
        HibernateUtil.shutdown();
        super.stop();
    }
}
