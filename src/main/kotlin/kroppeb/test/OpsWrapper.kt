package kroppeb.test

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.mojang.datafixers.util.Function3
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import net.minecraft.datafixer.NbtOps
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.Tag
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream

abstract class OpsWrapper<T>protected constructor(val ops: DynamicOps<T>) : DynamicOps<T> by ops {
	open fun isEmpty(item : T?): Boolean = item == null || ops.empty() == item
	open fun getPseudoNumberValue(value: T): DataResult<Number> =
		if(compressMaps())
			getNumberValue(value)
		else
			getStringValue(value).flatMap { str ->
				str.toLongOrNull()?.let{ DataResult.success(it as Number)}
					?: str.toDoubleOrNull()?.let { DataResult.success(it as Number) }
					?: DataResult.error("Not a integer value: $str")
			}
	open fun createPseudoIntValue(value : Int): T = if(compressMaps()) createInt(value) else createString(value.toString())
	abstract fun canEncodeNull(): Boolean

	// region fixing delegation
	override fun <E : Any?> list(list: Iterable<E>?, prefix: T, encoder: Encoder<E>?): DataResult<T> {
		return ops.list(list, prefix, encoder)
	}

	override fun <E : Any?> list(
		list: Iterable<E>?,
		prefix: T,
		elementSerializer: Function<in E, out DataResult<T>>?
	): DataResult<T> {
		return ops.list(list, prefix, elementSerializer)
	}

	override fun <E : Any?> withEncoder(encoder: Encoder<E>?): Function<E, DataResult<T>> {
		return ops.withEncoder(encoder)
	}

	override fun createByteList(input: ByteBuffer?): T {
		return ops.createByteList(input)
	}

	override fun <U : Any?> convertList(outOps: DynamicOps<U>?, input: T): U {
		return ops.convertList(outOps, input)
	}

	override fun createIntList(input: IntStream?): T {
		return ops.createIntList(input)
	}

	override fun <K : Any?, V : Any?> map(
		map: Map<K, V>?,
		prefix: T,
		keySerializer: Function<in K, out DataResult<T>>?,
		elementSerializer: Function<in V, out DataResult<T>>?
	): DataResult<T> {
		return ops.map(map, prefix, keySerializer, elementSerializer)
	}

	override fun <U : Any?> convertMap(outOps: DynamicOps<U>?, input: T): U {
		return ops.convertMap(outOps, input)
	}

	override fun getMap(input: T): DataResult<MapLike<T>> {
		return ops.getMap(input)
	}

	override fun getLongStream(input: T): DataResult<LongStream> {
		return ops.getLongStream(input)
	}

	override fun createInt(value: Int): T {
		return ops.createInt(value)
	}

	override fun emptyList(): T {
		return ops.emptyList()
	}

	override fun <R : Any?> readMap(
		input: T,
		empty: DataResult<R>?,
		combiner: Function3<R, T, T, DataResult<R>>?
	): DataResult<R> {
		return ops.readMap(input, empty, combiner)
	}

	override fun compressMaps(): Boolean {
		return ops.compressMaps()
	}

	override fun getNumberValue(input: T, defaultValue: Number?): Number {
		return ops.getNumberValue(input, defaultValue)
	}

	override fun createByte(value: Byte): T {
		return ops.createByte(value)
	}

	override fun getByteBuffer(input: T): DataResult<ByteBuffer> {
		return ops.getByteBuffer(input)
	}

	override fun createFloat(value: Float): T {
		return ops.createFloat(value)
	}

	override fun mergeToPrimitive(prefix: T, value: T): DataResult<T> {
		return ops.mergeToPrimitive(prefix, value)
	}

	override fun getList(input: T): DataResult<Consumer<Consumer<T>>> {
		return ops.getList(input)
	}

	override fun getMapEntries(input: T): DataResult<Consumer<BiConsumer<T, T>>> {
		return ops.getMapEntries(input)
	}

	override fun updateGeneric(input: T, key: T, function: Function<T, T>?): T {
		return ops.updateGeneric(input, key, function)
	}

	override fun getGeneric(input: T, key: T): DataResult<T> {
		return ops.getGeneric(input, key)
	}

	override fun <E : Any?> withParser(decoder: Decoder<E>?): Function<T, DataResult<E>> {
		return ops.withParser(decoder)
	}

	override fun emptyMap(): T {
		return ops.emptyMap()
	}

	override fun mergeToList(list: T, values: MutableList<T>?): DataResult<T> {
		return ops.mergeToList(list, values)
	}

	override fun mergeToMap(map: T, values: MutableMap<T, T>?): DataResult<T> {
		return ops.mergeToMap(map, values)
	}

	override fun mergeToMap(map: T, values: MapLike<T>?): DataResult<T> {
		return ops.mergeToMap(map, values)
	}

	override fun listBuilder(): ListBuilder<T> {
		return ops.listBuilder()
	}

	override fun update(input: T, key: String?, function: Function<T, T>?): T {
		return ops.update(input, key, function)
	}

	override fun createLongList(input: LongStream?): T {
		return ops.createLongList(input)
	}

	override fun createMap(map: Map<T, T>?): T {
		return ops.createMap(map)
	}

	override fun getIntStream(input: T): DataResult<IntStream> {
		return ops.getIntStream(input)
	}

	override fun get(input: T, key: String?): DataResult<T> {
		return ops.get(input, key)
	}

	override fun set(input: T, key: String?, value: T): T {
		return ops.set(input, key, value)
	}

	override fun createDouble(value: Double): T {
		return ops.createDouble(value)
	}

	override fun createLong(value: Long): T {
		return ops.createLong(value)
	}

	override fun <E : Any?> withDecoder(decoder: Decoder<E>?): Function<T, DataResult<Pair<E, T>>> {
		return ops.withDecoder(decoder)
	}

	override fun mapBuilder(): RecordBuilder<T> {
		return ops.mapBuilder()
	}

	override fun createBoolean(value: Boolean): T {
		return ops.createBoolean(value)
	}

	override fun getBooleanValue(input: T): DataResult<Boolean> {
		return ops.getBooleanValue(input)
	}

	override fun createShort(value: Short): T {
		return ops.createShort(value)
	}
	// endregion

	companion object{
		operator fun <T>invoke(ops: DynamicOps<T>): OpsWrapper<T> = when(ops){
			JsonOps.INSTANCE -> JsonInstanceOpsW as OpsWrapper<T>
			JsonOps.COMPRESSED -> JsonCompressedOpsW as OpsWrapper<T>
			NbtOps.INSTANCE -> NbtOpsW as OpsWrapper<T>
			else -> {
				try{
					ops.listBuilder().add(ops.createInt(0)).add(ops.empty()).build(ops.empty())
					NullableOpsW(ops)
				}catch(e:Exception){
					NonNullableOpsW(ops)
				}
			}
		}
	}
}

open class NullableOpsW<T>(ops: DynamicOps<T>) : OpsWrapper<T>(ops){
	override fun canEncodeNull(): Boolean = true
}

open class NonNullableOpsW<T>(ops: DynamicOps<T>) : OpsWrapper<T>(ops){
	override fun canEncodeNull(): Boolean = false
}

object NbtOpsW: NonNullableOpsW<Tag>(NbtOps.INSTANCE){
	override fun isEmpty(item: Tag?): Boolean = item == null || item === EndTag.INSTANCE
}

object JsonInstanceOpsW: NullableOpsW<JsonElement>(JsonOps.INSTANCE){
	override fun isEmpty(item: JsonElement?): Boolean = item == null || item === JsonNull.INSTANCE
}

object JsonCompressedOpsW: NullableOpsW<JsonElement>(JsonOps.COMPRESSED){
	override fun isEmpty(item: JsonElement?): Boolean = item == null || item === JsonNull.INSTANCE
}
