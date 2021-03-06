<#ftl strip_text=true>
<#-- @ftlvariable name="" type="at.yawk.javabrowser.server.view.LeafArtifactView" -->
<#import "page.ftl" as page>
<#import "declarationNode.ftl" as declarationNode>
<#import "fullTextSearchForm.ftl" as ftsf>

<#assign additionalMenu>
  <a id="alt-versions" href="javascript:showAlternativeSourceFiles([
      <#list alternatives as alternative>{artifact:'${alternative.artifact.stringId}',path:''<#if alternative.diffPath??>,diffPath:'${alternative.diffPath}'</#if>},</#list>
      ])"><i class="ij ij-history"></i></a>
</#assign>
<@page.page title="${artifactId.stringId}" realm='source' artifactId=artifactId additionalMenu=additionalMenu>
  <div id="noncode">
      <#include "metadata.ftl">

      <#if oldArtifactId??>
        Showing changes in
        <span class="foreground-new"><b>${artifactId.stringId}</b> (new version)</span> from
        <span class="foreground-old"><b>${oldArtifactId.stringId}</b> (old version)</span>.
      </#if>

      <#if dependencies?has_content>
        <div class="size-expander-wrapper <#if (dependencies?size) gt 20> retracted</#if>">
          <h2>Dependencies</h2>
          <ul class="size-expander-target">
              <#list dependencies as dependency>
                <li>
                    <#if dependency.prefix??>
                      <a href="/${dependency.prefix}">${dependency.prefix}</a></#if>${dependency.suffix}
                    <#if dependency.aliasedTo??>(available as <a href="/${dependency.aliasedTo}">${dependency.aliasedTo}</a>)</#if>
                </li>
              </#list>
          </ul>
          <a class="size-expander-expand" href="javascript:"><i>Show more dependencies</i></a>
        </div>
      </#if>

    <#if !oldArtifactId??>
      <@ftsf.fullTextSearchForm query='' searchArtifact=artifactId/>
      <div class="search-box">
        <div class="search-spinner-wrapper">
            <input type="text" class="search" autocomplete="off" data-target="#result-list" data-hide-empty data-realm="source" data-artifact-id="${artifactId.stringId}" data-include-dependencies="false" placeholder="Search for type…">
            <div class="spinner"></div>
        </div>
        <ul id="result-list"></ul>
      </div>
    </#if>
    <div class="declaration-tree">
      <ul>
        <#if oldArtifactId??>
          <#assign diffArtifactId=oldArtifactId.stringId>
        <#else>
          <#assign diffArtifactId="">
        </#if>
        <@ConservativeLoopBlock iterator=topLevelPackages; package>
          <li><@declarationNode.declarationNode node=package diffArtifactId=diffArtifactId/></li>
        </@ConservativeLoopBlock>
      </ul>
    </div>
  </div>
  <div id="tooltip">
  </div>
</@page.page>