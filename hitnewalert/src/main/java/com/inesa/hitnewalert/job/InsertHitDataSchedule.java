package com.inesa.hitnewalert.job;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inesa.hitnewalert.entity.HitNewDataVo;
import com.inesa.hitnewalert.entity.Hitnew;
import com.inesa.hitnewalert.mapper.HitnewMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.*;

/**
 * @author 小傅哥，微信：fustack
 * @description 问题任务
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@EnableScheduling
@Configuration
public class InsertHitDataSchedule {

    private Logger logger = LoggerFactory.getLogger(InsertHitDataSchedule.class);

//    @Value("${chatbot-api.groupId}")
//    private String groupId;


    @Autowired
    private HitnewMapper hitnewMapper;


    // 表达式：cron.qqe2.com
    @Scheduled(cron = "0 0 8 * * ?")
    public void run() throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // https://www.jisilu.cn/webapi/cb/pre/?history=N
        HttpGet get = new HttpGet("https://www.jisilu.cn/webapi/cb/pre/?history=N");
        get.addHeader("cookie", "kbzw__Session=v4ckuot8ijfo57rof381kjb3m4; Hm_lvt_164fe01b1433a19b507595a43bf58262=1677648433; kbz_newcookie=1; Hm_lpvt_164fe01b1433a19b507595a43bf58262=1677653421");
        get.addHeader("Content-Type", "application/json;charset=utf8");
        CloseableHttpResponse response = httpClient.execute(get);



        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String res = EntityUtils.toString(response.getEntity());
            HitNewDataVo hitNewDataVo = new HitNewDataVo();
            HitNewDataVo hitNewDataVo1 = JSON.parseObject(res, HitNewDataVo.class);
            List<HitNewDataVo.DataBean> dataBeanList = hitNewDataVo1.getData();


            dataBeanList.stream().forEach(dataBean -> {
                // bond_nm : 中旗转债
                String bond_nm = dataBean.getBond_nm();
                if(bond_nm==null){

                }else{
                    System.out.println(bond_nm);
                    // progress_dt 2023-03-03
                    String progress_dt = dataBean.getProgress_dt();
                    Hitnew hitnew = new Hitnew();
                    hitnew.setName(bond_nm);
                    hitnew.setTime(progress_dt);

                    QueryWrapper<Hitnew> hitnewQueryWrapper = new QueryWrapper<>();
                    hitnewQueryWrapper.eq("name",bond_nm);

                    Hitnew hitnew1 = hitnewMapper.selectOne(hitnewQueryWrapper);
                    if(hitnew1==null){
                        int insert = hitnewMapper.insert(hitnew);
                        if(insert==1){
                            logger.info("数据更新成功");
                            System.out.println("数据更新成功！");
                        }

                    }

                }

            });


//            Top50.DataBean dataBean = data.get(0);
//            String title = dataBean.getTarget().getTitle();
//            System.out.println(title);
//            String openAiQuestion = openAI.doChatGPT(title);
//            System.out.println(openAiQuestion);

        } else {
            System.out.println(response.getStatusLine().getStatusCode());
        }


    }

}
