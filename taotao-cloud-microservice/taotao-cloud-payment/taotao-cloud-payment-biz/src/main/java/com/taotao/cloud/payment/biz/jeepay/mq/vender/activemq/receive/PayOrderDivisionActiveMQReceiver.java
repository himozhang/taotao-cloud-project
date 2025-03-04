/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.payment.biz.jeepay.mq.vender.activemq.receive;

import com.taotao.cloud.payment.biz.jeepay.mq.constant.MQVenderCS;
import com.taotao.cloud.payment.biz.jeepay.mq.model.PayOrderDivisionMQ;
import com.taotao.cloud.payment.biz.jeepay.mq.vender.IMQMsgReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * activeMQ 消息接收器：仅在vender=activeMQ时 && 项目实现IMQReceiver接口时 进行实例化
 * 业务：  支付订单分账通知
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/8/22 16:43
 */
@Component
@ConditionalOnProperty(name = MQVenderCS.YML_VENDER_KEY, havingValue = MQVenderCS.ACTIVE_MQ)
@ConditionalOnBean(PayOrderDivisionMQ.IMQReceiver.class)
public class PayOrderDivisionActiveMQReceiver implements IMQMsgReceiver {

    @Autowired
    private PayOrderDivisionMQ.IMQReceiver mqReceiver;

    /** 接收 【 queue 】 类型的消息 **/
    @Override
    @JmsListener(destination = PayOrderDivisionMQ.MQ_NAME)
    public void receiveMsg(String msg){
        mqReceiver.receive(PayOrderDivisionMQ.parse(msg));
    }

}
