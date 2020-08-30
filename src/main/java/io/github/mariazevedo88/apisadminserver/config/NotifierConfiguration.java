package io.github.mariazevedo88.apisadminserver.config;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.notify.CompositeNotifier;
import de.codecentric.boot.admin.server.notify.Notifier;
import de.codecentric.boot.admin.server.notify.RemindingNotifier;
import de.codecentric.boot.admin.server.notify.filter.FilteringNotifier;

/**
 * Notifier configuration to send notifications by telegram or email.
 * 
 * @author Mariana Azevedo
 * @since 29/08/2020
 */
@Configuration(proxyBeanMethods = false)
public class NotifierConfiguration {
	
	private final InstanceRepository repository;
	private final ObjectProvider<List<Notifier>> otherNotifiers;

	public NotifierConfiguration(InstanceRepository repository, ObjectProvider<List<Notifier>> otherNotifiers) {
		this.repository = repository;
	    this.otherNotifiers = otherNotifiers;
	}

	/**
	 * Method that allows to filter certain events based on policies.
	 * 
	 * @author Mariana Azevedo
	 * @since 29/08/2020
	 * 
	 * @return <code>FilteringNotifier</code> object
	 */
	@Bean
	public FilteringNotifier filteringNotifier() { 
		CompositeNotifier delegate = new CompositeNotifier(this.otherNotifiers.getIfAvailable(Collections::emptyList));
	    return new FilteringNotifier(delegate, this.repository);
	}

	/**
	 * Method that creates a notifier that reminds certain statuses (start and stop instances) to send reminder 
	 * notification using a delegate.
	 * 
	 * @author Mariana Azevedo
	 * @since 29/08/2020
	 * 
	 * @return <code>RemindingNotifier</code> object
	 */
	@Primary
	@Bean(initMethod = "start", destroyMethod = "stop")
	public RemindingNotifier remindingNotifier() { 
		RemindingNotifier notifier = new RemindingNotifier(filteringNotifier(), this.repository);
	    notifier.setReminderPeriod(Duration.ofMinutes(5));
	    notifier.setCheckReminderInverval(Duration.ofSeconds(10));
	    return notifier;
	}

}
