package org.ofbiz.commons.db;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;


import javolution.util.FastList;


public class PaginationUtil{
	public static final String module = PaginationUtil.class.getName();
	
	private int viewIndex = 1; //page no
	private int viewSize = 10; //page size
	private int viewIndexMax =0; //page count
	
	private int lowIndex = 0; //
	private int highIndex = 0; //
	private List resultList = FastList.newInstance();
	private int listSize = 0;  //total count
	private int resultCount = 0;  //total result count
	private String entityName = null; //entity name
	private Set<String> fieldsToSelect = null; //result fields
	private EntityCondition cond = null; //query condition
	private List<String> orderBy = null; //order by
	private EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
	
	//private GenericDelegator delegator = null;
	private Delegator delegator = null;
	private HttpServletRequest request = null;
	public PaginationUtil(Delegator delegator,HttpServletRequest request){
		this.delegator = delegator;
		this.request = request;
		try {
	        viewIndex = Integer.valueOf((String) request.getParameter("VIEW_INDEX")).intValue();
	    } catch (Exception e) {
	        viewIndex = 1;
	    }
    
	    try {
	        viewSize = Integer.valueOf((String) request.getParameter("VIEW_SIZE")).intValue();
	    } catch (Exception e) {
	       viewSize = 10; 
	    }
	}

	/**
	 * 分页查询处理
	 * 先设置好下面几项：entityName，cond
	 * 其它为可选项
	 * @param delegator
	 * @param request
	 */
	public void doQuery(){
	        
	    

	    // get the index for the partial list
	    lowIndex = (((viewIndex - 1) * viewSize) + 1);
	    highIndex = viewIndex * viewSize;
	    
//	    if (cond != null) {
	        EntityListIterator eli = null;
	        try {
	            // do the lookup
	            eli = delegator.find(entityName, cond, null, fieldsToSelect, orderBy, findOpts);

	            // attempt to get the full size
	            eli.last();
	            resultCount = eli.currentIndex();

	            // get the partial list for this page
	            eli.beforeFirst();
	            if (resultCount > viewSize) {
	            	resultList = eli.getPartialList(lowIndex, viewSize);
	            } else if (resultCount > 0) {
	            	resultList = eli.getCompleteList();
	            }

	            if (highIndex > resultCount) {
	                highIndex = resultCount;
	            }
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            
	        } finally {
	            if (eli != null) {
	                try {
	                    eli.close();
	                } catch (GenericEntityException e) {
	                    Debug.logWarning(e, e.getMessage(), module);
	                }
	            }
	        }
//	    }
	    
	    listSize=resultCount;
	}

	public int getViewIndex() {
		return viewIndex;
	}

	public void setViewIndex(int viewIndex) {
		this.viewIndex = viewIndex;
	}

	public int getViewSize() {
		return viewSize;
	}

	public void setViewSize(int viewSize) {
		this.viewSize = viewSize;
	}

	public List getResultList() {
		return resultList;
	}

	public int getListSize() {
		return listSize;
	}

	public int getResultCount() {
		return resultCount;
	}

	public EntityCondition getCond() {
		return cond;
	}

	public void setCond(EntityCondition cond) {
		this.cond = cond;
	}

	public List getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List orderBy) {
		this.orderBy = orderBy;
	}

	public EntityFindOptions getFindOpts() {
		return findOpts;
	}

	public void setFindOpts(EntityFindOptions findOpts) {
		this.findOpts = findOpts;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Set<String> getFieldsToSelect() {
		return fieldsToSelect;
	}

	public void setFieldsToSelect(Set<String> fieldsToSelect) {
		this.fieldsToSelect = fieldsToSelect;
	}

	public int getLowIndex() {
		return lowIndex;
	}

	public int getHighIndex() {
		return highIndex;
	}

}