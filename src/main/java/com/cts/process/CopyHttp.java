package com.cts.process;

import com.alibaba.fastjson2.JSONObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author xpmiao
 * @date 2023/11/20
 */
public class CopyHttp extends AnAction {
    private static final Logger LOG = Logger.getInstance(CopyHttp.class);
    private static Map<String, Properties> values = new HashMap<>();
    CloseableHttpClient build = HttpClientBuilder.create().build();
    private long systemTime;
    private PropertiesShow propertiesShow = new PropertiesShow();
    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor selectedTextEditor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        SelectionModel selectionModel = selectedTextEditor.getSelectionModel();
        String selectText = selectionModel.getSelectedText(true);
        if (StringUtil.isEmpty(selectText)) {
            Messages.showErrorDialog("Must select text", "Error");
            return;
        }
        if ((System.currentTimeMillis()/1000L - systemTime) > 120L) {
            AppSettingsState instance = AppSettingsState.getInstance();
            JSONObject appMappings = JSONObject.parseObject(instance.fetchText).getJSONObject("urls");
            values.clear();
            appMappings.forEach((key,value)->{
                values.put(key,getHttp((String) value));
            });
            systemTime = System.currentTimeMillis() / 1000L;
        }
        propertiesShow.show(values, selectText, selectedTextEditor);
    }
    private Properties getHttp(String url){
        AppSettingsState instance = AppSettingsState.getInstance();
        JSONObject jsonObject = JSONObject.parseObject(instance.fetchText);
        String method = jsonObject.getString("method");
        JSONObject headersJson = jsonObject.getJSONObject("headers");
        String body = jsonObject.getString("body");
        String parseBody = jsonObject.getString("parse-body");
        String[] split = parseBody.split("\\.");

        Header[] headers = new Header[headersJson.size()];
        headersJson.entrySet().stream().map((item) -> new BasicHeader(item.getKey(),
                (String) item.getValue())).collect(
                Collectors.toList()).toArray(headers);
        HttpRequestBase request = null;
        if (method.equals(HttpGet.METHOD_NAME)) {
            request = new HttpGet(url);

        } else if (method.equals(HttpPost.METHOD_NAME)) {
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(body, "UTF-8");
            httpPost.setEntity(stringEntity);
            request = httpPost;
        }
        if (request == null) {
            Messages.showErrorDialog("Must check json request \n" + instance.fetchText, "Error");
            return new Properties();
        }
        request.setHeaders(headers);
        HttpEntity httpEntity = null;
        String result = null;
        try {
            CloseableHttpResponse httpResponse = build.execute(request);
            httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (IOException ex) {
            Messages.showErrorDialog("request \n" + instance.fetchText + "error " + ex.getMessage(), "Error");
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ex) {
            }
        }
        if (result == null) {
            return new Properties();
        }
        String configData = "";
        JSONObject resultJson = JSONObject.parseObject(result);
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                configData = resultJson.getString(split[i]);
                break;
            }
            resultJson = resultJson.getJSONObject(split[i]);
        }
        Properties properties = new Properties();
        StringReader reader = new StringReader(configData);
        try {
            properties.load(reader);
        } catch (IOException ex) {
            Messages.showErrorDialog("Load properties error" + ex.getMessage(), "Error");
        }
        return properties;
    }
}
