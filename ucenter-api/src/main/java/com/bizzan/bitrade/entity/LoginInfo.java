package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.MemberLevelEnum;
import com.bizzan.bitrade.entity.Country;
import com.bizzan.bitrade.entity.Location;
import com.bizzan.bitrade.entity.Member;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hevin QQ:390330302 E-mail:xunibidev@gmail.com
 * @date 2020年01月31日
 */
@Data
@Builder
public class LoginInfo {
    private String username;
    private Location location;
    private MemberLevelEnum memberLevel;
    private String token;
    private String realName;
    private Country country;
    private String avatar;
    private String promotionCode;
    private long id;
    private int loginCount;
    private String superPartner;
    /**
     * 推广地址前缀
     */
    private String promotionPrefix;

    /**
     * 签到能力
     */
    private Boolean signInAbility;
    
    private int firstLevel = 0; // 一级邀请好友数量
    private int secondLevel = 0; // 二级邀请好友数量
    private int thirdLevel = 0; // 三级邀请好友数量
    private int fourthLevel = 0;
    private int fifthLevel = 0;
    private int sixthLevel = 0;
    private int seventhLevel = 0;
    private int eighthLevel = 0;
    private int ninthLevel = 0;
    private int tenthLevel = 0;
    private int eleventhLevel = 0;
    
    /**
     * 是否存在签到活动
     */
    private Boolean signInActivity;
    private  String memberRate ;

    public static LoginInfo getLoginInfo(Member member, String token, Boolean signInActivity, String prefix) {
        return LoginInfo.builder().location(member.getLocation())
                .memberLevel(member.getMemberLevel())
                .username(member.getUsername())
                .token(token)
                .realName(member.getRealName())
                .country(member.getCountry())
                .avatar(member.getAvatar())
                .promotionCode(member.getPromotionCode())
                .id(member.getId())
                .loginCount(member.getLoginCount())
                .superPartner(member.getSuperPartner())
                .promotionPrefix(prefix)
                .signInAbility(member.getSignInAbility())
                .signInActivity(signInActivity)
                .firstLevel(member.getFirstLevel())
                .secondLevel(member.getSecondLevel())
                .thirdLevel(member.getThirdLevel())
                .fourthLevel(member.getFourthLevel())
                .sixthLevel(member.getSixthLevel())
                .seventhLevel(member.getSeventhLevel())
                .eighthLevel(member.getEighthLevel())
                .ninthLevel(member.getNinthLevel())
                .tenthLevel(member.getTenthLevel())
                .eleventhLevel(member.getEleventhLevel())
                .build();
    }
}
