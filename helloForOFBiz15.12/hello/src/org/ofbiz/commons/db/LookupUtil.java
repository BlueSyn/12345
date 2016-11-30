package org.ofbiz.commons.db;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;


public class LookupUtil{
	
	public static EntityExpr makeExpr(String fieldName, String value) {
        return makeExpr(fieldName, value, false);
    }

    public static EntityExpr makeExpr(String fieldName, String value, boolean forceLike) {
        EntityComparisonOperator op = forceLike ? EntityOperator.LIKE : EntityOperator.EQUALS;

        if (value.startsWith("*")) {
            op = EntityOperator.LIKE;
            value = "%" + value.substring(1);
        }
        else if (value.startsWith("%")) {
            op = EntityOperator.LIKE;
        }

        if (value.endsWith("*")) {
            op = EntityOperator.LIKE;
            value = value.substring(0, value.length() - 1) + "%";
        }
        else if (value.endsWith("%")) {
            op = EntityOperator.LIKE;
        }

        if (forceLike) {
            if (!value.startsWith("%")) {
                value = "%" + value;
            }
            if (!value.endsWith("%")) {
                value = value + "%";
            }
        }

        //return new EntityExpr(fieldName, op, value);
        return EntityCondition.makeCondition(fieldName,op,value);
    }
    
    /**
     * 
     * @param fieldName
     * @return
     */
    //public static EntityExpr makeThruDateExpr(String fieldName){
    public static EntityConditionList<EntityExpr> makeThruDateExpr(String fieldName){
    
    	String thruDateFieldName="thruDate";
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	if(UtilValidate.isNotEmpty(fieldName)){
    		thruDateFieldName=fieldName;
    	}
    	//return new EntityExpr(new EntityExpr(thruDateFieldName, EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr(thruDateFieldName, EntityOperator.GREATER_THAN, nowTimestamp));
    	List<EntityExpr> orExprs = FastList.newInstance();
    	orExprs.add(EntityCondition.makeCondition(thruDateFieldName,EntityOperator.EQUALS, null));
    	orExprs.add(EntityCondition.makeCondition(thruDateFieldName, EntityOperator.GREATER_THAN, nowTimestamp));
    	return EntityCondition.makeCondition(orExprs, EntityOperator.OR);
    }
}