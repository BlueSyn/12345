<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link rel="stylesheet" href="../images/main.css" type="text/css">
<#if layoutSettings.styleSheets?has_content>
        <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
        <#list layoutSettings.styleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    <#else>
        <link rel="stylesheet" href="<@ofbizContentUrl>/images/maincss.css</@ofbizContentUrl>" type="text/css"/>
    </#if>
<title>Hello World</title>
</head>
<body>
<p align="right">It is now ${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()}</p>
<a href="<@ofbizUrl>/main</@ofbizUrl>">Main</a> 
<a href="<@ofbizUrl>/news</@ofbizUrl>">News</a> 
<a href="<@ofbizUrl>/sports</@ofbizUrl>">Sports</a> 
<a href="<@ofbizUrl>/weather</@ofbizUrl>">Weather</a> 
<a href="<@ofbizUrl>/horoscope</@ofbizUrl>">Horoscope</a> 
<a href="<@ofbizUrl>/people</@ofbizUrl>">People</a>
<a href="<@ofbizUrl>/peopleNav</@ofbizUrl>">People Pagination</a> 
<a href="<@ofbizUrl>/guestbook</@ofbizUrl>">Guestbook</a> 
<a href="<@ofbizUrl>/findPartySimple</@ofbizUrl>">findPartySimple</a>
<hr>
