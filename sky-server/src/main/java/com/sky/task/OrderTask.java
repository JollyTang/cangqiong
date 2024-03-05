package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orderLis = orderMapper.getByStatusAndOrderTImeLT(Orders.PENDING_PAYMENT, time);

        if(orderLis != null && !orderLis.isEmpty()){
            for(Orders orders : orderLis){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }


    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理处于派送中的订单");

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> list = orderMapper.getByStatusAndOrderTImeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if(list != null && !list.isEmpty()){
            for(Orders orders : list){
                orders.setStatus(Orders.COMPLETED);
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

}
