package geeks.crud

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import java.text.NumberFormat

/**
 * A behavior specification for {@link ValueFormat}.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 2/9/11
 * Time: 7:59 PM
 */

@RunWith(classOf[JUnitRunner])
class ValueFormatSpec extends Spec with ShouldMatchers {
  describe("BasicValueFormat") {
    it("should convert between basic types") {
      itShouldConvertBetweenTypes[String]("hello")
      itShouldConvertBetweenTypes[Long](123)
      itShouldConvertBetweenTypes[Int](123)
      itShouldConvertBetweenTypes[Short](123)
      itShouldConvertBetweenTypes[Byte](123)
      itShouldConvertBetweenTypes[Double](3232.11)
      itShouldConvertBetweenTypes[Float](2.3f)
      itShouldConvertBetweenTypes[Boolean](true)
    }

    def itShouldConvertBetweenTypes[T](value: T)(implicit m: Manifest[T]) {
      val format = new BasicValueFormat[T]
      val string = format.toString(value)
      format.toValue(string).get should be (value)
    }
  }

  describe("TextValueFormat") {
    it("should convert numbers") {
      itShouldParseNumbers[Long](123)
      itShouldParseNumbers[Int](123)
      itShouldParseNumbers[Short](123)
      itShouldParseNumbers[Byte](123)
    }

    it("should return None if unable to parse") {
      val format = new TextValueFormat[Int](NumberFormat.getIntegerInstance)
      format.toValue("blah") should be (None)
    }

    def itShouldParseNumbers[T](value: T)(implicit m: Manifest[T]) {
      val format = new TextValueFormat[T](NumberFormat.getIntegerInstance)
      val string = format.toString(value)
      format.toValue(string).get should be (value)
    }
  }
}