package com.acezhhh.tool.utils;

import de.felixroske.jfxsupport.GUIState;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * @author acezhhh
 * @date 2022/4/21
 */
public class AlertUtil {

    public static void showConfirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, new ButtonType("确定", ButtonBar.ButtonData.YES));
        alert.setHeaderText(null);
        alert.initOwner(GUIState.getStage());
        alert.showAndWait();
    }
}
