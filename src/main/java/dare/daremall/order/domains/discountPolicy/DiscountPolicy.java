package dare.daremall.order.domains.discountPolicy;

public interface DiscountPolicy {
    int discount(int price);
    boolean isDiscountShip(int price); // 할인되면 true를 return


}
