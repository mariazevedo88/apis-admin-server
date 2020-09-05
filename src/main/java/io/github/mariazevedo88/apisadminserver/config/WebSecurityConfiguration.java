package io.github.mariazevedo88.apisadminserver.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.codecentric.boot.admin.server.config.AdminServerProperties;

/**
 * Class that implements the Spring Boot Admin security configurations.
 * 
 * @author Mariana Azevedo
 * @since 26/08/2020
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
	private final AdminServerProperties adminServer;
	
	@Value("${spring.security.user.name}")
	private String user;
	
	@Value("${spring.security.user.password}")
	private String password;
 
    public WebSecurityConfiguration(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }
 
    /**
	 * @see WebSecurityConfigurerAdapter#configure(HttpSecurity)
	 * 
	 * @author Mariana Azevedo
	 * @since 26/08/2020
	 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.getContextPath() + "/");
 
        http.authorizeRequests()
                .antMatchers(this.adminServer.getContextPath() + "/assets/**").permitAll()
                .antMatchers(this.adminServer.getContextPath() + "/login").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage(this.adminServer.getContextPath() + "/login")
                .successHandler(successHandler)
                .and()
            .logout()
                .logoutUrl(this.adminServer.getContextPath() + "/logout")
                .and()
            .httpBasic()
                .and()
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers(this.adminServer.getContextPath() + "/instances", 
                		             this.adminServer.getContextPath() + "/instances/*",
                		             this.adminServer.getContextPath() + "/manage/**")
                .and()
                .rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));
    }
    
    /**
     * Method required to provide UserDetailsService for "remember functionality"
     * 
     * @see WebSecurityConfigurerAdapter#configure(AuthenticationManagerBuilder)
	 * 
	 * @author Mariana Azevedo
	 * @since 26/08/2020
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.inMemoryAuthentication().withUser(user).password("{noop}"+password).roles("USER");
    }
}
