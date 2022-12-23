package at.ac.uibk.swa.config;

import at.ac.uibk.swa.config.exceptionHandling.RestAccessDeniedHandler;
import at.ac.uibk.swa.config.filters.BearerTokenAuthenticationFilter;
import at.ac.uibk.swa.config.filters.CookieTokenAuthenticationFilter;
import at.ac.uibk.swa.models.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.ADMIN_ROUTES;
import static at.ac.uibk.swa.util.EndpointMatcherUtil.PROTECTED_API_ROUTES;

/**
 * <p>
 *     Class for configuring the Authentication Process of the Web-Server.
 * </p>
 * <p>
 *     All API-Paths (except for "/api/login") are secured using an Authentication Token.
 * </p>
 * <p>
 *     The Front-End can fetch a Token from "/api/login" using a User's username and password-Hash.
 * </p>
 * <p>
 *     This Token can then be used in the Authentication Header as a Bearer Token to authenticate
 *     the user for the API.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
    //region Autowired Components
    @Autowired
    private AuthenticationProvider provider;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private AuthenticationEntryPoint entryPoint;

    @Autowired
    private RestAccessDeniedHandler accessDeniedHandler;
    //endregion

    //region Authentication Manager Bean
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }
    //endregion

    //region Custom Authentication Filter Beans
    @Bean
    AbstractAuthenticationProcessingFilter bearerAuthenticationFilter(HttpSecurity http) throws Exception {
        final AbstractAuthenticationProcessingFilter filter = new BearerTokenAuthenticationFilter(PROTECTED_API_ROUTES);

        filter.setAuthenticationManager(authManager(http));
        filter.setAuthenticationFailureHandler(failureHandler);

        return filter;
    }

    @Bean
    AbstractAuthenticationProcessingFilter cookieAuthenticationFilter(HttpSecurity http) throws Exception {
        final AbstractAuthenticationProcessingFilter filter = new CookieTokenAuthenticationFilter(ADMIN_ROUTES);

        filter.setAuthenticationManager(authManager(http));
        filter.setAuthenticationFailureHandler(failureHandler);

        return filter;
    }
    //endregion

    //region Filter Chain Bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Use the custom AuthenticationProvider and AuthenticationFilter
                .authenticationProvider(provider)
                .addFilterBefore(bearerAuthenticationFilter(http), AnonymousAuthenticationFilter.class)
                .addFilterBefore(cookieAuthenticationFilter(http), AnonymousAuthenticationFilter.class)
                // Specify which Routes/Endpoints should be protected and which ones should be accessible to everyone.
                .authorizeHttpRequests(auth ->
                    auth
                            // Only allow authenticated Users to use the API
                            .requestMatchers(PROTECTED_API_ROUTES).authenticated()
                            .requestMatchers(ADMIN_ROUTES).hasAuthority(Permission.ADMIN.toString())
                            // Permit everyone to get the static resources
                            .requestMatchers(AnyRequestMatcher.INSTANCE).permitAll()
                )

                // Disable CORS, CSRF as well as the default Web Security Login and Logout Pages.
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .logout().disable()
        ;

        http.exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        ;

        return http.build();
    }
    //endregion
}
