package spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import spring.security.jwt.JwtSecurityConfigurer;
import spring.security.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    CharacterEncodingFilter encodingFilter;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(encodingFilter, ChannelProcessingFilter.class)
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/archery/auth/signIn").permitAll()
                .antMatchers(HttpMethod.POST, "/archery/auth/register").permitAll()
                .antMatchers(HttpMethod.POST,"/archery/auth/refreshToken").permitAll()
                .antMatchers(HttpMethod.PUT, "/archery/auth/exit").hasAnyRole("ADMIN", "USER")

                .antMatchers(HttpMethod.GET, "/archery/test/studentList").permitAll()
                .antMatchers(HttpMethod.GET, "/archery/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/archery/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/archery/profile/*").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/archery/profile/updateAll").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/archery/timetable").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/archery/timetable/day").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/archery/timetable/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/archery/timetable/**").hasRole("USER")

                .anyRequest().authenticated()
                .and()

                .apply(new JwtSecurityConfigurer(jwtTokenProvider));
    }
}
