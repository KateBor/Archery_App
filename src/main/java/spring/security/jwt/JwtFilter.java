package spring.security.jwt;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    private static final String BACKEND_HOST = "http://localhost:8080";
    private static final String OUR_DOMAIN = "http://f0643001.xsph.ru";
    private static final String FRONTEND_HOST = "http://localhost:63342";

    private void setAccessControlAllowOrigin(HttpServletResponse response,
                                             HttpServletRequest request) {
        if (BACKEND_HOST.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", BACKEND_HOST);
        } else if (OUR_DOMAIN.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", OUR_DOMAIN);
        } else if (FRONTEND_HOST.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", FRONTEND_HOST);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, "
                + "Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
        response.setHeader("Access-Control-Allow-Credentials","true");
        setAccessControlAllowOrigin(response, request);
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);

        if ( request.getMethod().equals("OPTIONS") ) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
