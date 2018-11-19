package com.xxl.mq.client.producer;

import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.mq.client.factory.XxlMqClientFactory;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import com.xxl.rpc.util.IpUtil;

import java.util.Date;
import java.util.Set;

/**
 * Created by xuxueli on 16/8/28.
 */
public class XxlMqProducer {

    // ---------------------- valid message ----------------------

    /**
     * valid message
     *
     * @param mqMessage
     * @return
     */
    public static void validMessage(XxlMqMessage mqMessage){
        if (mqMessage == null) {
            throw new IllegalArgumentException("xxl-mq, XxlMqMessage can not be null.");
        }

        // topic
        if (mqMessage.getTopic()==null || mqMessage.getTopic().trim().length()==0 || mqMessage.getTopic().length()>512) {
            throw new IllegalArgumentException("xxl-mq, topic invalid.");
        }

        // group
        if (mqMessage.getGroup()==null || mqMessage.getGroup().trim().length()==0) {
            mqMessage.setGroup(MqConsumer.DEFAULT_GROUP);
        }
        if (mqMessage.getGroup().length() > 256) {
            throw new IllegalArgumentException("xxl-mq, group invalid.");
        }

        // data
        if (mqMessage.getData() == null) {
            mqMessage.setData("");
        }

        // status
        mqMessage.setStatus(XxlMqMessageStatus.NEW.name());

        // retryCount
        if (mqMessage.getRetryCount() < 0) {
            mqMessage.setRetryCount(0);
        }

        // shardingId
        if (mqMessage.getShardingId() < 0) {
            mqMessage.setShardingId(0);
        }

        // delayTime
        if (mqMessage.getEffectTime() == null) {
            mqMessage.setEffectTime(new Date());
        }

        // timeout
        if (mqMessage.getTimeout() < 0) {
            mqMessage.setTimeout(0);
        }

        // log
        String appendLog = "<hr>操作: 消息新增<br>》》》消息生产者: " + IpUtil.getIp();
        mqMessage.setLog(appendLog);
    }


    // ---------------------- produce message ----------------------

    /**
     * produce produce
     */
    public static void produce(XxlMqMessage mqMessage, boolean async){
        // valid
        validMessage(mqMessage);

        // send
        XxlMqClientFactory.addMessages(mqMessage, async);
    }

    public static void produce(XxlMqMessage mqMessage){
        produce(mqMessage, true);
    }


    // ---------------------- broadcast message ----------------------

    /**
     * broadcast produce
     */
    public static void broadcast(XxlMqMessage mqMessage, boolean async){
        // valid
        validMessage(mqMessage);

        // find online group
        Set<String> groupList = XxlMqClientFactory.getConsumerRegistryHelper().getTotalGroupList(mqMessage.getTopic());

        // broud total online group
        for (String group: groupList) {

            // clone msg
            XxlMqMessage cloneMsg = new XxlMqMessage(mqMessage);
            cloneMsg.setGroup(group);

            // produce clone msg
            produce(cloneMsg, true);
        }
    }

    public static void broadcast(XxlMqMessage mqMessage){
        broadcast(mqMessage, true);
    }

}
