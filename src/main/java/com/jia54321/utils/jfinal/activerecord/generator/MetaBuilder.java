/**
 * Copyright (c) 2011-2021, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.activerecord.generator.TypeMapping;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.Predicate;

/**
 * MetaBuilder
 */
public class MetaBuilder extends com.jfinal.plugin.activerecord.generator.MetaBuilder {

	public MetaBuilder(DataSource dataSource) {
		super(dataSource);
	}

	// 移除没有主键的 table
	protected void removeNoPrimaryKeyTable(List<TableMeta> ret) {
		for (java.util.Iterator<TableMeta> it = ret.iterator(); it.hasNext();) {
			TableMeta tm = it.next();
			if (StrKit.isBlank(tm.primaryKey)) {
				it.remove();
				System.err.println("Skip table " + tm.name + " because there is no primary key");
			}
		}
	}

	protected void buildPrimaryKey(TableMeta tableMeta) throws SQLException {
		ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, tableMeta.name);

		String primaryKey = "";
		int index = 0;
		while (rs.next()) {
			String cn = rs.getString("COLUMN_NAME");

			// 避免 oracle 驱动的 bug 生成重复主键，如：ID,ID
			if (primaryKey.equals(cn)) {
				continue ;
			}

			if (index++ > 0) {
				primaryKey += ",";
			}
			primaryKey += cn;
		}

		// 无主键的 table 将在后续的 removeNoPrimaryKeyTable() 中被移除，不再抛出异常
		// if (StrKit.isBlank(primaryKey)) {
			// throw new RuntimeException("primaryKey of table \"" + tableMeta.name + "\" required by active record pattern");
		// }

		tableMeta.primaryKey = primaryKey;
		rs.close();
	}

}







