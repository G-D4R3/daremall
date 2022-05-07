package dare.daremall;

import dare.daremall.domain.discountPolicy.DiscountPolicy;
import dare.daremall.domain.discountPolicy.RateDiscountPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public DiscountPolicy discountPolicy(){
        return new RateDiscountPolicy();
    }
}
