package project5.bean;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import org.apache.logging.log4j.*;


@Singleton
@Startup
public class StartupBean {
    @Inject
    UserBean userBean;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(StartupBean.class);


    @PostConstruct
    public void init() {
        userBean.createDefaultUsersIfNotExistent();
    }
}