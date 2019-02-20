package com.hapex.electrostore.controller;

import com.hapex.electrostore.App;
import com.hapex.electrostore.entity.Item;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by barthap on 2019-02-18.
 */
public class DetailsPaneController extends NestedController implements Initializable {
    @FXML private Label nameLabel;
    @FXML private Label descLabel;
    @FXML private Label locationLabel;
    @FXML private Label quantityLabel;
    @FXML private Hyperlink wwwUrl;
    @FXML private Hyperlink googleUrl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLabel.setText("-");
        wwwUrl.setText("-");
        googleUrl.setText("-");
        quantityLabel.setText("-");
        locationLabel.setText("-");
        descLabel.setText("Select item to see details");
    }

    public void setItem(Item item) {
        nameLabel.setText(item.getName());
        quantityLabel.setText(Integer.toString(item.getQuantity()));
        descLabel.setText(item.getDescription() != null ? item.getDescription() : "No description specified");
        if(item.getLocation() != null)
            locationLabel.setText(item.getLocation().getFullName());
        else
            locationLabel.setText("N/A");

        wwwUrl.setText(item.getWebsite());
        googleUrl.setText("https://google.com/search?q=" + item.getName().replace(" ", "%20"));

        googleUrl.setVisited(false);
        wwwUrl.setVisited(false);
    }

    private void openBrowser(String url) {
        HostServicesDelegate hostServices = HostServicesFactory.getInstance(App.getInstance());
        hostServices.showDocument(url);
    }

    public void onGoogleClick(ActionEvent actionEvent) {
        openBrowser(googleUrl.getText());
    }

    public void onWwwClick(ActionEvent actionEvent) {
        openBrowser(wwwUrl.getText());
    }
}
