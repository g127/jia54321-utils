package com.jia54321.utils.entity.query;


/**
 * 
 * @author G
 */
public enum Operator {
	// Logical Operator
	AND("AND", "AND"), OR("OR", "OR"),
	
	// Condition Operator
	EQ("EQ", "="), LIKE("LIKE", "LIKE"), GT("GT", ">"), LT("LT","<"), GTE("GTE", ">="), LTE("LTE","<="), IN("IN","IN"), EXISTS("EXISTS","EXISTS"),
	
	NOTEQ("NOTEQ", "<>"), ISNULL("ISNULL", "ISNULL"), EMPTY("EMPTY", "EMPTY"),
	
	// Bracket Operator
	LEFT_BRACKET("LB", "("),RIGHT_BRACKET("RB", ")"),
	
	// Sort Operator
	ORDER("ORDER","ORDER"),
	
	// Sort Operator
	GROUP("GROUP","GROUP"),
	
	BLANK("BLANK", " "),
	
	ERROR("ERROR", " ERROR ");
	
	private String logicVal;
	private String sqlVal;
	Operator(String logicVal, String sqlVal){
		this.logicVal = logicVal;
		this.sqlVal = sqlVal;
	}
	
	protected static Operator logicValueOf(String logicVal, Operator defaultVal) {
		if(null == logicVal || "".equals(logicVal)){
			return defaultVal;
		}
        if(AND.tologicVal().equals(logicVal.trim())){
        	return AND;
        }
        if(OR.tologicVal().equals(logicVal.trim())){
        	return OR;
        }
        if(EQ.tologicVal().equals(logicVal.trim())){
        	return EQ;
        }
        if(NOTEQ.tologicVal().equals(logicVal.trim())){
        	return NOTEQ;
        }
        if(ISNULL.tologicVal().equals(logicVal.trim())){
        	return ISNULL;
        }
        if(EMPTY.tologicVal().equals(logicVal.trim())){
        	return EMPTY;
        }
        if(LIKE.tologicVal().equals(logicVal.trim())){
        	return LIKE;
        }
        if(GT.tologicVal().equals(logicVal.trim())){
        	return GT;
        }
        if(LT.tologicVal().equals(logicVal.trim())){
        	return LT;
        }
        if(GTE.tologicVal().equals(logicVal.trim())){
        	return GTE;
        }
        if(LTE.tologicVal().equals(logicVal.trim())){
        	return LTE;
        }
        if(IN.tologicVal().equals(logicVal.trim())){
        	return IN;
        }
        if(EXISTS.tologicVal().equals(logicVal.trim())){
        	return EXISTS;
        }
        
        if(LEFT_BRACKET.tologicVal().equals(logicVal.trim())){
        	return LEFT_BRACKET;
        }
        if(RIGHT_BRACKET.tologicVal().equals(logicVal.trim())){
        	return RIGHT_BRACKET;
        }
        if(ORDER.tologicVal().equals(logicVal.trim())){
        	return ORDER;
        }
        return defaultVal;
    }
	
	protected static Operator sqlValueOf(String sqlVal, Operator defaultVal) {
		if(null == sqlVal || "".equals(sqlVal)){
			return defaultVal;
		}
        if(AND.sqlVal.equals(sqlVal.trim())){
        	return AND;
        }
        if(OR.sqlVal.equals(sqlVal.trim())){
        	return OR;
        }
        if(EQ.sqlVal.equals(sqlVal.trim())){
        	return EQ;
        }
        if(NOTEQ.sqlVal.equals(sqlVal.trim())){
        	return NOTEQ;
        }
        if(ISNULL.sqlVal.equals(sqlVal.trim())){
        	return ISNULL;
        }
        if(EMPTY.sqlVal.equals(sqlVal.trim())){
        	return EMPTY;
        }
        if(LIKE.sqlVal.equals(sqlVal.trim())){
        	return LIKE;
        }
        if(GT.sqlVal.equals(sqlVal.trim())){
        	return GT;
        }
        if(LT.sqlVal.equals(sqlVal.trim())){
        	return LT;
        }
        if(GTE.sqlVal.equals(sqlVal.trim())){
        	return GTE;
        }
        if(LTE.sqlVal.equals(sqlVal.trim())){
        	return LTE;
        }
        if(IN.sqlVal.equals(sqlVal.trim())){
        	return IN;
        }
        if(EXISTS.sqlVal.equals(sqlVal.trim())){
        	return EXISTS;
        }
        
        if(LEFT_BRACKET.sqlVal.equals(sqlVal.trim())){
        	return LEFT_BRACKET;
        }
        if(RIGHT_BRACKET.sqlVal.equals(sqlVal.trim())){
        	return RIGHT_BRACKET;
        }
        if(ORDER.sqlVal.equals(sqlVal.trim())){
        	return ORDER;
        }
        return defaultVal;
    }
	
	public static boolean isConditionOper(String sqlVal){
		if (AND.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (OR.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (EQ.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (NOTEQ.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (ISNULL.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (LIKE.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (GT.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (LT.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (GTE.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
		if (LTE.sqlVal.equals(sqlVal.trim())) {
			return true;
		}
        if(IN.sqlVal.equals(sqlVal.trim())){
			return true;
        }
        if(EXISTS.sqlVal.equals(sqlVal.trim())){
			return true;
        }
		return false;
	}

	@Override
	public String toString() {
		return this.sqlVal;
	}
	
	public String tologicVal() {
		return this.logicVal;
	}
}
