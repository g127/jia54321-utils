package com.jia54321.utils.entity.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 *
 * @author G
 *
 */
public class OperationBean implements Cloneable, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7243937579442329911L;

	protected static class AssembleAndAttirbute{
		Operator oper;
		String attribute;
		private AssembleAndAttirbute(Operator oper, String attribute) {
			this.oper = oper;
			this.attribute = attribute;
		}
		private static AssembleAndAttirbute isAssemble(String attribute){
			if(null == attribute || attribute.length() == 0){
				return null;
			}
			String[] array = attribute.split("_");
			if(null == array || array.length == 1){
				return null;
			}else {
				String attr = attribute.substring(attribute.indexOf("_") + 1 );
				Operator pos0 = Operator.logicValueOf(array[0], Operator.ERROR);
				if(Operator.ERROR.equals(pos0)){
					throw new RuntimeException(String.format("查询属性%s不正确, 缺少合适的操作符。 例如:LIKE_NAME, EQ_NAME", attribute));
				}
				return new AssembleAndAttirbute(pos0, attr);
			}
		}

	}

	private static OperationBean conditionOne(Operator cmd, OperationBean con ,String attribute, Object val){
		OperationBean c = con;
		if(null == con) {
			c = new OperationBean();
		}

		c.setLogicalOperator(cmd.toString());

//		c.setLeftBracket(Operator.LEFT_BRACKET.toString());
//		c.setRightBracket(Operator.RIGHT_BRACKET.toString());

		c.setAttribute(attribute);
		c.setValue(val);
		return c;
	}

	private static List<OperationBean> conditionOneMap(Operator cmd, List<OperationBean> opers, Map<String, Object> oneMap){
		//
		final List<OperationBean> result = (null == opers)? new ArrayList<OperationBean>(): opers;
		//
		if (null != oneMap && oneMap.size() > 0) {
			Iterator<Entry<String, Object>> iter = oneMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Object> oper = iter.next();
				result.add(conditionOne(cmd, null, oper.getKey(), oper.getValue()));
			}
		}
		return result;
	}

	private static List<OperationBean> conditionOneListMap(Operator cmd, List<OperationBean> opers, List<Map<String, Object>> oneListMap){
		//
		final List<OperationBean> result = (null == opers)? new ArrayList<OperationBean>(): opers;
		//
		if (null != oneListMap && oneListMap.size() > 0) {
			for (Iterator<Map<String, Object>> iterator = oneListMap.iterator(); iterator.hasNext();) {
				Map<String, Object> oneMap = iterator.next();
				conditionOneMap(cmd, opers, oneMap);
			}
		}
		return result;
	}

	public static List<OperationBean> conditionOneListMapAnd(List<OperationBean> opers, List<Map<String, Object>> oneListMap){
		return conditionOneListMap(Operator.AND, opers, oneListMap);
	}

	public static List<OperationBean> conditionOneListMapOr(List<OperationBean> opers, List<Map<String, Object>> oneListMap){
		return conditionOneListMap(Operator.OR, opers, oneListMap);
	}

	public static List<OperationBean> conditionOneListMapOrder(List<OperationBean> opers, List<Map<String, Object>> oneListMap){
		return conditionOneListMap(Operator.AND, opers, oneListMap);
	}

	public static List<OperationBean> conditionOneMapAnd(List<OperationBean> opers, Map<String, Object> oneMap){
		return conditionOneMap(Operator.AND, opers, oneMap);
	}

	public static List<OperationBean> conditionOneMapOr(List<OperationBean> opers, Map<String, Object> oneMap){
		return conditionOneMap(Operator.OR, opers, oneMap);
	}

	public static List<OperationBean> sortOneMapOrder(List<OperationBean> opers, Map<String, Object> oneMap){
		return conditionOneMap(Operator.AND, opers, oneMap);
	}

	public static OperationBean conditionAnd(OperationBean con ,String attribute, Object val){
		return conditionOne(Operator.AND, con, attribute, val);
	}


	public static OperationBean conditionOr(OperationBean con ,String attribute, Object val){
		return conditionOne(Operator.OR, con, attribute, val);
	}

	public static OperationBean sortOrder(OperationBean con , String attribute, Object val){
		return conditionOne(Operator.AND, con, attribute, val);
	}

	private String logicalOperator = "";

	private String leftBracket = "";

	private String attribute = "";

	private String operator = "";

	private Object value = "";

	private String rightBracket = "";


	public OperationBean() {
	}

	/**
	 * @return the logicalOper
	 */
	public String getLogicalOperator() {
		return Operator.sqlValueOf(logicalOperator, Operator.BLANK).toString();
	}

	/**
	 * @param logicalOper the logicalOper to set
	 */
	public void setLogicalOperator(String logicalOper) {
		this.logicalOperator = logicalOper;
	}

	/**
	 * @return the leftBracket
	 */
	public String getLeftBracket() {
		return Operator.sqlValueOf(leftBracket, Operator.BLANK).toString();
	}

	/**
	 * @param leftBracket the leftBracket to set
	 */
	public void setLeftBracket(String leftBracket) {
		this.leftBracket = leftBracket;
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		AssembleAndAttirbute aa = AssembleAndAttirbute.isAssemble(attribute);
		if(null == aa){
			this.attribute = attribute;
		}else{
			this.attribute = aa.attribute;
			this.operator = aa.oper.toString();
		}
	}

	/**
	 * @return the oper
	 */
	public String getOperator() {
		return Operator.sqlValueOf(operator, Operator.EQ).toString();
	}

	/**
	 * @param oper the oper to set
	 */
	public void setOperator(String oper) {
		if(null == this.operator || "".equals(this.operator)){
			this.operator = oper;
		}
	}

	/**
	 * @return the val
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param val the val to set
	 */
	public void setValue(Object val) {
		this.value = val;
	}

	/**
	 * @return the rightBracket
	 */
	public String getRightBracket() {
		return Operator.sqlValueOf(rightBracket, Operator.BLANK).toString();
	}

	/**
	 * @param rightBracket the rightBracket to set
	 */
	public void setRightBracket(String rightBracket) {
		this.rightBracket = rightBracket;
	}

	@Override
    public String toString() {
		String strMessage = this.logicalOperator + " " + this.leftBracket
				+ this.attribute + " " + this.operator + " "
				+ String.valueOf(this.value) + this.rightBracket;
		return strMessage;
	}

//	@Override
//    public Object clone() throws CloneNotSupportedException {
//		try {
//			OperationBean v = (OperationBean) super.clone();
//			return v;
//		} catch (CloneNotSupportedException e) {
//		}
//		return null;
//	}
}
