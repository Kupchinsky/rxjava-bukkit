package rx.bukkit.observable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import rx.Subscriber;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 2014 Ryan Michela
 */
class MultiplexingCommandExecutor implements CommandExecutor {
    private List<Subscriber<? super CommandEvent>> subscribers = new LinkedList<>();

    public void AddSubscriber(Subscriber<? super CommandEvent> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandEvent commandEvent = new CommandEvent(sender, command, label, args);

        List<Subscriber<? super CommandEvent>> unsubscribed = new LinkedList<>();
        for (Subscriber<? super CommandEvent> subscriber : subscribers) {
            if (subscriber.isUnsubscribed()) {
                unsubscribed.add(subscriber);
            } else {
                subscriber.onNext(commandEvent);
            }
        }
        // purge unsubscribed subscribers
        subscribers.removeAll(unsubscribed);

        return !commandEvent.isCancelled();
    }
}
