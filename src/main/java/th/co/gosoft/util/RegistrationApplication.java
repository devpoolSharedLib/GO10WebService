package th.co.gosoft.util;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RegistrationApplication extends Application {
    
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Resources
        classes.add(th.co.gosoft.rest.TopicService.class);
        classes.add(th.co.gosoft.rest.RoomService.class);

        // Providers

        return classes;
    }

}
