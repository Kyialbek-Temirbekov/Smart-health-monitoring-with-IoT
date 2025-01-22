package kg.edu.manas.cloud.service.executor;

import org.springframework.messaging.Message;

public interface Executor {
    void run(Message<?> message);
    String getName();
}
