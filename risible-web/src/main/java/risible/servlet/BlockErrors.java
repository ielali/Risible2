package risible.servlet;

import java.util.Collections;
import java.util.Set;

public interface BlockErrors {
    /**
     * @return the list of currently blocked ip addresses
     */
    Set blocked();

    /**
     * call this method when the ip address generates an error.
     *
     * @param ip the ip of the client causing an error
     */
    void error(String ip);

    BlockErrors NULL = new BlockErrors() {
        public Set blocked() {
            return Collections.EMPTY_SET;
        }

        public void error(String ip) {
        }
    };
}
