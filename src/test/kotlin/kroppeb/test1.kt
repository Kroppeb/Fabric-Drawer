package kroppeb

import drawer.getFrom
import drawer.put
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.CompoundTag
import utils.testCases

@Serializable
data class Test(val p:Map<Pair<Int,Int>, Int>){
}

fun main() {
	val module = SerializersModule {

	}

	val case = testCases[5]
	val input = case.obj
	val res = CompoundTag()
	val kSerializer = case.serializer as KSerializer<Any>
	kSerializer.put(input, res, "result", EmptyModule)
	val back = kSerializer.getFrom(res, "result", EmptyModule)
	println(input)
	println(res)
	println(input == back)
}