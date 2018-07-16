package com.ga.wyc.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonUtil {

    public Object strTobject(String jsonStr, Class<?> cls) {
        ObjectMapper mapper = new ObjectMapper();
        Object object = null;
        try {
            object = mapper.readValue(jsonStr, cls);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return object;
    }

    public String string2Json(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public String ListToJsonStr(List<?> list) {
        String jsonStr = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonStr = mapper.writeValueAsString(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public List<?> StrToList(String jsonStr, Class<?> cls) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = getCollectionType(mapper, ArrayList.class, cls);
        List<?> lst = null;
        try {
            lst = (List<?>) mapper.readValue(jsonStr, javaType);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lst;
    }

    public String ObjectToJsonStr(Object c) {
        String jsonStr = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonStr = mapper.writeValueAsString(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    @SuppressWarnings("deprecation")
    JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass,
                               Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass,
                elementClasses);
    }

}
