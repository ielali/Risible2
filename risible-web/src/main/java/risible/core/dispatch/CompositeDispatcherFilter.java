package risible.core.dispatch;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 17/11/13
 * Time: 22:49
 */
public class CompositeDispatcherFilter implements javax.servlet.Filter {
    private DispatchingStrategy dispatchingStrategy = new NoDotDispatchingStrategy();

    private List<? extends Filter> filters = new ArrayList<Filter>();

    public void setFilters(List<? extends Filter> filters) {
        this.filters = new ArrayList<Filter>(filters);
    }

    /**
     * Clean up all the filters supplied, calling each one's destroy method in turn, but in reverse order.
     *
     * @see Filter#init(FilterConfig)
     */
    public void destroy() {
        for (int i = filters.size(); i-- > 0; ) {
            Filter filter = filters.get(i);
            filter.destroy();
        }
    }

    /**
     * Initialize all the filters, calling each one's init method in turn in the order supplied.
     *
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        for (Filter filter : filters) {
            filter.init(config);
        }
    }

    /**
     * Forms a temporary chain from the list of delegate filters supplied ({@link #setFilters(List)}) and executes them
     * in order. Each filter delegates to the next one in the list, achieving the normal behaviour of a
     * {@link FilterChain}, despite the fact that this is a {@link Filter}.
     *
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        new VirtualFilterChain(chain, filters).doFilter(request, response);
    }

    private class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<? extends Filter> additionalFilters;
        private int currentPosition = 0;

        private VirtualFilterChain(FilterChain chain, List<? extends Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
        }

        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException,
                ServletException {
            if (dispatchingStrategy.shouldDispatch((HttpServletRequest) request)) {
                if (currentPosition == additionalFilters.size()) {
                    originalChain.doFilter(request, response);
                } else {
                    currentPosition++;
                    Filter nextFilter = additionalFilters.get(currentPosition - 1);
                    nextFilter.doFilter(request, response, this);
                }
            } else {
                originalChain.doFilter(request, response);
            }
        }

    }

}
