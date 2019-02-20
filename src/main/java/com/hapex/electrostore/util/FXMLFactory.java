package com.hapex.electrostore.util;

import com.hapex.electrostore.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by barthap on 2019-02-19.
 */
public class FXMLFactory {
    public static Parent load(String fxmlPath) throws IOException {
        return getLoader(fxmlPath).load();
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        Locale locale = new Locale("pl", "PL");

        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        loader.setResources(App.getLocaleBundle());
        loader.setControllerFactory(type -> App.getContext().injectBeans(type));
        return loader;
    }
}
