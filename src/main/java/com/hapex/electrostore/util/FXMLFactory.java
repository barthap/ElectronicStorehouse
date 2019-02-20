package com.hapex.electrostore.util;

import com.hapex.electrostore.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Created by barthap on 2019-02-19.
 */
public class FXMLFactory {
    public static Parent load(String fxmlPath) throws IOException {
        return getLoader(fxmlPath).load();
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        loader.setControllerFactory(type -> App.getContext().injectBeans(type));
        return loader;
    }
}
