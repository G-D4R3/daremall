package dare.daremall;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/members/login").permitAll()
                .antMatchers("/shop/**").authenticated()
                .antMatchers("/like/**").authenticated()
                .antMatchers("/order/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/members/login")
                .loginProcessingUrl("/members/login")
                .usernameParameter("loginId")
                .defaultSuccessUrl("/");
    }
}
