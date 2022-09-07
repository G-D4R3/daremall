package dare.daremall;

import dare.daremall.order.domains.discountPolicy.DiscountPolicy;
import dare.daremall.order.domains.discountPolicy.RateDiscountPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
//@EnableJpaRepositories(basePackages="dare.daremall.repository")
public class AppConfig {
    @Bean
    public DiscountPolicy discountPolicy(){
        return new RateDiscountPolicy();
    }


}
