<#include "header.ftl">

<script src="raphael.2.1.0.min.js"></script>
<script src="justgage.1.0.1.min.js"></script>


 
<#if container??>
  <div ="${container}">
<#else>
  <div ="default">
</#if>
<h1>Rule Coverage by previous Unit-Tests</h1>
<table border="1" cellspacing="0" cellpadding="0" style="width:100%;height:100%">
<#list rstEntities as rstEntity>
<#if (rstEntity_index % 5) == 0>
<tr>
</#if>
<td style="text-align: center;">
<div id="gauge-${rstEntity_index}" style="width:180px; height:160px; display: inline-block;"></div>
<script>
  var g${rstEntity_index} = new JustGage({
    id: "gauge-${rstEntity_index}", 
    value: ${rstEntity.getPercentageOfRulesFired()}, 
    min: 0,
    max: 100,
    title: "${rstEntity.getModuleName()} (% fired)",
    titleFontColor: "#000000",
    levelColors: [ "#ff0000", "#F27C07", "#a9d70b"],
    levelColorsGradient : true
  }); 
</script>
<div style="text-align: center;">Rules [&nbsp;fired&nbsp;/&nbsp;total&nbsp;] ${rstEntity.getNumberOfRulesFired()}&nbsp;/&nbsp;${rstEntity.getNumberOfRules()}</div>
</td>
<#if (rstEntity_index % 5) == 4>
</tr>
</#if>
</#list>
</div>

</table>
<#include "footer.ftl"> 