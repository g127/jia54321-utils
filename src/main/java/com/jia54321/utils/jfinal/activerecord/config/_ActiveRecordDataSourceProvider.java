package com.jia54321.utils.jfinal.activerecord.config;

import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;

public class _ActiveRecordDataSourceProvider implements IDataSourceProvider {
    DataSource dataSource;

    public _ActiveRecordDataSourceProvider setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

}
