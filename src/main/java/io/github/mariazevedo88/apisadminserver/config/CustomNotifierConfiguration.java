package io.github.mariazevedo88.apisadminserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import reactor.core.publisher.Mono;

/**
 * Custom notifier configuration to log all instance status
 * 
 * @author Mariana Azevedo
 * @since 29/08/2020
 */
@Configuration
public class CustomNotifierConfiguration extends AbstractEventNotifier {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomNotifierConfiguration.class);
	

	public CustomNotifierConfiguration(InstanceRepository repository) {
		super(repository);
	}

	/**
	 * Method that allows filtering of events in Spring Admin instances.
	 * 
	 * @author Mariana Azevedo
	 * @since 29/08/2020
	 * 
	 * @param event
	 * @param instance
	 * 
	 */
	@Override
	protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
		return Mono.fromRunnable(() -> {
			if (event instanceof InstanceStatusChangedEvent) {
				LOGGER.info("Instance {} ({}) is {}", instance.getRegistration().getName(), event.getInstance(),
						((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
			}else {
				LOGGER.info("Instance {} ({}) {}", instance.getRegistration().getName(), event.getInstance(),
						event.getType());
			}
		});
	}

}
