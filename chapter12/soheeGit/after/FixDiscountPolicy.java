package Hello.core.discount;

import Hello.core.member.Grade;
import Hello.core.member.Member;

public class FixDiscountPolicy implements DiscountPolicy{

    private int discountFixAmount = 1000;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){ // enum타입은 ==쓰는게 맞음
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}
