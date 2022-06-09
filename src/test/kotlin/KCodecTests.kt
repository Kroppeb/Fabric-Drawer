import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import drawer.nbt.TagModule
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.plus
import kroppeb.test.asDebugCodec
import kroppeb.test.take
import net.minecraft.datafixer.NbtOps
import utils.TestResult

class KCodecJsonInstanceTests : ItemTests() {
	override fun run(kSerializer: KSerializer<Any>, any: Any, serialModule: SerialModule): TestResult =
		testKCodec(kSerializer, any, serialModule, JsonOps.INSTANCE)
}

class KCodecJsonCompressedTests : ItemTests() {
	override fun run(kSerializer: KSerializer<Any>, any: Any, serialModule: SerialModule): TestResult =
		testKCodec(kSerializer, any, serialModule, JsonOps.COMPRESSED)
}

class KCodecNbtTests : ItemTests() {
	override fun run(kSerializer: KSerializer<Any>, any: Any, serialModule: SerialModule): TestResult =
		testKCodec(kSerializer, any, serialModule, NbtOps.INSTANCE)
}

fun <T> testKCodec(
	kSerializer: KSerializer<Any>,
	any: Any,
	serialModule: SerialModule,
	ops: DynamicOps<T>
): TestResult {
	val codec = kSerializer.asDebugCodec(serialModule + TagModule)
	val encoded = codec.encode(any, ops).take()
	println(encoded)
	val decoded = codec.decode(ops, encoded).take().first
	return TestResult(any, decoded, encoded.toString())
}
