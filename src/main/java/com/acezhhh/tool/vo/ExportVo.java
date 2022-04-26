package com.acezhhh.tool.vo;

import java.util.List;

/**
 * @author acezhhh
 * @date 2022/4/21
 */
public class ExportVo {

    private String tableName;

    private String tableComment;

    private List<ColumnVo> columnVoList;

    public ExportVo() {
    }

    public ExportVo(String tableName, String tableComment, List<ColumnVo> columnVoList) {
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.columnVoList = columnVoList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public List<ColumnVo> getColumnVoList() {
        return columnVoList;
    }

    public void setColumnVoList(List<ColumnVo> columnVoList) {
        this.columnVoList = columnVoList;
    }
}
