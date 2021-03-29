package com.jia54321.utils.entity.rowmapper;

import com.jia54321.utils.entity.query.ITableDesc;
import org.easymock.EasyMock;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class AbstractRowMapperTest {

    @Test
    public void mapRow() throws SQLException {
        AbstractRowMapper<ITableDesc> mapper  = new AbstractRowMapper<ITableDesc>() {
        };
        //
        ResultSetMetaData metaData = (ResultSetMetaData) EasyMock.createMock(ResultSetMetaData.class);
        EasyMock.expect(metaData.getColumnCount()).andReturn(7).anyTimes();   //期望使用参数
        EasyMock.expect(metaData.getColumnName(1)).andReturn("TYPE_ID").anyTimes();   //期望使用参数
        EasyMock.expect(metaData.getColumnName(2)).andReturn("TYPE_ALIAS_ID").anyTimes();   //期望使用参数
        EasyMock.expect(metaData.getColumnName(3)).andReturn("TYPE_MK").anyTimes();   //期望使用参数
        EasyMock.expect(metaData.getColumnName(4)).andReturn("TYPE_ENTITY_NAME").anyTimes();   //期望使用参数
        EasyMock.expect(metaData.getColumnName(5)).andReturn("TYPE_DISPLAY_NAME").anyTimes();   //期望使用参数
        EasyMock.expect(metaData.getColumnName(6)).andReturn("TYPE_PK_NAME").anyTimes();   //期望使用参数
        EasyMock.expect(metaData.getColumnName(7)).andReturn("TYPE_OPTS").anyTimes();   //期望使用参数
        EasyMock.replay(metaData);//保存期望结果

        ResultSet rs = (ResultSet) EasyMock.createMock(ResultSet.class);
        EasyMock.expect(rs.getMetaData()).andReturn(metaData).anyTimes();   //期望使用参数
        EasyMock.expect(rs.getObject(1)).andReturn("TYPE_ID").anyTimes();   //期望使用参数
        EasyMock.expect(rs.getObject(2)).andReturn("TYPE_ALIAS_ID").anyTimes();   //期望使用参数
        EasyMock.expect(rs.getObject(3)).andReturn("SYS").anyTimes();   //期望使用参数
        EasyMock.expect(rs.getObject(4)).andReturn("USER").anyTimes();   //期望使用参数
        EasyMock.expect(rs.getObject(5)).andReturn("TYPE_DISPLAY_NAME").anyTimes();   //期望使用参数
        EasyMock.expect(rs.getObject(6)).andReturn("TYPE_PK_NAME").anyTimes();   //期望使用参数
        EasyMock.expect(rs.getObject(7)).andReturn("10").anyTimes();   //期望使用参数
        EasyMock.replay(rs);//保存期望结果


        ITableDesc desc = (ITableDesc)mapper.mapRow(rs,1);

        System.out.println(desc);
    }

    @Test
    public void newInstance() {
    }
}