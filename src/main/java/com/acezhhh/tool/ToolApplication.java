package com.acezhhh.tool;

import com.acezhhh.tool.view.IndexView;
import com.acezhhh.tool.view.StartView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ToolApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(ToolApplication.class, IndexView.class, new StartView(), args);
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.setTitle("mysql工具");
    }

}
