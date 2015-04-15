package reactive.jaxjug.testing;


import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Created by bcarlson on 4/15/15.
 */
public class TestSchedulerJUnit {
    @Test
    public void testScheduler() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> cancel = PublishSubject.create();
        Observable<Long> ticks = Observable.interval(1, SECONDS, scheduler).takeUntil(cancel);

        TestSubscriber first = new TestSubscriber();
        TestSubscriber second = new TestSubscriber();

        ticks.subscribe(first);
        scheduler.advanceTimeBy(2, SECONDS);
        Assert.assertEquals(first.getOnNextEvents(), Lists.<Long>newArrayList(0L, 1L));

        ticks.subscribe(second);
        scheduler.advanceTimeBy(2, SECONDS);
        Assert.assertEquals(second.getOnNextEvents(), Lists.<Long>newArrayList(0L, 1L));
        Assert.assertEquals(first.getOnNextEvents(), Lists.<Long>newArrayList(0L, 1L, 2L, 3L));

        cancel.onNext(-1);
        scheduler.triggerActions();
        first.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS);
        second.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS);
    }

    @Test
    public void testHot() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> cancel = PublishSubject.create();
        ConnectableObservable<Long> ticks = Observable.interval(1, SECONDS, scheduler).takeUntil(cancel).publish();

        TestSubscriber first = new TestSubscriber();
        TestSubscriber second = new TestSubscriber();

        ticks.subscribe(first);
        ticks.connect();
        scheduler.advanceTimeBy(2, SECONDS);
        Assert.assertEquals(first.getOnNextEvents(), Lists.<Long>newArrayList(0L, 1L));

        ticks.subscribe(second);
        scheduler.advanceTimeBy(2, SECONDS);
        Assert.assertEquals(second.getOnNextEvents(), Lists.<Long>newArrayList(2L, 3L));
        Assert.assertEquals(first.getOnNextEvents(), Lists.<Long>newArrayList(0L, 1L, 2L, 3L));

        cancel.onNext(-1);
        scheduler.triggerActions();
        first.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS);
        second.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS);
    }

}
