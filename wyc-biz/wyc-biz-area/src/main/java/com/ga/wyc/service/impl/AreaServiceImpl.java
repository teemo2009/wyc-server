package com.ga.wyc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ga.wyc.dao.AreaMapper;
import com.ga.wyc.domain.entity.Area;
import com.ga.wyc.service.IAreaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("areaService")
@Slf4j
public class AreaServiceImpl implements IAreaService{

    @Resource
    AreaMapper areaMapper;

    @Override
    public void initArea() {
        List<Area> list = new ArrayList<>();
        String jsonstr = doGet();
        JSONObject jsonObject = JSONObject.parseObject(jsonstr);
        JSONArray jsonArray = jsonObject.getJSONArray("districts");
        dealCity(jsonArray, list, "100000");
        areaMapper.batchInsert(list);
        log.info("区域初始化完成");
    }

    private String doGet() {
        String temp = "";
        // httpClient
        HttpClient httpClient = new DefaultHttpClient();
        // get method
        HttpGet httpGet = new HttpGet("http://restapi.amap.com/v3/config/district?key=fe98d919416465db7f3b0059f7ca911e"
                + "&keywords=&subdistrict=3&extensions=base");
        // response
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
        }
        try {
            HttpEntity entity = response.getEntity();
            temp = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
        }

        return temp;
    }

    private void dealCity(JSONArray districts, List<Area> list, String pdcode) {
        for (Object json : districts) {
            JSONObject my = (JSONObject) json;
            JSONArray t = my.getJSONArray("districts");
            Area area = new Area();
            area.setCitycode(my.getString("citycode"));
            area.setAdcode(my.getString("adcode"));
            area.setName(my.getString("name"));
            area.setCenter(my.getString("center"));
            area.setLevel(my.getString("level"));
            area.setPdcode(pdcode);
            area.setLabel(my.getString("name"));
            area.setValue(my.getString("adcode"));
            if (my.getString("level").equals("street")){
                return;
            }else{
                list.add(area);
                dealCity(t, list, my.getString("adcode"));
            }
        }
    }

}
