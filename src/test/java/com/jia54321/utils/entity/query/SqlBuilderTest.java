package com.jia54321.utils.entity.query;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SqlBuilderTest {

    private ITableDesc tableDesc = CrudTableDesc.SYS_USER;

    @Before
    public void setUp() throws Exception {
        System.out.println("");
    }

    @Test
    public void buildInsertSql() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        CrudTableDesc table = new CrudTableDesc();
        table.setTableDesc(tableDesc);

        SqlContext test =  sqlBuilder.buildInsertSql(table);

        System.out.println(test);
    }

    @Test
    public void buildUpdateSql() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        CrudTableDesc table = new CrudTableDesc();
        table.setTableDesc(tableDesc);
        table.getColumnProps().put(table.getTableDesc().getTypePkName(), "2");

        SqlContext test =  sqlBuilder.buildUpdateSql(table);

//        table.getColumnProps().clear();

        System.out.println(test);
    }

    @Test
    public void buildDeleteSql() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        CrudTableDesc table = new CrudTableDesc();
        table.setTableDesc(tableDesc);

        SqlContext test =  sqlBuilder.buildDeleteSql(table);

        System.out.println(test);
    }

    @Test
    public void buildGetSql() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        CrudTableDesc table = new CrudTableDesc();
        table.setTableDesc(tableDesc);

        SqlContext test =  sqlBuilder.buildGetSql(table);

        System.out.println(test);
    }

    @Test
    public void buildQueryCondition() {
    }

    @Test
    public void buildQueryOperation() {
    }

    @Test
    public void buildQuerySQL() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        CrudTableDesc table = new CrudTableDesc();
        table.setTableDesc(tableDesc);

        SqlContext test =  sqlBuilder.buildQuerySQL(table, " WHERE     session_id = ?   ", new ArrayList<>(0), true);
        System.out.println(test);

        test =  sqlBuilder.buildQuerySQL(table, " ORDER BY  create_time desc ", new ArrayList<>(0), true);
        System.out.println(test);

        test =  sqlBuilder.buildQuerySQL(table, " GROUP BY  create_time  ", new ArrayList<>(0), true);
        System.out.println(test);

        test =  sqlBuilder.buildQuerySQL(table, "WHERE  session_id = ?  ORDER BY  create_time desc ", new ArrayList<>(0), true);
        System.out.println(test);

        test =  sqlBuilder.buildQuerySQL(table, "GROUP BY  create_time  ORDER BY  create_time desc ", new ArrayList<>(0), true);
        System.out.println(test);

        test =  sqlBuilder.buildQuerySQL(table, "WHERE     session_id = ?  GROUP BY  create_time  ORDER BY  create_time desc ", new ArrayList<>(0), true);
        System.out.println(test);


        CrudTableDesc table2 = new CrudTableDesc();
        table2.setTableDesc(CrudTableDesc.SYS_ENTITY_TYPE);
        System.out.println(CrudTableDesc.SYS_ENTITY_TYPE);
        test =  sqlBuilder.buildQuerySQL(table2, "", new ArrayList<>(0), true);

        System.out.println(test);


    }
}
