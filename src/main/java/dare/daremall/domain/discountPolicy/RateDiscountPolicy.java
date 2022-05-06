package dare.daremall.domain.discountPolicy;

public class RateDiscountPolicy implements DiscountPolicy{

    private static final int shippingFee = 2500;
    private static final int discountPercent = 10;

    private static final int shippingCriteria = 50000;
    private static final int discountCriteria = 80000;

    @Override
    public int discount(int price) {
        int res = price;

        if(price >= discountCriteria) res =  price * discountPercent / 100;
        if(res<shippingCriteria) res += shippingFee;

        return res;
    }

    @Override
    public boolean isDiscountShip(int price) {
        if(price>=shippingCriteria) return true;
        else return false;
    }
}
