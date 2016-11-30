<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#assign extInfo = parameters.extInfo?default("N")>

<!-- begin findParty.ftl -->
<div id="findPartyParameters" class="screenlet">
  

    <div class="screenlet-body">
      <form method="post" name="lookupparty" action="<@ofbizUrl>findPartySimple</@ofbizUrl>" class="basic-form" onsubmit="javascript:lookupParty('<@ofbizUrl>viewprofile</@ofbizUrl>');">
        <input type="hidden" name="lookupFlag" value="Y"/>
        <input type="hidden" name="hideFields" value="Y"/>
        <table cellspacing="0">
          <tr>
            <td class="label">${uiLabelMap.PartyPartyId} :</td>
            <td><input type="text" name="partyId" value="${parameters.partyId?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.PartyUserLogin} :</td>
            <td><input type="text" name="userLoginId" value="${parameters.userLoginId?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.PartyLastName} :</td>
            <td><input type="text" name="lastName" value="${parameters.lastName?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.PartyFirstName} :</td>
            <td><input type="text" name="firstName" value="${parameters.firstName?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.PartyPartyGroupName} :</td>
            <td><input type="text" name="groupName" value="${parameters.groupName?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.PartyRoleType} :</td>
            <td>
              <select name="roleTypeId">
                <#if currentRole?has_content>
                  <option value="${currentRole.roleTypeId}">${currentRole.get("description",locale)}</option>
                  <option value="${currentRole.roleTypeId}">---</option>
                </#if>
                <option value="ANY">${uiLabelMap.CommonAnyRoleType}</option>
                <#list roleTypes as roleType>
                  <option value="${roleType.roleTypeId}">${roleType.get("description",locale)}</option>
                </#list>
              </select>
            </td>
          </tr>
          <tr><td colspan="3"><hr/></td></tr>
          <tr>
            <td>&nbsp;</td>
            <td>
              <input type="submit" value="${uiLabelMap.PartyLookupParty}" onClick="javascript:document.lookupparty.submit();"/>
            </td>
          </tr>
        </table>
      </form>
    </div>

</div>


  <script language="JavaScript" type="text/javascript">
    <!--//
      document.lookupparty.partyId.focus();
    //-->
  </script>

<#if partyList?exists>
  <br/>
  <div id="findPartyResults" class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <h3>${uiLabelMap.PartyPartiesFound}</h3>
          <#if (partyListSize > 0)>
            <#if (partyListSize > highIndex)>
              <li><a class="nav-next" href="<@ofbizUrl>findPartySimple?VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}&amp;hideFields=${parameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.CommonNext}</a></li>
            <#else>
              <li class="disabled">${uiLabelMap.CommonNext}</li>
            </#if>
            <li>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${partyListSize}</li>
            <#if (viewIndex > 0)>
              <li><a class="nav-previous" href="<@ofbizUrl>findPartySimple?VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}&amp;hideFields=${parameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.CommonPrevious}</a></li>
            <#else>
              <li class="disabled">${uiLabelMap.CommonPrevious}</li>
            </#if>
          </#if>
      </ul>
      <br class="clear" />
    </div>
    <#if partyList?has_content>
      <table class="basic-table" cellspacing="0">
        <tr class="header-row">
          <td>${uiLabelMap.PartyPartyId}</td>
          <td>${uiLabelMap.PartyUserLogin}</td>
          <td>${uiLabelMap.PartyName}</td>
          <#if extInfo?default("") == "P" >
            <td>${uiLabelMap.PartyCity}</td>
          </#if>
          <#if extInfo?default("") == "P">
            <td>${uiLabelMap.PartyPostalCode}</td>
          </#if>
          <#if extInfo?default("") == "T">
            <td>${uiLabelMap.PartyAreaCode}</td>
          </#if>
          <td>${uiLabelMap.PartyType}</td>
          <td>&nbsp;</td>
        </tr>
        <#assign rowClass = "2">
        <#list partyList as partyRow>
          <#assign partyType = partyRow.getRelatedOne("PartyType")?if_exists>
          <tr<#if rowClass == "1"> class="alternate-row"</#if>>
            <td><a href="<@ofbizUrl>viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
            <td>
              <#if partyRow.containsKey("userLoginId")>
                ${partyRow.userLoginId?default("N/A")}
              <#else>
                <#assign userLogins = partyRow.getRelated("UserLogin")>
                <#if (userLogins.size() > 0)>
                  <#if (userLogins.size() > 1)>
                    (${uiLabelMap.CommonMany})
                  <#else>
                  <#assign userLogin = userLogins.get(0)>
                    ${userLogin.userLoginId}
                  </#if>
                <#else>
                  (${uiLabelMap.CommonNone})
                </#if>
              </#if>
            </td>
            <td>
              <#if partyRow.containsKey("lastName") && (partyRow.lastName)?has_content>
                ${partyRow.lastName!}<#if partyRow.containsKey("firstName") && partyRow.firstName?has_content>, ${partyRow.firstName!}</#if>
              <#elseif partyRow.containsKey("groupName") && partyRow.groupName?has_content>
                ${partyRow.groupName!}
              <#else>
                <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(partyRow, true)>
                <#if partyName?has_content>
                  ${partyName}
                <#else>
                  (${uiLabelMap.PartyNoNameFound})
                </#if>
              </#if>
            </td>
            <#if extInfo?default("") == "T">
              <td>${partyRow.areaCode?if_exists}</td>
            </#if>
            <#if extInfo?default("") == "P" >
               <td>${partyRow.city?if_exists}, ${partyRow.stateProvinceGeoId?if_exists}</td>
            </#if>
            <#if extInfo?default("") == "P">
              <td>${partyRow.postalCode?if_exists}</td>
            </#if>
            <td><#if partyType.description?exists>${partyType.get("description", locale)}<#else>???</#if></td>
          <td class="button-col align-float">
              <a href="<@ofbizUrl>viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>">${uiLabelMap.CommonDetails}</a>
              <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
                <a href="/ordermgr/control/searchorders?lookupFlag=Y&amp;hideFields=Y&amp;partyId=${partyRow.partyId + externalKeyParam}&amp;viewIndex=1&amp;viewSize=20">${uiLabelMap.OrderOrders}</a>
                <a href="/ordermgr/control/FindQuote?partyId=${partyRow.partyId + externalKeyParam}">${uiLabelMap.OrderOrderQuotes}</a>
              </#if>
              <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
                <a href="/ordermgr/control/checkinits?partyId=${partyRow.partyId + externalKeyParam}">${uiLabelMap.OrderNewOrder}</a>
                <a href="/ordermgr/control/EditQuote?partyId=${partyRow.partyId + externalKeyParam}">${uiLabelMap.OrderNewQuote}</a>
              </#if>
            </td>
          </tr>
          <#-- toggle the row color -->
          <#if rowClass == "2">
            <#assign rowClass = "1">
          <#else>
            <#assign rowClass = "2">
          </#if>
        </#list>
      </table>
    <#else>
      <div class="screenlet-body">
        <span class="head3">${uiLabelMap.PartyNoPartiesFound}</span>
      </div>
    </#if>
    <#if lookupErrorMessage?exists>
      <div><h3>${lookupErrorMessage}</h3></div>
    </#if>
  </div>
</#if>
<!-- end findParty.ftl -->
