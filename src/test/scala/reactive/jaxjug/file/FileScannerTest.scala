package reactive.jaxjug.file

import java.io.File
import java.nio.file.{Files, NoSuchFileException, Path}
import java.util.UUID
import java.util.concurrent.TimeUnit

import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSuite, ShouldMatchers}
import rx.Observable
import rx.observers.TestSubscriber

import scala.collection.JavaConverters._

class FileScannerTest extends FunSuite with ShouldMatchers with PropertyChecks {

  // File system generator

  val tmpDir = new File(System.getProperty("java.io.tmpdir"))

  def fileGen(parent: File): Gen[File] = for {
    name <- Gen.alphaStr
    f = new File(parent, name)
    _ = f.deleteOnExit()
  } yield f

  def dirGen(parent: File, maxDepth: Int = 5): Gen[File] = for {
    name <- Gen.alphaStr
    dir = new File(parent, name)
    _ = dir.mkdir()
    _ = dir.deleteOnExit()
    children <- if (maxDepth == 0) Gen.listOf(fileGen(dir)) else Gen.listOf(Gen.oneOf(fileGen(dir), dirGen(dir, maxDepth - 1)))
  } yield dir

  test("Scan a non-existent directory yields an onError condition") {
    val nonexistent: File = new File(tmpDir, UUID.randomUUID().toString)
    val sub: Observable[Path] = new FileScanner(nonexistent.toPath).tree()
    val ts = new TestSubscriber[Path]()

    sub.subscribe(ts)

    ts.awaitTerminalEventAndUnsubscribeOnTimeout(500, TimeUnit.MILLISECONDS)
    val errors = ts.getOnErrorEvents.asScala
    val events = ts.getOnNextEvents.asScala

    errors.map(_.getClass) should equal(classOf[NoSuchFileException] :: Nil)
    events should be('empty)
  }

  test("Scan an empty directory produces an Observable with that entry") {
    val temp = Files.createTempDirectory("reactive-file")
    temp.toFile.deleteOnExit()
    val ts = new TestSubscriber[Path]()

    new FileScanner(temp).tree.subscribe(ts)
    ts.awaitTerminalEventAndUnsubscribeOnTimeout(500, TimeUnit.MILLISECONDS)

    ts.getOnErrorEvents.asScala should be('empty)
    ts.getOnNextEvents.asScala should equal(temp :: Nil)
  }

  test("FileScanner finds all files and directories") {
    forAll(dirGen(Files.createTempDirectory("dirscan").toFile), maxSize(10)) { (f: File) =>
      val ts = new TestSubscriber[Path]
      new FileScanner(f.toPath).tree.subscribe(ts)
      ts.awaitTerminalEventAndUnsubscribeOnTimeout(500, TimeUnit.MILLISECONDS)
      val files = ts.getOnNextEvents

      ts.getOnErrorEvents.asScala should be('empty)
      ts.getOnCompletedEvents should have size 1

      files.size should be >= 1
      files should contain(f.toPath)
    }
  }
}
