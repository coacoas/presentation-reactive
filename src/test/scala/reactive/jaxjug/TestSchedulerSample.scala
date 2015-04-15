package reactive.jaxjug

import java.lang.{Long => JLong}
import java.util.concurrent.TimeUnit._

import org.scalatest.{FunSuite, ShouldMatchers}
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.TestScheduler
import rx.subjects.PublishSubject

import scala.collection.JavaConverters._

class TestSchedulerSample extends FunSuite with ShouldMatchers {
  test("Subscribe to cold observable replays all items") {
    val scheduler = new TestScheduler()
    val cancel = PublishSubject.create[Int]
    val ticks = Observable.
      interval(1, SECONDS, scheduler).
      takeUntil(cancel).
      publish

    val first = new TestSubscriber[JLong]
    val second = new TestSubscriber[JLong]

    ticks.subscribe(first)
    ticks.connect

    scheduler.advanceTimeBy(2, SECONDS)
    first.getOnNextEvents should equal(List(0L, 1L).asJava)

    ticks.subscribe(second)
    scheduler.advanceTimeBy(2, SECONDS)
    first.getOnNextEvents should equal(List[Long](0, 1, 2, 3).asJava)
    second.getOnNextEvents should equal(List[Long](2, 3).asJava)

    first.getOnCompletedEvents should be ('empty)

    cancel.onNext(-1)
    scheduler.triggerActions
    first.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS)
    second.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS)
  }

  test("Subscribe to hot observable takes next emitted items") {
    val scheduler = new TestScheduler()
    val cancel = PublishSubject.create[Int]
    val ticks = Observable.
      interval(1, SECONDS, scheduler).
      takeUntil(cancel).
      publish

    val first = new TestSubscriber[JLong]
    val second = new TestSubscriber[JLong]

    ticks.subscribe(first)
    ticks.connect

    scheduler.advanceTimeBy(2, SECONDS)
    first.getOnNextEvents should equal(List(0L, 1L).asJava)

    ticks.subscribe(second)
    scheduler.advanceTimeBy(2, SECONDS)
    first.getOnNextEvents should equal(List[Long](0, 1, 2, 3).asJava)
    second.getOnNextEvents should equal(List[Long](2, 3).asJava)

    cancel.onNext(-1)
    scheduler.triggerActions
    first.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS)
    second.awaitTerminalEventAndUnsubscribeOnTimeout(1, SECONDS)
  }
}
