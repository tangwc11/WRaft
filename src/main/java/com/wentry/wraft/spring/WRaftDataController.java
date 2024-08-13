package com.wentry.wraft.spring;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wentry.wraft.core.AppendLogManager;
import com.wentry.wraft.storage.StorageManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供web交互
 */
@RestController()
@RequestMapping("/w-raft/data")
public class WRaftDataController {

    @GetMapping("/get")
    public String get(String key) {
        return StorageManager.get(key);
    }

    @GetMapping("/getAll")
    public String getAll(){
        return JSONObject.toJSONString(StorageManager.getAllData(), JSONWriter.Feature.PrettyFormat);
    }

    //返回旧值
    @GetMapping("/set")
    public String set(String key, String val) {
        return AppendLogManager.set(key, val);
    }


}
