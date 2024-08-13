package com.wentry.wraft.spring;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.core.Scheduler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 节点控制接口
 */
@RestController()
@RequestMapping("/w-raft/op")
public class WRaftOpController {

    /**
     * 连接节点，ports格式：internalPort:httpPort，如10001_8080
     * @param host
     * @param ports
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/connect")
    public String connect(String host, String ports) throws InterruptedException {
        String[] portArr = StringUtils.splitByWholeSeparator(ports, ",");
        for (String p : portArr) {
            String[] pp = p.split("_");
            int internalPort = NumberUtils.toInt(pp[0]);
            int httpPort = NumberUtils.toInt(pp[1]);
            ClusterManager.connect(host, internalPort, httpPort);
        }
        return "ok";
    }

    //开启服务
    @GetMapping("/start")
    public String start() {
        Scheduler.getInstance().start();
        return "ok";
    }

    @GetMapping("/state")
    public String state(){
        return JSONObject.toJSONString(ClusterManager.state(), JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat);
    }



}
