package com.example.fase1_grupob.controller;

import com.example.fase1_grupob.security.RepositoryUserDetailsService;
import com.example.fase1_grupob.security.jwt.JwtRequestFilter;
import com.example.fase1_grupob.security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity//(debug = true)
public class SecurityConfiguration {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Autowired
    public RepositoryUserDetailsService userDetailService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.securityMatcher("/api/**")
				.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http
				.authorizeHttpRequests(authorize -> authorize
						// PRIVATE ENDPOINTS
						.requestMatchers(HttpMethod.DELETE,"/api/user/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST,"/api/auth/logout").hasRole("USER")
						.requestMatchers(HttpMethod.POST,"/api/posts/**").hasRole("USER")
						.requestMatchers(HttpMethod.POST,"/api/posts").hasRole("USER")
						.requestMatchers(HttpMethod.PUT,"/api/posts/*/like").hasRole("USER")
						.requestMatchers(HttpMethod.PUT,"/api/posts/*/comment").hasRole("USER")
						.requestMatchers(HttpMethod.PUT,"/api/user").hasRole("USER")
						.requestMatchers(HttpMethod.DELETE,"/api/posts/**").hasRole("USER")
						.requestMatchers(HttpMethod.DELETE,"/api/posts/*/comment/**").hasRole("USER")
						// PUBLIC ENDPOINTS
						.requestMatchers(HttpMethod.GET,"/api/searchBar").permitAll()
						.requestMatchers(HttpMethod.GET,"/api/posts/*/file").permitAll()
						.requestMatchers(HttpMethod.GET,"/api/posts/**").permitAll()
						.anyRequest().permitAll()
				);

		// Disable Form login Authentication
		http.formLogin(formLogin -> formLogin.disable());

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf(csrf -> csrf.disable());

		// Disable Basic Authentication
		http.httpBasic(httpBasic -> httpBasic.disable());

		// Stateless session
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}


	@Bean
	@Order(2)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.authorizeHttpRequests(authorize -> authorize
					// PUBLIC PAGES
					.requestMatchers("/","/login", "/register","/contactus","/**.css" ,"/search" ,"/**.js" ,"/download_image/**" ,"/viewPost/**" ,"/index", "/images/**", "/error/**").permitAll()
					// PRIVATE PAGES
					.requestMatchers("/", "/upload_image").hasRole("USER")
					.requestMatchers("/admin", "/deleteuser/**").hasRole("ADMIN")
					.anyRequest().authenticated()
			)
			.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/loginerror")
					.defaultSuccessUrl("/")
					.permitAll()
			)
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);

		return http.build();
	}

}
