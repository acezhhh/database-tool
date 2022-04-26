package com.acezhhh.tool.service;

import cn.hutool.core.bean.BeanUtil;
import com.acezhhh.tool.utils.DataSourceUtil;
import com.acezhhh.tool.vo.ColumnVo;
import com.acezhhh.tool.vo.TableViewVo;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author acezhhh
 * @date 2022/4/21
 */
@Service
public class MysqlService {

    /**
     * 查询表信息
     *
     * @return
     */
    public List<TableViewVo> findTableList() {
        NamedParameterJdbcTemplate jdbcTemplate = DataSourceUtil.getJdbcTemplate();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT table_name tableName, ");
        sql.append(" TABLE_COMMENT tableComment ");
        sql.append(" FROM information_schema.tables ");
        sql.append(" WHERE TABLE_SCHEMA = :tableSchema ");
        sql.append(" and TABLE_TYPE = 'BASE TABLE' ");
        Map<String, Object> params = new HashMap<>();
        params.put("tableSchema", DataSourceUtil.dataBase);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString(), params);
        return mapList.stream()
                .map(row -> BeanUtil.fillBeanWithMap(row, new TableViewVo(), false))
                .collect(Collectors.toList());
    }

    /**
     * 根据表名查询字段信息
     *
     * @return
     */
    public List<ColumnVo> findTableInfo(List<String> tableNames) {
        NamedParameterJdbcTemplate jdbcTemplate = DataSourceUtil.getJdbcTemplate();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT TABLE_NAME tableName, COLUMN_NAME columnName, ");
        sql.append(" CASE WHEN COLUMN_KEY='PRI' THEN '主键' ELSE COLUMN_COMMENT END columnComment, ");
        sql.append(" CASE WHEN IS_NULLABLE='YES' THEN '是' ELSE '否' END nullAble, ");
        sql.append(" COLUMN_TYPE columnType ");
        sql.append(" FROM information_schema.COLUMNS ");
        sql.append(" WHERE TABLE_NAME in(:tableNames) ");
        sql.append(" AND TABLE_SCHEMA = :tableSchema ");
        Map<String, Object> params = new HashMap<>();
        params.put("tableNames", tableNames);
        params.put("tableSchema", DataSourceUtil.dataBase);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString(), params);
        return mapList.stream()
                .map(row -> BeanUtil.fillBeanWithMap(row, new ColumnVo(), false))
                .collect(Collectors.toList());
    }
}
