package com.acezhhh.tool.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.acezhhh.tool.ToolApplication;
import com.acezhhh.tool.service.MysqlService;
import com.acezhhh.tool.utils.AlertUtil;
import com.acezhhh.tool.utils.CellFactory;
import com.acezhhh.tool.utils.CreateWordUtil;
import com.acezhhh.tool.utils.DataSourceUtil;
import com.acezhhh.tool.view.IndexView;
import com.acezhhh.tool.vo.ColumnVo;
import com.acezhhh.tool.vo.ExportVo;
import com.acezhhh.tool.vo.TableViewVo;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author acezhhh
 * @date 2022/4/20
 */
@FXMLController
public class GenerateController implements Initializable {

    @FXML
    private TableView<TableViewVo> tableView;

    @Autowired
    private MysqlService mysqlService;

    @FXML
    private TextField text;

    @FXML
    private AnchorPane loadingPane;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GUIState.getStage().sizeToScene();
        loadingPane.setVisible(false);
        buildColumn();
        //搜索输入
        text.textProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> scrollTable());
    }

    /**
     * 初始化加载数据
     */
    public void initData() {
        initList();
    }

    /**
     * 滚动表格
     */
    private void scrollTable() {
        CompletableFuture.runAsync(() -> {
            String searchText = text.getText();
            int tableIndex = 0;
            List<TableViewVo> tableViewVoList = tableView.getItems();
            for (int i = 0; i < tableViewVoList.size(); i++) {
                TableViewVo item = tableViewVoList.get(i);
                if (item.getTableName().contains(searchText)) {
                    tableIndex = i;
                    break;
                }
            }
            int scrollIndex = tableIndex;
            Platform.runLater(() -> tableView.scrollTo(scrollIndex));
        }, executor);
    }

    /**
     * 构建列表数据
     */
    private void initList() {
        List<TableViewVo> rows = mysqlService.findTableList();
        tableView.setItems(FXCollections.observableArrayList(rows));
        tableView.setEditable(true);
    }

    /**
     * 构建tableView列
     */
    private void buildColumn() {
        TableColumn<TableViewVo, Boolean> selectedColumn = new TableColumn<TableViewVo, Boolean>();
        selectedColumn.setCellValueFactory(new PropertyValueFactory<TableViewVo, Boolean>("selected"));
        selectedColumn.setCellFactory(
                CellFactory.tableCheckBoxColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(Integer index) {
                        final TableViewVo g = (TableViewVo) tableView.getItems().get(index);
                        ObservableValue<Boolean> ret =
                                new SimpleBooleanProperty(g, "selected", g.getSelected());
                        ret.addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends Boolean> observable,
                                    Boolean oldValue, Boolean newValue) {
                                g.setSelected(newValue);
                            }
                        });
                        return ret;
                    }
                }));
        TableColumn tableNameColumn = new TableColumn("表名");
        tableNameColumn.setPrefWidth(320);
        tableNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("tableName"));
        TableColumn commentColumn = new TableColumn("备注");
        commentColumn.setPrefWidth(320);
        commentColumn.setCellValueFactory(
                new PropertyValueFactory<>("tableComment"));
        tableView.getColumns().addAll(selectedColumn, tableNameColumn, commentColumn);
    }

    /**
     * 全选/反选
     *
     * @param event
     */
    @FXML
    void batchSelect(ActionEvent event) {
        Button button = (Button) event.getSource();
        boolean selected = StrUtil.equals("全选", button.getText());
        List<TableViewVo> list = tableView.getItems().stream()
                .peek(item -> item.setSelected(selected))
                .collect(Collectors.toList());
        tableView.setItems(FXCollections.observableArrayList(list));
        tableView.getColumns().get(0).setVisible(false);
        tableView.getColumns().get(0).setVisible(true);
    }

    /**
     * 返回
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    void returnPage(ActionEvent event) throws SQLException {
        DataSourceUtil.close();
        ToolApplication.showView(IndexView.class);
    }

    /**
     * 导出word
     *
     * @param event
     */
    @FXML
    void exportWord(ActionEvent event) {
        List<ExportVo> dataList = buildExportVo();
        if (dataList == null) {
            AlertUtil.showConfirm("未选中表");
            return;
        }
        //打开保存文件窗口
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存文件");
        fileChooser.setInitialFileName("数据库文档.doc");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("word Files", "*.doc", "*.docx"));
        File file = fileChooser.showSaveDialog(GUIState.getStage());
        if (file == null) {
            return;
        }
        //导出
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("dataList", dataList);
        CreateWordUtil.createWord(file.getAbsolutePath(), "database_doc.ftl", dataMap);
    }

    /**
     * 构建导出数据
     *
     * @return
     */
    private List<ExportVo> buildExportVo() {
        //获取选中表名
        List<TableViewVo> tableVoList = tableView.getItems().stream()
                .filter(TableViewVo::getSelected)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(tableVoList)) {
            return null;
        }
        loadingPane.setVisible(true);
        List<String> tableNames = tableVoList.stream().map(TableViewVo::getTableName).collect(Collectors.toList());
        List<ColumnVo> columnList = mysqlService.findTableInfo(tableNames);
        //构建导出数据
        List<ExportVo> exportVoList = tableVoList.stream().map(table -> {
            List<ColumnVo> columnVos = columnList.stream()
                    .filter(column -> StrUtil.equals(column.getTableName(), table.getTableName()))
                    .collect(Collectors.toList());
            return new ExportVo(table.getTableName(), table.getTableComment(), columnVos);
        }).collect(Collectors.toList());
        loadingPane.setVisible(false);
        return exportVoList;
    }

}
