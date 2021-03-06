<#ftl strip_text=true>
<#import "path.ftl" as path>
<#macro page realm artifactId title hasSearch=false additionalTitle="" additionalMenu=""><!doctype html>
  <html lang="en" class="theme-${theme} <#if javadocRenderEnabled>javadoc-render-enabled</#if>">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${title}</title>
    <link rel="stylesheet" href="/assets/layout.css">
    <link rel="stylesheet" href="/assets/text.css">
    <link rel="stylesheet" href="/assets/search.css">
    <link rel="stylesheet" href="/assets/references.css">
    <link rel="stylesheet" href="/assets/icons.css">
    <link rel="stylesheet" href="/assets/theme-default.css">
    <link rel="stylesheet" href="/assets/theme-darcula.css">
    <script src="/webjars/zeptojs/1.2.0/zepto.js"></script>
    <script src="/webjars/zeptojs/1.2.0/ajax.js"></script>
    <script src="/webjars/zeptojs/1.2.0/event.js"></script>
    <script src="/assets/app.js"></script>
    <script src="/assets/search.js"></script>
    <script src="/assets/code.js"></script>
  </head>
  <body>
  <div id="wrapper">
    <div id="header">
      <div>
        <h1><a href="/"><i class="ij ij-home"></i> /</a>&nbsp;</h1>
        <#if artifactId?has_content><h1 class="shrink" style="flex-shrink: 1"><span><@path.showNode artifactId/></span></h1>&nbsp;</#if>
        <h1 class="shrink" style="flex-shrink: 2"><span>${additionalTitle}</span></h1>

        ${additionalMenu}
        <#if hasSearch>
          <a class="search-button" href="javascript:SearchDialog.instance.open()" title="Hotkey: [T]"><i class="ij ij-search"></i></a>
        </#if>
      </div>
    </div>
    <div id="content">
      <div>
        <#nested/>

        <div class="small" style="text-align: right">
          <label>
            Theme (changing this sets a cookie):
            <select id="theme-selector">
              <option value="default"<#if theme == "default"> selected</#if>>Default</option>
              <option value="darcula"<#if theme == "darcula"> selected</#if>>Darcula</option>
            </select>
          </label><br>
          <label>
            Javadoc Rendering (changing this sets a cookie):
            <input class="javadoc-render-toggle" type="checkbox" <#if javadocRenderEnabled>checked</#if>>
          </label><br>
          Missing a library or version you want to see?
          <a href="https://github.com/yawkat/java-browser/issues/new?labels=library">Open an issue!</a><br>
          Icon set and themes from <a href="https://github.com/JetBrains/intellij-community/">IntelliJ Community</a>
        </div>
      </div>
    </div>
    <#if hasSearch>
      <#include "search-dialog.ftl">
    </#if>
  </div>
  </body>
  </html>
</#macro>