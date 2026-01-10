package gr.hua.dit.studyrooms.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;



@Component
public class RoleBasedLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Set<String> roles =
                AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_STUDENT")) {
            response.sendRedirect("/study-rooms");
            return;
        }

        if (roles.contains("ROLE_TEACHER")) {
            response.sendRedirect("/profile");
            return;
        }

        response.sendRedirect("/");
    }
}
