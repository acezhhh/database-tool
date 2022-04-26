package com.acezhhh.tool.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author acezhhh
 * @date 2022/4/20
 */
@FXMLController
public class LoadingController implements Initializable {

    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Image image = null;
//        try {
//            File file = ResourceUtils.getFile("classpath:images/loading.gif");
//            image = new Image(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        imageView.setImage(image);
    }

}
