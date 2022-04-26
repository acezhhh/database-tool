package com.acezhhh.tool.controller;

import cn.hutool.core.util.StrUtil;
import com.acezhhh.tool.ToolApplication;
import com.acezhhh.tool.utils.AlertUtil;
import com.acezhhh.tool.utils.DataSourceUtil;
import com.acezhhh.tool.view.GenerateView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author acezhhh
 * @date 2022/4/20
 */
@FXMLController
public class IndexController implements Initializable {

    @FXML
    private TextField url;

    @FXML
    private TextField dataBase;

    @FXML
    private TextField port;

    @FXML
    private PasswordField passWord;

    @FXML
    private TextField userName;

    @FXML
    private AnchorPane loadingPane;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Autowired
    private GenerateController generateController;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GUIState.getStage().sizeToScene();
        //初始化隐藏loading组件
        loadingPane.setVisible(false);
        dataBase.setText("test");
        url.setText("localhost");
        port.setText("3306");
        userName.setText("root");
    }

    @FXML
    void connect(ActionEvent event) {
        if (verification()) {
            return;
        }
        loadingPane.setVisible(true);
        //异步连接数据库否则主程序会卡住
        CompletableFuture.runAsync(() -> {
            boolean isConnect = DataSourceUtil.connect(
                    buildUrl(),
                    dataBase.getText(),
                    userName.getText(),
                    passWord.getText()
            );
            loadingPane.setVisible(false);
            if (!isConnect) {
                Platform.runLater(() -> {
                    AlertUtil.showConfirm("数据库连接失败,请检查配置!");
                });
                return;
            }
            //异步线程需要使用Platform.runLater来切换界面
            Platform.runLater(() -> {
                ToolApplication.showView(GenerateView.class);
                generateController.initData();
            });
        }, executor);
    }

    private boolean verification() {
        if (StrUtil.isBlank(url.getText())) {
            AlertUtil.showConfirm("地址不能为空");
            return true;
        }
        if (StrUtil.isBlank(port.getText())) {
            AlertUtil.showConfirm("端口不能为空");
            return true;
        }
        if (StrUtil.isBlank(dataBase.getText())) {
            AlertUtil.showConfirm("数据库不能为空");
            return true;
        }
        if (StrUtil.isBlank(userName.getText())) {
            AlertUtil.showConfirm("账号不能为空");
            return true;
        }
        if (StrUtil.isBlank(passWord.getText())) {
            AlertUtil.showConfirm("密码不能为空");
            return true;
        }
        return false;
    }

    private String buildUrl() {
        return String.format(jdbcUrl, url.getText(), port.getText(), dataBase.getText());
    }

}
