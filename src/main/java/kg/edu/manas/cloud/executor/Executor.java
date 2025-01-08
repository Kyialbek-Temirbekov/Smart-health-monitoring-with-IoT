package kg.edu.manas.cloud.executor;

import org.springframework.messaging.Message;

public interface Executor {
    void run(Message<?> message);
    String getName();
}
