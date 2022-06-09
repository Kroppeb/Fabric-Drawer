package kroppeb.test

import drawer.getFrom
import drawer.put
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.CompoundTag
import kotlin.math.nextDown

@Serializable
class Test(){
	var input = 0
	constructor(input: Int) : this(){
		this.input = input
	}

	val t by lazy{input}
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (t != (other as Test).t)
			return false
		return true
	}

	override fun hashCode(): Int {
		return 15298555 + t.hashCode()
	}

	override fun toString(): String {
		return "Test($t)"
	}


}

fun main() {
	/*
	val module = SerializersModule {

	}

	val input = Test(5)
	val kSerializer = Test.serializer()

	val res = Json(JsonConfiguration.Default.copy(allowStructuredMapKeys = true)).stringify(kSerializer, input)
	//kSerializer.put(input, res, "result", EmptyModule)
	//val back = kSerializer.getFrom(res, "result", EmptyModule)

	val back = Json(JsonConfiguration.Stable	).parse(kSerializer, res)
	//println(input)
	println(res)
	println(back)
	*/
	val size = 30e6
	val step = size - size.nextDown()
	println(step)
	println(step.nextDown() * 2)
}