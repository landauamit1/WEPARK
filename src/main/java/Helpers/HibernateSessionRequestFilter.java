package Helpers;

import DAL.DBAccess;
import org.hibernate.*;
import org.hibernate.context.internal.ManagedSessionContext;

import javax.servlet.Filter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Represents Servlet Session Filter foreach Request
 */
public class HibernateSessionRequestFilter implements Filter {
    private SessionFactory sf;

    public static final String HIBERNATE_SESSION_KEY = "hibernateSession";
    public static final String END_OF_CONVERSATION_FLAG = "endOfConversation";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Session currentSession;

        // Try to get a Hibernate Session from the HttpSession
        HttpSession httpSession = ((HttpServletRequest) request).getSession();
        Session disconnectedSession = (Session) httpSession.getAttribute(HIBERNATE_SESSION_KEY);

        Transaction transaction = null;
        try {
            // Start a new conversation or in the middle?
            if (disconnectedSession == null) {
                currentSession = sf.openSession();
            } else {
                currentSession = (Session) disconnectedSession;
            }
            currentSession.setHibernateFlushMode(FlushMode.MANUAL);
            //currentSession.setHibernateFlushMode(FlushMode.MANUAL);

            ManagedSessionContext.bind(currentSession);
            transaction = currentSession.beginTransaction();
            chain.doFilter(request, response);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            int status = httpServletResponse.getStatus();
            if (status == HttpServletResponse.SC_OK) {
                if (transaction.isActive()) {
                    currentSession.flush();
                    transaction.commit();
                }
            } else {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        } catch (StaleObjectStateException staleEx) {
            staleEx.printStackTrace();
            // Rollback
            throw staleEx;
        } catch (Throwable ex) {
            ex.printStackTrace();
            // Rollback
            try {
                if (transaction != null & transaction.isActive()) {
                    transaction.rollback();
                }
            } catch (Throwable rbEx) {
            }
            throw new ServletException(ex);
        } finally {
            // Cleanup
            currentSession = ManagedSessionContext.unbind(sf);
            if (currentSession != null) {
                currentSession.close();
            }
            httpSession.setAttribute(HIBERNATE_SESSION_KEY, null);
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        sf = DBAccess.GetInstance().getSessionFactory();
    }

    public void destroy() {
    }

}
