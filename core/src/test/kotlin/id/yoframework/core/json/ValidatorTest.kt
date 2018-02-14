package id.yoframework.core.json

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class ValidatorTest {

    @Test
    fun `NonNullValidator must able to check non null value and return correct value`() {
        val jsonObject = json {
            obj(
                "some_string" to "this is String",
                "some_integer" to 12,
                "some_boolean" to true,
                "some_double" to 0.0,
                "parent" to obj(
                    "some_string" to "this is String",
                    "some_integer" to 12,
                    "some_boolean" to true,
                    "some_double" to 0.0,
                    "parent" to obj(
                        "some_string" to "this is String",
                        "some_integer" to 12,
                        "some_boolean" to true,
                        "some_double" to 0.0
                    )
                )
            )
        }

        assertEquals(NonNullValidator(String::class, jsonObject, "some_string").validate(), "this is String")
        assertEquals(NonNullValidator(Int::class, jsonObject, "some_integer").validate(), 12)
        assertEquals(NonNullValidator(Boolean::class, jsonObject, "some_boolean").validate(), true)
        assertEquals(NonNullValidator(Double::class, jsonObject, "some_double").validate(), 0.0)

        assertEquals(NonNullValidator(String::class, jsonObject, "parent.some_string").validate(), "this is String")
        assertEquals(NonNullValidator(Int::class, jsonObject, "parent.some_integer").validate(), 12)
        assertEquals(NonNullValidator(Boolean::class, jsonObject, "parent.some_boolean").validate(), true)
        assertEquals(NonNullValidator(Double::class, jsonObject, "parent.some_double").validate(), 0.0)

        assertEquals(
            NonNullValidator(String::class, jsonObject, "parent.parent.some_string").validate(),
            "this is String"
        )
        assertEquals(NonNullValidator(Int::class, jsonObject, "parent.parent.some_integer").validate(), 12)
        assertEquals(NonNullValidator(Boolean::class, jsonObject, "parent.parent.some_boolean").validate(), true)
        assertEquals(NonNullValidator(Double::class, jsonObject, "parent.parent.some_double").validate(), 0.0)

        assertFailsWith(ParentNotFoundException::class) {
            NonNullValidator(String::class, jsonObject, "random.some_string").validate()
        }
        assertFailsWith(NullValueException::class) {
            NonNullValidator(Int::class, jsonObject, "some_integer_random").validate()
        }
        assertFailsWith(ParentNotFoundException::class) {
            NonNullValidator(Boolean::class, jsonObject, "random.random.some_boolean").validate()
        }
        assertFailsWith(ParentNotFoundException::class) {
            NonNullValidator(Double::class, jsonObject, "some_double.random").validate()
        }
        assertFailsWith(ClassIncompatibleException::class) {
            println(NonNullValidator(String::class, jsonObject, "some_double").validate())
        }
    }

}