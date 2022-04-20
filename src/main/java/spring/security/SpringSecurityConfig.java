package spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import spring.security.jwt.JwtSecurityConfigurer;
import spring.security.jwt.JwtTokenProvider;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //не нужно создавать сессию тк храним пользователя по токену
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/archery/auth/*").permitAll()
                .antMatchers(HttpMethod.GET,"/archery/test/studentList").permitAll()
                .antMatchers(HttpMethod.PUT,"/archery/auth/exit").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET,"/archery/admin/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/archery/admin/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/archery/admin/*").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST, "/archery/admin/edit/ranks").permitAll()
//                .antMatchers(HttpMethod.GET, "/archery/admin/edit/ranks").permitAll()
//                .antMatchers(HttpMethod.GET, "/archery/admin/timetable/days").permitAll()
                .anyRequest().authenticated()
                .and()

                .apply(new JwtSecurityConfigurer(jwtTokenProvider));
    }
}
