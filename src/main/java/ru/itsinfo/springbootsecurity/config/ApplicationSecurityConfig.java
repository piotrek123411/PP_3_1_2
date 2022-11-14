package ru.itsinfo.springbootsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itsinfo.springbootsecurity.config.handler.CustomAccessDeniedHandler;
import ru.itsinfo.springbootsecurity.config.handler.CustomAuthenticationFailureHandler;
import ru.itsinfo.springbootsecurity.config.handler.CustomAuthenticationSuccessHandler;
import ru.itsinfo.springbootsecurity.config.handler.CustomUrlLogoutSuccessHandler;
import ru.itsinfo.springbootsecurity.service.AppService;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    //тащим пользователя
    private final AppService appService;

    private final PasswordEncoder passwordEncoder;

    //перенаправление пользователей по ролям
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    //при неудачной авторизации
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    //при удачной авторизации
    private final CustomUrlLogoutSuccessHandler urlLogoutSuccessHandler;

    //при отказе в доступе
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public ApplicationSecurityConfig(AppService appServiceTmp,
                                     PasswordEncoder passwordEncoder,
                                     CustomAuthenticationSuccessHandler authenticationSuccessHandler,
                                     CustomAuthenticationFailureHandler authenticationFailureHandler,
                                     CustomUrlLogoutSuccessHandler urlLogoutSuccessHandler,
                                     CustomAccessDeniedHandler accessDeniedHandler) {
        this.appService = appServiceTmp;
        this.passwordEncoder = passwordEncoder;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.urlLogoutSuccessHandler = urlLogoutSuccessHandler;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(appService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/**", "/js/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        http.formLogin()
                .loginPage("/")
                .permitAll()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .usernameParameter("email")
                .passwordParameter("password");
        http.logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/?logout")
                .logoutSuccessHandler(urlLogoutSuccessHandler)
                .permitAll();
    }
}
