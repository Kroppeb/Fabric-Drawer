import drawer.getFrom
import drawer.put
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerialModule
import net.minecraft.nbt.CompoundTag
import utils.TestResult

class NbtEncoderItemTests : ItemTests(){
	override fun run(kSerializer: KSerializer<Any>, any: Any, serialModule: SerialModule): TestResult {
		val res = CompoundTag()
		kSerializer.put(any, res, "result", serialModule)
		val back = kSerializer.getFrom(res, "result", serialModule)
		return TestResult(any, back, res)
	}


}