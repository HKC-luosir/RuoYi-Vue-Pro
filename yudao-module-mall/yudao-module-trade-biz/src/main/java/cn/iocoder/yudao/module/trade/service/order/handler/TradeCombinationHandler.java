package cn.iocoder.yudao.module.trade.service.order.handler;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.core.KeyValue;
import cn.iocoder.yudao.module.promotion.api.combination.CombinationRecordApi;
import cn.iocoder.yudao.module.trade.convert.order.TradeOrderConvert;
import cn.iocoder.yudao.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.order.TradeOrderItemDO;
import cn.iocoder.yudao.module.trade.enums.order.TradeOrderStatusEnum;
import cn.iocoder.yudao.module.trade.enums.order.TradeOrderTypeEnum;
import cn.iocoder.yudao.module.trade.service.order.TradeOrderQueryService;
import cn.iocoder.yudao.module.trade.service.order.TradeOrderUpdateService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.trade.enums.ErrorCodeConstants.ORDER_CREATE_FAIL_EXIST_UNPAID;

/**
 * 拼团订单 handler 接口实现类
 *
 * @author HUIHUI
 */
@Component
public class TradeCombinationHandler implements TradeOrderHandler {

    @Resource
    @Lazy
    private TradeOrderUpdateService orderUpdateService;
    @Resource
    @Lazy
    private TradeOrderQueryService orderQueryService;

    @Resource
    @Lazy
    private CombinationRecordApi combinationRecordApi;

    @Override
    public void beforeOrderCreate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        // 如果不是拼团订单则结束
        if (TradeOrderTypeEnum.isCombination(order.getType())) {
            return;
        }
        Assert.isTrue(orderItems.size() == 1, "拼团时，只允许选择一个商品");

        // 校验是否满足拼团活动相关限制
        TradeOrderItemDO item = orderItems.get(0);
        combinationRecordApi.validateCombinationRecord(order.getUserId(), order.getCombinationActivityId(),
                order.getCombinationHeadId(), item.getSkuId(), item.getCount());
        // 校验该用户是否存在未支付的拼团活动订单；就是还没支付的时候，重复下单了；需要校验下；不然的话，一个拼团可以下多个单子了；
        TradeOrderDO activityOrder = orderQueryService.getActivityOrderByUserIdAndActivityIdAndStatus(
                order.getUserId(), order.getCombinationActivityId(), TradeOrderStatusEnum.UNPAID.getStatus());
        if (activityOrder != null) {
            throw exception(ORDER_CREATE_FAIL_EXIST_UNPAID);
        }
    }

    @Override
    public void afterPayOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        // 1.如果不是拼团订单则结束
        if (TradeOrderTypeEnum.isCombination(order.getType())) {
            return;
        }
        Assert.isTrue(orderItems.size() == 1, "拼团时，只允许选择一个商品");

        // 2.获取商品信息
        TradeOrderItemDO item = orderItems.get(0);
        // 2.1.创建拼团记录
        KeyValue<Long, Long> recordIdAndHeadId = combinationRecordApi.createCombinationRecord(
                TradeOrderConvert.INSTANCE.convert(order, item));
        // 3.更新拼团相关信息到订单
        orderUpdateService.updateOrderCombinationInfo(order.getId(), order.getCombinationActivityId(),
                recordIdAndHeadId.getKey(), recordIdAndHeadId.getValue());
    }

}