import drawer.nbt.TagModule
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.plus
import kroppeb.test.asDebugCodec
import kroppeb.test.take
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import utils.TestResult

/**
 * Tests the serializers by serializing the ItemTests to json and back.
 * This does a lot of serializations that are useless
 */
class GeneralSerializationItemTests : ItemTests() {

	override fun run(kSerializer: KSerializer<Any>, any: Any, serialModule: SerialModule): TestResult {

		// tags are saved as primitive, so you need array
		val json = Json(JsonConfiguration.Stable.copy(useArrayPolymorphism = true), context = serialModule)

		val encoded = json.stringify(kSerializer, any)
		println(encoded)
		val decoded = json.parse(kSerializer, encoded)
		return TestResult(any, decoded, encoded)
	}

	@Disabled("contains -Infinity which isn't allowed in json")
	override fun `non-nullable lists without nulls`() {
		super.`non-nullable lists without nulls`()
	}

	@Disabled("contains -Infinity which isn't allowed in json")
	override fun `nullable lists without nulls`() {
		super.`nullable lists without nulls`()
	}
}