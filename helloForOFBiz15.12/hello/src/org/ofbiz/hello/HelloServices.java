/**
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author     Si Chen (sichen@opensourcestrategies.com)
 */

package org.ofbiz.hello;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;        // uses Log4J
import org.ofbiz.base.util.UtilMisc;     // helpful utility for working with Maps, Lists, etc.
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class HelloServices {
    
    public static final String module = HelloServices.class.getName(); // used for debugging
   
    public static Map createHelloPerson(DispatchContext dctx, Map context) {
        //GenericDelegator delegator = dctx.getDelegator();  // always passed in with DispatchContext        
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
    	
        try {
            String helloPersonId = delegator.getNextSeqId("HelloPerson"); // gets next available key for HelloPerson
            Debug.logInfo("helloPersonId = " + helloPersonId, module); // prints to the console or console.log
            GenericValue helloPerson = delegator.makeValue("HelloPerson", 
                    UtilMisc.toMap("helloPersonId", helloPersonId)); // create a GenericValue from ID we just got
            helloPerson.setNonPKFields(context); // move non-primary key fields from input parameters to GenericValue
            delegator.create(helloPerson); // store the generic value, ie persists it
            
            Map result = ServiceUtil.returnSuccess(); // gets standard Map for successful service operations
            result.put("helloPersonId", helloPersonId); // puts output parameter into Map to return
            return result; // return Map
        } catch (GenericEntityException ex) { // required if you use delegator in Java
            return ServiceUtil.returnError(ex.getMessage());
        }
    }
    
    public static Map deletePersonHobby(DispatchContext dctx, Map context) {
        //GenericDelegator delegator = dctx.getDelegator();  // always passed in with DispatchContext        
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        try {
            String helloPersonId = (String)context.get("helloPersonId"); // gets next available key for HelloPerson
            String helloHobbyId = (String)context.get("helloHobbyId");
            Debug.logInfo("helloPersonId = " + helloPersonId, module); // prints to the console or console.log
            GenericValue helloPersonHobby = delegator.findOne("HelloPersonHobby", UtilMisc.toMap("helloPersonId", helloPersonId,"helloHobbyId",helloHobbyId),false); // create a GenericValue from ID we just got
            if(helloPersonHobby!=null){
            	helloPersonHobby.remove();
            }
            
            Map result = ServiceUtil.returnSuccess(); // gets standard Map for successful service operations
            result.put("helloPersonId", helloPersonId); // puts output parameter into Map to return
            return result; // return Map
        } catch (GenericEntityException ex) { // required if you use delegator in Java
            return ServiceUtil.returnError(ex.getMessage());
        }
    }
    
    public static Map findPartySimple(DispatchContext dctx, Map context) {
        Map result = ServiceUtil.returnSuccess();
        //GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        // get the role types
        try {
            List roleTypes = delegator.findList("RoleType", null,null,UtilMisc.toList("description"),null,false);
            result.put("roleTypes", roleTypes);
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up RoleTypes: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // current role type
        String roleTypeId;
        try {
            roleTypeId = (String) context.get("roleTypeId");
            if (roleTypeId != null && roleTypeId.length() > 0) {
                GenericValue currentRole = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId),true);
                result.put("currentRole", currentRole);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current RoleType: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }


        // set the page parameters
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }
        result.put("viewIndex", new Integer(viewIndex));

        int viewSize = 20;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 20;
        }
        result.put("viewSize", new Integer(viewSize));

        // get the lookup flag
        String lookupFlag = (String) context.get("lookupFlag");

        // blank param list
        String paramList = "";

        List partyList = null;
        int partyListSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        if ("Y".equals(lookupFlag)) {
            String showAll = (context.get("showAll") != null ? (String) context.get("showAll") : "N");
            paramList = paramList + "&lookupFlag=" + lookupFlag + "&showAll=" + showAll ;

            // create the dynamic view entity
            DynamicViewEntity dynamicView = new DynamicViewEntity();

            // default view settings
            dynamicView.addMemberEntity("PT", "Party");
            dynamicView.addAlias("PT", "partyId");
            dynamicView.addAlias("PT", "statusId");
            dynamicView.addAlias("PT", "partyTypeId");
            dynamicView.addRelation("one-nofk", "", "PartyType", ModelKeyMap.makeKeyMapList("partyTypeId"));
            dynamicView.addRelation("many", "", "UserLogin", ModelKeyMap.makeKeyMapList("partyId"));

            // define the main condition & expression list
            List andExprs = FastList.newInstance();
            EntityCondition mainCond = null;

            List orderBy = FastList.newInstance();
            List fieldsToSelect = FastList.newInstance();
            // fields we need to select; will be used to set distinct
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("statusId");
            fieldsToSelect.add("partyTypeId");

            // get the params
            String partyId = (String) context.get("partyId");
            String statusId = (String) context.get("statusId");
            String userLoginId = (String) context.get("userLoginId");
            String firstName = (String) context.get("firstName");
            String lastName = (String) context.get("lastName");
            String groupName = (String) context.get("groupName");

            if (!"Y".equals(showAll)) {
                // check for a partyId
                if (partyId != null && partyId.length() > 0) {
                    paramList = paramList + "&partyId=" + partyId;
                    //andExprs.add(new EntityExpr("partyId", true, EntityOperator.LIKE, "%"+partyId+"%", true));
                    andExprs.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE,"%"+partyId+"%"));
                }

                // now the statusId - send ANY for all statuses; leave null for just enabled; or pass a specific status
                if (statusId != null) {
                    paramList = paramList + "&statusId=" + statusId;
                    if (!"ANY".equalsIgnoreCase(statusId)) {
                        //andExprs.add(new EntityExpr("statusId", EntityOperator.EQUALS, statusId));
                        andExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,statusId));
                    }
                } else {
                    // NOTE: _must_ explicitly allow null as it is not included in a not equal in many databases... odd but true
                    //andExprs.add(new EntityExpr(new EntityExpr("statusId", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")));
                	List<EntityCondition> orExprs = FastList.newInstance();
                	orExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null));
                	orExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
                    andExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                }

                // ----
                // UserLogin Fields
                // ----

                // filter on user login
                if (userLoginId != null && userLoginId.length() > 0) {
                    paramList = paramList + "&userLoginId=" + userLoginId;

                    // modify the dynamic view
                    dynamicView.addMemberEntity("UL", "UserLogin");
                    dynamicView.addAlias("UL", "userLoginId");
                    dynamicView.addViewLink("PT", "UL", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    //andExprs.add(new EntityExpr("userLoginId", true, EntityOperator.LIKE, "%"+userLoginId+"%", true));
                    andExprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.LIKE,"%"+userLoginId+"%"));
                    
                    fieldsToSelect.add("userLoginId");
                }

                // ----
                // PartyGroup Fields
                // ----

                // filter on groupName
                if (groupName != null && groupName.length() > 0) {
                    paramList = paramList + "&groupName=" + groupName;

                    // modify the dynamic view
                    dynamicView.addMemberEntity("PG", "PartyGroup");
                    dynamicView.addAlias("PG", "groupName");
                    dynamicView.addViewLink("PT", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    //andExprs.add(new EntityExpr("groupName", true, EntityOperator.LIKE, "%"+groupName+"%", true));
                    andExprs.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE,"%"+groupName+"%"));
                    
                    fieldsToSelect.add("groupName");
                }

                // ----
                // Person Fields
                // ----

                // modify the dynamic view
                if ((firstName != null && firstName.length() > 0) || (lastName != null && lastName.length() > 0)) {
                    dynamicView.addMemberEntity("PE", "Person");
                    dynamicView.addAlias("PE", "firstName");
                    dynamicView.addAlias("PE", "lastName");
                    dynamicView.addViewLink("PT", "PE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    fieldsToSelect.add("firstName");
                    fieldsToSelect.add("lastName");
                    orderBy.add("lastName");
                    orderBy.add("firstName");
                }

                // filter on firstName
                if (firstName != null && firstName.length() > 0) {
                    paramList = paramList + "&firstName=" + firstName;
                    //andExprs.add(new EntityExpr("firstName", true, EntityOperator.LIKE, "%"+firstName+"%", true));
                    andExprs.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE,"%"+firstName+"%"));
                }

                // filter on lastName
                if (lastName != null && lastName.length() > 0) {
                    paramList = paramList + "&lastName=" + lastName;
                    //andExprs.add(new EntityExpr("lastName", true, EntityOperator.LIKE, "%"+lastName+"%", true));
                    andExprs.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE,"%"+lastName+"%"));
                }

                // ----
                // RoleType Fields
                // ----

                // filter on role member
                if (roleTypeId != null && !"ANY".equals(roleTypeId)) {
                    paramList = paramList + "&roleTypeId=" + roleTypeId;

                    // add role to view
                    dynamicView.addMemberEntity("PR", "PartyRole");
                    dynamicView.addAlias("PR", "roleTypeId");
                    dynamicView.addViewLink("PT", "PR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    //andExprs.add(new EntityExpr("roleTypeId", EntityOperator.EQUALS, roleTypeId));
                    andExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,roleTypeId));

                    fieldsToSelect.add("roleTypeId");
                }

 

                // ---- End of Dynamic View Creation

                // build the main condition
                //if (andExprs.size() > 0) mainCond = new EntityConditionList(andExprs, EntityOperator.AND);
                if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
            }

            Debug.logInfo("In findParty mainCond=" + mainCond, module);

            // do the lookup
            if (mainCond != null || "Y".equals(showAll)) {
                try {
                    // set distinct on so we only get one row per order
                    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
                    // using list iterator
                    EntityListIterator pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);

                    // get the indexes for the partial list
                    lowIndex = viewIndex * viewSize + 1;
                    highIndex = (viewIndex + 1) * viewSize;

                    // get the partial list for this page
                    partyList = pli.getPartialList(lowIndex, viewSize);

                    // attempt to get the full size
                    pli.last();
                    partyListSize = pli.currentIndex();
                    if (highIndex > partyListSize) {
                        highIndex = partyListSize;
                    }
                    
                    // close the list iterator
                    pli.close();
                } catch (GenericEntityException e) {
                    String errMsg = "Failure in party find operation, rolling back transaction: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            } else {
                partyListSize = 0;
            }
        }

        if (partyList == null) partyList = FastList.newInstance();
        result.put("partyList", partyList);
        result.put("partyListSize", new Integer(partyListSize));
        result.put("paramList", paramList);
        result.put("highIndex", new Integer(highIndex));
        result.put("lowIndex", new Integer(lowIndex));

        return result;
    }
    

}