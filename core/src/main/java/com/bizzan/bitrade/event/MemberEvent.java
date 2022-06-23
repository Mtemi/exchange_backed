package com.bizzan.bitrade.event;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.PromotionLevel;
import com.bizzan.bitrade.constant.PromotionRewardType;
import com.bizzan.bitrade.constant.RewardRecordType;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.BigDecimalUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Hevin QQ:390330302 E-mail:xunibidev@gmail.com
 * @date 2020年01月09日
 */
@Service
@Slf4j
public class MemberEvent {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private MemberPromotionService memberPromotionService;
    @Autowired
    private RewardPromotionSettingService rewardPromotionSettingService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private RewardRecordService rewardRecordService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private MemberWeightUpperService memberWeightUpperService;
    /**
     * 如果值为1，推荐注册的推荐人必须被推荐人实名认证才能获得奖励
     */
    @Value("${commission.need.real-name:1}")
    private int needRealName;
    
    @Value("${commission.promotion.second-level:0}")
    private int promotionSecondLevel ;

    /**
     * 注册成功事件
     *
     * @param member 持久化对象
     */
    public void onRegisterSuccess(Member member, String promotionCode) throws InterruptedException {
        JSONObject json = new JSONObject();
        json.put("uid", member.getId());
        //发送给wallet项目consumer处理（）
        kafkaTemplate.send("member-register", json.toJSONString());

        //发送给contract-swap-api项目consumer处理
        kafkaTemplate.send("member-register-swap", json.toJSONString());
        //推广活动
        if (StringUtils.hasText(promotionCode)) {
            Member member1 = memberDao.findMemberByPromotionCode(promotionCode);
            if (member1 != null) {
                member.setInviterId(member1.getId());
                //如果不需要实名认证，直接发放奖励
                if (needRealName == 0) {
                    promotion(member1, member);
                }
            }
        }
        //增加upper关系
        memberWeightUpperService.saveMemberWeightUpper(member);
    }

    /**
     * 设置邀请人
     * @param member
     * @param inviterMember
     * @throws InterruptedException
     */
    public void setMemberInviter(Member member, Member inviterMember) throws InterruptedException {
        //推广活动
        if (inviterMember != null) {
            member.setInviterId(inviterMember.getId());
            //如果不需要实名认证，直接发放奖励
            if (needRealName == 0) {
                promotion(inviterMember, member);
            }
        }
        //增加upper关系
        memberWeightUpperService.saveMemberWeightUpper(member);
    }

    /**
     * 登录成功事件
     *
     * @param member 持久化对象
     */
    public void onLoginSuccess(Member member, String ip) {

    }

    private void promotion(Member member1, Member member) {
        RewardPromotionSetting rewardPromotionSetting = rewardPromotionSettingService.findByType(PromotionRewardType.REGISTER);
        if (rewardPromotionSetting != null) {
            MemberWallet memberWallet1 = memberWalletService.findByCoinAndMember(rewardPromotionSetting.getCoin(), member1);
            BigDecimal amount1 = JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("one");
            memberWallet1.setToReleased(BigDecimalUtils.add(memberWallet1.getToReleased(), amount1));
            memberWalletService.save(memberWallet1);
            RewardRecord rewardRecord1 = new RewardRecord();
            rewardRecord1.setAmount(amount1);
            rewardRecord1.setCoin(rewardPromotionSetting.getCoin());
            rewardRecord1.setMember(member1);
            rewardRecord1.setRemark(rewardPromotionSetting.getType().getCnName());
            rewardRecord1.setType(RewardRecordType.PROMOTION);
            rewardRecordService.save(rewardRecord1);
            MemberTransaction memberTransaction = new MemberTransaction();
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setAmount(amount1);
            memberTransaction.setSymbol(rewardPromotionSetting.getCoin().getUnit());
            memberTransaction.setType(TransactionType.PROMOTION_AWARD);
            memberTransaction.setMemberId(member1.getId());
            memberTransaction.setRealFee("0");
            memberTransaction.setDiscountFee("0");
            memberTransaction.setCreateTime(new Date());
            memberTransactionService.save(memberTransaction);
        }
        member1.setFirstLevel(member1.getFirstLevel() + 1);
        MemberPromotion one = new MemberPromotion();
        one.setInviterId(member1.getId());
        one.setInviteesId(member.getId());
        one.setLevel(PromotionLevel.ONE);
        memberPromotionService.save(one);
        
        if (member1.getInviterId() != null) {
            Member member2 = memberDao.findOne(member1.getInviterId());
            // 当A推荐B，B推荐C，如果C通过实名认证，则B和A都可以获得奖励
            promotionLevelTwo(rewardPromotionSetting, member2, member);
        }
    }

    private void promotionLevelTwo(RewardPromotionSetting rewardPromotionSetting, Member member1, Member member) {
        if (rewardPromotionSetting != null) {
            MemberWallet memberWallet1 = memberWalletService.findByCoinAndMember(rewardPromotionSetting.getCoin(), member1);
            BigDecimal amount1 = JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("two");
            memberWallet1.setToReleased(BigDecimalUtils.add(memberWallet1.getToReleased(), amount1));
            memberWalletService.save(memberWallet1);

            RewardRecord rewardRecord1 = new RewardRecord();
            rewardRecord1.setAmount(amount1);
            rewardRecord1.setCoin(rewardPromotionSetting.getCoin());
            rewardRecord1.setMember(member1);
            rewardRecord1.setRemark(rewardPromotionSetting.getType().getCnName());
            rewardRecord1.setType(RewardRecordType.PROMOTION);
            rewardRecordService.save(rewardRecord1);
            MemberTransaction memberTransaction = new MemberTransaction();
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setAmount(amount1);
            memberTransaction.setSymbol(rewardPromotionSetting.getCoin().getUnit());
            memberTransaction.setType(TransactionType.PROMOTION_AWARD);
            memberTransaction.setMemberId(member1.getId());
            memberTransaction.setRealFee("0");
            memberTransaction.setDiscountFee("0");
            memberTransaction.setCreateTime(new Date());
            memberTransactionService.save(memberTransaction);

        }
        member1.setSecondLevel(member1.getSecondLevel() + 1);
        MemberPromotion two = new MemberPromotion();
        two.setInviterId(member1.getId());
        two.setInviteesId(member.getId());
        two.setLevel(PromotionLevel.TWO);
        memberPromotionService.save(two);
        if (member1.getInviterId() != null) {
            Member member3 = memberDao.findOne(member1.getInviterId());
            promotionLevelThree(rewardPromotionSetting, member3, member);
        }
    }

    private void promotionLevelThree(RewardPromotionSetting rewardPromotionSetting, Member member1, Member member) {
        if (rewardPromotionSetting != null) {
            MemberWallet memberWallet1 = memberWalletService.findByCoinAndMember(rewardPromotionSetting.getCoin(), member1);
            BigDecimal amount1 = JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("three");
            memberWallet1.setToReleased(BigDecimalUtils.add(memberWallet1.getToReleased(), amount1));
            memberWalletService.save(memberWallet1);
            RewardRecord rewardRecord1 = new RewardRecord();
            rewardRecord1.setAmount(amount1);
            rewardRecord1.setCoin(rewardPromotionSetting.getCoin());
            rewardRecord1.setMember(member1);
            rewardRecord1.setRemark(rewardPromotionSetting.getType().getCnName());
            rewardRecord1.setType(RewardRecordType.PROMOTION);
            rewardRecordService.save(rewardRecord1);
            MemberTransaction memberTransaction = new MemberTransaction();
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setAmount(amount1);
            memberTransaction.setSymbol(rewardPromotionSetting.getCoin().getUnit());
            memberTransaction.setType(TransactionType.PROMOTION_AWARD);
            memberTransaction.setMemberId(member1.getId());
            memberTransaction.setRealFee("0");
            memberTransaction.setDiscountFee("0");
            memberTransaction.setCreateTime(new Date());
            memberTransactionService.save(memberTransaction);
        }
        member1.setThirdLevel(member1.getThirdLevel() + 1);
        MemberPromotion promotion = new MemberPromotion();
        promotion.setInviterId(member1.getId());
        promotion.setInviteesId(member.getId());
        promotion.setLevel(PromotionLevel.THREE);
        memberPromotionService.save(promotion);
        if (member1.getInviterId() != null) {
            Member member2 = memberDao.findOne(member1.getInviterId());
            promotionLevelFourth(rewardPromotionSetting, member2, member);
        }
    }

    private void promotionLevelFourth(RewardPromotionSetting rewardPromotionSetting, Member member1, Member member) {
        if (rewardPromotionSetting != null) {
            MemberWallet memberWallet1 = memberWalletService.findByCoinAndMember(rewardPromotionSetting.getCoin(), member1);
            BigDecimal amount1 = JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("four");
            memberWallet1.setToReleased(BigDecimalUtils.add(memberWallet1.getToReleased(), amount1));
            memberWalletService.save(memberWallet1);
            RewardRecord rewardRecord1 = new RewardRecord();
            rewardRecord1.setAmount(amount1);
            rewardRecord1.setCoin(rewardPromotionSetting.getCoin());
            rewardRecord1.setMember(member1);
            rewardRecord1.setRemark(rewardPromotionSetting.getType().getCnName());
            rewardRecord1.setType(RewardRecordType.PROMOTION);
            rewardRecordService.save(rewardRecord1);
            MemberTransaction memberTransaction = new MemberTransaction();
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setAmount(amount1);
            memberTransaction.setSymbol(rewardPromotionSetting.getCoin().getUnit());
            memberTransaction.setType(TransactionType.PROMOTION_AWARD);
            memberTransaction.setMemberId(member1.getId());
            memberTransaction.setRealFee("0");
            memberTransaction.setDiscountFee("0");
            memberTransaction.setCreateTime(new Date());
            memberTransactionService.save(memberTransaction);
        }
        member1.setFourthLevel(member1.getFourthLevel() + 1);
        MemberPromotion promotion = new MemberPromotion();
        promotion.setInviterId(member1.getId());
        promotion.setInviteesId(member.getId());
        promotion.setLevel(PromotionLevel.FOUR);
        memberPromotionService.save(promotion);
//        if (member1.getInviterId() != null) {
//            Member member2 = memberDao.findOne(member1.getInviterId());
//            promotionLevelFourth(rewardPromotionSetting, member2, member);
//        }
    }


}
