package com.jia54321.utils.entity.dao;

/**
 * @author G
 */
public class EntityTypeDaoConst {
	
	  public final static String TAB_ENTITY_TYPE_INSERT = "INSERT INTO SYS_ENTITY_TYPE(TYPE_ID,TYPE_ALIAS_ID,TYPE_DISPLAY_NAME,TYPE_MK, TYPE_ENTITY_NAME,TYPE_PK_NAME,TYPE_OPTS) VALUES(?,?,?,?,?,?,?)";
	  public final static String TAB_ENTITY_TYPE_DELETE = "DELETE FROM SYS_ENTITY_TYPE";
	  public final static String ENTITY_TYPE_SELECT_FROM = "SELECT TYPE_ID,TYPE_ALIAS_ID,TYPE_DISPLAY_NAME,TYPE_MK, TYPE_ENTITY_NAME,TYPE_PK_NAME,TYPE_OPTS FROM SYS_ENTITY_TYPE ";
	  public final static String ENTITY_TYPE_WHERE_BY_TYPEID = " WHERE TYPE_ID=?";
	  public final static String ENTITY_TYPE_WHERE_BY_TYPEMK = " WHERE TYPE_MK=?";
	  public final static String ENTITY_TYPE_WHERE_BY_TYPE_ALIAS_ID = " WHERE TYPE_ALIAS_ID=?";
	  
	  public final static String ENTITY_ITEM_SELECT_FROM = ""
			  + "SELECT ITEM_ID, ITEM_COL_NAME, ITEM_DISPLAY_NAME, "
			  + "ITEM_LEVEL, ITEM_TYPE, ITEM_LEN, ITEM_DEC, REF_TYPE_ID, ITEM_PARENT_ID, TYPE_ID FROM SYS_ENTITY_ITEM ";
	  public final static String ENTITY_ITEM_WHERE_BY_ITEMID = " WHERE ITEM_ID=?";
	  public final static String ENTITY_ITEM_WHERE_BY_TYPEID = " WHERE TYPE_ID=?";
	  
//	  public final static String ENTITY_DESC_MYSQL_OLD = "DESC %s";
	  
//	  public final static String ENTITY_DESC_MYSQL = "SHOW FULL COLUMNS FROM %s";
	  
	  public final static String ENTITY_DESC_ORACLE = 
			"SELECT A.COLUMN_NAME, A.DATA_TYPE, B.COMMENTS FROM USER_TAB_COLUMNS A,USER_COL_COMMENTS B WHERE A.TABLE_NAME=B.TABLE_NAME AND B.TABLE_NAME='%s' AND A.COLUMN_NAME=B.COLUMN_NAME(+) ";

	  public final static String SHOW_TAB_MYSQL = "SHOW TABLES LIKE '%s'";
	  
	  public final static String SHOW_TAB_ORACLE = "SELECT TABLE_NAME FROM TABS WHERE TABLE_NAME LIKE '%s'";
	  
	  public final static String TAB_ENTITY_TYPE = "SYS_ENTITY_TYPE";
	  public final static String TAB_ENTITY_ITEM = "SYS_ENTITY_ITEM";
	  
	  

}
